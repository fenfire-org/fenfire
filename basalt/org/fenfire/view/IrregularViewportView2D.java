/*
IrregularViewportView2D.java
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
import org.nongnu.libvob.VobScene;
import org.nongnu.libvob.gl.*;
import org.nongnu.libvob.*;

import java.awt.*;

/** A View2D which renders a sub-view2D framed 
 * using an irregularframe.
 * The parameters are tuned to having the paper coordinate system
 * approximately pixel-size being normal.
 */
public class IrregularViewportView2D extends View2D {
    public float border = 25;
    public float ripple = 300;
    public int type = 2;
    public boolean needDepth = true;

    public static final int DEBUG_CONTENT = 1;
    public static final int DEBUG_FRAME = 2;
    public static final int DEBUG_BLANK = 4;
    public static final int DEBUG_CHILD = 8;
    /** What to render. If 0, render the stencil completely.
     * Else, render according to flags, without stenciling.
     */
    public int debugFlags = 0;

    public View2D getContentView2D() { 
	if (child != null) return child.getContentView2D(); 
	return null;
    }
    public void chgFast(VobScene vs, 
			Object plane,
			int matchingParent,
			int box2screen, int box2plane) { 
	if (child != null) 
	    child.chgFast(vs, plane, matchingParent, box2screen, box2plane); 
    }


    public static class Clip extends AbstractVob {
	java.awt.Shape s = null;
	public void render(Graphics g, boolean fast,
			   Vob.RenderInfo info1,
			   Vob.RenderInfo info2) {
	    s = g.getClip();
	    //System.out.println("push clip");
	    g.setClip((int)info1.x, (int)info1.y, 
		      (int)info1.width, (int)info1.height);
	}
	public AbstractVob pop() {
	    return new AbstractVob() {
		    public void render(Graphics g, boolean fast,
				       Vob.RenderInfo info1,
				       Vob.RenderInfo info2) {
			final java.awt.Shape ss = s;
			//System.out.println("pop clip");
			g.setClip(ss);
		    }
		};
	}
    }


    public View2D child;
    public Vob awtClip = null;
    public IrregularViewportView2D(View2D child) {
	this.child = child;
	
	if (GraphicsAPI.getInstance() instanceof
	    org.nongnu.libvob.impl.awt.AWTAPI) {
	    awtClip = new Clip();
	}

    }

    private IrregularFrame irregu;
    private float cw, ch;

    private float wh[] = new float[2];

    transient VobScene vs;
    transient Object plane;
    transient int matchingParent;
    transient int paper2screen;
    transient int box2screen;
    transient int box2paper;

    private Runnable putContent = new Runnable() {
	public void run() {
	    vs.map.put(irregu.getContent(), paper2screen, box2paper);
	}
    };

    private Runnable putBlank = new Runnable() {
	public void run() {
	    vs.map.put(irregu.getBlank(), paper2screen, box2paper);
	}
    };

    private Runnable putFrame = new Runnable() {
	public void run() {
	    vs.map.put(irregu.getFrame(), paper2screen, box2paper);
	}
    };

    private Runnable putChild = new Runnable() {
	public void run() {
	    child.render(vs, plane, matchingParent, box2screen, 
			    box2paper);
	}
    };

    public Object getSelectedObject(Object plane, float x, float y, float w, float h) {
	return child.getSelectedObject(plane, x, y, w, h);
    }

    public void render(VobScene vs, 
		Object plane,
		int matchingParent,
		    int box2screen, int box2paper
			) {
	this.vs = vs;
	this.plane = plane;
	this.matchingParent = matchingParent;

	if (awtClip != null) {
	    vs.put(awtClip, box2screen);
	    child.render(vs, plane, matchingParent, 
			 box2screen, box2paper);
	    vs.put(((Clip)awtClip).pop(), box2screen);
	    return;
	}








	child.getSize(plane, wh);
	    // XXX May thrash!
	if(irregu == null ||
	    cw != wh[0] ||
	    ch != wh[1]) {
	    cw = wh[0];
	    ch = wh[1];
	    if(cw < 0) {
		irregu = IrregularFrame.create(-1e10f,-1e10f,1e10f,1e10f,
			border, ripple, type);
	    } else {
		irregu = IrregularFrame.create(0,0,cw,ch,
			border, ripple, type);
	    }
	}

	int paper2box = vs.invertCS(box2paper, "IRREGU_INVE");

	this.paper2screen = vs.concatCS(box2screen, "IRREGU_B2P", paper2box);
	this.box2screen = box2screen;
	this.box2paper = box2paper;
	this.vs = vs;

	if(debugFlags == 0) {
	    Stencil.drawStenciled(vs,
			putContent,
			putBlank,
			putFrame,
			putChild,
			needDepth);
	} else {
	    if((debugFlags & DEBUG_CONTENT) != 0) putContent.run();
	    if((debugFlags & DEBUG_FRAME) != 0) putFrame.run();
	    if((debugFlags & DEBUG_BLANK) != 0) putBlank.run();
	    if((debugFlags & DEBUG_CHILD) != 0) putChild.run();
	}
    }

    public void getSize(Object plane, float[] wh) {
	child.getSize(plane, wh);
    }


}

