/*
PaperMainView.java
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
import org.fenfire.view.management.*;
import org.fenfire.util.*;
import org.fenfire.swamp.*;
import org.fenfire.*;
import org.fenfire.vocab.*;
import org.fenfire.functional.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.lava.placeable.*;
import org.nongnu.libvob.vobs.*;
import org.nongnu.libvob.mouse.*;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.unit.*;
import org.nongnu.libvob.layout.component.*;

import java.util.*;

/** Show paper canvas view.
 */
public class PaperMainView extends AbstractDelegateLob {

    static public boolean dbg = false;
    private void p(String s) { System.out.println("PaperMainView:: "+s); }


    public Replaceable[] getParams() {
	throw new Error("unimplemented"); 
    }
    protected Object clone(Object[] i) {
	throw new Error("unimplemented"); 
    }


    Object paper;
    FServer f;
    final Model 
	zoom = new FloatModel(1),
	panX = new FloatModel(0),
	panY = new FloatModel(0);
    PureNodeFunction paperFunc;
    public PaperMainView(final FServer f, Object n) {
	this.paper = n;
	this.f = f;

	Fen[] fen = new Fen[1];
	f.environment.request("fen", fen, null);

	
	paperFunc = new CachedPureNodeFunction(2, fen[0].constgraph, 
	     new PureNodeFunction() {
		 public Object f(ConstGraph g, Object o) {
		     Lob lob = NullLob.instance;
		     lob = new PanZoomLob(drawPaper(g,o), panX, panY, zoom);
		     lob = new DragController(lob, 3, new RelativeAdapter() {
			     public void changedRelative(float dx, float dy) {
				 zoom.setFloat(zoom.getFloat() + dy/100);
				 f.getWindowAnimation().rerender();
			     }
			 });
		     lob = new DragController(lob, 1, new RelativeAdapter() {
			     public void changedRelative(float dx, float dy) {
				 panX.setFloat(panX.getFloat() - dx/zoom.getFloat());
				 panY.setFloat(panY.getFloat() - dy/zoom.getFloat());
				 f.getWindowAnimation().rerender();
			     }
			 });
		     lob = new Between(new Label("This is a zoomable area (mouse button 3).  You can pan the paper with mouse button 1."), 
				       lob, NullLob.instance);
		     return lob;
		 }
	     });
    }

    public Lob getDelegate() { 
	Fen[] fen = new Fen[1];
	f.environment.request("fen", fen, null);
	Lob l = (Lob) paperFunc.f(fen[0].graph, paper); 
	return l;
    }
    
    
    private Lob drawPaper(ConstGraph g, Object container) {
	if (!RDFUtil.isNodeType(g, container, CANVAS2D.Canvas))
	    throw new Error("Paper node is NOT a container! " + container);
	
	Tray nodes = new Tray(false);
	
	Iterator iter = g.findN_11X_Iter(container, 
					 CANVAS2D.contains);
	while (iter.hasNext()) {
	    Object n = iter.next();
	    p("paper: "+n);
	    float x_ = RDFUtil.getFloat(g, n, CANVAS2D.x);
	    float y_ = RDFUtil.getFloat(g, n, CANVAS2D.y);
	    
	    final Model x = new FloatModel(x_);
	    final Model y = new FloatModel(y_);
	    
            //# draw text etc..
	    Lob l = new Label(n.toString().substring(0, 12));
	    l = new Frame(l, Theme.lightColor, 
			  Theme.darkColor, 3, 3, 
			  true, true, false);
	    l = new TranslationLob(l, x, y);
	    nodes.add(l, n);
	}

	return nodes;
    }

}
