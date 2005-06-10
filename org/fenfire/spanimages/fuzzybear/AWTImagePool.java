/*
AWTImagePool.java
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
import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class AWTImagePool {
    static void p(String s) { System.out.println("AWTImagePool:: "+s); }


    public int getSizes() { 
	init();
	return sizes.length; 
    }
    public int getSizeW(int index) {
	init();
	return sizes[index][1]; 
    }
    public int getSizeH(int index) { 
	init();
	return sizes[index][2]; 
    }

    protected final int[][] sizes;
    protected int[] mask;


    public AWTImagePool(int[][] sizes, DirectColorModel cm) {
	this.sizes = sizes;
	if (cm.getAlphaMask() != 0)
	    this.mask = new int[]{cm.getRedMask(), cm.getGreenMask(), 
				  cm.getBlueMask(), cm.getAlphaMask()};
	else 
	    this.mask = new int[]{cm.getRedMask(), cm.getGreenMask(), 
				  cm.getBlueMask()};
    }



    
    protected int pixs[][];
    protected ColorModel cms[];
    protected DataBufferInt dataBuffs[];
    protected SampleModel models[];
    private int[] /*buff, */ WxH;
    protected int count, maxW, maxH;
    static protected Object instance;
    static protected boolean inited = false;

    /** Singleton pool for awt
     */
    protected synchronized void init() { 

	//p("inited: "+inited+" "+this);
	if (inited) return;

	count=0;
	for (int i=0; i<sizes.length; i++)
	    count += sizes[i][0];

	// create images
	pixs = new int[count][];
	WxH = new int[count*2];
	dataBuffs = new DataBufferInt[count];
	models = new SampleModel[count];

	int ind = 0;
	maxH = 0; maxW = 0;

	for (int i=0; i<sizes.length; i++) {
	    //p("size: "+i);
	    for(int j=0; j<sizes[i][0]; j++) {
		int w = sizes[i][1], h = sizes[i][2];
		WxH[ind*2] = w; WxH[ind*2+1] = h;

		maxW = (maxW < w ? w : maxW);
		maxH = (maxH < h ? h : maxH);

		pixs[ind] = new int[w*h];
		dataBuffs[ind] = new DataBufferInt(pixs[ind], w*h);
		models[ind] = new SinglePixelPackedSampleModel(
		    DataBuffer.TYPE_INT, getW(ind), getH(ind), mask);
		ind++;
	    }
	}

	for (int i=0; i<count; i++) {
	    int c =0;
	    if ((i%3) == 0) c = (255 << 24) | (255 << 16);
	    if ((i%3) == 1) c = (255 << 24) | (255 << 8);
	    if ((i%3) == 2) c = (255 << 24) | (255);
	    
	    for(int x=0; x<getW(i); x++) {
		for(int y=0; y<getH(i); y++) {
		    pixs[i][y*getW(i)+x] = c;
		}
	    }
	}

	/*
	Random r = new Random();
	for (int i=0; i<count; i++) {
	    int c = (255 << 24) | r.nextInt();
	    for(int x=0; x<getW(i); x++) {
		for(int y=0; y<getH(i); y++) {
		    pixs[i][y*getW(i)+x] = c;
		}
	    }
	    imgs[i].setRGB(0,0,getW(i), getH(i), 
			  pixs[i], 0, getW(i));
	}
	*/
	//buff = new int[maxH*maxW];
	inited = true;
    }

    public int getW(int index) { 
	//if (!inited) throw new Error("should be already inited..");
	return WxH[2*index]; 
    }
    public int getH(int index) { 
	//if (!inited) throw new Error("should be already inited..");
	return WxH[2*index+1]; 
    }

    ImageConsumerImpl imgConsumer = new ImageConsumerImpl();
	
    class ImageConsumerImpl implements ImageConsumer {
	int [] mic = null;
	int width = -1;
	void p(String s) { System.out.println("Consumer:: "+s); }
	public void imageComplete(int status) {
	    //p("imageComplete: "+status);
	}
	public void setColorModel(ColorModel model) { 
	    //p("setColorModel: "+model);
	}
	public void setDimensions(int width, int height) { 
	    //p("setDimensions: "+width+"x"+height);
	}
	public void setHints(int hintflags) {
	    //p("hint: "+hintflags);
	}
	public void setPixels(int x, int y, int w, int h, 
			      ColorModel model, byte[] pixels, 
			      int off, int scansize) {
	    p("setPixels: "+x+":"+y+"@"+w+"x"+h);
	    throw new Error("i don't like bytes..");
	}
	public void setPixels(int x, int y, int w, int h, ColorModel m,
				  int[] pixels, int off, int scansize) {

           for(int _y=0; _y<h; _y++) {
               for (int _x=0; _x<w; _x++) {
                   int p = pixels[off + _y*scansize + _x];
                   int r = m.getRed(p);
                   int g = m.getGreen(p);
                   int b = m.getBlue(p);
                   int a = m.getAlpha(p);
                   mic[(y+_y)*width + x+_x] = (a<<24)|(r<<16)|(g<<8)|b;
               }
           }
	}
	public void setProperties(java.util.Hashtable props) {
	    //p("setProperties: "+props);
	}
	
	public void setMem(int[] mic, int width) {
	    this.mic = mic;
	    this.width = width;
	}
    }

    public synchronized void setImage(InputStream in, int index, 
				      int width, int height) 
	throws IOException {

	if (!inited)
	    init();

	if (width > maxW || height > maxH)
	    throw new IllegalArgumentException("Width or height too big! "+
					       width+"x"+height+" > "+
					       maxW+"x"+maxH);
	com.sixlegs.image.png.PngImage png = 
	    new com.sixlegs.image.png.PngImage(in, true);
	
	//png.setBuffer(buff);
	png.setFlushAfterNextProduction(true);

	imgConsumer.setMem(pixs[index], width);
	png.startProduction(imgConsumer);
	/*
	if ((png.getWidth() != getW(index)) || 
	    (png.getHeight() != getH(index)))
	    throw new IllegalArgumentException("Not the exact size!"+
					       png.getWidth()+"x"+
					       png.getHeight()+" != "+
					       getW(index)+"x"+getH(index));
	*/
	png.removeConsumer(imgConsumer);
    }
}
