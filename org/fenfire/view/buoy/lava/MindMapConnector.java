/*
MindMapConnector.java
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

package org.fenfire.view.buoy.lava;
import org.nongnu.libvob.*;
import org.nongnu.libvob.buoy.*;
import org.nongnu.libvob.impl.DefaultVobMatcher;
import org.nongnu.alph.*;
import org.fenfire.*;
import org.fenfire.swamp.*;
import org.fenfire.structure.StructLink;
import org.fenfire.view.*;
import org.fenfire.view.buoy.*;
import org.fenfire.util.*;
import org.fenfire.vocab.*;
import org.fenfire.functional.*;
import org.fenfire.vocab.lava.*;

import java.util.Iterator;
import java.util.List;

/** MindMap connector. Connects MindMapView2D to others. 
 * First only canvas perhaps (later more).
 */
public class MindMapConnector implements BuoyViewConnector {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("MindMapConnector:: "+s); }

    private Fen fen;
    private NodeFunction nodeView;
    private StructLink structLink;

    public AbstractNodeType2D mindMapNodeType;
    public AbstractNodeType2D normalNodeNodeType;

    public MindMapConnector(Fen fen, NodeFunction nodef) {
	this.fen = fen;
	this.nodeView = nodef;
	this.structLink = StructLink.create(fen.graph);
    }
    
    private NodeFunction mindMapForNode = 
	new NodeFunction() {
	    public Object f(ConstGraph a, Object node) {
		if (dbg) p("b: "+node);
		//return null;

		float x=0,y=0, w=100, h=100;
		return new View2D.Anchor(node,
					 x, y, 
					 w, h,
					 null);
	    }
	};

    
    public void addBuoys(VobScene vs, int parentCs,
			 BuoyViewMainNode mainNode,
			 BuoyLinkListener l) {
	if(!(mainNode instanceof AbstractMainNode2D)) return;
	AbstractMainNode2D mn2d = (AbstractMainNode2D)mainNode;
	DefaultVobMatcher m = (DefaultVobMatcher)vs.matcher;
	if(mn2d.getView2D() == normalNodeNodeType.getView2D()) {
	    if (dbg) p("canvas -> mindmap");

	    // Loop over the placed nodes' keys
            int containerCS = CanvasView2D.getContainerCS(vs, parentCs);

            for(Iterator i=m.getKeys(containerCS).iterator(); i.hasNext();) {
                Object node = i.next();
		if (RDFUtil.isNodeType(fen.graph, node, MINDSTRUCT.Data) &&
		    structLink.isLinked(node)) {

		    View2D.Anchor anchor = 
			(View2D.Anchor)mindMapForNode.f(fen.constgraph, node);
		    if(dbg) p("node: "+node+", anchor: "+anchor);
		    if(anchor == null) continue;

		    int cs =m.getCS(containerCS, node);
		    int culledCS = m.getCS(cs, "CULL");
		    if(culledCS > 0) cs = culledCS;

		    l.link(1, cs, mindMapNodeType,
			   new Pair(node, ((View2D.Anchor)anchor).plane), 
			   anchor);
		}
	    }

	} else if(mn2d.getView2D() == mindMapNodeType.getView2D()) {
	    if (dbg) p("mindmap -> outside");
	    
	    // see mindmapView2D for correct nodes..
	    for(Iterator i=m.getKeys(parentCs).iterator(); i.hasNext();) {
		Object node = i.next();
		p("key: "+node);
		
		if (!RDFUtil.isNodeType(fen.graph, node, MINDSTRUCT.Data)) continue;
		
		int cs =m.getCS(parentCs, node);
		p("cs: "+cs);
		Object plane = fen.
		    constgraph.find1_X11(CANVAS2D.contains, node);
		if(plane == null) {
		    if(dbg) p("No plane!");
		    continue;
		}
		Object anchor = getAnchor(plane, node);
		l.link(-1, cs, normalNodeNodeType,
		       new Pair(node, ((View2D.Anchor)anchor).plane),
		       anchor);
		
	    }
	} else if(dbg) p("Unknown view2d type");
    }

    public float mx = 1.1f, my = 1.1f;
    protected View2D.Anchor getAnchor(Object plane, Object node) {
	if (node == null) throw new Error("Impossible!");
	float x = RDFUtil.getFloat(fen.graph, node, CANVAS2D.x);
	float y = RDFUtil.getFloat(fen.graph, node, CANVAS2D.y);
	org.nongnu.libvob.lava.placeable.Placeable p = 
		(org.nongnu.libvob.lava.placeable.Placeable)nodeView.f(fen.constgraph, node);
	float w = p.getWidth();
	float h = p.getHeight();
	int bw = (int)(mx * w);
	int bh = (int)(my * h);
	return new View2D.Anchor(plane,
			    x-bw, y-bh, w+2*bw, h+2*bh,
			    node);
    }
}
