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
import java.util.*;

public class CanvasSpatialView implements ViewSettings.SpatialView {

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

    private int spatialContextCS;

    private class FooLob extends AbstractMonoLob {
	private Object key;

	public FooLob(Lob content, Object key) {
	    super(content);
	    this.key = key;
	}

	public Object clone(Object[] params) {
	    return new FooLob((Lob)params[0], key);
	}

	public void render(VobScene scene, int into, int matchingParent,
			   float w, float h, float d,
			   boolean visible) {
	    int cs = scene.coords.box(into, w, h);
	    ConnectionVobMatcher m = (ConnectionVobMatcher)scene.matcher;
	    m.add(matchingParent, cs, key);
	    m.link(spatialContextCS, 1, cs, "structure point");
	    m.getLink(spatialContextCS, 1, key, "structure point");
	    super.render(scene, cs, cs, w, h, d, visible);
	}
    }

    private class BarLob extends AbstractMonoLob {
	public BarLob(Lob content) {
	    super(content);
	}

	public Object clone(Object[] params) {
	    return new BarLob((Lob)params[0]);
	}

	public void render(VobScene scene, int into, int matchingParent,
			   float w, float h, float d,
			   boolean visible) {
	    spatialContextCS = into;
	    scene.matcher.add(matchingParent, into, "spatial context");
	    super.render(scene, into, matchingParent, w, h, d, visible);
	    
	}
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

	for(Iterator i=graph.findN_11X_Iter(canvas, CANVAS2D.contains); 
	    i.hasNext();) {

	    Object n = i.next();
	    String s = Nodes.toString(n);
	    Lob l = new Label(s.substring(s.length()-5));
	    l = new FooLob(l, n);

	    Model x = getModel(n, CANVAS2D.x), y = getModel(n, CANVAS2D.y);
	    l = new TranslationLob(l, x, y);
	    tray.add(l);
	}

	Model x = getModel(node, CANVAS2D.x), y = getModel(node, CANVAS2D.y);
	Model zero = new FloatModel(0);
	Model offs = new FloatModel(35);

	Lob l = new TranslationLob(tray, zero.minus(x).plus(offs), 
				         zero.minus(y).plus(offs));
	l = new ThemeFrame(l);
	l = new RequestChangeLob(l, 100, 100, 100, 100, 100, 100);
	l = new BarLob(l);
	cache.put(node, l);
	return l;
    }
}
