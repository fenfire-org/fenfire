/*   
Resource.java
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
import java.io.InputStream;
import java.io.IOException;
import java.util.Date;

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
public interface Resource {

    String getURI();
    InputStream getInputStream() throws IOException;
    String getContentType() throws IOException;

    /** Return the date that the server was last asked for the newest version
     *  of this resource.
     *  If the server is contacted and replies that the resource
     *  is unchanged, the lastRead date is updated; if the
     *  resource is loaded from the local cache, and the server
     *  is <em>not</em> contacted, the date remains the same.
     */
    Date lastRead() throws IOException;

    /** If the server replied with 301 Moved Permanently, the new URI
     *  of this resource; otherwise, null.
     *  If the client followed a reference it can change (for example,
     *  an entry in the subscriptions list of a feed reader), it should
     *  call this method after re-loading and, if it returns non-null, 
     *  change the reference.
     */
    String getNewURI() throws IOException;

    /** Reload the representation associated with the resource.
     *  @param force If true, a request to the server will be
     *         executed even if the 'Expires' header of the cached version
     *         specifies a date in the future.
     */
    void reload(boolean force) throws IOException;
}
