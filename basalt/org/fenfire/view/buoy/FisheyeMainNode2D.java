/*
FisheyeMainNode2D.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka
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
 * Written by Tuomas J. Lukka
 */

package org.fenfire.view.buoy;
import org.fenfire.view.View2D;
import org.nongnu.libvob.mouse.MouseMultiplexer;

import org.nongnu.libvob.*;
import org.nongnu.libvob.view.FisheyeState;
import org.nongnu.libvob.impl.DefaultVobMatcher;
import org.nongnu.libvob.buoy.*;

/** A buoy view MainNode for View2Ds, using a fisheye
 * transformation.
 */
public class FisheyeMainNode2D extends AbstractMainNode2D {
    public static boolean dbg = true;
    private static void pa(String s) { System.out.println(s); }

    public FisheyeState fisheyeState = 
	new FisheyeState(1, .01f, 100, 10, 1000, 1.2f, 215);


    static public class FisheyeMainNode2DFactory implements Factory {
	protected MouseMultiplexer mouseController;
	public FisheyeMainNode2DFactory(MouseMultiplexer mouse) {
	    this.mouseController = mouse;
	}
	public AbstractMainNode2D create(Object plane, View2D view2d,
					 float panx, float pany, float zoom) {
	    return new FisheyeMainNode2D(plane, view2d,
					 new SimpleFocus(panx, pany),
					 mouseController);
	}
    }

    // box 2 screen = always (0, 0) -> center

    /** Create a main node with a given plane, pan.
     */
    public FisheyeMainNode2D(Object plane, View2D view2d,
			     Focus focus, MouseMultiplexer mouse) {
	super(plane, view2d, focus, mouse);
        focus.setZoom(1);
    }

    public void changeZoom(float change) {
	super.changeZoom(change);
	fisheyeState.changeMagnitude(-change);
    }
    public void changeSize(float change) {
	super.changeSize(change);
	fisheyeState.changeSize(change); 
    }

    public void setZoomPan(VobScene oldVs) {
	oldVs.coords.setTranslateParams(box2paper, 
				     focus.getPanX(), focus.getPanY());
	fisheyeState.setCoordsysParams();
    }

    private float tmp[] = new float[2];
    public void renderMain(VobScene vs, int into) {
	vs.coords.getSqSize(into, tmp);
	boxw = tmp[0]; boxh = tmp[1];

	int ctr = vs.translateCS(into, "FTR", boxw/2, boxh/2);
	int fish = fisheyeState.getCoordsys(vs, ctr, "FISH");

	int translate = vs.coords.translate(0, focus.getPanX(), 
					    focus.getPanY());
	vs.matcher.add(fish, translate, "BOX2PAPER");


	box2screen = fish;
	box2paper = translate;

	view2d.render(vs, plane, into, box2screen, box2paper);

        super.renderMain(vs, into);
    }
    

    public boolean mouse(VobMouseEvent e, VobScene oldVobScene) {
	if(fisheyeState.event(e)) {
	    setZoomPan(oldVobScene);
	    return true;
	}
	return super.mouse(e, oldVobScene);
    }


    public boolean hasMouseHit(VobScene oldVobScene, int x, int y, float[] zout) {
	if (super.box2screen < 2) return false;
	super.hasMouseHit(oldVobScene, x,y, zout);

	oldVobScene.coords.transformPoints3(box2paper, hit_pt, hit_pt);
	view2d.getSize(plane, v2dwh);
	if(v2dwh[0] >= 0) {
	    if(hit_pt[0] >= 0 && hit_pt[0] < v2dwh[0] &&
	       hit_pt[1] >= 0 && hit_pt[1] < v2dwh[1]) return true;
	}
	return false;
    }

}

