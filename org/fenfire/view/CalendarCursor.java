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
import java.util.Date;

/** Cursor for Calendar spatial view.
 *  Used as a value for org.fenfire.Cursor.SpatialCursor.spatialPosition.
 */
public class CalendarCursor implements Cursor.SpatialPosition {
    private final TypedLiteral [] shownDays;

    // may be accursed if any..
    private final float zoom;
    private final long DAY = 24 * 60 * 60 * 1000;
    public CalendarCursor(Date curr, int nDays, float zoom) { 
	this.zoom = zoom;

	System.out.println("ZOOM: "+zoom);

	int half = nDays / 2;
	shownDays = new TypedLiteral[nDays];
	for (int i=0; i<nDays; i++) {
	    Date d = new Date(curr.getTime());
	    d = new Date(curr.getTime() - half*DAY + i*DAY);
	    shownDays[i] = new TypedLiteral(
		org.nongnu.storm.util.DateParser.getIsoDate(d),
		XSD.date);
	    //System.out.println("date: "+shownDays[i]);
	}
    }

    public TypedLiteral[] getShownDates() {
	return shownDays;
    }


    public float getZoom() { return zoom; }

    public int hashCode() {
	int hash = (int)(942*zoom);
	for(int i=0; i<shownDays.length; i++) {
	    hash += shownDays[i].hashCode();
	    hash *= 234809;
	}
	return hash;
    }
	
    public boolean equals(Object o) {
	if (!(o instanceof CalendarCursor)) return false;
	CalendarCursor c = (CalendarCursor)o;
	if(shownDays.length != c.shownDays.length) return false;
	for (int i=0; i<shownDays.length; i++)
	    if (!shownDays[i].equals(c.shownDays[i]))
		return false;
	return zoom == c.zoom;
    }
}