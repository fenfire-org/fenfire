/*
ImageSpatialView.java
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

public class ImageSpatialView implements SpatialViewSettings.SpatialView {
    static private void p(String s) { System.out.println("ImageSpatialView:: "+s); }

    private Graph graph;
    private ContentViewSettings contentViewSettings;
    private WindowAnimation winAnim;

    private Map imageCache = new org.nongnu.navidoc.util.WeakValueMap();
    private Map buoyCache = new org.nongnu.navidoc.util.WeakValueMap();

    public ImageSpatialView(Graph graph, 
			     ContentViewSettings contentViewSettings,
			     WindowAnimation winAnim) {
	this.graph = graph;
	this.contentViewSettings = contentViewSettings;
	this.winAnim = winAnim;
    }



    public final ViewSettings.Type TYPE = // XXX should be static
	new ViewSettings.AbstractType() {
	    public boolean containsNode(Object node) {
		if (!(node instanceof TypedLiteral)) return false;
		TypedLiteral lit = (TypedLiteral) node;
		return lit.getType() == FF.AlphImg;
	    }
	};
    
    public Set getTypes() {
	return Collections.singleton(TYPE);
    }

    public boolean showBig() {
	return true;
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
	Lob imageContent = getImageContent(node);

	final Model panX = new Adapter(cursor, 0);
	final Model panY = new Adapter(cursor, 1);
	final Model zoom = new Adapter(cursor, 2);

	final Model theLob = new ObjectModel();
	
	final PanZoomLob pzl = new PanZoomLob(imageContent, panX.getFloat(), panY.getFloat(), zoom.getFloat());

	Lob l = new DragController(pzl, 3, new org.nongnu.libvob.mouse.RelativeAdapter() {
		public void startDrag(int x, int y) {
		    super.startDrag(x, y);
		    mainviewCache = (Lob)theLob.get();
		}

		public void changedRelative(float dx, float dy) {
		    float nx = panX.getFloat();
		    float ny = panY.getFloat();
		    float nz = zoom.getFloat() + dy/100;

		    zoom.setFloat(nz);

		    pzl.setParams(nx, ny, nz);
		    mainviewCache = (Lob)theLob.get();
		    winAnim.rerender();
		}
	    });
	l = new DragController(l, 1, new org.nongnu.libvob.mouse.RelativeAdapter() {
		public void startDrag(int x, int y) {
		    super.startDrag(x, y);
		    mainviewCache = (Lob)theLob.get();
		}

		public void changedRelative(float dx, float dy) {
		    float nx = panX.getFloat() - dx/zoom.getFloat();
		    float ny = panY.getFloat() - dy/zoom.getFloat();
		    float nz = zoom.getFloat();

		    panX.setFloat(nx);
		    panY.setFloat(ny);

		    pzl.setParams(nx, ny, nz);
		    mainviewCache = (Lob)theLob.get();
		    winAnim.rerender();
		}
	    }); 

	l = new SpatialContextLob(l, (Model)imageContent.getTemplateParameter("cs"));
	l = new Margin(l, 40);

	theLob.set(l);
	return l;
    }

    public Lob getBuoyLob(Object node) {
	if(buoyCache.get(node) != null) return (Lob)buoyCache.get(node);

	Lob imageContent = (Lob)imageCache.get(node);
	if(imageContent == null) {
	    imageContent = getImageContent(node);
	    imageCache.put(node, imageContent);
	}

	Lob ct = imageContent;
	Model x = new FloatModel(0), y = new FloatModel(0);
	x = x.plus(ct.getNatSize(Lob.X) / 2);
	y = y.plus(ct.getNatSize(Lob.Y) / 2);
	
	Lob l = new PanZoomLob(imageContent, x, y, new FloatModel(1));
	l = new SpatialContextLob(l, (Model)l.getTemplateParameter("cs"));
	
	buoyCache.put(node, l);
	return l;
    }

    /**
     *  'cursor' may not be null.
     */
    protected Lob getImageContent(final Object node) {

	try {
	    Lob l = new Image(new java.io.File("../libvob/testdata/libvob.png"));
	    
	    Model cs = Parameter.model("cs", new IntModel());
	    
	    l = new BuoyConnectorLob(l, node, cs);
	    
	    VobScene sc = winAnim.getCurrentVS();
	    ConnectionVobMatcher m = 
		(ConnectionVobMatcher)sc.matcher;
	    
	    int focus = m.getFocus();
	    int context = m.getLink(focus, -1, "spatial context", "structure point");
	    m.setNextFocus(m.getLink(context, 1, node, "structure point"));
	    
	    return new RequestChangeLob(l, 100, 100, 100, 100, 100, 100);
	} catch (Exception e) {
	    e.printStackTrace();
	    return new Label("Error found: "+e);
	}
    }



    protected class Adapter extends AbstractModel.AbstractFloatModel {
	protected Cursor cursor;
	protected int type;
	protected float cache;
	protected boolean current;
	
	public Adapter(Cursor cursor, int type) {
	    this.cursor = cursor;
	    this.type = type;

	    if (!(cursor.getSpatialCursor() instanceof SpatialCursor))
		cursor.setSpatialCursor(new SpatialCursor(cursor.getNode(), 0,0,1));

	    cursor.spatialCursor.addObs(this);
	}

	public void chg() {
	    current = false;
	    super.chg();
	}

	public float getFloat() {
	    if(!current) {
		//System.out.println("cursor: "+cursor.spatialCursor);
		SpatialCursor c = (SpatialCursor)cursor.getSpatialCursor();

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
	    
	    SpatialCursor c = (SpatialCursor)cursor.getSpatialCursor();
	    
	    float panX = c.getPanX(), panY = c.getPanY(), zoom = c.getZoom();
	    if(type == 0) panX = value;
	    else if(type == 1) panY = value;
	    else if(type == 2) zoom = value;
	    else throw new IllegalArgumentException("adapter type "+type);

	    cursor.setSpatialCursor(new SpatialCursor(c.getNode(), panX, panY, zoom));
	}
    }
}
