/*
TransclusionConnector.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka
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
 * Written by Tuomas J. Lukka
 */


package org.fenfire.view.buoy;
import org.nongnu.libvob.*;
import org.nongnu.libvob.buoy.*;
import org.nongnu.libvob.impl.DefaultVobMatcher;
import org.nongnu.alph.*;
import org.fenfire.*;
import org.fenfire.swamp.*;
import org.fenfire.functional.*;
import org.fenfire.view.*;
import org.fenfire.view.buoy.*;
import org.fenfire.util.*;
import org.fenfire.vocab.*;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

/** Hang transclusion buoys.
 */
public class TransclusionConnector implements BuoyViewConnector {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("TransclusionConnector:: "+s); }


    private Fen fen;

    public AbstractNodeType2D pageImageScrollNodeType;
    public AbstractNodeType2D normalNodeNodeType;

    protected NodeFunction nodeView;
    protected Function textFunction;

    // Function: node -> View2D.Anchor on the scrollblock node
    public static class SB4Node implements PureNodeFunction {
	NodeFunction txtfunc;
	public SB4Node(NodeFunction txtfunc) {
	    this.txtfunc = txtfunc;
	}
	public Object f(ConstGraph g, Object node) {
	    Enfilade1D enf = (Enfilade1D)txtfunc.f(g, node);
	    List l = enf.getList();
	    if(l.size() < 1) return null;

	    PageImageSpan sp;
	    try {
		sp = (PageImageSpan)l.get(0);
	    } catch(ClassCastException _) {
		return null;
	    }

	    int x = sp.getLocation().x, 
		y = sp.getLocation().y, 
		w = sp.getSize().width, 
		h = sp.getSize().height;

	    PageScrollBlock b = (PageScrollBlock)sp.getScrollBlock();
	    for(int i=0; i<sp.getPageIndex(); i++)
		x += b.getPage(i).getSize().width;

	    return new View2D.Anchor(b, x,y,w,h, null);
	}
    }

    /** A link id, for reaching the node from the buoy.
     */
    public class LinkId {
	public LinkId(Object node, PageScrollBlock scrollBlock) {
	    this.node = node;
	    this.scrollBlock = scrollBlock;
	}
	/** The Fen node that contains the transclusion.
	 */
	public final Object node;
	/** The scrollblock from which the transclusion comes.
	 */
	public final PageScrollBlock scrollBlock;

	public int hashCode() {
	    return 
		(node.hashCode()*317501) ^ 
		(scrollBlock.hashCode()*1941);
	}

	public boolean equals(Object o) {
	    if(!(o instanceof LinkId)) return false;
	    LinkId p = (LinkId)o;
	    // Nodes can be compared with ==
	    return node == p.node && scrollBlock.equals(p.scrollBlock);
	}

    }

    Function scrollBlockForNode ;


    /** Create a new TransclusionConnector.
     * @param functional The Functional instance to use for
     * 			creating functions needed inside this class
     * @param textFunction The node function: node to enfilade
     * 			for getting the contents of nodes.
     * @param nodeView A NodeFunction: node to placeable, for getting
     * 			the real sizes of nodes. 
     */
    public TransclusionConnector(Fen fen, Functional functional,
	    FunctionInstance textFunction,
	    NodeFunction nodeView) {
	this.fen = fen;
	this.textFunction = textFunction.getCallableFunction();

	this.scrollBlockForNode = 
	    functional.createFunctionInstance(
		    "node2scrollBlockAnchor",
		    SB4Node.class,
		    new Object[] {
			textFunction
		    }).getCallableFunction();

	this.nodeView = nodeView;
    }

    public void addBuoys(VobScene vs, int parentCs,
			 BuoyViewMainNode mainNode,
			 BuoyLinkListener l) {
	if(!(mainNode instanceof AbstractMainNode2D)) return;
	AbstractMainNode2D mn2d = (AbstractMainNode2D)mainNode;
	DefaultVobMatcher m = (DefaultVobMatcher)vs.matcher;
	if(dbg) p("Trying transclusion addbuoys");
	if(mn2d.getView2D() == normalNodeNodeType.getView2D()) {
	    if (dbg) p("TransclusionConnector: canvas -> scrolls");

	    // Loop over the placed nodes' keys
            int containerCS = CanvasView2D.getContainerCS(vs, parentCs);

            for(Iterator i=m.getKeys(containerCS).iterator(); i.hasNext();) {
                Object node = i.next();

	        View2D.Anchor anchor = (View2D.Anchor)this.scrollBlockForNode.f(node);
		if(dbg) p("node: "+node+", anchor: "+anchor);
		if(anchor == null) continue;

		int cs =m.getCS(containerCS, node);
		int culledCS = m.getCS(cs, "CULL");
		if(culledCS > 0) cs = culledCS;

		l.link(1, cs, pageImageScrollNodeType,
		    new LinkId(node, 
			((PageScrollBlock)((View2D.Anchor)anchor).plane)), 
			    anchor);

	    }

	} else if(mn2d.getView2D() == pageImageScrollNodeType.getView2D()) {
	    if (dbg) 
		p("TransclusionConnector: Pagescroll -> outside");
	    for(Iterator i=m.getKeys(parentCs).iterator(); i.hasNext();) {
		Object key = i.next();
		if(!(key instanceof PageImageSpan)) continue;
		PageImageSpan span = (PageImageSpan)key;

		if(dbg) p("span "+key+", point: "+span.getLocation()+ ", size: "+span.getSize());
		int cs =m.getCS(parentCs, key);

		Enfilade1D tmpEnf = fen.enfMaker.makeEnfilade(span);
		for(Iterator nodes = fen.enfiladeOverlap.getMatches(tmpEnf).iterator(); nodes.hasNext(); ) {
		    Object node = nodes.next();

		    // get all pageimage spans and make anchor cs for them.
		    // dumb version first
		    Enfilade1D enf = (Enfilade1D)textFunction.f(node);
		    PageImageSpan img = (PageImageSpan)enf.getList().get(0);
		    if (dbg) p("point: "+img.getLocation()+ ", size: "+img.getSize());
		    Point p = img.getLocation();
		    Dimension s = img.getSize();

		    int enfAnchorCS = vs.coords
			.orthoBox(cs, 0f, (float)p.getX(), (float)p.getY(), 1,1, 
				  (float)s.getWidth(), (float)s.getHeight());

		    if(dbg) p("TRC: overlap "+node);
		    Object plane = fen.constgraph.find1_X11(
			    CANVAS2D.contains, node);
		    if(plane == null) {
			if(dbg) p("No plane!");
			continue;
		    }
		    Object anchor = getAnchor(plane, node);
		    l.link(-1, enfAnchorCS, normalNodeNodeType,
			    new LinkId(node, 
				(PageScrollBlock)span.getScrollBlock()),
			    anchor);

		}
	    }
	} else {
	    if(dbg) p("TransclusionConnector: Unknown view2d type");
	}
    }

    public float mx = 1.1f, my = 1.1f;
    protected View2D.Anchor getAnchor(Object plane, Object node) {
	if (node == null) throw new Error("Impossible!");
	float x = RDFUtil.getFloat(fen.graph, node, CANVAS2D.x);
	float y = RDFUtil.getFloat(fen.graph, node, CANVAS2D.y);
	org.nongnu.libvob.lava.placeable.Placeable p = 
		(org.nongnu.libvob.lava.placeable.Placeable)nodeView.f(
						fen.constgraph, node);
	float w,h;
	if(p != null) {
	    w = p.getWidth();
	    h = p.getHeight();
	} else {
	    w = 0;
	    h = 0;
	}
	int bw = (int)(mx * w);
	int bh = (int)(my * h);
	return new View2D.Anchor(plane,
			    x-bw, y-bh, w+2*bw, h+2*bh,
			    node);
    }
}
