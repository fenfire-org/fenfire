/*   
HTTPClient.java
 *    
 *    Copyright (c) 2004, Benja Fallenstein.
 *
 *    This file is part of Fenfire.
 *    
 *    Fenfire is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Fenfire is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Fenfire; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.fenfeed.http;
import org.nongnu.storm.util.CopyUtil;
import java.io.*;
import java.net.*;
import java.util.*;

/** A HTTP client with a disk cache.
 *  When this client retrieves a representation from the Web,
 *  it stores it in a directory on the disk. The filename is munged
 *  to map each HTTP URI to a unique filename (except for extremely long
 *  URIs, where we use the Java hash code at the end). The filename munging
 *  algorithm tries to stay reasonably close to the original URI. For example,
 *  if the cache directory is _httpcache, the representation of
 *  http://java.sun.com/j2se/1.4.2/docs/api/java/util/Date.html
 *  is stored in the file:
 *  <p>
 *  _httpcache/java.sun.com/j2se/1.4.2/docs/api/java/util/_Date.html-content
 *  <p>
 *  (The file .../_Date.html-header contains the HTTP headers
 *  of the message.)
 *  <p>
 *  Cached representations so far aren't deleted automatically.
 *  (For now this is intended to be used in a feed reader, where
 *  this isn't a priority. Still, it would be nice to have too.)
 *  <p>
 *  The Expires, Last-Modified and E-Tag headers are supported;
 *  the Cache-Control header isn't yet (XXX e.g. no-store
 *  should be supported). We always re-request unless the Expires date
 *  is in the future.
 */
public class HTTPClient {

    /** An IOException signalling that the HTTP server replied with
     *  an unhandled status code. The status code and message
     *  are contained in the <code>statusCode</code> and 
     *  <code>message</code> fields.
     */
    public class HTTPException extends IOException {
	/** The status code sent by the server.
	 */
	public final int statusCode;
	/** The message sent by the server along with the status code,
	 *  e.g. statusCode==404, message=="Not Found".
	 */
	public final String message;

	public HTTPException(int statusCode, String message) {
	    super("Server replied: "+statusCode+" "+message);
	    this.statusCode = statusCode;
	    this.message = message;
	}
    }

    File cacheDir;

    public HTTPClient() {
	this(new File("_httpcache"));
    }

    public HTTPClient(File cacheDir) {
	this.cacheDir = cacheDir.getAbsoluteFile();

	if(!cacheDir.exists()) cacheDir.mkdir();
    }

    public Resource get(String uri) throws IOException {
	return new CachedResource(uri);
    }

    private class CachedResource implements Resource {
	Resource redirect;
	String newURI;

	String uri;
	File dir;
	String name;

	private CachedResource(String uri) throws IOException {
	    this.uri = uri;
	    
	    File f = getResourceFile(uri);
	    dir = f.getParentFile();
	    name = f.getName();

	    reload(false);
	}

	protected File file(String suffix) { 
	    return new File(dir, name+'-'+suffix);
	}
	protected FileInputStream in(String name) throws IOException { 
	    return new FileInputStream(file(name)); 
	}
	protected FileOutputStream out(String name) throws IOException { 
	    return new FileOutputStream(file(name)); 
	}

	public void reload(boolean force) throws IOException {
	    System.err.println("FETCH "+uri);

	    HttpURLConnection conn =
		(HttpURLConnection)new URL(uri).openConnection();
	    
	    if(file("header").exists()) {
		Map hdr = header();

		if(!force && hdr.containsKey("expires")) {
		    Date expires = new Date((String)hdr.get("expires"));
		    Date now = new Date();

		    if(expires.before(now)) {
			System.out.println("- use cached version");
			return;
		    }
		}

		// XXX honor the Cache-Control header, too...

		if(hdr.containsKey("etag"))
		    conn.addRequestProperty("If-None-Match", 
					    (String)hdr.get("etag"));

		if(hdr.containsKey("last-modified"))
		    conn.addRequestProperty("If-Modified-Since", 
					    (String)hdr.get("last-modified"));
	    }

	    conn.setInstanceFollowRedirects(false); // we follow them ourselves
	    conn.connect();

	    int code = conn.getResponseCode();
	    
	    redirect = null;
	    newURI = null;

	    if(code == 200) {
		System.out.println("- ok");

		CopyUtil.copy(conn.getInputStream(), out("content"));

		Writer w = new OutputStreamWriter(out("header"), "UTF-8");
	    
		for(int i=1; conn.getHeaderField(i) != null; i++) {
		    w.write(conn.getHeaderFieldKey(i)+": "+
			    conn.getHeaderField(i)+"\n");
		}

		w.close();
	    } else if(code==301 || code==302 || code==303 || code==307) {
		System.out.println("- redirect");

		if(code == 301) // moved permanently
		    newURI = (String)conn.getHeaderField("location");

		redirect = get(conn.getHeaderField("location"));
	    } else if(code==304) {
		// 304 Not Modified -- just use the already cached version
		System.out.println("- unchanged");
	    } else {
		throw new HTTPException(code, conn.getResponseMessage());
	    }
	}

	public String getURI() {
	    return uri;
	}

	public InputStream getInputStream() throws IOException {
	    if(redirect != null) return redirect.getInputStream();
	    return in("content");
	}

	public String getContentType() throws IOException {
	    if(redirect != null) return redirect.getContentType();
	    return (String)header().get("content-type");
	}

	public String getNewURI() {
	    return newURI;
	}

	/** Return the headers as a String -> String mapping.
	 *  If there's more than one header of a particular kind,
	 *  only the last one is in the map. For the purposes
	 *  we're using this for, that's ok.
	 */
	protected Map header() throws IOException {
	    Map m = new HashMap();
	    Reader r0 = new InputStreamReader(in("header"), "UTF-8");
	    BufferedReader r = new BufferedReader(r0);

	    for(String l=r.readLine(); l != null; l=r.readLine()) {
		l = l.trim();
		if(l.equals("")) continue;

		int i = l.indexOf(':');

		m.put(l.substring(0, i).trim().toLowerCase(), 
		      l.substring(i+1).trim());
	    }

	    return m;
	}
    }

    protected final File getResourceFile(String uri) {
	File file = cacheDir;

	if(!uri.toLowerCase().startsWith("http://"))
	    throw new UnsupportedOperationException("Non-HTTP URI "+uri);

	uri = uri.substring("http://".length());

	boolean isHost = true;
	boolean more = true;

	while(true) {
	    int i = uri.indexOf('/');

	    if(i < 0) {
		i = uri.length();

		if(isHost) {
		    uri += "/";
		} else {
		    more = false;
		}
	    }
	    
	    String part = uri.substring(0, i);
	    if(isHost) part = part.toLowerCase();

	    if(part.equals(""))
		file = new File(file, "-dir");
	    else
		file = subdir(file, part);

	    if(!more) return file;

	    if(!file.exists()) file.mkdir();
	    uri = uri.substring(i+1);
	    isHost = false;
	}
    }

    protected File subdir(File dir, String name) {
	String s = "";
	for(int i=0; i<name.length(); i++) {
	    char c = name.charAt(i);
	    if((c>='a' && c<='z') ||
	       (c>='0' && c<='9') || c=='.') s += c;
	    else if(c >= 'A' && c <= 'Z') s += "_"+c;
	    else s += "_"+((int)c)+"_";
	}
	if(s.length() > 225)
	    s = s.substring(0, 200) + s.hashCode();

	return new File(dir, s);
    }

    public static void main(String[] args) throws IOException {
	String uri = args[0];
	HTTPClient http = new HTTPClient();

	Resource r = http.get(uri);
	System.out.println("Content type: "+r.getContentType());
	//System.out.println();
	//CopyUtil.copy(r.getInputStream(), System.out);
    }
}
