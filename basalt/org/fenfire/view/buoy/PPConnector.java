/*
PPConnector.java
 *    
 *    Copyright (c) 2003, Matti Katila
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
 * Written by Matti Katila
 */

package org.fenfire.view.buoy;
import org.fenfire.*;
import org.fenfire.swamp.*;
import org.fenfire.functional.*;
import org.fenfire.view.*;
import org.fenfire.view.buoy.*;
import org.fenfire.util.*;
import org.fenfire.vocab.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.buoy.*;
import org.nongnu.libvob.impl.DefaultVobMatcher;

import org.nongnu.storm.util.Pair;

import java.util.Iterator;

/** Make the kinds of links familiar from PP structure.
 */
public class PPConnector implements BuoyViewConnector {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("PPConnector:: "+s); }

    public static boolean hackForTesting = false;

    private Fen fen;
    private View2D view2d;
    private BuoyViewNodeType nodeType;

    /** A node view used to determine the size of items
     *  on the canvas, so that the appropriate rectangle to show
     *  can be determined.
     */
    private NodeFunction nodeView;

    /** Create a new PP connector.
     * @param view2d The view2D that a mainnode should have for this
     * 			connector to be active.
     * @param nodeType2D The nodetype2d to be used for the buoys
     */
    public PPConnector(Fen fen, View2D view2d, 
			NodeType2D nodeType2D,
		       NodeFunction nodeView) {
	this.fen = fen;
	this.view2d = view2d;
	this.nodeType = nodeType2D;
	this.nodeView = nodeView;
    }

    public void addBuoys(VobScene vs, int parentCs,
			 BuoyViewMainNode mainNode,
			 BuoyLinkListener l) {
	if(!(mainNode instanceof MainNode2D)) return;
	MainNode2D mn2d = (MainNode2D)mainNode;
	if(mn2d.getView2D() != view2d && !hackForTesting) return;

	Object container = CanvasView2D.getContainerKey(vs, parentCs);
	int containerCS = CanvasView2D.getContainerCS(vs, parentCs);
	if (dbg) p("containerCS: "+containerCS);

	DefaultVobMatcher m = (DefaultVobMatcher)vs.matcher;
	for(Iterator i=m.getKeys(containerCS).iterator(); i.hasNext();) {
	    Object node = i.next();
	    int cs = m.getCS(containerCS, node);
	    int culledCS = m.getCS(cs, "CULL");
	    if (dbg) p("node: "+node+", cs: "+cs+", cull: "+culledCS);
	    if(culledCS > 0) cs = culledCS;

	    if (cs < 0) {
		p("Error with node: "+node+", too low cs: "+cs);
		continue;
	    }

	    /* check all associations */

	    // Right links!
            int shift = 0;
	    Iterator iter = fen.constgraph.findN_11X_Iter(node, 
		    STRUCTLINK.linkedTo);
	    while (iter.hasNext()) {
		Object obj = iter.next();
		Object plane = fen.constgraph.find1_X11(CANVAS2D.contains, obj);
		if (container == plane || 
		    !(CanvasView2D.hasCanvas(fen.graph, obj))) continue;
		Object anchor = getAnchor(plane, obj);
		if(anchor == null) continue;
		if (dbg) p("anchor:"+anchor);
		l.link(1, cs, nodeType, new Pair(node, obj), anchor, shift);
                shift++;
	    }

	    // Left links!
            shift = 0;
	    iter = fen.constgraph.findN_X11_Iter(STRUCTLINK.linkedTo, node);
	    while (iter.hasNext()) {
		Object obj = iter.next();
		Object plane = fen.constgraph.find1_X11(CANVAS2D.contains, obj);
		if (container == plane || 
		    !(CanvasView2D.hasCanvas(fen.graph, obj))) continue;
		Object anchor = getAnchor(plane, obj);
		if(anchor == null) continue;
		if (dbg) p("anchor:"+anchor);
		l.link(-1, cs, nodeType, new Pair(obj, node), anchor, shift);
                shift++;
	    }
	}
    }

    public int dx = 30, dy = 30;
    protected View2D.Anchor getAnchor(Object plane, Object node) {
	if (node == null) throw new Error("Impossible!");
	float x = RDFUtil.getFloat(fen.graph, node, CANVAS2D.x);
	float y = RDFUtil.getFloat(fen.graph, node, CANVAS2D.y);
	org.nongnu.libvob.lava.placeable.Placeable p = 
		(org.nongnu.libvob.lava.placeable.Placeable)nodeView.f(fen.constgraph, node);
	if(p == null)
	    return null;
	return new View2D.Anchor(plane,
				x-dx, y-dy, 
				  p.getWidth()+dx+dx, 
				  p.getHeight()+dy+dy,
				  node);
    }
}
