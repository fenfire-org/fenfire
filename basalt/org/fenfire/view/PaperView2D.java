/*
PaperView2D.java
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

package org.fenfire.view;
import org.nongnu.libvob.*;
import org.nongnu.libvob.gl.*;
import org.fenfire.functional.Function;

/** A paper background for View2D.
 * For the render call, make sure the plane object
 * is the one whose identity (hashCode) is permanent.
 * <p>
 * If given another View2D in the constructor,
 * this other view is rendered on top of the paper.
 */
public class PaperView2D extends View2D {
    public static boolean dbg = false;
    private static void pa(String s) { System.out.println("PaperView2D::"+s); }

    public float dicefactor = 1;
    public int flags = 0;

    public float paperScaling = .5f; 

    private Function paperMill;

    // implement
    public void chgFast(VobScene vs, 
			Object plane,
			int matchingParent,
			int box2screen, int box2plane) { 
	if (child != null) 
	    child.chgFast(vs, plane, matchingParent, box2screen, box2plane); 
    }
    // implement
    public View2D getContentView2D() { 
	if (child != null) return child.getContentView2D(); 
	return null;
    }
    protected View2D child;
    
    /** Create a new PaperView2D.
     * @param paperMill A function; input: the plane object, output:
     * 			a org.nongnu.libvob.gl.Paper object.
     */
    public PaperView2D(Function paperMill) { 
	this(paperMill, null);
    }
    /** Create a new PaperView2D.
     * @param paperMill A function; input: the plane object, output:
     * 			a org.nongnu.libvob.gl.Paper object.
     * @param child The view to render inside this. 
     * 			XXX View2DList should take care of this already.
     */
    public PaperView2D(Function paperMill, View2D child) { 
	this.paperMill = paperMill;
	this.child = child; 
    }

    private float[] tmp = new float[2];
    private float[] tmp2 = new float[2];

    // implement
    public Object getSelectedObject(Object plane, float x, float y, float w, float h) {
	return child.getSelectedObject(plane, x, y, w, h);
    }

    public void render(VobScene vs, 
		Object plane,
		int matchingParent,
		    int box2screen, int box2paper
			) 
    {
	if (dbg) pa("begin render");
	    
	Paper p = (Paper)paperMill.f(plane) ; 
	    
	org.nongnu.libvob.Vob v = GLRen.createEasyPaperQuad(p, dicefactor, flags);
	int magnified = box2paper;
	if(paperScaling != 1) {
	    int magnify = vs.coords.scale(0, paperScaling, paperScaling);
	    magnified = vs.coords.concat(magnify, magnified);
	    if(dbg) {
		vs.coords.getSqSize(box2paper, tmp);
		vs.coords.getSqSize(magnified, tmp2);
		pa("SQSIZES: "+tmp[0]+" "+tmp[1]+" "+tmp2[0]+" "+tmp2[1]);
	    }
		
	}
	vs.put(v, vs.translateCS(box2screen, "Papertr", 0, 0, 1000), 
	       magnified);

	if(child != null)
	    child.render(vs, plane, matchingParent,
			 box2screen, box2paper);
	if (dbg) pa("begin render..DONE");
    }

    public void getSize(Object plane, float[] wh) {
	if(child != null)
	    child.getSize(plane, wh);
	else {
	    wh[0] = -1;
	    wh[1] = -1;
	}
    }

}
