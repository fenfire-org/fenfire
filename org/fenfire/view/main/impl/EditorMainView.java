/*
EditorMainView.java
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
package org.fenfire.view.main.impl;
import org.fenfire.*;
import org.fenfire.vocab.*;
import org.fenfire.fenedit.*;
import org.fenfire.view.*;
import org.fenfire.view.main.*;
import org.fenfire.view.management.*;
import org.fenfire.swamp.*;
import org.fenfire.functional.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.vobs.*;
import org.nongnu.libvob.mouse.*;
import org.nongnu.libvob.mouse.Action;
import org.nongnu.navidoc.util.Obs;

import java.util.*;

/** Show particle FenMM view.
 */
public class EditorMainView 
    implements MainView, /*Zoomable,*/ Keyable, Obs {

    static public boolean dbg = false;
    private void p(String s) { System.out.println("EditorMainView:: "+s); }

    Object plane;
    FServer f;
    Lob editor;
    NodeFunction func;
    public EditorMainView(final FServer f, Object n) {
	editor = new Editor();
	editor.addObs(this);

	plane = n;
	this.f = f;
	this.v = null;

	Fen[] fen = new Fen[1];
	f.environment.request("fen", fen, null);

	func = //new CachedNodeFunction(2, fen[0].graph, 
	     new NodeFunction() {
		 public Object f(ConstGraph g, Object o) {
		     VobScene vs = 
			 ((GraphicsAPI.RenderingSurface)f.getScreen(
			     ).window).createChildVobScene(new java.awt.Dimension(100,100), 2);
		     editor.setSize(v.w, v.h);
		     editor.render(vs, 1, 2, 
				0,0, v.w, v.h, 2^32, true);
		     return vs;
		 }
	     };
	//);

    }

    public void chg() { f.getScreen().switchVS(); }

    public void addActions(Action a) {
	if (dbg) p("add actions");
    }


    public Object handleRequest(Object req) {
	return null;
    }


    public String toString() { return "Editor View for Xanalogical Text"; }

    
    ViewPort v = null;


    // --- move to Zoomable!
    protected float zoom = 1;
    public float getZoom() { return zoom; }
    public void setZoom(float z) { ; /* empty */ }


    protected int box2screen = -1;

    public void render(VobScene vs, int into) {
	box2screen = into;

	vs.map.put(new RectVob(java.awt.Color.white, 
			       10, false, false), into);

	Fen[] fen = new Fen[1];
	f.environment.request("fen", fen, null);
	ChildVobScene cvs = (ChildVobScene) func.f(fen[0].constgraph, plane);
	int cs = vs.putChildVobScene(cvs, this.toString(), 
				     new int[]{ into, into});
	vs.coords.activateChildByCS(into, cs);
    }

 

    public void setCoordsysParams() {
    }

    
    public void key(String s) {
	p("key: "+s);
	editor.key(s);
    }
}
