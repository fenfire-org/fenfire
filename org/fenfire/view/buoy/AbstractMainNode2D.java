/*
AbstractMainNode2D.java
 *    
 *    Copyright (c) 2003, Benja Fallenstein and Matti Katila and Tuomas J. Lukka
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
 * Written by Benja Fallenstein and Matti Katila and Tuomas J. Lukka
 */

package org.fenfire.view.buoy;
import org.fenfire.view.*;
import org.nongnu.libvob.mouse.MouseMultiplexer;

import org.nongnu.libvob.*;
import org.nongnu.libvob.impl.DefaultVobMatcher;
import org.nongnu.libvob.buoy.*;

/** A BuoyViewMainNode showing a 2D plane rendered through
 *  a View2D. This class implements the keyboard and mouse
 *  command for moving on the spatial plane.
 */
public abstract class AbstractMainNode2D implements BuoyViewMainNode, 
	org.fenfire.view.lava.FastView {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("AbstractMainNode2D:: "+s); }

    /** The object representing the plane currently shown in this buoy node.
     */
    protected Object plane;
    public Object getPlane() { return plane; }
    public void setNewPlane(Object plane, 
			    float panX, float panY, float zoom) { 
	this.plane = plane;
	setFocus(new SimpleFocus(panX, panY) );
	focus.setZoom(zoom);
    }
	

    /** The mouse controller associated with this main node. 
     */
    protected MouseMultiplexer mouseController;

    /** An object representing a focus on a 2D plane.
     *  The SimpleFocus implementation below just stores
     *  an X and Y coordinate, but other implementations
     *  could store e.g. an accursed node on the plane.
     */
    public interface Focus {
	float getPanX();
	float getPanY();
        float getZoom();
        void setZoom(float zoom);
	void setPan(float panX, float panY);
    }

    public static class SimpleFocus implements Focus {
	protected float panX, panY, zoom=1;
	public SimpleFocus(float panX, float panY) {
	    this.panX = panX;
	    this.panY = panY;
	}
	public float getPanX() { return panX; }
	public float getPanY() { return panY; }
        public float getZoom() { return zoom; }
        public void setZoom(float zoom) {this.zoom=zoom; }
	public void setPan(float panX, float panY) {
	    this.panX = panX;
	    this.panY = panY;
	}
	public String toString() {
	    return "[SimpleFocus: "+panX+" "+panY+" "+zoom+"]";
	}
    }

    /** The view shown in this buoy node.
     */
    protected View2D view2d;
    public View2D getView2D() { return view2d; }

    protected Focus focus;
    public Focus getFocus() { return focus; }
    public void setFocus(Focus focus) { this.focus = focus; }

    public interface Factory {
	AbstractMainNode2D create(Object plane, View2D view2d,
				  float panX, float panY, float zoom);
    }


    public int getBox2Screen() { return box2screen; }
    /** The box2screen coordinate system we set last.
     */
    protected int box2screen = -1;

    /** The box2paper coordinate system last set.
     * Important rule: must always be matching child of box2screen,
     * with key "BOX2PAPER".
     * This is also done by the NodeType2D classes, to ensure proper
     * interpolation.
     */
    protected int box2paper = -1;
    protected float boxw, boxh;

    public AbstractMainNode2D(Object plane, View2D view2d,
			      Focus focus, MouseMultiplexer mouse) {
	this.plane = plane; this.view2d = view2d;
	this.focus = focus; this.mouseController = mouse;
    }


    /** Render the context into the main node view i.e., 
     * text cursor. 
     */
    static public Context context = null;
    public void keystroke(String s) {
	throw new Error("Not implemented.");
    }

    public void renderMain(VobScene vs, int into) {
        // inherited classes will render before this.
	if (context != null)
	    context.mainNodeToBeRender(vs, into, this);
    }
    public interface Context {
	void mainNodeToBeRender(VobScene vs, int into,
                                AbstractMainNode2D main);
    }

    /** Cause the changes to the zooming and panning variables
     * to be shown in the given vobscene.
     * This changes the parameters of the coordinate systems 
     * created by render().
     */
    public abstract void setZoomPan(VobScene vs);

    protected float[] v2dwh = new float[2];

    /** Clip the panX and panY values to the size of the canvas.
     */
    protected void clipPan() {
	view2d.getSize(plane, v2dwh);
	if(v2dwh[0] >= 0) {
	    float panX = focus.getPanX(), panY = focus.getPanY();
	    boolean chg = false;
	    if(focus.getPanX() < 0) { panX = 0; chg = true; }
	    if(focus.getPanX() > v2dwh[0]) {
		panX = v2dwh[0]; chg = true;
	    }
	    if(focus.getPanY() < 0) { panY = 0; chg = true; }
	    if(focus.getPanY() > v2dwh[1]) {
		panY = v2dwh[1];
		chg = true;
	    }
	    if(chg) focus.setPan(panX, panY);
	}
    }

    /** Get the xy coordinates in the view2D coordinate system.
     * Does not check if it was really a hit.
     */
    public float[] getXYHit(VobScene oldVobScene, float x, float y) {
	float[] pt = new float[] { x, y, 0 };
	if (dbg) p("P1: "+pt[0]+" "+pt[1]+" "+pt[2]);
	oldVobScene.coords.inverseTransformPoints3(box2screen, pt, pt);
	if (dbg) p("P2: "+pt[0]+" "+pt[1]+" "+pt[2]);
	oldVobScene.coords.transformPoints3(box2paper, pt, pt);
	if (dbg) p("P3: "+pt[0]+" "+pt[1]+" "+pt[2]);
	return pt;
    }


    protected float[] 
	hit_pt = new float[3], 
	hit_sq = new float[2];

    public boolean hasMouseHit(VobScene oldVobScene, int x, int y, float[] zout) {
	/** Any who uses this method *MUST* check that box2screen is > 1!
	 */
	if (box2screen < 2) return false;

	hit_pt[0] = x; hit_pt[1] = y; hit_pt[2] = 0;
	for (int i=0; i<hit_sq.length; i++) hit_sq[i] = 0;
	
	oldVobScene.coords.inverseTransformPoints3(
    	   box2screen, hit_pt, hit_pt);
	oldVobScene.coords.getSqSize(box2screen, hit_sq);
	return false;
    }

    public void moveToPoint(int x, int y, VobScene oldVS) {
        mouseMoveClick(x,y,oldVS);
    }

    protected void mouseMoveClick(int x, int y, VobScene oldVobScene) {
	float[] pt = new float[] { x, y, 0 };

	if(dbg) p("P1: "+pt[0]+" "+pt[1]+" "+pt[2]);
	oldVobScene.coords.inverseTransformPoints3(
		box2screen, pt, pt);
	if(dbg) p("P2: "+pt[0]+" "+pt[1]+" "+pt[2]);
	oldVobScene.coords.transformPoints3(box2paper, pt, pt);
	if(dbg) p("P3: "+pt[0]+" "+pt[1]+" "+pt[2]);

	focus.setPan(pt[0], pt[1]);
	clipPan();
	    
    }


    public void changeSize(float change) { ; }
    public void changeZoom(float change) { 	 
	float zoom = focus.getZoom() * (float) Math.exp(change/150.0); 	 
	focus.setZoom(zoom); 	 
    }

    /** @deprecated
     */
    public boolean mouse(VobMouseEvent e, VobScene oldVobScene) {
	/*
	mouseController.deliverEvent(e);
	if (context != null)
	    return context.changeFastAfterMouseEvent();
	else return true;
	*/
	return false;
    }

    public void chgFast(VobScene oldVobScene, int parent) {
        clipPan();
        setZoomPan(oldVobScene);

	getView2D().chgFast(oldVobScene, plane, box2screen, box2screen, -1);
    }
}
