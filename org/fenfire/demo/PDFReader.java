/*
PDFReader.java
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

package org.fenfire.demo;
import org.fenfire.view.*;
import org.fenfire.spanimages.*;
import org.fenfire.swamp.Nodes;

import org.nongnu.libvob.*;
import org.nongnu.libvob.fn.*;
import org.nongnu.libvob.lob.*;
import org.nongnu.libvob.lob.lobs.*;
import org.nongnu.libvob.impl.NewLobMain;
import javolution.realtime.*;
import javolution.util.*;
import java.awt.Color;
import java.util.*;


public class PDFReader extends NewLobMain {
    static private void p(String s) { System.out.println("PDFReader:: "+s); }

    public PDFReader(Color bg, String file) {
	super(bg);
	p("Read file: "+file);

	final float d = 90;
	keys.put("a", new Action() {
		public void run() {
		    p("zoom in");
		    zoom *= 1.25;
		    windowAnim.animate();
		}
		public javolution.lang.Text toText() { return null; }
		public boolean move(javolution.realtime.Realtime.ObjectSpace o) { return false; }
	    });
	keys.put("z", new Action() {
		public void run() {
		    p("zoom out");
		    zoom *= 0.75;
		    windowAnim.animate();
		}
		public javolution.lang.Text toText() { return null; }
		public boolean move(javolution.realtime.Realtime.ObjectSpace o) { return false; }
	    });
	keys.put("Up", new Action() {
		public void run() {
		    p("up");
		     y -= d;
		    windowAnim.animate();
		}
		public javolution.lang.Text toText() { return null; }
		public boolean move(javolution.realtime.Realtime.ObjectSpace o) { return false; }
	    });
	keys.put("Down", new Action() {
		public void run() {
		    p("down");
		     y += d;
		    windowAnim.animate();
		}
		public javolution.lang.Text toText() { return null; }
		public boolean move(javolution.realtime.Realtime.ObjectSpace o) { return false; }
	    });
	keys.put("Right", new Action() {
		public void run() {
		    p("right");
		     x += d;
		    windowAnim.animate();
		}
		public javolution.lang.Text toText() { return null; }
		public boolean move(javolution.realtime.Realtime.ObjectSpace o) { return false; }
	    });
	keys.put("Left", new Action() {
		public void run() {
		    p("left");
		     x -= d;
		    windowAnim.animate();
		}
		public javolution.lang.Text toText() { return null; }
		public boolean move(javolution.realtime.Realtime.ObjectSpace o) { return false; }
	    });


	//pageView = new PageReprView(null, null, windowAnim);
	node = Nodes.get("file://"+file);
    }

    Object node;
    Map keys = FastMap.newInstance();
    float zoom = 1f, 
	x=-100, 
	y=-50 ;

    public Lob createLob() {
	PagePool.init(null, windowAnim);
	PagePool.flush();
	
	Lob lob;
	//lob = Lobs.filledRect(Color.black);
	lob = PagePool.fullDocument(node, -x);

        lob = Lobs.key(lob, "PDF");
	lob = Lobs.keyController(lob, keys);
	lob = Lobs.translate(lob, x,y);
	lob = Lobs.scale(lob, zoom);
	lob = Lobs.align(lob, .5f, .5f);
	return lob;

    }



    public static void main(String[] argv) {
	PDFReader reader = new PDFReader(new Color(1, 1, .8f), argv[0]);
	reader.start();
    }

}
