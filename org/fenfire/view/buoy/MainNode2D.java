/*
MainNode2D.java
 *    
 *    Copyright (c) 2003 by Benja Fallenstein and Matti Katila
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
 * Written by Benja Fallenstein 
 * Based on code by Matti Katila
 */
package org.fenfire.view.buoy;
import org.fenfire.view.View2D;
import org.nongnu.libvob.mouse.MouseMultiplexer;

import org.nongnu.libvob.*;
import org.nongnu.libvob.impl.DefaultVobMatcher;
import org.nongnu.libvob.buoy.*;

/** A BuoyViewMainNode showing a 2D plane rendered through
 *  a View2D.
 */
public class MainNode2D extends AbstractMainNode2D {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("MainNode2D:: "+s); }

    private float tmp[] = new float[2];

    static public class MainNode2DFactory implements Factory {
	protected MouseMultiplexer mouseController;
	public MainNode2DFactory(MouseMultiplexer mouse) {
	    this.mouseController = mouse;
	}
	public AbstractMainNode2D create(Object plane, View2D view2d,
					 float panx, float pany, float zoom) {
	    return new MainNode2D(plane, view2d, 
				  new SimpleFocus(panx, pany), zoom,
				  mouseController);
	}
    }

    /** Create a main node with a given plane, pan, and zoom.
     */
    public MainNode2D(Object plane, View2D view2d,
		      Focus focus, float zoom, 
		      MouseMultiplexer mouse) {
	super(plane, view2d, focus, mouse);
        focus.setZoom(zoom);
    }
    
    /** Create a main node focused the center of a given plane.
     */
    public MainNode2D(Object plane, View2D view2d,
		      MouseMultiplexer mouse) {
	this(plane, view2d, new SimpleFocus(0, 0), 1,
	     mouse);
	view2d.getSize(plane, tmp);
	if(tmp[0] >= 0) {
	    focus.setPan(tmp[0] / 2f, tmp[1] / 2f);
	}
    }

    public void renderMain(VobScene vs, int into) {
	vs.coords.getSqSize(into, tmp);
	boxw = tmp[0]; boxh = tmp[1];
	box2screen = into;

	// create the coordsys, then position it through other method
	// this way, we only have the positioning code in one place
	box2paper = vs.coords.orthoBox(0,0,0,0,0,0,0,0);
	vs.matcher.add(box2screen, box2paper, "BOX2PAPER");
	setZoomPan(vs);
	view2d.render(vs, plane, into, into, box2paper);

        super.renderMain(vs, into);
    }

    public void setZoomPan(VobScene vs) {
	float zoom = focus.getZoom();
	vs.coords.setOrthoBoxParams(box2paper, 0, 
			focus.getPanX()-boxw/zoom/2, focus.getPanY()-boxh/zoom/2, 
				    1/zoom, 1/zoom, boxw, boxh);
    }

    public boolean hasMouseHit(VobScene oldVobScene, int x, int y, 
			       float[] zout) {
	if (super.box2screen < 2) return false;

	// XXX very bad optimization trick!
	// We know that main node cs is b2s and it's parent is 0.
	if (true) {
	    if (oldVobScene.getCSAt(0, (float)x,(float)y,null)!=box2screen) {
		if (dbg) p("optimization..");
		return false;
	    }
	}



	if (dbg) p("MOUSE: b2s: "+box2screen+", "+oldVobScene);

	super.hasMouseHit(oldVobScene, x,y, zout);

	if(hit_pt[0] >= 0 && hit_pt[0] < hit_sq[0] &&
	   hit_pt[1] >= 0 && hit_pt[1] < hit_sq[1]) {
	    hit_pt[2] = 0;
	    oldVobScene.coords
		.transformPoints3(box2screen, hit_pt, hit_pt);
	    if(zout != null)
		zout[0] = hit_pt[2];
	    return true;
	}
	return false;
    }
    
}
