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
import java.text.DateFormat;
import java.util.*;

public class CalendarSpatialView 
    implements SpatialViewSettings.SpatialView, Obs {
    static private void p(String s) { System.out.println("CalendarSpatialView:: "+s); }

    private Graph graph;
    private ContentViewSettings contentViewSettings;
    private WindowAnimation winAnim;
    private int ndays;

    private DateFormat dateFormat;

    private Map nodesByDay = new HashMap();
    private boolean isCurrent;

    private Map cache = new org.nongnu.navidoc.util.WeakValueMap();

    public CalendarSpatialView(Graph graph, 
			       ContentViewSettings contentViewSettings,
			       WindowAnimation winAnim, int days) {
	this.graph = graph;
	this.contentViewSettings = contentViewSettings;
	this.winAnim = winAnim;

	dateFormat = DateFormat.getDateInstance();

	isCurrent = false;
	
	if (days < 0) throw new IllegalArgumentException("0 > days = "+days);
	ndays = days;
    }

    public final ViewSettings.Type TYPE = // XXX should be static
	new ViewSettings.AbstractType() {
	    public boolean containsNode(Object node) {
		Iterator i = graph.findN_11X_Iter(node, DC.date);
		//p("i: "+i.hasNext());
		return i.hasNext();
	    }
	};
    
    public Set getTypes() {
	return Collections.singleton(TYPE);
    }

    public boolean showBig() {
	return true;
    }


    public void chg() {
	isCurrent = false;
    }

    protected void updateNodes() {
	if(isCurrent) return;

	nodesByDay.clear();

	for(Iterator i=graph.findN_X1A_Iter(DC.date, this); i.hasNext();) {
	    Object n = i.next();
	    
	    for(Iterator j=graph.findN_11X_Iter(n, DC.date, this); 
		j.hasNext();) {

		Object o = j.next();
		if(!(o instanceof Literal)) continue;
		Literal l = (Literal)o;
		String s = l.getString();

		Calendar day;

		try {
		    day = parseDay(s);
		} catch(NumberFormatException e) {
		    p("Malformed date: "+s);
		    continue;
		}

		Set set = (Set)nodesByDay.get(day);
		if(set == null)
		    nodesByDay.put(day, set = new HashSet());

		set.add(n);
	    }
	}

	isCurrent = true;
    }

    protected Calendar parseDay(String s) throws NumberFormatException {
	Date d = org.nongnu.storm.util.DateParser.parse(s);

	Calendar cal = new GregorianCalendar();
	cal.setTime(d);
	
	int year = cal.get(cal.YEAR);
	int month = cal.get(cal.MONTH);
	int day = cal.get(cal.DAY_OF_MONTH);

	return new GregorianCalendar(year, month, day);
    }


    protected Calendar getDay(Object node) {
	Iterator i = graph.findN_11X_Iter(node, DC.date);
	if (!i.hasNext()) return null;
	
	Literal date = (Literal) i.next();
	return parseDay(date.getString());
    }	


    protected CalendarCursor getCalendarCursor(Cursor c) {
	Object pos = c.getSpatialCursor();
	if (pos instanceof CalendarCursor) {
	    return (CalendarCursor)pos;
	} else {
	    return makeCalendarCursor(c.getNode());
	}
    }

    protected CalendarCursor makeCalendarCursor(Object node) {
	return new CalendarCursor(getDay(node), 1);
    }


    private Map mainviewCache = new org.nongnu.navidoc.util.WeakValueMap();
    private Map buoyCache = new org.nongnu.navidoc.util.WeakValueMap();

    public Lob getBuoyLob(Object node) {
	if(buoyCache.get(node) != null) 
	    return (Lob)buoyCache.get(node);

	Lob l = getCalendarContent(makeCalendarCursor(node), null);
	l = new AlignLob(l, .5f, .5f, .5f, .5f);
	l = new ThemeFrame(l);
	l = new SpatialContextLob(l, (Model)l.getTemplateParameter("cs"));

	float s = 100;
	l = new RequestChangeLob(l, s,s, s,s, s,s);

	buoyCache.put(node, l);
	return l;
    }

    public Lob getMainviewLob(Cursor cursor) {
	if(mainviewCache.get(cursor.getSpatialCursor()) != null) 
	    return (Lob)mainviewCache.get(cursor.getSpatialCursor());

	Lob l = getCalendarContent(getCalendarCursor(cursor), cursor);
	l = new AlignLob(l, .5f, .5f, .5f, .5f);
	l = new DepthChangeLob(l, -8);
	l = new ThemeFrame(l);
	l = new DepthChangeLob(l, 8);
	l = new SpatialContextLob(l, (Model)l.getTemplateParameter("cs"));

	l = new Margin(l, 200, 0);
	l = new Between(new KeyLob(NullLob.instance, "buoy circle"), l,
			NullLob.instance);

	l = new AlignLob(l, .5f, .5f, .5f, .5f);

	mainviewCache.put(cursor.getSpatialCursor(), l);
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

    protected Lob getCalendarContent(final CalendarCursor cc, 
				     final Cursor cursor) {
	updateNodes();
	
	Model cs = Parameter.model("cs", new IntModel());

	Box dateList = new Box(Lob.Y);

	for (int i=-ndays/2; i<=ndays/2; i++) {
	    final GregorianCalendar day = new GregorianCalendar();
	    day.setTimeInMillis(cc.getDay().getTimeInMillis());
	    day.add(day.DAY_OF_MONTH, i);

	    final Date time = day.getTime();

	    Lob l = new Label(dateFormat.format(time));
	    l = new AlignLob(l, .5f,.5f,.5f,.5f);
	    l = new ThemeFrame(l);

	    // argl
	    l = new BuoyConnectorLob(l, Nodes.get("day:"+org.nongnu.storm.util.DateParser.getIsoDate(time)), cs);

	    if(cursor != null) {
		l = new ClickController(l, 1, new Model.Change(cursor.spatialCursor, new ObjectModel(cc.getCursor(day))));
	    }
	    dateList.add(l);

	    Set nodes = (Set)nodesByDay.get(day);
	    Box v = new Box(Lob.Y);
	    if(nodes != null) {
		v.glue(25, 25, 25);
		for (Iterator j=nodes.iterator(); j.hasNext();) {
		    Object n = j.next();
		    Lob l3 = contentViewSettings.getLob(n);
		    l3 = new RequestChangeLob(Lob.X, l3,
					      Float.NaN, Float.NaN, 160);
		    // XXX add click controller to select new focus...
		    l3 = new ThemeFrame(l3);
		    l3 = new BuoyConnectorLob(l3, n, cs);
		    l3 = new DepthChangeLob(l3, 2);
		    l3 = new AlignLob(l3, .5f,.5f,.5f,.5f);
		    v.add(l3);
		    v.glue(25, 25, 25);
		}
	    }

	    //dateList.add(new Margin(v, 60, 0));
	    dateList.add(v);
	    //dateList.glue(5, 5, 5);
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
