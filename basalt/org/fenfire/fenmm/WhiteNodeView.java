/*
WhiteNodeView.java
 *    
 *    Copyright (c) 2003, Matti J. Katila
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

package org.fenfire.fenmm;

import org.fenfire.functional.PureNodeFunction;
import org.fenfire.functional.NodeFunction;
import org.fenfire.swamp.ConstGraph;
import org.nongnu.libvob.lava.placeable.TextPlaceable;
import org.nongnu.libvob.VobScene;
import org.nongnu.libvob.gl.GLRen;
import org.nongnu.libvob.gl.Paper;
import org.nongnu.libvob.gl.SpecialPapers;

import java.awt.Color;

/**  
 * Translucent rectangular background for TextNodeView.
 * XXX: Must be placed before TextPlaceable node function.
 */
public class WhiteNodeView implements PureNodeFunction {

    private Paper paper;
    private GLRen.FixedPaperQuad pq;
    private NodeFunction nodef;
    
    public WhiteNodeView(NodeFunction nodef, Color color) {
        paper = SpecialPapers.selectionPaper(color);
        pq = GLRen.createFixedPaperQuad(paper, 0, 0, 1, 1, 0, 10, 10, 10);

	this.nodef = nodef;
    }
    public Object f(ConstGraph g, Object node) {
	final TextPlaceable p = (TextPlaceable) nodef.f(g, node);
	final Object key = node;

	return new org.nongnu.libvob.lava.placeable.TextPlaceable() {
		public void place(VobScene vs, int cs) {
		    int cs2 = vs.orthoCS(cs, key,0, 0,0, p.getWidth(), p.getHeight());
		    vs.put(pq, cs2);
		    p.place(vs, cs);
		}
		
		public float getWidth() { return p.getWidth(); }
		public float getHeight() { return p.getHeight(); }
		public void getCursorXYY(int pos, float[] xy) { p.getCursorXYY(pos, xy); }
		public int getCursorPos(float x, float y) { return p.getCursorPos(x, y); }
	    };
    }
}
