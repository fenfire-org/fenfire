/*
CanvasSpatialView.java
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

public class CanvasSpatialView implements ViewSettings.SpatialView {
    static private void p(String s) { System.out.println("CanvasSpatialView:: "+s); }

    private Graph graph;
    private WindowAnimation winAnim;

    private Map cache = new org.nongnu.navidoc.util.WeakValueMap();

    public CanvasSpatialView(Graph graph, WindowAnimation winAnim) {
	this.graph = graph;
	this.winAnim = winAnim;
    }

    public Set getTypes() {
	return Collections.singleton(new ViewSettings.Type() {
		public boolean contains(Cursor cursor) {
		    Object node = cursor.getNode();
		    return graph.find1_X11(CANVAS2D.contains, node) != null;
		}
	    });
    }

    public boolean showBig() {
	return true;
    }

    public Cursor createViewSpecificCursor(Cursor c) {
	return makeCanvasCursor(c);
    }

    protected CanvasCursor makeCanvasCursor(Cursor c) {
	if (c instanceof CanvasCursor) {
	    return (CanvasCursor)c;
	} else {
	    Object n = c.getNode();
	    Iterator canvases = graph.findN_X11_Iter(CANVAS2D.contains, n);
	    
	    if(!canvases.hasNext()) return null;

	    Object canvas = canvases.next();

	    String s = Nodes.toString(n);
	    Lob l = new Label(s.substring(s.length()-5));

	    Model xm = getModel(n, CANVAS2D.x), ym = getModel(n, CANVAS2D.y);

	    float x = xm.getFloat() + l.getNatSize(Lob.X)/2;
	    float y = ym.getFloat() + l.getNatSize(Lob.Y)/2;
	    
	    return new CanvasCursor(canvas, c.getNode(), x, y, 1);
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
	CanvasCursor c = makeCanvasCursor((Cursor)cmodel.get());
	Object node = c.getNode();
	Object canvas = graph.find1_X11(CANVAS2D.contains, node);

	Lob canvasContent = (Lob)cache.get(canvas);
	
	if(canvasContent == null) {
	    Tray tray = new Tray(false);

	    Model cs = Parameter.model("cs", new IntModel());

	    Lob nl = null;

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

	    canvasContent = 
		new RequestChangeLob(tray, 100, 100, 100, 100, 100, 100);

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
		CanvasCursor c = makeCanvasCursor((Cursor)cursor.get());
		if(c == null) cache = (type==2) ? 1 : 0;
		else if(type == 0) cache = c.getPanX();
		else if(type == 1) cache = c.getPanY();
		else if(type == 2) cache = c.getZoom();
		else throw new IllegalArgumentException("adapter type "+type);

		current = true;
	    }
	    return cache;
	}

	public void setFloat(float value) {
	    if(current && value == cache) return;
	    
	    Cursor c0 = (Cursor)cursor.get();
	    if(!myNode.equals(c0.getNode())) return;
	    CanvasCursor c = makeCanvasCursor(c0);

	    if(c == null) return;
	    
	    float panX = c.getPanX(), panY = c.getPanY(), zoom = c.getZoom();
	    if(type == 0) panX = value;
	    else if(type == 1) panY = value;
	    else if(type == 2) zoom = value;
	    else throw new IllegalArgumentException("adapter type "+type);

	    cursor.set(new CanvasCursor(c.getCanvas(), c.getNode(),
					panX, panY, zoom));
	}
    }


    protected Map coordlobs = new org.nongnu.navidoc.util.WeakValueMap();
    public Lob getCoordinateLob(Lob content, Model cmodel) {
	Cursor c = (Cursor)cmodel.get();
	Lob l = (Lob) coordlobs.get(c);
	if (l == null) {
	    CanvasCursor ca = makeCanvasCursor(c);

	    final Model panX = new Adapter(cmodel, 0);
	    final Model panY = new Adapter(cmodel, 1);
	    final Model zoom = new Adapter(cmodel, 2);

	    l = content;
	    l = new PanZoomLob(l, panX, panY, zoom);
	    l = new DragController(l, 3, new org.nongnu.libvob.mouse.RelativeAdapter() {
		    public void changedRelative(float dx, float dy) {
			zoom.setFloat(zoom.getFloat() + dy/100);
			winAnim.rerender();
		    }
		});
	    l = new DragController(l, 1, new org.nongnu.libvob.mouse.RelativeAdapter() {
		    public void changedRelative(float dx, float dy) {
			panX.setFloat(panX.getFloat() - dx/zoom.getFloat());
			panY.setFloat(panY.getFloat() - dy/zoom.getFloat());
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
