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

    private Map cache = new org.nongnu.navidoc.util.WeakValueMap();

    public CanvasSpatialView(Graph graph) {
	this.graph = graph;
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
	return new Cursor.CanvasCursor(c.getNode(), null);
    }


    private Model getModel(Object node, Object prop) {
	Model m = new ObjectModel(node);
	m = new PropValueModel(new ObjectModel(graph), m, prop, 1);
	m = new LiteralStringModel(m);
	m = Models.parseFloat(m);
	return m;
    }

    public Lob getLob(Cursor c) {
	Object node = c.getNode();
	if(cache.get(node) != null) return (Lob)cache.get(node);

	Object canvas = graph.find1_X11(CANVAS2D.contains, node);

	Tray tray = new Tray(false);

	Model cs = new IntModel();

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

	Model x = getModel(node, CANVAS2D.x), y = getModel(node, CANVAS2D.y);
	Model middle = new FloatModel(.5f);

	Lob l = tray;
	/*
	l = new ViewportLob(Lob.X, l, x.plus(nl.getNatSize(Lob.X)/2f), middle);
	l = new ViewportLob(Lob.Y, l, y.plus(nl.getNatSize(Lob.Y)/2f), middle);
	l = new ThemeFrame(l);
	*/
	l = new RequestChangeLob(l, 100, 100, 100, 100, 100, 100);
	l = getCoordinateLob(l, c);
	l = new SpatialContextLob(l, cs);
	cache.put(node, l);
	return l;
    }



    protected Map coordlobs = new org.nongnu.navidoc.util.WeakValueMap();
    public Lob getCoordinateLob(Lob content, Cursor c) {
	Lob l = (Lob) coordlobs.get(c);
	if (l == null) {
	    if (!(c instanceof Cursor.CanvasCursor)) {
		Object canvas = graph.findN_X11_Iter(CANVAS2D.contains, c.getNode()).next();
		c = new Cursor.CanvasCursor(canvas, c.getNode());
	    }
	    Cursor.CanvasCursor ca = (Cursor.CanvasCursor)c;
	    
	    final Model panX = ca.getPanX();
	    final Model panY = ca.getPanY();
	    final Model zoom = ca.getZoom();

	    l = content;
	    l = new PanZoomLob(l, panX, panY, zoom);
	    l = new DragController(l, 3, new org.nongnu.libvob.mouse.RelativeAdapter() {
		    public void changedRelative(float dx, float dy) {
			zoom.setFloat(zoom.getFloat() + dy/100);
			rerender();
		    }
		});
	    l = new DragController(l, 1, new org.nongnu.libvob.mouse.RelativeAdapter() {
		    public void changedRelative(float dx, float dy) {
			panX.setFloat(panX.getFloat() - dx/zoom.getFloat());
			panY.setFloat(panY.getFloat() - dy/zoom.getFloat());
			rerender();
		    }
		}); 

	    Model bgcolor = new UniqueColorModel(new ObjectModel(c.getNode()));
	    l = new Frame(l, bgcolor, new ObjectModel(Color.black),
			  2, 0, false, false, true);

	    coordlobs.put(c, l);
	}
	return l;
    }

    WindowAnimation winAnim = null;
    private void rerender() {
	if (winAnim != null) {
	    winAnim.switchVS();
	}
    }
    

    private class UniqueColorModel extends AbstractModel.AbstractObjectModel {
	Model key;

	UniqueColorModel(Model key) {
	    this.key = key;
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
