/*
Cursor.java
 *
 *    Copyright (c) 2005 by Benja Fallenstein
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
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire;
import org.nongnu.libvob.layout.*;

/** A Fenfire cursor position for use by BrowserLob.
 */
public interface Cursor {

    /** The RDF node that can be seen as the 'current node,' if any;
     *  otherwise, null.
     */
    Object getNode();

    class SimpleCursor implements Cursor {
	private Object node;

	public SimpleCursor(Object node) { this.node = node; }

	public Object getNode() { return node; }

	public int hashCode() { return 328490+node.hashCode(); }
	public boolean equals(Object o) {
	    if(!(o instanceof SimpleCursor)) return false;
	    return node.equals(((SimpleCursor)o).node);
	}
    }
}
