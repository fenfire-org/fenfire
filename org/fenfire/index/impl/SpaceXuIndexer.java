/*
SpaceXuIndexer.java
 *    
 *    Copyright (c) 2003, Benja Fallenstein and Matti J. Katila and Tuomas J. Lukka
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
 * Written by Benja Fallenstein and Matti J. Katila and Tuomas J. Lukka
 */

package org.fenfire.index.impl;
import org.fenfire.*;
import org.fenfire.swamp.*;
import org.fenfire.vocab.*;
import org.fenfire.structure.Ff;
import org.fenfire.util.*;
import org.fenfire.index.*;
import org.nongnu.alph.*;
import java.util.*;


/** An index of xanadu links stored in a space.
 */
public class SpaceXuIndexer implements XuIndexer {
    public static final boolean dbg = true;
    protected static void pa(String s) { System.out.println(s); }

    protected Fen fen;
    protected Ff ff;

    protected MyIndex forwardIndex = new MyIndex(1),
                      backwardIndex = new MyIndex(2);


    public SpaceXuIndexer(Fen fen, Ff ff) {
        this.fen = fen;
	this.ff = ff;
    }

    protected class MyIndex implements Index {
        /** Return only links corresponding to nth cells on
         *  d.xu-link (count starts with 0) */
        int n;
	
	public MyIndex(int n) { this.n = n; }

        public Collection getMatches(Object o) {
            Collection res = new ArrayList();
	    Collection all =
		fen.enfiladeOverlap.getMatches(o);

	    /*
            for(Iterator i=all.iterator(); i.hasNext();) {
		Cell c = (Cell)i.next();
		if(c.s(link, -n) != null &&
		   c.s(link, -n-1) == null) {
                    res.add(getLinkForNode(c.h(link)));
		}
	    }
	    */


		// XXX ????
		

	    return res;
         }
    }

    public Index getForwardIndex() { return forwardIndex; }
    public Index getBackwardIndex() { return backwardIndex; }

    /*
    protected Object getLinkForNode(Object node) {
	Object res = linksByNode.get(node);
	if (res == null) {
	    if(ShortRDF.isNodeType(space.getModel(), node, ALPH.xuType)) {
		RDFNode from = 
		    ShortRDF.getObj(space.getModel(), 
				    node, ALPH.xuLinkFrom);
		RDFNode to = 
		    ShortRDF.getObj(space.getModel(), 
				    node, ALPH.xuLinkTo);
		res = new XuLink(texter.getEnfilade(from, null),
				 texter.getEnfilade(to, null));
		linksByNode.put(node, res);
		nodesByLink.put(res, node);
	    } else {
		return null;
	    }
	}
	return res;
    }
    */

    /**         
     *                                       ,--ALPH:xuLinkFrom -> enfilade 
     *      ALPH.xuType <--RDF:type --- id--.
     *                                       '--ALPH:xuLinkTo-> enfilade
     * 
     */
    public void add(XuLink l) {
	Object idnode = RDFUtil.N(fen.graph, CONTENTLINK.Link);

	Literal from = ff.literalFromEnfilade(l.from);
	Literal to = ff.literalFromEnfilade(l.to);

	fen.graph.add(idnode, CONTENTLINK.from, from);
	fen.graph.add(idnode, CONTENTLINK.to, to);

    }

    public void remove(XuLink l) {
	pa("Not implemented!!!!");
    }
}

