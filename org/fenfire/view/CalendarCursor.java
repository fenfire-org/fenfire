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
 */
public class CalendarCursor implements Cursor {
    private final TypedLiteral [] shownDays;

    // may be accursed if any..
    private final Object node; 
    private final float zoom;
    private final long DAY = 24 * 60 * 60 * 1000;
    public CalendarCursor(Object node, Date curr, 
			  int nDays, float zoom) { 
	this.node = node; 
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

    public Object getNode() { 
	return node; 
    }
    public TypedLiteral[] getShownDates() {
	return shownDays;
    }


    public float getZoom() { return zoom; }

    public int hashCode() {
	return (int)(132489*node.hashCode() +
		     942*zoom);
    }
	
    public boolean equals(Object o) {
	if (!(o instanceof CalendarCursor)) return false;
	CalendarCursor c = (CalendarCursor)o;
	for (int i=0; i<shownDays.length || i<c.shownDays.length; i++)
	    if (!shownDays[i].equals(c.shownDays[i]))
		return false;
	return equals(node, c.node) &&
	    zoom == c.zoom;
    }

    private boolean equals(Object o1, Object o2) {
	if(o1 == o2) return true;
	if(o1 == null || o2 == null) return false;
	return o1.equals(o2);
    }
}
