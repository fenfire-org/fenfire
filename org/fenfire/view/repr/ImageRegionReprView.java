/*
ImageRegionReprView.java
 *    
 *    Copyright (c) 2003-2005, Benja Fallenstein
 *                  2005, Matti J. Katila
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
 *
 */
/*
 * Written by Benja Fallenstein and Matti J. Katila
 */
package org.fenfire.view.repr;
import org.fenfire.view.*;
import org.fenfire.swamp.*;
import org.fenfire.vocab.*;
import org.fenfire.util.*;
import org.fenfire.spanimages.*;
import org.nongnu.libvob.lob.*;
import java.util.*;


/** see http://www.bnowack.de/w3photo/pages/image_vocabs
 */
public class ImageRegionReprView extends ReprView.AbstractLobView {
    static private void p(String s) { System.out.println("ImgRegionReprView:: "+s); }

    private Graph graph;

    public ImageRegionReprView(Graph graph) {
	this.graph = graph;
    }

    public ViewSettings.Type TYPE = new ViewSettings.AbstractType() {
	    public boolean containsNode(Object node) {
		return RDFUtil.isNodeType(graph, node, IMAGE.Region) &&
		    !(graph.find1_11X(node, IMAGE.regionOf) instanceof Literal);
	    }
	};

    public Set getTypes() {
	return Collections.singleton(TYPE);
    }

    public Lob getLob(Object node) {
	Object src = graph.find1_11X(node, IMAGE.regionOf);
	float x0 = RDFUtil.getFloat(graph, node, FF.startX);
	float y0 = RDFUtil.getFloat(graph, node, FF.startY);
	float x1 = RDFUtil.getFloat(graph, node, FF.endX);
	float y1 = RDFUtil.getFloat(graph, node, FF.endY);
	
	if (src instanceof Literal)
	    throw new IllegalArgumentException();

	return ImagePool.region(src, x0,y0,x1,y1);
    }
}
