/*   
HTTPException.java
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
import java.io.IOException;

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
