/*   
HTTPResource.java
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
import org.nongnu.storm.util.DateParser;
import java.io.*;
import java.net.*;
import java.util.*;

/** A resource identified by an HTTP URI.
 *  Allows getting the header and body of the resource currently
 *  associated with the resource (currently there is no support
 *  for content negotiation, which allows different representations
 *  to be associated with the same resource). The representation
 *  may be cached; it will not be reloaded unless explicitly requested
 *  through the reload() method or through executing a new HTTPClient.get()
 *  on the resource.
 *  <p>
 *  If the client can change the reference it got the resource's URI from,
 *  it should check the getNewURI() method when getting the representation.
 *  <p>
 *  XXX add interface for getting all header fields
 */
public final class HTTPResource {
    public static boolean dbg = false;
    private void p(String s) { System.out.println("HTTPResource:: "+s); }

    private HTTPContext context;

    private String uri;
    private File dir;
    private String name;

    private boolean hasLoaded;

    private HTTPResource redirect;
    private String newURI;

    public HTTPResource(String uri, HTTPContext context) throws IOException {
	this.uri = uri;
	this.context = context;
	    
	File f = context.getResourceFile(uri);
	dir = f.getParentFile();
	name = f.getName();

	if(file("location").exists()) {
	    redirect = new HTTPResource(CopyUtil.readString(in("location")),
					context);
	    hasLoaded = true;
	} else {
	    hasLoaded = file("content").exists();
	}
    }

    private File file(String suffix) { 
	return new File(dir, name+'-'+suffix);
    }
    private FileInputStream in(String name) throws IOException { 
	return new FileInputStream(file(name)); 
    }
    private FileOutputStream out(String name) throws IOException { 
	return new FileOutputStream(file(name)); 
    }

    /** Reload the representation associated with the resource.
     *  @param force If true, a request to the server will be
     *         executed even if the 'Expires' header of the cached version
     *         specifies a date in the future.
     *  @return Whether the resource changed.
     */
    public boolean reload(boolean force) throws IOException {
	if(dbg) p("FETCH "+uri);

	HttpURLConnection conn = getConnection(uri);

	hasLoaded = true;
	    
	if(file("header").exists()) {
	    Map hdr = header();

	    if(!force && hdr.containsKey("expires")) {
		Date expires = new Date((String)hdr.get("expires"));
		Date now = new Date();

		if(expires.after(now)) {
		    if(dbg) p("- use cached version");
		    // does not update lastRead because we didn't
		    // contact the server
		    return false;
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

	if(context.getAccept() != null)
	    conn.addRequestProperty("Accept", context.getAccept());

	conn.setInstanceFollowRedirects(false); // we follow them ourselves
	conn.connect();

	int code = conn.getResponseCode();
	    
	boolean changed;
	newURI = null;

	file("content").delete();
	file("location").delete();
	file("header").delete();
	file("lastRead").delete();


	if(code == 200) {
	    if(dbg) p("- ok");

	    writeHeaders(conn);
	    CopyUtil.copy(conn.getInputStream(), out("content"));

	    redirect = null;
	    changed = true;
	} else if(code==301 || code==302 || code==303 || code==307) {
	    if(dbg) p("- redirect");

	    writeHeaders(conn);

	    String location = (String)conn.getHeaderField("location");
	    CopyUtil.writeString(location, out("location"));

	    if(code == 301) newURI = location; // moved permanently

	    if(redirect == null || !redirect.getURI().equals(location)) {
		redirect = new HTTPResource(location, context);
		changed = true;
	    } else {
		changed = redirect.reload(force);
	    }
	} else if(code==304) {
	    // 304 Not Modified -- just use the already cached version
	    if(dbg) p("- unchanged");
	    redirect = null;
	    changed = false;
	} else {
	    throw new HTTPException(code, conn.getResponseMessage());
	}

	// The server was asked for the newest version of the resource
	// and has replied -- store current time as the "last read" date
	Date now = new Date();
	CopyUtil.writeString(DateParser.getIsoDate(now), out("lastRead"));

	return changed;
    }

    private void writeHeaders(URLConnection conn) throws IOException {
	Writer w = new OutputStreamWriter(out("header"), "UTF-8");
	    
	for(int i=1; true; i++) {
	    String value;
	    try {
		value = conn.getHeaderField(i);
		if(value == null) break;
	    } catch(NoSuchElementException e) {
		// shouldn't be thrown, but is thrown by Classpath currently
		break;
	    }
		
	    w.write(conn.getHeaderFieldKey(i)+": "+value+"\n");
	}
	
	w.close();
    }

    public String getURI() {
	return uri;
    }

    public InputStream getInputStream() throws IOException {
	if(!hasLoaded) reload(false);
	if(redirect != null) return redirect.getInputStream();
	return in("content");
    }

    public String getContentType() throws IOException {
	if(!hasLoaded) reload(false);
	if(redirect != null) return redirect.getContentType();
	return (String)header().get("content-type");
    }

    /** Return the date that the server was last asked for the newest version
     *  of this resource.
     *  If the server is contacted and replies that the resource
     *  is unchanged, the lastRead date is updated; if the
     *  resource is loaded from the local cache, and the server
     *  is <em>not</em> contacted, the date remains the same.
     */
    public Date lastRead() throws IOException {
	try {
	if(!hasLoaded) reload(false);
	} catch (RuntimeException e) {
	    System.out.println(uri);
	    throw e;
	}
	return DateParser.parse(CopyUtil.readString(in("lastRead")));
    }

    /** If the server replied with 301 Moved Permanently, the new URI
     *  of this resource; otherwise, null.
     *  If the client followed a reference it can change (for example,
     *  an entry in the subscriptions list of a feed reader), it should
     *  call this method after re-loading and, if it returns non-null, 
     *  change the reference.
     */
    public String getNewURI() {
	return newURI;
    }

    /** Return the headers as a String -> String mapping.
     *  If there's more than one header of a particular kind,
     *  only the last one is in the map. For the purposes
     *  we're using this for, that's ok.
     */
    private Map header() throws IOException {
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

    private static HttpURLConnection getConnection(String uri) 
	throws IOException {

	// temporary workaround until Kaffe is updated 
	// to the newest Classpath, which uses the inetlib
	// HTTPURLConnection by default.
	
	try {
	    Class c = Class.forName("gnu.inet.http.HTTPURLConnection");

	    Class[] paramClasses = new Class[] { URL.class };
	    Object[] params = new Object[] { new URL(uri) };
	    return (HttpURLConnection)
		c.getConstructor(paramClasses).newInstance(params);
	} catch(ClassNotFoundException e) {
	} catch(NoSuchMethodException e) {
	} catch(InstantiationException e) {
	} catch(IllegalAccessException e) {
	} catch(java.lang.reflect.InvocationTargetException e) {
	} // Reflection is fun, huh?

	return (HttpURLConnection)new URL(uri).openConnection();
    }

    public static void main(String[] args) throws IOException {
	dbg = true;
	
	String uri = args[0];
	HTTPContext context = new HTTPContext();

	context.setAccept("application/turtle, "+
			  "application/rdf+xml; q=0.9, "+
			  "application/rss+xml; q=0.4, "+
			  "application/xml; q=0.3, "+
			  "text/xml; q=0.2, "+
			  "*/*; q=0.1");

	HTTPResource r = new HTTPResource(uri, context);

	System.out.println("Content type: "+r.getContentType());
	//System.out.println();
	//CopyUtil.copy(r.getInputStream(), System.out);
    }
}
