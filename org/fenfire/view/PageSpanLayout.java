/*
PageSpanLayout.java
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

import java.awt.Point;
import java.awt.Dimension;

import org.fenfire.util.*;

import org.nongnu.libvob.*;
import org.nongnu.libvob.gl.*;
import org.nongnu.alph.*;
import org.fenfire.spanimages.*;
import org.fenfire.functional.Function;

/** A single pagespan enfilade laid out on the plane.
 */
public class PageSpanLayout 
    implements org.nongnu.libvob.lava.placeable.Placeable {

    public static boolean dbg = false;
    private void p(String s) { System.out.println("PageSpanLayout:: "+s); }

    public static Enfilade1D.Maker enfilade1DMaker = 
	org.nongnu.alph.impl.Enfilade1DImpl.theMaker;

    private int npages;
    private PageImageSpan[] pages;
    private SpanImageVob[] spivs;
    private float[] xywh;

    private float w; 
    private float h;

    public float getWidth() { return w; }
    public float getHeight() { return h; }

    /** Allocate the structure to hold a given number of pages.
     */
    private void alloc(int size) {
	npages = size;
	pages = new PageImageSpan[npages];
	spivs = new SpanImageVob[npages];
	xywh = new float[npages * 4]; // x, y, w, h in PAPER coordinates
	w = 0;
	h = 0;
    }

    /** (To be called with increasing p): add a page.
     */
    private void page(int p, PageImageSpan sp, 
		Function spanImageFactory) {
	pages[p] = sp; 

	spivs[p] = (SpanImageVob)spanImageFactory.f(sp);

	xywh[4*p + 0] = 0;
	xywh[4*p + 1] = 0;
	xywh[4*p + 2] = spivs[p].getWidth();
	xywh[4*p + 3] = spivs[p].getHeight();

	if(dbg) p("Page "+p+" "+
		  xywh[4*p+0]+" "+
		  xywh[4*p+1]+" "+
		  xywh[4*p+2]+" "+
		  xywh[4*p+3]+" "+
		  pages[p]);

	w += xywh[4*p + 2];
	if(h < xywh[4*p + 3])
	    h = xywh[4*p + 3];
    }

    public PageSpanLayout(Enfilade1D enf, 
		    Function spanImageFactory) {
	alloc(enf.length());

	for(int p = 0; p < npages; p++) {
	    // XXX INEFFICIENT!
	    Object span = enf.sub(p, p+1).getList().get(0);
	    if(span instanceof PageSpan)
		span = ((PageSpan)span).getPage(0);
	    if(span instanceof PageImageSpan)
		page(p, (PageImageSpan)span, spanImageFactory); 
	    else
		p("PAGESPAN: NOT PAGE OR PAGEIMAGESPAN??? "+span);
	    // XXX what if not?
	}

    }

    public PageSpanLayout(PageSpan sp, 
		Function spanImageFactory) {
	alloc(sp.length());

	for(int p = 0; p < npages; p++) {
	    page(p, sp.getPage(p), spanImageFactory);
	}
    }

    public PageSpanLayout(PageImageSpan sp, 
		Function spanImageFactory) {
	alloc(1);
	page(0, sp, spanImageFactory);
    }


    /** Get the extents (in the output coordinates)
     * of a given page span.
     * Currently, only the first intersecting page
     * is used, but this may change.
     */
    public float[] getExtents(PageImageSpan s, float[] xywh_out) {
	if(xywh_out == null) xywh_out = new float[4];

	float curw = 0;

	for(int p = 0; p < npages; p++) {
	    if(s.intersects(pages[p])) {
		Point l_c = s.getLocation(); 
		Dimension d_c = s.getSize(); 

		float x0 = spivs[p].getRealX(l_c.x);
		float x1 = spivs[p].getRealX(l_c.x+d_c.width);
		float y0 = spivs[p].getRealY(l_c.y);
		float y1 = spivs[p].getRealY(l_c.y+d_c.height);

		xywh_out[0] = curw + x0;
		xywh_out[2] = x1-x0;

		xywh_out[1] = y0;
		xywh_out[3] = y1-y0;

		if(dbg) p("Center found: "+p+" "+pages[p]+" "+
			  xywh_out[0] + " " + xywh_out[1] + " " +
			  xywh_out[2] + " " + xywh_out[3] + " " );
		
		return xywh_out;
	    }
	    curw += xywh[4*p + 2];
	}
	return null;
    }

    public void place(VobScene vs, int into) {
	place(vs, into, -1, -1);
    }

    /** Place this layout into the given coordinate system.
     * Note that the layout is not affected by the 
     * box size of the coordinate system.
     * @param cullCS The coordinate system against which
     *      all objects should be culled.
     * @param matchCS the matching parent for the spans as keys of
     * 			coordinate systems
     */
    public void place(VobScene vs, int into, int cullCS, int matchCs) {
	float curx = 0;
	// Now we can draw the pages.
	if(matchCs < 0) matchCs = into;
	for(int p = 0; p < npages; p++) {
	    if(dbg) p("Place page: "+p+" "+spivs[p]+" "+curx+" "+
			+ xywh[4*p+2]+" "+
			+ xywh[4*p+3]+" ");
	    // We want a coordinate system
	    // whose box is exactly the span
	    int around = vs.coords.orthoBox(into, 0, 
			    curx, 0, 1, 1,
			    xywh[4*p+2], xywh[4*p+3]);
	    vs.matcher.add(matchCs, around, pages[p]);
	    if(cullCS >= 0)
		around = vs.cullCS(around, "CULL", cullCS);

	    curx += xywh[4*p + 2];

	    vs.map.put(spivs[p], around);
	}
    }

    /** Place this layout strictly fitted into the given
     * box.
     * Note that this won't interpolate nicely to
     * this layout placed with the
     * "place" method.
     */
    public void placeBoxed(VobScene vs, int into) {
	int unit = vs.unitSqCS(into, "PSPCV.UNIT");
	int scaled = vs.scaleCS(unit, "PSPCV.SCALED",
		    1.0f / w, 1.0f / h);
	place(vs, scaled);
    }

    /** Get selected area of pagespanlayout in enfilade
     */
    public Enfilade1D getSelection(float x, float y, float width, float height) {
	if (x<0 || y<0) throw new Error ("X or Y smaller than zero, x:"+x+", y:"+y);

	Enfilade1D enf = enfilade1DMaker.makeEnfilade();

	float w = 0;

	if(dbg) p("getSelection: "+x+" "+y+" "+width+" "+height);

	// Go into page where selection starts
	for (int p = 0; p<pages.length; p++)  {
	    // Vob coordinates of the intersecting rectangle
	    float sx0, sx1, sy0, sy1;

	    sx0 = x - xywh[4*p] - w;
	    sy0 = y - xywh[4*p+1];

	    sx1 = x + width - xywh[4*p] - w;
	    sy1 = y + height - xywh[4*p + 1];

	    if(dbg) p("Page "+p+": "+sx0+" "+sy0+" "+sx1+" "+sy1+" - "+
			xywh[4*p+2]);

	    w += xywh[4*p + 2];

	    int rx0, rx1, ry0, ry1;
	    
	    rx0 = spivs[p].getSpanX(sx0);
	    ry0 = spivs[p].getSpanY(sy0);
	    rx1 = spivs[p].getSpanX(sx1);
	    ry1 = spivs[p].getSpanY(sy1);

	    if(dbg) p(": "+rx0+" "+ry0+" "+rx1+" "+ry1+" - ");

	    if(rx0 < 0) rx0 = 0;
	    if(ry0 < 0) ry0 = 0;

	    Dimension d = pages[p].getSize();
	    if(rx0 >= d.width) continue;
	    if(ry0 >= d.height) continue;
	    if(rx1 < 0) continue;
	    if(ry1 < 0) continue;

	    if(rx1 >= d.width) rx1 = d.width - 1;
	    if(ry1 >= d.height) ry1 = d.height - 1;

	    if(dbg) p("final: "+rx0+" "+ry0+" "+rx1+" "+ry1+" - ");

	    enf = enf.plus(enfilade1DMaker.makeEnfilade(pages[p].subArea(rx0,ry0, rx1-rx0, ry1-ry0)));
	}
	return enf;
    }
}


