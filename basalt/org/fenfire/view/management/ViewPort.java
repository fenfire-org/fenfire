/*
ViewPort.java
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

package org.fenfire.view.management;
import org.fenfire.view.management.*;
import org.nongnu.libvob.mouse.*;
import org.nongnu.libvob.*;

public class ViewPort implements Keyable {
private void p(String s) { System.out.println("ViewPort:: "+s); }
    public int x,y,w,h; // screen coordinates are pixels.
    public ViewPort() { this(0,0,0,0); }
    public ViewPort(int x, int y, int w, int h) {
	this.x=x; this.y=y; this.w=w; this.h=h;
    }

    public void key(String s) { }
    public void mouse(VobMouseEvent ev) { }
    public void render(VobScene vs) { 
	lastScene = vs;
	viewPortCS = vs.orthoBoxCS(0, this, 0,x,y, 1,1, w,h);
    }

    protected Action action = null;
    public Action getAction() { return action; }
    public void activate() { }

    protected int viewPortCS = -1;
    protected VobScene lastScene = null;
    public VobScene getLastVS() { return lastScene; }
    public void setCoordsysParams() {
	if (viewPortCS < 0 || lastScene == null) return;
	lastScene.coords.setOrthoBoxParams(viewPortCS, 0, x,y, 1,1,w,h);
	//p("rerender...");
    }

}
