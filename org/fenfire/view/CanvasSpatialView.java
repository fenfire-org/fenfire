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

    private Map canvasCache = new org.nongnu.navidoc.util.WeakValueMap();
    private Map buoyCache = new org.nongnu.navidoc.util.WeakValueMap();

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

    Lob lastMainview;
    Object lastMainviewNode;

    private boolean equals(Object o1, Object o2) {
	if(o1 == o2) return true;
	if(o1 == null || o2 == null) return false;
	return o1.equals(o2);
    }
    
    public Lob getMainviewLob(Model cursor) {
	CanvasCursor c = makeCanvasCursor((Cursor)cursor.get());
	Object node = c.getNode();

	// argl, need this so that no new DragControllers are created
	// during dragging -- they don't have isDragging set, so drag events
	// aren't handled... :-(
	if(equals(node, lastMainviewNode)) return lastMainview; 

	Object canvas = graph.find1_X11(CANVAS2D.contains, node);

	Lob canvasContent = getCanvasContent(canvas, cursor);

	final Model panX = new Adapter(cursor, 0);
	final Model panY = new Adapter(cursor, 1);
	final Model zoom = new Adapter(cursor, 2);

	Lob l = canvasContent;
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

	l = addBackground(l, canvas);
	l = new SpatialContextLob(l, (Model)l.getTemplateParameter("cs"));

	lastMainview = l;
	lastMainviewNode = node;

	return l;
    }

    public Lob getBuoyLob(Object node) {
	if(buoyCache.containsKey(node)) return (Lob)buoyCache.get(node);

	Object canvas = graph.find1_X11(CANVAS2D.contains, node);
	Lob canvasContent = (Lob)canvasCache.get(canvas);
	if(canvasContent == null) {
	    canvasContent = getCanvasContent(canvas, null);
	    canvasCache.put(canvas, canvasContent);
	}
	
	Lob l = new PanZoomLob(canvasContent, getModel(node, CANVAS2D.x),
			       getModel(node, CANVAS2D.y), new FloatModel(1));
	l = addBackground(l, canvas);
	l = new SpatialContextLob(l, (Model)l.getTemplateParameter("cs"));
	
	buoyCache.put(node, l);
	return l;
    }

    /**
     *  'cursor' may be null.
     */
    protected Lob getCanvasContent(final Object canvas, final Model cursor) {
	
	Tray tray = new Tray(false);
	
	Model cs = Parameter.model("cs", new IntModel());
	
	for(Iterator i=graph.findN_11X_Iter(canvas, CANVAS2D.contains); 
	    i.hasNext();) {
	    
	    final Object n = i.next();
	    String s = Nodes.toString(n);
	    final Lob label = new Label(s.substring(s.length()-5));
	    Lob l = new BuoyConnectorLob(label, n, cs);
	    
	    final Model x = getModel(n, CANVAS2D.x);
	    final Model y = getModel(n, CANVAS2D.y);

	    if(cursor != null)
		l = new ClickController(l, 1, new AbstractAction() {
			public void run() {
			    Cursor c0 = (Cursor)cursor.get();
			    CanvasCursor cc = makeCanvasCursor(c0);

			    float nx = x.getFloat()+label.getNatSize(Lob.X)/2;
			    float ny = y.getFloat()+label.getNatSize(Lob.Y)/2;
			    float nz = cc.getZoom();

			    Cursor c = new CanvasCursor(canvas, n, nx, ny, nz);
			    cursor.set(c);

			    VobScene sc = winAnim.getCurrentVS();
			    ConnectionVobMatcher m = 
				(ConnectionVobMatcher)sc.matcher;
			    
			    int focus = m.getFocus();
			    int context = m.getLink(focus, -1, "spatial context", "structure point");
			    m.setNextFocus(m.getLink(context, 1, n, "structure point"));
			}
		    });

	    l = new TranslationLob(l, x, y);
	    tray.add(l);
	}
	
	return new RequestChangeLob(tray, 100, 100, 100, 100, 100, 100);
    }

    protected Lob addBackground(Lob content, Object canvas) {
	Model bgcolor = new UniqueColorModel(new ObjectModel(canvas));
	return new Frame(content, bgcolor, new ObjectModel(Color.black),
			 2, 0, false, false, true);
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
