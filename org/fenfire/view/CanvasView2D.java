/*
CanvasView2D.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka and Matti J. Katila
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
 * Written by Tuomas J. Lukka and Matti J. Katila
 */

package org.fenfire.view;
import org.fenfire.modules.pp.*;
import org.fenfire.*;
import org.fenfire.view.*;
import org.fenfire.view.buoy.*;
import org.fenfire.vocab.*;
import org.fenfire.swamp.*;
import org.fenfire.functional.*;
import org.fenfire.util.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.gl.*;
import org.nongnu.libvob.impl.DefaultVobMatcher;
import org.nongnu.libvob.impl.gl.*;
import org.nongnu.libvob.buoy.*;
import org.nongnu.libvob.vobs.SimpleConnection;
import org.nongnu.alph.*;

import java.util.*;

/** A View2D of a CANVAS2D, with a given node function.
 */
public class CanvasView2D extends View2D 
    implements NodedView2D {
    public static boolean dbg = true;
    private static void p(String s) { System.out.println("CanvasView2D:: "+s); }
    
    private Fen fen;
    private NodeFunction nodeView;
    public boolean cull = (GraphicsAPI.getInstance() 
	instanceof GLAPI? true: false);

    protected SimpleConnection lineconn = new SimpleConnection(.5f, .5f, .5f, .5f);

    public CanvasView2D(Fen fen, NodeFunction nodeView) {
	this.fen = fen;
	this.nodeView = nodeView;
	if (GraphicsAPI.getInstance() instanceof org.nongnu.libvob.impl.gl.GLAPI) {
	    lineconn.glsetup = GLCache.getCallList(
	    "PushAttrib ENABLE_BIT LINE_BIT\n"+
	    "Disable TEXTURE_2D\n"+
	    "LineWidth 5\n" +
	    "Enable BLEND\n" +
	    "Color 0 0 0 0.3\n"
		);
	    lineconn.glteardown = GLCache.getCallList("PopAttrib");
	}
    }

    public Object getSelectedObject(Object plane, float x, float y, float w, float h) {
	return null;
    }

    public View2D getContentView2D() { return this; }

    public void render(VobScene vs, Object container,
		       int matchingParent, int box2screen, int box2plane)
    {
	if (!RDFUtil.isNodeType(fen.graph, container, CANVAS2D.Canvas))
	    throw new Error("plane node is NOT a container! " + container);

	int paper2box = vs.invertCS(box2plane, "canvasview_INv");
	int paper2screen = vs.concatCS(box2screen, "canvasview_conc",
                                   paper2box);


	if (dbg) {
	    p("canvas container: "+container);
	    p("box2screen: "+box2screen);
	    p("matchingparent: "+matchingParent);
	    p("paper2box"+paper2box);
	    p("paper2screen"+paper2screen);
	}

	/* The code adds two dummy cs:
	 * 1) cs with "CANVAS" as a key.
	 * 2) cs into the previous one with container as a key.
	 * All nodes are matched into latter dummy cs and connectors etc.
	 * must know this and follow that specification to work correctly.
	 * [specification specified in irc at 2003-07-31T9:50/10:35Z]
	 */
	int canvasCS = vs.coords.translate(matchingParent, 0,0);
	vs.matcher.add(matchingParent, canvasCS, "CANVAS");
	int containerCS = vs.coords.translate(canvasCS, 0,0);
	vs.matcher.add(canvasCS, containerCS, container);

	p("containerCS: "+containerCS);
	Iterator iter = fen.constgraph.findN_11X_Iter(container, 
						      CANVAS2D.contains);
	if (dbg) p("Canvas begin");
	while (iter.hasNext()) {
	    Object n = iter.next();
	    if (dbg) p("placeable object: "+n);
	    org.nongnu.libvob.lava.placeable.Placeable p = 
		    (org.nongnu.libvob.lava.placeable.Placeable)nodeView.f(fen.constgraph, n);
	    // May return null if it's not placeable or
	    // if it's not yet available
	    if(p == null)
		continue;

	    int cs = vs.coords.orthoBox(paper2screen, 0,1,2,3,4,5,6);
	    vs.matcher.add(containerCS, cs, n);
	    vs.activate(cs);
	    p("cs: "+cs);
	    if(cull) {
		//cs = vs.cullCS(cs, "CULL", box2screen);
		cs = vs.cullCS(cs, "CULL", matchingParent);
	    }
	    p.place(vs, cs);
        }
        chgFast(vs, container, matchingParent, box2screen, box2plane);

	
	// [mudyc] thinks that something which knows
	// about STRUCTLINK should be an adaptor.

	// show connections
	Iterator iter1 = fen.constgraph.findN_11X_Iter(container, 
						      CANVAS2D.contains);
	while (iter1.hasNext()) {
	    Object node1 = iter1.next();
	    int cs1 = vs.matcher.getCS(containerCS, node1);

	    Iterator iter2 = fen.constgraph.findN_11X_Iter(node1, 
		    STRUCTLINK.linkedTo);
	    while (iter2.hasNext()) {
		Object node2 = iter2.next();
		Object container2 = fen.constgraph.find1_X11(CANVAS2D.contains, node2);
		if(container2 != container)
		    continue;
		int cs2 = vs.matcher.getCS(containerCS, node2);
		
		vs.map.put(lineconn, cs1, cs2);
	    }
        }
    }

    static public Object getContainerKey(VobScene vs, int from) {
	DefaultVobMatcher m = (DefaultVobMatcher)vs.matcher;
	int canvasCS = m.getCS(from, "CANVAS");

	Object container = null;
	for(Iterator i=m.getKeys(canvasCS).iterator(); i.hasNext();) {
	    container = i.next();
	    if (dbg) p("container object is: " +container);
	}
	return container;
    }
    static public int getContainerCS(VobScene vs, int from) {
	Object container = getContainerKey(vs, from);
	int canvasCS = vs.matcher.getCS(from, "CANVAS");
	int containerCS = vs.matcher.getCS(canvasCS, container);
	if (containerCS < 0) throw new Error("Impossible!");
	return containerCS;
    }

    public void chgFast(VobScene oldVS, Object container,
			int matchingParent, int box2screen, int box2plane) {
	int containerCS = getContainerCS(oldVS, matchingParent);

	if (dbg) p("chg fast: "+containerCS);

	DefaultVobMatcher m = (DefaultVobMatcher)oldVS.matcher;
	for(Iterator i=m.getKeys(containerCS).iterator(); i.hasNext();) {
	    Object n = i.next();
	    if (dbg) p("Object is: "+n);
	    float x = RDFUtil.getFloat(fen.graph, n, CANVAS2D.x);
	    float y = RDFUtil.getFloat(fen.graph, n, CANVAS2D.y);

	    org.nongnu.libvob.lava.placeable.Placeable p = 
		    (org.nongnu.libvob.lava.placeable.Placeable)nodeView
			.f(fen.constgraph, n);
	    // May return null if it's not placeable or
	    // if it's not yet available
	    if(p == null) continue;
	    
	    if(dbg) p("size: "+p.getWidth()+" "+p.getHeight()+
		      ", p: "+p+", x: "+x+", y: "+y);

	    int cs = oldVS.matcher.getCS(containerCS, n);
	    p("cs: "+cs);
	    if (cs < 1) throw new Error(cs+" is not possible!");

            oldVS.coords.setOrthoBoxParams(cs, 0, 
                                           x, y, 1, 1,
                                           p.getWidth(), p.getHeight());
	}
    }

    public int getNodeCS(VobScene oldvs, 
			 int x, int y,
			 Object plane, int matchingParent, 
			 int box2screen, int box2plane, 
			 float[] xyout) {
	return oldvs.getCSAt(box2screen, x, y, xyout);
    }

    public int getNodeCS(VobScene oldVS, 
			 Object node, 
			 Object plane, int matchingParent, 
			 int box2screen, int box2plane) {
	int containerCS = getContainerCS(oldVS, box2screen);
	int cs = oldVS.matcher.getCS(containerCS, node);
	if (cs < 1) throw new Error(cs+" is not possible!");
	return cs;
    }

    public Object getNode(VobScene oldVS, int cs) {
	if (cs < 1) throw new Error(cs+" is not possible!");
	return oldVS.matcher.getKey(cs);
    }


    static public boolean hasCanvas(ConstGraph g, Object node) {
	return g.find1_X11(CANVAS2D.contains, node) != null;
    }
}
