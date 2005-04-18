// (c): Matti J. Katila

package org.fenfire.spanimages.fuzzybear;
import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class AWTPagePool {
    static void p(String s) { System.out.println("AWTPagePool:: "+s); }


    public int getSizes() { return sizes.length; }
    public int getSizeW(int index) { return sizes[index][1]; }
    public int getSizeH(int index) { return sizes[index][2]; }

    protected final int[][] sizes = {
	// how many, width, height
	{ 1, 2048, 2048 },
	{ 2, 256, 512 },
	{ 4, 128, 256 },
	{ 64, 64, 64 },
    };


    protected BufferedImage[] imgs;
    protected int pixs[][];
    private int[] buff, WxH;
    protected int count, maxW, maxH;
    static protected Object instance;
    static protected boolean inited = false;

    /** Singleton pool for awt
     */
    protected void init() { //AWTPagePool() {
	count=0;
	for (int i=0; i<sizes.length; i++)
	    count += sizes[i][0];

	// create images
	imgs = new BufferedImage[count];
	pixs = new int[count][];
	WxH = new int[count*2];

	int ind = 0;
	maxH = 0; maxW = 0;

	for (int i=0; i<sizes.length; i++) {
	    p("size: "+i);
	    for(int j=0; j<sizes[i][0]; j++) {
		int w = sizes[i][1], h = sizes[i][2];
		WxH[ind*2] = w; WxH[ind*2+1] = h;

		maxW = (maxW < w ? w : maxW);
		maxH = (maxH < h ? h : maxH);

		pixs[ind] = new int[w*h];
		imgs[ind] = new BufferedImage(w,h, 
					      BufferedImage.TYPE_INT_ARGB);
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
	    imgs[i].setRGB(0,0,getW(i), getH(i), 
			  pixs[i], 0, getW(i));
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
	buff = new int[maxH*maxW];
	inited = true;
    }

    public int getW(int index) { return WxH[2*index]; }
    public int getH(int index) { return WxH[2*index+1]; }

    ImageConsumerImpl imgConsumer = new ImageConsumerImpl();
	
    class ImageConsumerImpl implements ImageConsumer {
	int[] mic = null;
	int width = 0;
	ColorModel cm = null;
	void p(String s) { System.out.println("Consumer:: "+s); }
	public void imageComplete(int status) {
	    //p("imageComplete: "+status);
	}
	public void setColorModel(ColorModel model) { 
	    //p("setColorModel: "+model);
	    cm = model;
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
	
	public void setMem(int[] mem, int width) {
	    this.mic = mem;
	    this.width = width;
	}
    }

    static int FOO = 255;

    public synchronized void setImage(InputStream in, int index, 
				      int width, int height) 
	throws IOException {

	if (!inited) return;

	if (width > maxW || height > maxH)
	    throw new IllegalArgumentException("Width or height too big! "+
					       width+"x"+height+" > "+
					       maxW+"x"+maxH);
	com.sixlegs.image.png.PngImage png = 
	    new com.sixlegs.image.png.PngImage(in, true);
	png.setBuffer(buff);
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
	imgs[index].setRGB(0,0,getW(index), getH(index), 
			   pixs[index], 0, getW(index));
	png.removeConsumer(imgConsumer);
    }

}
