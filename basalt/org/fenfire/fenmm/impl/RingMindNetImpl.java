/*
RingMindNetImpl.java
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

package org.fenfire.fenmm.impl;
import org.fenfire.fenmm.*;
import org.fenfire.swamp.*;
import org.fenfire.util.lava.Traversals;
import java.util.*;

public class RingMindNetImpl implements RingMindNet {
    static private void p(String s) { System.out.println("RingMindNetImpl:: "+s); }
    static public boolean dbg = false;

    protected ConstGraph g;
    protected Object center = null;
    protected Map nodes = new HashMap();
    protected List leafs = new ArrayList();
    public int getLeafCount() { return getLeafs().size(); }
    public List getLeafs() { return leafs; }
    private void recursivelyGetLeafs(int depth, int start, int end) {
	if (depth >= rings.size()) return;
	
	for (int i=start; i<end; i++) {
	    if (dbg) p("depth: "+depth+", index: "+i);
	    MNode node = (MNode)getNode(getRing(depth).get(i));
	    if (node.hasChilds()) {
		if (false && depth+1 == rings.size()-2) {
		    int f = node.first;
		    int l = node.last;
		    int med = f + (l-f)/2;
		    recursivelyGetLeafs(depth+1, med, med+1);
		} else {
		    recursivelyGetLeafs(depth+1, node.first, node.last);
		}
	    } else {
		leafs.add(node.getNode());
		//p(" found!");
	    }
	}
    }

    public RingMindNetImpl(ConstGraph g) {
	this.g = g;
    }

    Object property = org.fenfire.vocab.STRUCTLINK.linkedTo;


    /* index 0 is depth 1, idnex 1 is depth 2...
     */
    List rings = new ArrayList();

    public Node getNode(Object node) { return (Node) nodes.get(node); }
    public class MNode implements Node {
	Object node;
	int parentIndex, depth;
	public int first, last; // index in rings of childs.
	MNode(Object node, int parentIndex, int depth) {
	    this.node = node;
	    this.parentIndex = parentIndex;
	    this.depth = depth;
	}
	public Object getParent() {
	    if (node == center) return null;
	    return getRing(getDepth()-1).get(parentIndex());
	}
	public Object getNode() { return node; }
	public int parentIndex() { return parentIndex; }
	public int getDepth() { return depth; }
	public boolean hasChilds() { return (last-first) > 0; }
	public int getFirst() { return first; }
	public int getLast() { return last; }
    }

    public int maxDepth(){ return rings.size(); }


    
    private int addLinks(Object node, int depth, 
			 int parentIndex, Iterator i) {
	if (depth < 1) throw new Error("depth error"+depth);

	while (depth + 1 > rings.size())
	    rings.add(new ArrayList());


	if (dbg) p("how many?"+maxDepth());

	List ring = (List) rings.get(depth);
	int first = ring.size();
	int last = first;
	while (i.hasNext()) {
	    Object link = i.next();
	    if (dbg) p("link"+link);
	    if (nodes.keySet().contains(link)) {
		continue;
	    } else {
		ring.add(link);
		nodes.put(link, (new MNode(link, parentIndex, depth)));
		last++;
	    }
	}

	MNode n = (MNode) getNode(node);
	n.first = first;
	n.last = last;

	return last-first;
    }

    public List getRing(int depth) {
	return (List) rings.get(depth);
    }

    public void constructNet(Object centerNode) {
	rings.clear();
	nodes.clear();
	leafs.clear();
	this.center = centerNode;

	rings.add(Collections.singletonList(center));

	// create center
	nodes.put(center, new MNode(null, 0, 0)); 
	for (int depth=0; depth<maxDepth(); depth++) {
	    if (dbg) p("depth"+depth);
	    for (Iterator i=getRing(depth).iterator(); i.hasNext();) {
		if (dbg) p("it");
		Object node = i.next();
		int links = addLinks(node, depth + 1, getRing(depth).indexOf(node),
				     Traversals.findConnected_Iter(g, node, property));
		/*
		if (links == 0) {
		    leafs.add(node);
		    //p("leaf!: "+node);
		}
		*/
	    }
	}

	leafs.clear();
	recursivelyGetLeafs(1,0, getRing(1).size());
    }


    public Iterator iterator(final Object node) {
	return  Traversals.findConnected_Iter(g, node, property);
	/*
	return new Iterator() {
		public boolean hasNext() {
		    Object parent = 
			getRing(n.getDepth()-1).get(n.getParentIndex());

		    Object ret = i.next();
		    if (ret == parent) return next();

		    if(active.hasNext())
			return true;
		    if(active != second)
			return second.hasNext();
		    return false;
		}
		public Object next() {
		    if (n.getDepth() <= 0) return i.next();

		    Object parent = 
			getRing(n.getDepth()-1).get(n.getParentIndex());

		    Object ret = i.next();
		    if (ret == parent) return next();
		    return ret;
		}
		public void remove() {
		    ;
		}
	    };
	*/
    }
    public Iterator iterateAllNodes() {
	List l = new ArrayList();
	for (Iterator i=rings.iterator(); i.hasNext();) {
	    List ring = (List) i.next();
	    l.addAll(ring);
	}
	return l.iterator();
    }
}
