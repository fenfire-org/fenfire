/*
NodeType2D.java
 *    
 *    Copyright (c) 2003 by Benja Fallenstein
 *    
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
 * Written by Benja Fallenstein 
 */
package org.fenfire.view.buoy;
import org.fenfire.view.View2D;
import org.fenfire.view.CanvasView2D;
import org.fenfire.view.NodedView2D;
import org.fenfire.util.Triple;
import org.nongnu.libvob.*;
import org.nongnu.libvob.impl.DefaultVobMatcher;
import org.nongnu.libvob.buoy.*;

import java.awt.Rectangle;
import java.awt.Color;
import java.awt.event.MouseEvent;

/** A BuoyViewNodeType showing areas of 2D planes rendered through
 *  a View2D.
 */
public class NodeType2D extends AbstractNodeType2D {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("NodeType2D:: "+s); }

    public static int effigy = 0;
    

    public NodeType2D(View2D view2d, AbstractMainNode2D.Factory factory) {
	super(view2d, factory);
    }
    
    public Object getSize(Object linkId, Object a,
			  float wh[]) {
	View2D.Anchor anchor = (View2D.Anchor) a;
	wh[0] = anchor.w;
	wh[1] = anchor.h;
	return null;
    }

    public int renderBuoy(VobScene vs, 
			    int into, 
			    float w, float h,
			    Object linkId,
			  Object a, Object cachedSize) {
	if (dbg) p("start renderBuoy");
	if(effigy != 0) {
	    Vob eff = org.nongnu.libvob.gl.GLRen.createQuad(effigy-1, 0,0);
	    vs.map.put( eff, into);
	    return into;
	}
	View2D.Anchor anchor = (View2D.Anchor)a;

	if (dbg) p("rect "+anchor+" coords "+w+","+h);

	float scale = w / anchor.w;
	if(scale < h / anchor.h) scale = h / anchor.h;

	int box2paper = vs.coords.orthoBox(0, 0, anchor.x, anchor.y, 
					   1/scale, 1/scale, 
					   w, h);
//	int box2paper = vs.coords.orthoBox(0, 0, 0, 0, 
//					   1, 1, 
//					   w, h);
	vs.matcher.add(into, box2paper, "BOX2PAPER");

	view2d.render(vs, anchor.plane, into, into, box2paper);

	if (dbg) p("start renderBuoy..DONE");

	// anchor.node may be null if this
	// is not a noded View2D
	
	int box = into;
	if (anchor.node != null ) {
	    int containerCS = CanvasView2D.getContainerCS(vs, into);
	    box = vs.matcher.getCS(containerCS, anchor.node);
	    if (dbg) p("box is: "+box);
	}
	// May happen due to superlaziness, if the other node wasn't
	// rendered.
	if (box < 0) {
	    box = into;
	}
	return vs.coords.unitSq(box);
    }
}
