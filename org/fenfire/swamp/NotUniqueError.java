/*
NotUniqueError.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka
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
 */
/*
 * Written by Tuomas J. Lukka
 */

package org.fenfire.swamp;


public class NotUniqueError extends Error {
    public final Object subject;
    public final Object predicate;
    public final Object object;
    public final Object quad;

    public NotUniqueError(Object subject, Object predicate, Object object) {
	this(subject, predicate, object, null);
    }
    public NotUniqueError(Object subject, Object predicate,
			  Object object, Object quad) {
	this.subject = subject;
	this.predicate = predicate;
	this.object = object;
	this.quad = quad;
    }
    public String toString() {
	if (quad == null)
	    return "Not Unique triple exception: "+subject+" "+predicate+" "+object;
	return "Not Unique quad exception: "+subject+" "+predicate+" "+object+" quad: "+quad;
    }
}
