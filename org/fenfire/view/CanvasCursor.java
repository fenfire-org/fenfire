/*
CanvasCursor.java
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

/** Spatial position for canvas spatial view.
 *  Used as a value for org.fenfire.Cursor.SpatialCursor.spatialPosition.
 */
public class CanvasCursor {

    private final Object canvas;

    private final float panX;
    private final float panY;
    private final float zoom;

    public CanvasCursor(Object canvas, float panX, float panY, float zoom) { 
	this.canvas = canvas;
	this.panX = panX;
	this.panY = panY;
	this.zoom = zoom;
    }

    public Object getCanvas() { return canvas; }
    public float getPanX() { return panX; }
    public float getPanY() { return panY; }
    public float getZoom() { return zoom; }

    public int hashCode() {
	return (int)(canvas.hashCode() + 23480*panX + 21348*panY + 8942*zoom);
    }
	
    public boolean equals(Object o) {
	if (!(o instanceof CanvasCursor)) return false;
	CanvasCursor c = (CanvasCursor)o;
	return panX == c.panX && panY == c.panY && zoom == c.zoom &&
	    canvas.equals(c.canvas);
    }
}
