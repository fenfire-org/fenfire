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

public class CanvasSpatialView implements SpatialViewSettings.SpatialView {
    static private void p(String s) { System.out.println("CanvasSpatialView:: "+s); }

    private Graph graph;
    private ContentViewSettings contentViewSettings;
    private WindowAnimation winAnim;

    private Map canvasCache = new org.nongnu.navidoc.util.WeakValueMap();
    private Map buoyCache = new org.nongnu.navidoc.util.WeakValueMap();

    public CanvasSpatialView(Graph graph, 
			     ContentViewSettings contentViewSettings,
			     WindowAnimation winAnim) {
	this.graph = graph;
	this.contentViewSettings = contentViewSettings;
	this.winAnim = winAnim;
    }



    public final ViewSettings.Type TYPE = // XXX should be static
	new ViewSettings.AbstractType() {
	    public boolean containsNode(Object node) {
		return graph.find1_X11(CANVAS2D.contains, node) != null;
	    }
	};
    
    public Set getTypes() {
	return Collections.singleton(TYPE);
    }

    public boolean showBig() {
	return true;
    }

    protected CanvasCursor getCanvasCursor(Cursor c) {
	Object pos = c.getSpatialCursor();

	if (pos instanceof CanvasCursor) {
	    return (CanvasCursor)pos;
	} else {
	    Object n = c.getNode();
	    return makeCanvasCursor(n);
	}
    }

    protected CanvasCursor makeCanvasCursor(Object n) {
	Lob l = getContentLob(n);

	Object canvas = graph.find1_X11(CANVAS2D.contains, n);
	
	Model xm = getModel(n, CANVAS2D.x), ym = getModel(n, CANVAS2D.y);
	
	float x = xm.getFloat() + l.getNatSize(Lob.X)/2;
	float y = ym.getFloat() + l.getNatSize(Lob.Y)/2;
	
	return new CanvasCursor(canvas, x, y, 1);
    }


    private Model getModel(Object node, Object prop) {
	Model m = new ObjectModel(node);
	m = new PropValueModel(new ObjectModel(graph), m, prop, 1);
	m = new LiteralStringModel(m);
	m = Models.parseFloat(m);
	return m;
    }

    /**
     *  Cache of the mainview lob during dragging.
     *  The problem was that we can't re-create the mainview during
     *  dragging / when starting to drag, because then 
     *  the DragControllers are re-created and the new controllers' 
     *  isDragging is set to 'false'... :-o
     *
     *  So I hacked it so that the drag listeners set the mainview cache.
     *  It's ugly.
     */
    Lob mainviewCache;
    
    public Lob getMainviewLob(Cursor cursor) {
	if(mainviewCache != null) {
	    Lob result = mainviewCache;
	    mainviewCache = null;
	    return result;
	}

	Object node = cursor.getNode();
	Object canvas = getCanvasCursor(cursor).getCanvas();

	Lob canvasContent = getCanvasContent(canvas, cursor);

	final Model panX = new Adapter(cursor, 0);
	final Model panY = new Adapter(cursor, 1);
	final Model zoom = new Adapter(cursor, 2);

	final MonoLob result = new SpatialContextLob(NullLob.instance, (Model)canvasContent.getTemplateParameter("cs"));

	final PanZoomLob pzl = new PanZoomLob(canvasContent, panX.getFloat(), panY.getFloat(), zoom.getFloat());

	Lob l = new DragController(pzl, 3, new org.nongnu.libvob.mouse.RelativeAdapter() {
		public void startDrag(int x, int y) {
		    super.startDrag(x, y);
		    mainviewCache = result;
		}

		public void changedRelative(float dx, float dy) {
		    float nx = panX.getFloat();
		    float ny = panY.getFloat();
		    float nz = zoom.getFloat() + dy/100;

		    zoom.setFloat(nz);

		    pzl.setParams(nx, ny, nz);
		    mainviewCache = result;
		    winAnim.rerender();
		}
	    });
	l = new DragController(l, 1, new org.nongnu.libvob.mouse.RelativeAdapter() {
		public void startDrag(int x, int y) {
		    super.startDrag(x, y);
		    mainviewCache = result;
		}

		public void changedRelative(float dx, float dy) {
		    float nx = panX.getFloat() - dx/zoom.getFloat();
		    float ny = panY.getFloat() - dy/zoom.getFloat();
		    float nz = zoom.getFloat();

		    panX.setFloat(nx);
		    panY.setFloat(ny);

		    pzl.setParams(nx, ny, nz);
		    mainviewCache = result;
		    winAnim.rerender();
		}
	    }); 

	l = addBackground(l, canvas);
	l = new DepthChangeLob(l, 5); // XXX how to get the background rendered
                                      // at the right time before fading it?!?
                                      // should we change the fader? hmmm...

	result.setContent(l);
	return result;
    }

    public Lob getBuoyLob(Object node) {
	if(buoyCache.get(node) != null) return (Lob)buoyCache.get(node);

	Object canvas = graph.find1_X11(CANVAS2D.contains, node);
	Lob canvasContent = (Lob)canvasCache.get(canvas);
	if(canvasContent == null) {
	    canvasContent = getCanvasContent(canvas, null);
	    canvasCache.put(canvas, canvasContent);
	}

	Lob ct = getContentLob(node);
	Model x = getModel(node, CANVAS2D.x), y = getModel(node, CANVAS2D.y);
	x = x.plus(ct.getNatSize(Lob.X) / 2);
	y = y.plus(ct.getNatSize(Lob.Y) / 2);
	
	Lob l = new PanZoomLob(canvasContent, x, y, new FloatModel(1));
	l = addBackground(l, canvas);
	l = new SpatialContextLob(l, (Model)l.getTemplateParameter("cs"));
	
	buoyCache.put(node, l);
	return l;
    }

    protected Lob getContentLob(Object node) {
	return contentViewSettings.getLob(node);
    }

    /**
     *  'cursor' may be null.
     */
    protected Lob getCanvasContent(final Object canvas, final Cursor cursor) {
	
	Tray tray = new Tray(false);
	
	Model cs = Parameter.model("cs", new IntModel());
	
	for(Iterator i=graph.findN_11X_Iter(canvas, CANVAS2D.contains); 
	    i.hasNext();) {
	    
	    final Object n = i.next();
	    final Lob label = getContentLob(n);
	    Lob l = new BuoyConnectorLob(label, n, cs);

	    final Model x = getModel(n, CANVAS2D.x);
	    final Model y = getModel(n, CANVAS2D.y);

	    if(cursor != null)
		l = new ClickController(l, 1, new AbstractAction() {
			public void run() {
			    CanvasCursor cc = getCanvasCursor(cursor);

			    float nx = x.getFloat()+label.getNatSize(Lob.X)/2;
			    float ny = y.getFloat()+label.getNatSize(Lob.Y)/2;
			    float nz = cc.getZoom();

			    CanvasCursor c = new CanvasCursor(cc.getCanvas(),
							      nx, ny, nz);

			    cursor.set(n, c);

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
	Model bgcolor = new UniqueColorModel(new ObjectModel(canvas),.75f,1f);
	return new Frame(content, bgcolor, new ObjectModel(Color.black),
			 2, 0, false, false, true);
    }


    protected class Adapter extends AbstractModel.AbstractFloatModel {
	protected Cursor cursor;

	protected int type;

	protected float cache;
	protected boolean current;
	
	public Adapter(Cursor cursor, int type) {
	    this.cursor = cursor;
	    this.type = type;
	    cursor.spatialCursor.addObs(this);
	}

	public void chg() {
	    current = false;
	    super.chg();
	}

	private CanvasCursor getCanvasCursor() {
	    Object pos = cursor.getSpatialCursor();
	    if(pos instanceof CanvasCursor)
		return (CanvasCursor)pos;

	    return makeCanvasCursor(cursor.getNode());
	}
	
	public float getFloat() {
	    if(!current) {
		CanvasCursor c = getCanvasCursor();

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
	    
	    CanvasCursor c = getCanvasCursor();
	    
	    Object canvas = c.getCanvas();
	    float panX = c.getPanX(), panY = c.getPanY(), zoom = c.getZoom();
	    if(type == 0) panX = value;
	    else if(type == 1) panY = value;
	    else if(type == 2) zoom = value;
	    else throw new IllegalArgumentException("adapter type "+type);

	    cursor.setSpatialCursor(new CanvasCursor(canvas, panX, panY, zoom));
	}
    }
}
