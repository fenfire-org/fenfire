/*
FenApplet.java
 *    
 *    Copyright (c) 2004, PUBLIC DOMAIN
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
 * Written by PUBLIC DOMAIN
 */

//package org.fenfire;

import org.fenfire.view.management.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.impl.awt.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class FenApplet extends Applet
    implements AWTScreen.Applet, KeyListener {

    private void p(String s) { System.out.println("FenApplet:: "+s); }

    FServer f;
    AWTScreen screen = null;
    public void setAWTScreen(AWTScreen c) {
	this.screen = c; 
    }
    
    
    public void init() {
	f = FServer.startApplet(this);
	addKeyListener(this);
    }
    
    private KeyEvent lastEvent;
    public void processKeyEvent(KeyEvent e) {
	//if(dbg) p("AWTScreen received: " + e);
	if(e == lastEvent) throw new Error("Re-used event object: "+e);
	lastEvent = e;
	JUpdateManager.addEvent(screen, e);
    }
    public void keyTyped(KeyEvent e) { 
	p("key typed");
	processKeyEvent(e);
    }
    public void keyPressed(KeyEvent e) { 
	p("key pressed");
	processKeyEvent(e); 
    }
    public void keyReleased(KeyEvent e) {}

    public boolean isFocusTraversable() { return true; }
    
    /** Draws cached image of the view onto screen */
    public void paint(Graphics gr) {
	Image cache = screen.getCached();
	
	Dimension d = this.getSize();
	if (cache == null) {
	    gr.setColor(Color.white);
	    gr.fillRect(0, 0, d.width, d.height);
	    return;
	}
	try {
	    gr.drawImage(cache, 0, 0, null);
	} catch(NullPointerException e) {
	    System.out.println("exc "+gr+" "+cache+" "+cache.getGraphics());
	    throw e;
	}
    }
    public void update(Graphics gr) {
	// Default behaviour overridden because we clear the canvas ourselves
	paint(gr);
    }
    
}
