/*
CalendarSpatialView.java
 *    
 *    Copyright (c) 2005, Benja Fallenstein and Matti Katila
 *
 *    This file is part of Fenfire.
 *    
 *    Libvob is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Libvob is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Libvob; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *
 */
/*
 * Written by Benja Fallenstein and Matti Katila
 */
package org.fenfire.view;
import org.fenfire.Cursor;
import org.fenfire.swamp.*;
import org.fenfire.vocab.*;
import org.fenfire.lob.*;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.unit.*;
import org.nongnu.libvob.layout.component.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.util.*;
import org.nongnu.navidoc.util.Obs;
import java.awt.Color;
import java.util.*;

public class CalendarSpatialView implements SpatialViewSettings.SpatialView {
    static private void p(String s) { System.out.println("CalendarSpatialView:: "+s); }

    private Graph graph;
    private ContentViewSettings contentViewSettings;
    private WindowAnimation winAnim;
    private int ndays;

    private Map cache = new org.nongnu.navidoc.util.WeakValueMap();

    public CalendarSpatialView(Graph graph, 
			       ContentViewSettings contentViewSettings,
			       WindowAnimation winAnim, int days) {
	this.graph = graph;
	this.contentViewSettings = contentViewSettings;
	this.winAnim = winAnim;
	
	if (days < 0) throw new IllegalArgumentException("0 > days = "+days);
	ndays = days;
    }

    public Set getTypes() {
	return Collections.singleton(new ViewSettings.AbstractType() {
		public boolean containsNode(Object node) {
		    Iterator i = graph.findN_11X_Iter(node, DC.date);
		    //p("i: "+i.hasNext());
		    return i.hasNext();
		}
	    });
    }

    public boolean showBig() {
	return true;
    }


    protected Date getDate(Object node) {
	Iterator i = graph.findN_11X_Iter(node, DC.date);
	if (!i.hasNext()) return null;
	
	Literal date = (Literal) i.next();
	return org.nongnu.storm.util.DateParser.parse(date.getString());
    }	

    protected CalendarCursor getCalendarCursor(Cursor c) {
	Object pos = c.getSpatialCursor().getSpatialPosition();
	if (pos instanceof CalendarCursor) {
	    return (CalendarCursor)pos;
	} else {
	    return makeCalendarCursor(c.getNode());
	}
    }

    protected CalendarCursor makeCalendarCursor(Object node) {
	return new CalendarCursor(getDate(node), ndays, 1);
    }


    public Lob getBuoyLob(Object node) {
	Lob l = getCalendarContent(makeCalendarCursor(node));
	l = new AlignLob(l, .5f, .5f, .5f, .5f);
	l = new ThemeFrame(l);
	l = new SpatialContextLob(l, (Model)l.getTemplateParameter("cs"));

	float s = 100;
	l = new RequestChangeLob(l, s,s, s,s, s,s);

	return l;
    }

    public Lob getMainviewLob(Cursor cursor) {
	Lob l = getCalendarContent(getCalendarCursor(cursor));
	l = new AlignLob(l, .5f, .5f, .5f, .5f);
	l = new ThemeFrame(l);
	l = new SpatialContextLob(l, (Model)l.getTemplateParameter("cs"));
	return l;
    }

    /*
	CalendarCursor c = makeCalendarCursor((Cursor)cmodel.get());
	Object node = c.getNode();
	Object canvas = graph.find1_X11(CANVAS2D.contains, node);

	Lob canvasContent = (Lob)cache.get(canvas);

	Lob l = canvasContent;
	l = getCoordinateLob(l, cmodel);
	l = new SpatialContextLob(l, (Model)l.getTemplateParameter("cs"));
	return l;
    }
    */	

    protected Lob getCalendarContent(CalendarCursor cc) {
	Model cs = Parameter.model("cs", new IntModel());
	
	Box dateList = new Box(Lob.Y);
	for (int i=0; i<cc.getShownDates().length; i++) {
	    TypedLiteral date = cc.getShownDates()[i];

	    Box v = new Box(Lob.Y);
	    Lob l = new Label(date.getString().substring(0, 10));
	    l = new ThemeFrame(l, new ObjectModel(date));
	    l = new AlignLob(l, .5f,.5f,.5f,.5f);
	    v.add(l);
	    //p(cc.getShownDates()[i].getString());
	    for (Iterator j=graph.findN_X11_Iter(DC.date,date); j.hasNext();) {
		Object n = j.next();
		Lob l3 = contentViewSettings.getLob(n);
		// add click controller to select new focus...
		l3 = new BuoyConnectorLob(l3, n, cs);
		l3 = new AlignLob(l3, .5f,.5f,.5f,.5f);
		v.add(l3);
	    }
	    dateList.add(v);
	    dateList.glue(5, 5, 5);
	}

	return dateList;
    }


    /*
    protected Map coordlobs = new org.nongnu.navidoc.util.WeakValueMap();
    public Lob getCoordinateLob(Lob content, Model cmodel) {
	Cursor c = (Cursor)cmodel.get();
	Lob l = (Lob) coordlobs.get(c);
	if (l == null) {
	    CalendarCursor ca = makeCalendarCursor(c);

	    final Model panX = new FloatModel(0);
	    final Model panY = new FloatModel(0);
	    final Model zoom = new FloatModel(1);//new Adapter(cmodel, 0);

	    l = content;
	    l = new PanZoomLob(l, panX, panY, zoom);
	    l = new DragController(l, 3, new org.nongnu.libvob.mouse.RelativeAdapter() {
		    public void changedRelative(float dx, float dy) {
			zoom.setFloat(zoom.getFloat() + dy/100);
			winAnim.rerender();
		    }
		});
	    Model bgcolor = new UniqueColorModel(new ObjectModel(c.getNode()));
	    l = new Frame(l, bgcolor, new ObjectModel(Color.black),
			  2, 0, false, false, true);

	    coordlobs.put(c, l);
	}
	return l;
    }
    */
}
