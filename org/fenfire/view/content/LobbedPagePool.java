// (c): Matti J. Katila

package org.fenfire.view.content;

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


    /** 
     */
    protected LobbedPagePool() {
	super();

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
			
			g.drawImage(img, x,y,w,h, null); 
		    }
		};
	}
    }

    public Lob getLob(int index) {
	Lob l;
	l = Lobs.vob(vobs[index]);
	int w = getW(index);
	int h = getH(index);
	w = 100; h = 141;
	return Lobs.request(l, w,w,w, h,h,h);
    }
}
