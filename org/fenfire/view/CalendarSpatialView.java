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

public class CalendarSpatialView implements ViewSettings.SpatialView {
    static private void p(String s) { System.out.println("CalendarSpatialView:: "+s); }

    private Graph graph;
    private WindowAnimation winAnim;
    private int ndays;

    private Map cache = new org.nongnu.navidoc.util.WeakValueMap();

    public CalendarSpatialView(Graph graph, 
			       WindowAnimation winAnim, int days) {
	this.graph = graph;
	this.winAnim = winAnim;
	
	if (days <0) throw new Error("Please give more days! '"+days+"'");
	ndays = days;
    }

    public Set getTypes() {
	return Collections.singleton(new ViewSettings.Type() {
		public boolean contains(Cursor cursor) {
		    Object node = cursor.getNode();
		    Iterator i = graph.findN_11X_Iter(node, DC.date);
		    //p("i: "+i.hasNext());
		    return i.hasNext();
		}
	    });
    }

    public boolean showBig() {
	return true;
    }

    public Cursor createViewSpecificCursor(Cursor c) {
	return makeCalendarCursor(c);
    }


    protected Date getDate(Object node) {
	Iterator i = graph.findN_11X_Iter(node, DC.date);
	if (!i.hasNext()) return null;
	
	Literal date = (Literal) i.next();
	return org.nongnu.storm.util.DateParser.parse(date.getString());
    }	

    protected CalendarCursor makeCalendarCursor(Cursor c) {
	if (c instanceof CalendarCursor) {
	    return (CalendarCursor)c;
	} else {
	    Object node = c.getNode();
	    return new CalendarCursor(node, getDate(node), ndays, 1);
	}
    }


    private Model getModel(Object node, Object prop) {
	Model m = new ObjectModel(node);
	m = new PropValueModel(new ObjectModel(graph), m, prop, 1);
	m = new LiteralStringModel(m);
	m = Models.parseFloat(m);
	return m;
    }

    public Lob getLob(Model cmodel) {
	CalendarCursor c = makeCalendarCursor((Cursor)cmodel.get());
	Object node = c.getNode();
	Object canvas = graph.find1_X11(CANVAS2D.contains, node);

	Lob canvasContent = (Lob)cache.get(canvas);
	
	if(canvasContent == null) {
	    Tray tray = new Tray(false);

	    Model cs = Parameter.model("cs", new IntModel());

	    
	    Lob nl = null;

	    Box hbox = new Box(Lob.X);
	    for (int i=0; i<c.getShownDates().length; i++) {
		Box v = new Box(Lob.Y);
		Lob l = new Label(c.getShownDates()[i].getString(
				      ).substring(0, 10));
		l = new ThemeFrame(l);
		l = new AlignLob(l, .5f,.5f,.5f,.5f);
		v.add(l);
		//p(c.getShownDates()[i].getString());
		for (Iterator j=graph.findN_X11_Iter(DC.date, 
						     c.getShownDates()[i]); 
		     j.hasNext();) {
		    Object n = j.next();
		    // add click controller to select new focus...
		    Lob l3 = new Label((String)n);
		    l3 = new AlignLob(l3, .5f,.5f,.5f,.5f);
		    l3 = new BuoyConnectorLob(l3, n, cs);
		    v.add(l3);
		}
		hbox.add(v);
	    }
	    Lob l2 = new ThemeFrame(hbox);
	    l2 = new AlignLob(l2, .5f,.5f,.5f,.5f);
	    tray.add(l2);

	    /*
	    for(Iterator i=graph.findN_11X_Iter(canvas, CANVAS2D.contains); 
		i.hasNext();) {

		Object n = i.next();
		String s = Nodes.toString(n);
		Lob l = new Label(s.substring(s.length()-5));
		if(n.equals(node)) nl = l;
		l = new BuoyConnectorLob(l, n, cs);

		Model x = getModel(n, CANVAS2D.x), y = getModel(n, CANVAS2D.y);
		l = new TranslationLob(l, x, y);
		tray.add(l);
	    }
	    */
	    float s = 100;
	    canvasContent = 
		new RequestChangeLob(tray, s,s, s,s, s,s);

	    cache.put(node, canvasContent);
	}

	Lob l = canvasContent;
	l = getCoordinateLob(l, cmodel);
	l = new SpatialContextLob(l, (Model)l.getTemplateParameter("cs"));
	return l;
    }


    protected class Adapter extends AbstractModel.AbstractFloatModel {
	protected Model cursor;
	protected int type;

	protected Object myNode;

	protected float cache;
	protected boolean current;
	
	public Adapter(Model cursor, int type) {
	    this.cursor = cursor; this.type = type;
	    myNode = ((Cursor)cursor.get()).getNode();
	    cursor.addObs(this);
	}

	public void chg() {
	    Cursor c = (Cursor)cursor.get();
	    if(myNode.equals(c.getNode())) {
		current = false;
		super.chg();
	    }
	}
	
	public float getFloat() {
	    if(!current) {
		CalendarCursor c = makeCalendarCursor((Cursor)cursor.get());
		if(c == null) cache = c.getZoom();
		current = true;
	    }
	    return cache;
	}

	public void setFloat(float value) {
	    if(current && value == cache) return;
	    
	    Cursor c0 = (Cursor)cursor.get();
	    if(!myNode.equals(c0.getNode())) return;
	    CalendarCursor c = makeCalendarCursor(c0);

	    if(c == null) return;
	    

	    Object node = c.getNode();
	    float zoom = value;
	    cursor.set(new CalendarCursor(node, getDate(node),
					  ndays, zoom));
	}
    }


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


    private class UniqueColorModel extends AbstractModel.AbstractObjectModel {
	Model key;

	UniqueColorModel(Model key) {
	    this.key = key;
	    key.addObs(this);
	}
	
	protected Replaceable[] getParams() {
	    return new Replaceable[] { key };
	}
	protected Object clone(Object[] params) {
	    return new UniqueColorModel((Model)params[0]);
	}

	Color color;

	public void chg() {
	    color = null;
	    super.chg();
	}

	public Object get() {
	    if(color == null) {
		java.util.Random r = new Random(key.get().hashCode());
		float 
		    R = 1 - r.nextFloat() * 0.2f,
		    G = 1 - r.nextFloat() * 0.2f,
		    B = 1 - r.nextFloat() * 0.2f;
		color = new Color(R,G,B); 
	    }
	    return color;
	}
    }
}
