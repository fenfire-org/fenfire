/*   
HTTPContext.java
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
import java.io.File;

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
 *  of the message, the file .../_Date.html-lastRead the date
 *  the server was last asked for the newest version.)
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
public final class HTTPContext {
    private File cacheDir;
    private String accept = null;

    public HTTPContext() {
	this(new File("_httpcache"));
    }

    public HTTPContext(File cacheDir) {
	this.cacheDir = cacheDir.getAbsoluteFile();

	if(!cacheDir.exists()) cacheDir.mkdir();
    }

    /** Set the string to send in the Accept header.
     *  null means send no such header (the default).
     */
    public void setAccept(String accept) {
	this.accept = accept;
    }

    public String getAccept() {
	return accept;
    }

    File getResourceFile(String uri) {
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

    private File subdir(File dir, String name) {
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
}
