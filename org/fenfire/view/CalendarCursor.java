/*
CalendarCursor.java
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
import org.fenfire.vocab.XSD;
import org.fenfire.swamp.*;
import java.util.*;

/** Cursor for Calendar spatial view.
 *  Used as a value for org.fenfire.Cursor.SpatialCursor.spatialPosition.
 */
public class CalendarCursor {
    private final Calendar day;

    // may be accursed if any..
    private final float zoom;

    public CalendarCursor(Calendar day, float zoom) { 
	this.zoom = zoom;
	this.day = day;
    }

    public Calendar getDay() {
	return day;
    }


    /** get cursor with different day but same zoom
     */
    public CalendarCursor getCursor(Calendar day) {
	return new CalendarCursor(day, zoom);
    }


    public float getZoom() { return zoom; }

    public int hashCode() {
	return (int)(942*zoom) + day.hashCode();
    }
	
    public boolean equals(Object o) {
	if (!(o instanceof CalendarCursor)) return false;
	CalendarCursor c = (CalendarCursor)o;
	return zoom == c.zoom && day.equals(c.day);
    }
}
