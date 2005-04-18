// (c): Matti J. Katila

package org.fenfire.spanimages.fuzzybear;

//import org.nongnu.libvob.vobs.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.lob.*;

import java.awt.image.*;
import java.awt.*;
import java.io.*;


public class LobbedPagePool extends AWTPagePool {
    static void p(String s) { System.out.println("LobbedPagePool:: "+s); }

    static public LobbedPagePool getInstance() {
	if (instance==null)
	    instance = new LobbedPagePool();

	return (LobbedPagePool) instance;
    }

    
    protected LobbedPagePool() {
	Thread t = new Thread() {
		public void run() {
		    try {
			sleep(1000);
		    } catch (Exception e) {}
		    init();
		}
	    };
	t.start();
    }


    static class ImageVob extends AbstractVob {
	protected Image img;
	protected int x0, y0, w0, h0;
	private ImageVob() {}

	public static ImageVob newInstance(Image content, int x, 
					   int y, int w, int h) {
	    ImageVob m = (ImageVob)FACTORY.object();
	    m.img = content;
	    m.x0 = x; 
	    m.y0 = y;
	    m.w0 = w; 
	    m.h0 = h;
	    return m;
	}

	/*
	protected Vob wrap(Image img) {
	    return newInstance(img, x0, y0, w0, h0);
	}
	*/
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
	    g.drawImage(img, 
			x,y,x+w,y+h, 
			x0,y0,x0+w0,y0+h0, 
			null); 
	}


	private static final Factory FACTORY = new Factory() {
		public Object create() {
		    return new ImageVob();
		}
	    };
    }


    protected boolean inited = false;
    protected void init() { //LobbedPagePool() {
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
		return Components.label("Waiting for initialazion.");

	    int x = (int) (getW(index) * x0);
	    int y = (int) (getH(index) * y0);
	    int w = (int) (getW(index) * w0);
	    int h = (int) (getH(index) * h0);
	    l = Lobs.vob(ImageVob.newInstance(imgs[index], x,y,w,h));
	} catch (ArrayIndexOutOfBoundsException e) {
	    l = Components.label("no such index but it might be available soon");
	}
	return l;
    }
}
