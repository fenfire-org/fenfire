/*
TTConnector.java
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
import java.util.Iterator;
import org.fenfire.*;
import org.fenfire.swamp.*;
import org.fenfire.view.*;
import org.fenfire.view.buoy.*;
import org.fenfire.util.*;
import org.fenfire.vocab.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.buoy.*;
import org.nongnu.libvob.impl.DefaultVobMatcher;

import org.nongnu.alph.*;

/** A Buoy connector for TreeTime.
 * XXX Generalize to general page / block relation!
 * Note that this connector gives a null anchor.
 * <p>
 * If a node's type is unrecognized, the next level will
 * be used. This avoids problems with deletion of planes -
 * no node will be deleted from treetime.
 * XXX NOT IMPLEMENTED
 */
public class TTConnector implements BuoyViewConnector {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("TTConnector:: "+s); }

    private Fen fen;
    private Object relation;

    private Object key = "TTConnection";

    public AbstractNodeType2D pageImageScrollNodeType;
    public AbstractNodeType2D normalNodeNodeType;

    public TTConnector(Fen fen, Object relation) {
	this.fen = fen;
	this.relation = relation;
    }


    private void addBuoy(VobScene vs, Object linkId,
			Object node, int direction,
			int index,
			BuoyLinkListener l) {
	String s = Nodes.toString(node);
	Object plane;
	BuoyViewNodeType nodeType;
	if(s.startsWith("vnd-storm-hash:application/pdf") ||
	   s.startsWith("vnd-storm-hash:application/postscript")) {
	    if(dbg) p("Got "+s+" was pagescroll " + direction);
	    plane = fen.alph.getScrollBlock(s);
	    nodeType = pageImageScrollNodeType;
	} else {
	    if(dbg) p("Got "+s+" was plane " + direction);
	    plane = node;
	    nodeType = normalNodeNodeType;
	}
	View2D.Anchor anchor = new View2D.Anchor(
		    plane,  0, 0, 400, 400, null);
	l.link(direction, -1, nodeType, linkId, anchor, index);
    }

    public void addBuoys(VobScene vs, int parentCs,
			 BuoyViewMainNode mainNode0,
			 BuoyLinkListener l) {
	if(!(mainNode0 instanceof AbstractMainNode2D)) return;
	AbstractMainNode2D mainNode = (AbstractMainNode2D)mainNode0;

	Object plane = mainNode.getPlane();
	// Assuming two possibilities:  either 
	// 1) a PageScrollBlock, or
	// 2) a canvas.
	
	Object node;
	
	if(plane instanceof PageScrollBlock) {
	    PageScrollBlock sb = (PageScrollBlock)plane;
	    if(dbg) p("Scrollblock: "+sb.getID());
	    node = Nodes.get(sb.getID());
	} else {
	    if(dbg) p("Plane: "+plane);
	    node = plane;
	}

	int index = 0;

	for(
	    Iterator ileft = fen.constgraph.findN_11X_Iter(node, relation);
	    ileft.hasNext();) {
	    Object linknode = ileft.next();
	    addBuoy(vs, 
		new Triple(this.key, node, linknode),
		linknode, -1, index, l);
	    index++;
	}
	
	index = 0;

	for(
	    Iterator iright = fen.constgraph.findN_X11_Iter(relation, node);
	    iright.hasNext();) {
	    Object linknode = iright.next();
	    addBuoy(vs, 
		new Triple(this.key, node, linknode),
		    linknode, 1, index, l);
	    index++;
	}

	

    }
}

