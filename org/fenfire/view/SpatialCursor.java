/*
SpatialCursor.java
 *
 *    Copyright (c) 2005 by Matti J. Katila and Benja Fallenstein
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
 * Written by Matti J. Katila and Benja Fallenstein
 */
package org.fenfire.view;
import org.fenfire.Cursor;

/** Spatial position for spatial view, e.g., image view.
 *  Used as a value for org.fenfire.Cursor.SpatialCursor.spatialPosition.
 */
public class SpatialCursor {

    private final Object node;

    private final float panX;
    private final float panY;
    private final float zoom;

    public SpatialCursor(Object node, float panX, float panY, float zoom) { 
	this.node = node;
	this.panX = panX;
	this.panY = panY;
	this.zoom = zoom;
    }

    public Object getNode() { return node; }
    public float getPanX() { return panX; }
    public float getPanY() { return panY; }
    public float getZoom() { return zoom; }

    public int hashCode() {
	return (int)(node.hashCode() + 213480*panX + 213548*panY + 80942*zoom);
    }
	
    public boolean equals(Object o) {
	if (!(o instanceof SpatialCursor)) return false;
	SpatialCursor c = (SpatialCursor)o;
	return panX == c.panX && panY == c.panY && zoom == c.zoom &&
	    node.equals(c.node);
    }
}
