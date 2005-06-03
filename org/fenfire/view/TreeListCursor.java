/*
TreeListCursor.java
 *
 *    Copyright (c) 2005 by Matti J. Katila 
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
 * Written by Matti J. Katila 
 */
package org.fenfire.view;
import org.fenfire.Cursor;

/** The root of tree list spatial view and zoom of it.
 */
public class TreeListCursor {

    private final Object rootNode;
    private final float zoom;
    public int textMark = -1;

    public TreeListCursor(Object root, float zoom) { 
	this.rootNode = root;
	this.zoom = zoom;
    }

    public Object getRoot() { return rootNode; }
    public float getZoom() { return zoom; }

    public int hashCode() {
	return (int)(rootNode.hashCode() + 243855*zoom);
    }
	
    public boolean equals(Object o) {
	if (!(o instanceof TreeListCursor)) return false;
	TreeListCursor c = (TreeListCursor)o;
	return zoom == c.zoom &&
	    rootNode.equals(c.rootNode);
    }
}
