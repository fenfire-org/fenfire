/*
NodeType2DFull.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka and Benja Fallenstein
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
 * Written by Tuomas J. Lukka and Benja Fallenstein
 */

package org.fenfire.view.buoy;
import org.fenfire.view.View2D;
import org.nongnu.libvob.*;
import org.nongnu.libvob.impl.DefaultVobMatcher;
import org.nongnu.libvob.buoy.*;

import java.awt.Rectangle;
import java.awt.Color;
import java.awt.event.MouseEvent;

/** A buoy view node type showing the entire view2d 
 * in a buoy.
 */
public class NodeType2DFull extends AbstractNodeType2D {
    public static boolean dbg = false;
    private static void pa(String s) { System.out.println("NodeType2DFull:: "+s); }

    public static int effigy = 0;

    public NodeType2DFull(View2D view2d, AbstractMainNode2D.Factory factory) {
	super(view2d, factory);
    }

    public Object getSize(Object linkId, Object anchor,
			  float wh[]) {
	View2D.Anchor a = (View2D.Anchor)anchor;
	view2d.getSize(a.plane, wh);
	return null;
    }

    float [] tmpSize = new float[2];
    float [] tmpSqSize = new float[2];
    public int renderBuoy(VobScene vs, int into, float w, float h, Object linkId,
			  Object anchor, Object cachedSize) {
	if (dbg) pa("renderBuoy");
	if(effigy != 0) {
	    Vob eff = org.nongnu.libvob.gl.GLRen.createQuad(effigy-1, 0,0);
	    vs.map.put( eff, into);
	    return into;
	}
	View2D.Anchor a = (View2D.Anchor)anchor;

	view2d.getSize(a.plane, tmpSize);

	
	float scale1 = tmpSize[0] / w;
	float scale2 = tmpSize[1] / h;
	float scale = (scale1 < scale2 ? scale1 : scale2);

	int box2paper = vs.coords.orthoBox(0, 0, 0, 0, 
					   scale, scale, 
					   w, h);
	vs.matcher.add(into, box2paper, "BOX2PAPER");

	view2d.render(vs, a.plane, into, into, box2paper);

	if (dbg) pa("renderBuoy..DONE");
	return vs.coords.translate(into, 
				   (a.x+a.w/2f) / scale, 
				   (a.y+a.h/2f) / scale);
    }
}
