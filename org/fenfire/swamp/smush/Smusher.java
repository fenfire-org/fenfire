/*
Smusher.java
 *    
 *    Copyright (c) 2004, Benja Fallenstein
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

package org.fenfire.swamp.smush;
import org.fenfire.swamp.*;
import org.fenfire.swamp.impl.AllQuadsGraph;
import java.util.*;

public class Smusher {

    /** For now, be dumb and use fixed set of well-known IFPs.
     */
    static final Object[] IFPs = {
	Nodes.get("http://xmlns.com/foaf/0.1/mbox"),
	Nodes.get("http://xmlns.com/foaf/0.1/mbox_sha1sum"),
	Nodes.get("http://xmlns.com/foaf/0.1/homepage"),
	Nodes.get("http://xmlns.com/foaf/0.1/aimChatID"),
	Nodes.get("http://xmlns.com/foaf/0.1/icqChatID"),
	Nodes.get("http://xmlns.com/foaf/0.1/jabberID"),
	Nodes.get("http://xmlns.com/foaf/0.1/msnChatID"),
	Nodes.get("http://xmlns.com/foaf/0.1/weblog"),
	Nodes.get("http://xmlns.com/foaf/0.1/yahooChatID"),
    };


    public static void smush(QuadsGraph in, QuadsGraph out) {
	Map canon = canonicalizeNodes(new AllQuadsGraph(in, "foo"));

	for(Iterator iter=in.findN_AAAX_Iter(); iter.hasNext();) {
	    Object c = iter.next(); // context

	    for(Iterator i=in.findN_XAA1_Iter(c); i.hasNext();) {
		Object si = i.next();
		Object so = canon.get(si);
		if(so == null) so = si;
		
		for(Iterator j=in.findN_1XA1_Iter(si,c); j.hasNext();) {
		    Object pi = j.next();
		    Object po = canon.get(pi);
		    if(po == null) po = pi;
		    
		    for(Iterator k=in.findN_11X1_Iter(si,pi,c); k.hasNext();) {
			Object oi = k.next();
			Object oo = canon.get(oi);
			if(oo == null) oo = oi;
			
			out.add(so, po, oo, c);
		    }
		}
	    }
	}
    }


    /** Return a mapping from nodes to 'canonical' nodes.
     *  If we know that multiple nodes represent the same resource,
     *  this method will choose one of them as the 'canonical' one
     *  and map all of them to this canonical node.
     *  <p>
     *  If a node in the graph is not known to represent
     *  the same resource as any other node in the graph,
     *  it may or may not appear in the map; if it does,
     *  it will be mapped to itself.
     */
    public static Map canonicalizeNodes(ConstGraph g) {
	// The set of nodes that have IFPs
	Set nodes = new HashSet();

	Map byIFP = new HashMap();

	for(int i=0; i<IFPs.length; i++) {
	    Map m = new HashMap();

	    byIFP.put(IFPs[i], m);

	    for(Iterator j=g.findN_X1A_Iter(IFPs[i]); j.hasNext();) {
		Object n = j.next();
		nodes.add(n);
		
		for(Iterator k=g.findN_11X_Iter(n, IFPs[i]); k.hasNext();) {
		    Object val = k.next();
		    
		    Set s = (Set)m.get(val);
		    if(s == null) {
			s = new HashSet();
			m.put(val, s);
		    }

		    s.add(n);
		}
	    }
	}

	Map nodeSets = new HashMap();

	Comparator nodeComparator = new NodeComparator();

	for(Iterator i=nodes.iterator(); i.hasNext();) {
	    Object node = i.next();

	    Set equivNodes = (Set)nodeSets.get(node);
	    if(equivNodes == null) {
		equivNodes = new TreeSet(nodeComparator);
		equivNodes.add(node);
		nodeSets.put(node, equivNodes);
	    }

	    for(int p=0; p<IFPs.length; p++) {
		Map m = (Map)byIFP.get(IFPs[p]);

		for(Iterator j=g.findN_11X_Iter(node, IFPs[p]); j.hasNext();) {
		    Object val = j.next();
		    
		    Set s2 = (Set)m.get(val);
		    for(Iterator k=s2.iterator(); k.hasNext();) {
			Object equivNode = k.next();
			equivNodes.add(equivNode);
			nodeSets.put(equivNode, equivNodes);
		    }
		}
	    }
	}

	Map result = new HashMap();

	for(Iterator i=nodes.iterator(); i.hasNext();) {
	    Object node = i.next();

	    SortedSet equivNodes = (SortedSet)nodeSets.get(node);
	    
	    result.put(node, equivNodes.first());
	}

	return result;
    }


    private static class NodeComparator implements Comparator { 
	public int compare(Object m, Object n) {
	    return Nodes.toString(m).compareTo(Nodes.toString(n));

	    // don't need the below -- with our algorithm so far
	    // we only need to be able to sort nodes, not literals
	    /*
	    if(Nodes.isNode(m)) {
		if(Nodes.isNode(n))
		    return Nodes.toString(m).compareTo(Nodes.toString(n));
		else
		    return -1;
	    } else if(Nodes.isNode(n)) {
		return 1;
	    }
	    */
	}
    }
}
