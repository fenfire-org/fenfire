/*
LobbedImagePool.java
 *    
 *    Copyright (c) 2005, Matti J. Katila
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

//import org.nongnu.libvob.vobs.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.lob.*;

import java.awt.image.*;
import java.awt.color.*;
import java.awt.*;
import java.util.*;
import java.io.*;


public class LobbedImagePool extends AWTImagePool {
    static void p(String s) { System.out.println("LobbedImagePool:: "+s); }


    static public LobbedImagePool pagePool(int highResPages) {
	return new LobbedImagePool(
	    new int[][]{
		// how many, width, height
		{ highResPages, 2048, 2048 },
		{ 2, 256, 512 },
		{ 4, 128, 256 },
		{ 64, 64, 64 },
	    },
	    new DirectColorModel(
		ColorSpace.getInstance(ColorSpace.CS_sRGB),
		32, // int
		0x00ff0000,
		0x0000ff00,
		0x000000ff,
		0, // we don't need alpha
		true,
		DataBuffer.TYPE_INT)
	    );
    }
    static public LobbedImagePool imagePool() {
	return new LobbedImagePool(
	    new int[][]{
		// how many, width, height
		{ 1, 256, 256 },
		{ 10, 128, 128 },
		{ 64, 64, 64 },
	    },
	    new DirectColorModel(
		ColorSpace.getInstance(ColorSpace.CS_sRGB),
		32, // int
		0x00ff0000,
		0x0000ff00,
		0x000000ff,
		0xff000000, // we need alpha
		true,
		DataBuffer.TYPE_INT)
	    );
    }

    protected ColorModel cm;
    protected LobbedImagePool(int[][] sizes, DirectColorModel cm) {
	super(sizes, cm);
	this.cm = cm;
    }

    static class ImageVob extends AbstractVob {
	protected Image img;
	
	protected int x0, y0, w0, h0;
	
	static Point p = new Point(0,0);
	private ImageVob() {}

	public static ImageVob newInstance(LobbedImagePool pool, 
					   int ind, int x, 
					   int y, int w, int h) {
	    ImageVob m = (ImageVob)FACTORY.object();
	    WritableRaster raster =
		Raster.createWritableRaster(
		    pool.models[ind],
		    pool.dataBuffs[ind],
		    p);
	    m.img = new BufferedImage(pool.cm, raster, false, null);
	    m.x0 = x; 
	    m.y0 = y;
	    m.w0 = w; 
	    m.h0 = h;
	    return m;
	}
	
	public void render(Graphics g, boolean fast, 
			   RenderInfo info1, RenderInfo info2) {
	    int x = (int)info1.x, 
		y = (int)info1.y;
	    int w = (int)(info1.width), 
		h = (int)(info1.height);
	    
	    if (g instanceof Graphics2D)
		((Graphics2D) g).setRenderingHint(
		    RenderingHints.KEY_INTERPOLATION,
		    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    
	    //p("size: "+x0+"x"+y0+"@"+w0+"x"+h0);
	    //p("this is really drawn! "+img);
	    g.drawImage(img, 
			x,y, x+w,y+h, 
			x0,y0, x0+w0,y0+h0, 
			null); 
	}
	
	
	private static final Factory FACTORY = new Factory() {
		public Object create() {
		    return new ImageVob();
		}
	    };
    }


    protected boolean inited = false;
    protected void init() { //LobbedImagePool() {
	if(inited) return;

	super.init();
	inited = true;
    }

    public Lob getLob(int index) {
	return getLob(index, 0,0,1,1);
    }
    public Lob getLob(int index, float x0, float y0, 
		      float w0, float h0) {
	Lob l;
	try {
	    if (!inited) 
		return Components.label("Waiting for initialization.");

	    int x = (int) (getW(index) * x0);
	    int y = (int) (getH(index) * y0);
	    int w = (int) (getW(index) * w0);
	    int h = (int) (getH(index) * h0);
	    l = Lobs.vob(ImageVob.newInstance(this, index, x,y,w,h));
	} catch (/*ArrayIndexOutOfBounds */Exception e) {
	    e.printStackTrace();
	    l = Components.label("no such pool index but it might be available soon ("+index+")");
	}
	return l;
    }
}
