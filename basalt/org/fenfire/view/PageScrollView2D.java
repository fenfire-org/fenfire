/*
PageScrollView2D.java
 *    
 *    Copyright (c) 2003 by Benja Fallenstein and Tuomas J. Lukka
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
 * Written by Benja Fallenstein and Tuomas J. Lukka
 */
package org.fenfire.view;
import org.fenfire.spanimages.*;
import org.fenfire.functional.Function;
import org.nongnu.libvob.VobScene;
import org.nongnu.alph.*;
import org.fenfire.swamp.*;

/** A View2D showing a whole PageScrollBlock.
 *  The <code>plane</code> object passed to <code>render()</code>
 *  is the <code>PageScrollBlock</code> object.
 *  <p>
 *  XXX!!! NO CULLING IMPLEMENTED YET!
 */
public class PageScrollView2D extends View2D {

    private final Function pageScroll2layout;

    /** Create a PageScrollView2D.
     * @param pageScroll2layout A function taking
     * 	a PageScrollBlock and returning a PageSpanLayout.
     */
    public PageScrollView2D(Function pageScroll2layout) {
	this.pageScroll2layout = pageScroll2layout;
    }



    public void getSize(Object plane, float[] wh) {
	PageScrollBlock block = (PageScrollBlock)plane;
	PageSpanLayout layout = (PageSpanLayout)pageScroll2layout.f(block);
	if(layout != null) {
	    wh[0] = layout.getWidth();
	    wh[1] = layout.getHeight();
	} else {
	    wh[0] = 100;
	    wh[1] = 100;
	}
    }

    public Object getSelectedObject(Object plane, float x, float y, float w, float h) {
	PageScrollBlock block = (PageScrollBlock)plane;
	PageSpanLayout layout = (PageSpanLayout)pageScroll2layout.f(block);
	if(layout == null) return null;
	return layout.getSelection(x, y, w, h);

    }

    public void render(VobScene vs, 
		Object plane,
		int matchingParent,
		    int box2screen, int box2paper
			) {
	PageScrollBlock block = (PageScrollBlock)plane;
	PageSpanLayout layout = (PageSpanLayout)pageScroll2layout.f(block);
	if(layout == null) return;
	int paper2box = vs.invertCS(box2paper, "PGSVinv");
	int paper2screen = vs.concatCS(box2screen, "PGSVconc", paper2box);
	layout.place(vs, paper2screen, -1 /*box2screen*/, matchingParent);
    }


}
