/*
DefaultSpanImageFactory.java
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

package org.fenfire.spanimages.gl;
import org.fenfire.spanimages.*;
import org.fenfire.util.Pair;
import org.fenfire.functional.PureFunction;
import org.fenfire.functional.Function;
import org.nongnu.alph.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.gl.*;
import org.nongnu.libvob.memory.*;
import org.nongnu.libvob.util.Background;
import java.util.*;
import java.awt.Dimension;
import java.awt.Point;

/** A class that converts ImageSpans to SpanImageVobs.
 */
public class DefaultSpanImageFactory extends SpanImageFactory {
    public static boolean dbg = false;
    private static void p(String s) { 
	System.out.println("DefaultSpanImageFactory: "+s); }

    public Function paperMaker ;
    public ScrollBlockImager scrollBlockImager;

    public DefaultSpanImageFactory(ScrollBlockImager imager,
		    Function paperMaker) {
	this.scrollBlockImager = imager;
	this.paperMaker = paperMaker;
    }

    /** Get the whole contiguous 2D image that this span is a part of.
     * To be removed once alph/imagespan_getwholepage--tjl or something
     * like it is implemented.
     */
    static private ImageSpan getSuperImageSpan(ImageSpan s) {
	if(s instanceof PageImageSpan) {
	    PageImageSpan p = (PageImageSpan)s;
	    return ((PageScrollBlock)p.getScrollBlock())
			.getPage(p.getPageIndex());
	} else {
	    return (ImageSpan)s.getScrollBlock().getCurrent();
	} 
    }
    
    public Object f(Object imageSpan0) {
	ImageSpan imageSpan = (ImageSpan)imageSpan0;

	ImageSpan superImage = getSuperImageSpan(imageSpan);

	SingleImage singleImage = scrollBlockImager.getSingleImage(
					imageSpan.getSuperImageSpan());

	// x,y texcoords of (1,1) inside texture
	float x1 = singleImage.virtualTexture.mipzipFile.getOrigWidth();
	float y1 = singleImage.virtualTexture.mipzipFile.getOrigHeight();

	Dimension d = superImage.getSize();


	Dimension largest = singleImage.virtualTexture.mipzipFile.
				getLevelDimension(0);

	// Calculate pixels in output per texels in texture
	// Use 75 pixels per inch.
	// singleImage.resolution is in DPI, so to get pixels per dot
	// we divide
	float xpixReso = 75 / singleImage.xresolution;
	float ypixReso = 75 / singleImage.yresolution;
	// unless it's pixel data, in which case PPD is always 1
	if(xpixReso <= 0)
	    xpixReso = 1;
	if(ypixReso <= 0)
	    ypixReso = 1;

	float xscale = 1f / largest.width / xpixReso;
	float yscale = 1f / largest.height / ypixReso;

	Point p =imageSpan.getLocation();
	Dimension ps =imageSpan.getSize();

	float xoffs = p.x / (float)d.width * x1;
	float yoffs = p.y / (float)d.height * y1;

	// Then, the texgen is simple:
	float[] texgen = new float[] {
	    xscale, 0,                  0, xoffs,
	    0,       yscale,            0, yoffs,
	    0,       0,                 1,   0,
	    0,       0,                 0,   1
	};
	if(dbg) p("Texgen quants: "+xscale+" "+yscale+" "+xoffs+" "+yoffs);
	if(dbg) p("sizes: "+ps.width+" "+ps.height+" "+d.width+" "+d.height);

	Paper paper = (Paper)paperMaker.f(new Pair(singleImage, texgen));

	if(dbg) p("singleImage: "+singleImage+", paper: "+paper+
		  ", texgen: "+texgen);

	return PoolManager.getInstance().makeVob(
		    singleImage,
		    paper,
		    texgen,
		    ps.width / (float)d.width * x1 / xscale,
		    ps.height / (float)d.height * y1 / yscale,
		    p.x, p.y,
		    p.x+ps.width, p.y+ps.height
		    );

    }
}
