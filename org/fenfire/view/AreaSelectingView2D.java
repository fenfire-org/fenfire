/*
AreaSelectingView2D.java
 *    
 *    Copyright (c) 2003, Matti J. Katila
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
 * Written by Matti J. Katila
 */

package org.fenfire.view;
import org.nongnu.libvob.*;
import org.nongnu.libvob.impl.*;
import org.nongnu.libvob.gl.*;

import java.util.*;

/** View2D to draw the selecting area 
 */
public class AreaSelectingView2D extends View2D  {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("AreaSelectingView2D:: "+s); }

    
    private RectSelectionPainter rect = new RectSelectionPainter();
    private TextSelectionPainter text = new TextSelectionPainter();
    private SelectionPainter currentPainter = rect;


    /** An interface for selection shape painter.
     */
    private interface SelectionPainter {

	/** Render the selection into VobScene.
	 * @param plane The object of View2D plane where 
	 *              this selection is rendered.
	 */
	void render(VobScene vs, int into);

	/** Set coordinate system parameters for the selection.
	 * @param plane The plane where this selection is rendered.
	 * @param currentPlane 
	 *    The current accursed plane object.
	 *    Selection should be shown if currentPlane
	 *    is same as the plane object.
	 */
	void chgFast(VobScene old, int into, Object plane, 
		     Object currentPlane);

	/** Clears the selection.
	 */
	void clear();
    }

    private class TextSelectionPainter implements SelectionPainter {
	/* asfadasdfsadXXXXXXXXXX   <-begin
	 * XXXXXXXXXXXXXXXXXXXXXX   <-other
	 * XXXXasdfadsfasdfasdfsd   <-end
	 *                         where X is selection
	 */

	static private final String SELECT_BEGIN = "AreaSelectingView_selection_end";
	static private final String SELECT_OTHER = "AreaSelectingView_selection";
	static private final String SELECT_END = "AreaSelectingView_selection_begin";

	public void render(VobScene vs, int into) {
	    for (int i=0; i<pts.length; i++)
		renderImpl(vs, into, i);
	}
	private int renderImpl(VobScene vs, int into, int index) 
	{
	    int cs = -1;
	    switch(index) {
	    case 0: cs = vs.orthoCS(into, SELECT_BEGIN, 0, 1,2,3,4); break;
	    case 1: cs = vs.orthoCS(into, SELECT_OTHER, 0, 1,2,3,4); break; 
	    case 2: cs = vs.orthoCS(into, SELECT_END, 0, 1,2,3,4); break;
	    default:
		throw new Error("Impossible index!");
	    }
	    vs.put(pq, cs);
	    return cs;
	}

	public void chgFast(VobScene oldVS, int concatCs, 
			    Object plane, Object current) {
	    int [] cs = new int[3];
	    cs[0] = oldVS.matcher.getCS(concatCs, SELECT_BEGIN);
	    cs[1] = oldVS.matcher.getCS(concatCs, SELECT_OTHER);
	    cs[2] = oldVS.matcher.getCS(concatCs, SELECT_END);
	    
	    for (int i=0; i<cs.length; i++)
		if (cs[i] < 1) {
		    if (dbg) p("index: "+i+", "+cs[i]+" is not possible!");
		    cs[i] = renderImpl(oldVS, concatCs, i);
		}
	    
	    for(int i=0; i<pts.length; i++) {
		if (plane == current)
		    oldVS.coords.setOrthoParams(cs[i], 0,  pts[i][0], pts[i][1], pts[i][2]-pts[i][0], pts[i][3]-pts[i][1]);
		else
		    oldVS.coords.setOrthoParams(cs[i], 0,  0,0,0,0);
	    }
	}
	
	public void clear() {
	    for(int i=0; i<pts.length; i++)
		for (int j=0; j<pts[i].length; j++) pts[i][j] = 0;
	}

	void set(float bx, float byTop, float byBot,
		 float ex, float eyTop, float eyBot,
		 float edgeL, float edgeR) 
	{
	    clear();
	    if (dbg) {
		p("begin: "+bx+", "+byTop+", "+byBot);
		p("end:   "+ex+", "+eyTop+", "+eyBot);
		p("edges: "+edgeL+", "+edgeR);
	    }
	    // keep up the good work
	    // we have at least two lines here..
	    if (byBot <= eyTop) {
		if (dbg) p("at least two lines");
		setArea(0,bx, byTop, edgeR, byBot);
		setArea(1,edgeL,byBot,edgeR,eyTop);
		setArea(2,edgeL, eyTop, ex, eyBot);
	    }
	    else if (eyBot <= byTop) {
		setArea(0,ex, eyTop, edgeR, eyBot);
		setArea(1,edgeL,eyBot,edgeR,byTop);
		setArea(2,edgeL, byTop, bx, byBot);
	    }
	    // one line only
	    else {
		if (dbg) p("one line");
		setArea(0, bx,byTop, ex,eyBot);
	    }
	}

	private float[][] pts = new float[3][4];
	private void setArea(int index, float x0, float y0, float x1, float y1) {
	    pts[index][0] = x0; pts[index][1] = y0;
	    pts[index][2] = x1; pts[index][3] = y1;
	}
    }

    private class RectSelectionPainter implements SelectionPainter {
	static private final String SELECT = "AreaSelectingView_selection";

	public void render(VobScene vs, int into) {
	    int cs = vs.orthoCS(into, SELECT, 0, 1,2,3,4);
	    vs.put(pq, cs);
	}

	public void clear() {
	    setArea(0,0,0,0);
	}

	public void chgFast(VobScene oldVS, int concatCs, 
			    Object plane, Object current) {
	    int cs = oldVS.matcher.getCS(concatCs, SELECT);
	    if (cs < 1) throw new Error(cs+" is not possible!");

	    if (plane == current)
		oldVS.coords.setOrthoParams(cs, 0,  x0, y0, x1-x0, y1-y0);
	    else
		oldVS.coords.setOrthoParams(cs, 0,  0,0,0,0);
	}

	private float x0,y0,x1,y1;
	void setArea(float x0, float y0, float x1, float y1) {
	    this.x0 = x0; this.y0 = y0;
	    this.x1 = x1; this.y1 = y1;
	}
    }







    /** Set text area.
     * b prefix is begin, e prefix is end.
     */
    public void setTextArea(float bx, float byTop, float byBot,
			    float ex, float eyTop, float eyBot,
			    float edgeL, float edgeR) {
	currentPainter = text;
	rect.clear();
	text.set(bx,byTop, byBot, ex,eyTop,eyBot,edgeL, edgeR);
    }
    public void setArea(float x0, float y0, float x1, float y1) {
	currentPainter = rect;
	text.clear();
	rect.setArea(x0,y0,x1,y1);
    }


    private Object current = null;
    public void setCurrentPlane(Object obj) { current = obj; }

    private Paper paper;
    private GLRen.FixedPaperQuad pq;

    public AreaSelectingView2D(View2D child) {
        this(child, null);
    }
    public AreaSelectingView2D(View2D child, java.awt.Color color) {
        this.child = child;
        paper = SpecialPapers.selectionPaper(color);
        pq = GLRen.createFixedPaperQuad(paper, 0, 0, 1, 1, 0, 10, 10, 10);
    }


    static private final String CONCAT = "AreaSelectingView_concat";

    public void render(VobScene vs, Object plane,
                       int matchingParent,
                       int box2screen, int box2paper) {

   	if(child != null)
	    child.render(vs, plane, matchingParent,
			 box2screen, box2paper);


	int paper2box = vs.invertCS(box2paper, "areaselectview_INv");
	int paper2screen = vs.concatCS(box2screen, CONCAT,
                                   paper2box);

	currentPainter.render(vs, paper2screen);
	chgFast(vs, plane, matchingParent, box2screen, box2paper);
    }

    public void clear() {
	text.clear();
	rect.clear();
    }

    public void chgFast(VobScene oldVS, Object plane,
			int matchingParent, int box2screen, int box2plane) {
        if (dbg) p("box2screen: "+box2screen);
	int concatCs = oldVS.matcher.getCS(box2screen, CONCAT);
	if (dbg) p("conc: "+concatCs);
	text.chgFast(oldVS, concatCs, plane, current);
	currentPainter.chgFast(oldVS, concatCs, plane, current);
    }

    public Object getSelectedObject(Object plane, float x, float y, float w, float h) {
	if (child == null) return null;
	return child.getSelectedObject(plane, x, y, w, h);
    }

    public View2D getChildView2D() { return child; }
    protected View2D child;

    public void getSize(Object plane, float[] wh) {
	if(child != null)
	    child.getSize(plane, wh);
	else {
	    wh[0] = -1;
	    wh[1] = -1;
	}
    }


}
