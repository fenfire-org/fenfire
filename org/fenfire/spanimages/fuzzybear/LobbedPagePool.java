// (c): Matti J. Katila

package org.fenfire.spanimages.fuzzybear;

import org.nongnu.libvob.vobs.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.lob.*;

import java.awt.image.*;
import java.awt.*;
import java.io.*;


public class LobbedPagePool extends AWTPagePool {
    static void p(String s) { System.out.println("LobbedPagePool:: "+s); }

    protected Vob[] vobs;

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


    protected boolean inited = false;
    protected void init() { //LobbedPagePool() {
	super.init();

	// create vobs
	vobs = new Vob[count];

	for (int i=0; i<count; i++) {
	    final Image img = imgs[i];
	    vobs[i] = new AbstractVob() {
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

			g.drawImage(img, x,y,w,h, null); 
		    }
		};
	}
	inited = true;
    }

    public Lob getLob(int index, int x0, int y0, int w, int h) {
	return getLob(index);
    }

    public Lob getLob(int index) {
	Lob l;
	try {
	    l = Lobs.vob(vobs[index]);
	} catch (ArrayIndexOutOfBoundsException e) {
	    l = Components.label("no such index but it might be available soon");
	}
	return l;
    }
}
