/*
FuzzySpanImageFactory.java
 *    
 *    Copyright (c) 2004, Matti J. Katila
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

package org.fenfire.spanimages.fuzzybear;
import org.fenfire.spanimages.*;
import org.fenfire.util.Pair;
import org.fenfire.functional.PureFunction;
import org.fenfire.functional.Function;
import org.nongnu.libvob.*;
import org.nongnu.alph.*;

import java.awt.*;
import java.io.*;

/** A class that converts ImageSpans to Vobs.
 */
public class FuzzySpanImageFactory extends SpanImageFactory {
    public static boolean dbg = true;
    private static void p(String s) { 
	System.out.println("FuzzySpanImageFactory:: "+s); }

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

    static MediaTracker tracker;
    static public void setComponent(Component c) {
	tracker = new MediaTracker(c);
    }
    
    int id = 0;
    class ImgVob extends AbstractVob implements SpanImageVob {
	java.awt.Image img;
	ImageSpan is;
	int ID;
	BlockFile f;
	public ImgVob(java.awt.Image img, ImageSpan is, BlockFile f) { 
	    this.img = img;
	    this.is = is;
	    this.f = f;
	    ID = id;
	    tracker.addImage(img, id++);
	}
	public void render(Graphics g, boolean fast, RenderInfo info1, RenderInfo info2) {
	    try {
		if (dbg) p("Graah");
		if (tracker.isErrorID(ID)) {
		    p("error!!");
		    p("start loading");
		    tracker.waitForID(ID);
		    p("finished loading..");
		    return;
		}
		if (tracker.checkID(ID)) {
		    if (dbg) p("start loading");
		    tracker.waitForID(ID);
		    if (dbg) {
			p("finished loading..");
			p("all ok");
		    }
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }

	    if (getWidth() < 0 || getHeight() < 0) return;
	    int x = (int)info1.x, y = (int)info1.y;
	    int w = (int)(info1.width*getWidth()), 
		h = (int)(info1.height*getHeight());
	    if (dbg) p("img data: "+x+", "+y+", "+w+"x"+h+",  w: "+info1.width+", h: "+info1.height);
	    g.drawImage(img, x,y,w,h, new java.awt.image.ImageObserver() {
		    public boolean imageUpdate(java.awt.Image img, int infoflags, 
					       int x, int y, int width, int height) {
			if (dbg) p("Image ready!"+x+", "+y+", "+width+"x"+height+", inf: "+infoflags);
			/*p("abort: "+java.awt.image.ImageObserver.ABORT);
			p("error: "+java.awt.image.ImageObserver.ERROR);
			*/
			return false;
			//return true;
		    }
		});
	    // XXX observer for changes is needed..???
	}
	public float getWidth() { return (float) is.getSize().getWidth(); }
	public float getHeight() { return (float) is.getSize().getHeight(); }
	public float getRealX(float spanx) { return 0; }
	public float getRealY(float spany) { return 0; }
	public int getSpanX(float vobx) { return 0; }
	public int getSpanY(float voby) { return 0; }
    }


    public Object f(Object imageSpan0) {
	ImageSpan imageSpan = (ImageSpan)imageSpan0;
	imageSpan = getSuperImageSpan(imageSpan);

	ScrollBlock sb = imageSpan.getScrollBlock();
	try {
	    BlockFile file = sb.getBlockFile();


	    java.awt.Image img = 
		Toolkit.getDefaultToolkit().createImage(
		    new com.sixlegs.image.png.PngImage(
			new FileInputStream(file.getFilename()), true)
		    );
		/*
		net.sourceforge.jiu.gui.awt.ImageCreator.convertToAwtImage(
		    net.sourceforge.jiu.codecs.ImageLoader.load(file.getFilename()),0x88);
		//Toolkit.getDefaultToolkit().createImage(file.getFilename());
		*/
	    if(dbg) p("src: "+img.getSource());
	    //file.close();
	    return new ImgVob(img,imageSpan, file);
	} catch (Exception e) { 
	    e.printStackTrace();
	}
	throw new Error("No lucky with images");
    }
}
