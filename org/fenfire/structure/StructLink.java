/*
StructLink.java
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

package org.fenfire.structure;
import java.util.Iterator;
import org.fenfire.vocab.RDF;
import org.fenfire.vocab.STRUCTLINK;
import org.fenfire.swamp.*;
import java.util.*;

/** Some utility methods for handling StructLink.
 * XXX Should these be static or with the graph and constgraph here?
 * Or both?
 * How often will we change Graph / ConstGraph objects? Will we
 * always recreate everything?
 * <p>
 * The constructors are hidden so that we may cache the instances for each graph.
 */
public class StructLink {

    ConstGraph constGraph;
    Graph graph;

    private StructLink(ConstGraph g) {
	this.constGraph = g;
    }

    private StructLink(Graph g) {
	this.graph = g;
	this.constGraph = g;
    }

    /** Create a new StructLink.
     */
    static public StructLink create(ConstGraph g) {
	return new StructLink(g);
    }
    static public StructLink create(Graph g) {
	return new StructLink(g);
    }

    /** Copy the iterator into a set, then return an iterator 
     * into the set.
     * XXX Generalize into utility routine
     */
    private Iterator copyIterator(Iterator it) {
	Set s = new HashSet();
	while(it.hasNext()) s.add(it.next());
	return s.iterator();
    }

    /** Remove all structlink associations of the given node.
     */
    public void detach(Object node) {
        Iterator it = copyIterator(
			graph.findN_11X_Iter(node, STRUCTLINK.linkedTo));
        while(it.hasNext()) {
            Object other = it.next();
            detach(node, 1, other);
        }
        it = copyIterator(
			graph.findN_X11_Iter(STRUCTLINK.linkedTo, node));
        while(it.hasNext()) {
            Object other = it.next();
            detach(node, -1, other);
        }
    }

    /** Detach the two nodes.
     * Throws an error if not associated.
     */
    public void detach(Object node1, int side, Object node2) {
	if(side > 0)
	    detach(node1, node2);
	else
	    detach(node2, node1);
    }

    /** Detach the two nodes (directional!).
     * Throws an error if not associated.
     * This is not symmetric: detach(n1, n2) is different from detach(n2, n1)
     */
    public void detach(Object node1, Object node2) {
	graph.rm_111(node1, STRUCTLINK.linkedTo, node2);
    }

    /** Associate the given nodes.
     * A node cannot be associated to itself - will return if
     * this is tried.
     * If the nodes are already associated, does nothing.
     */
    public void link(Object node1, int side, Object node2) {
	if(side > 0) 
	    link(node1, node2);
	else
	    link(node2, node1);
    }

    /** Associate the given nodes.
     * A node cannot be associated to itself - will return if
     * this is tried.
     * If the nodes are already associated, does nothing.
     */
    public void link(Object node1, Object node2) {
	if(node1 == node2) return;
	graph.add(node1, STRUCTLINK.linkedTo, node2);
    }

    /** Get an iterator over the associations of the given node.
     */
    public Iterator getLinks(Object node, int side) {
	if(side > 0) 
	    return constGraph.findN_11X_Iter(node, STRUCTLINK.linkedTo);
	else
	    return constGraph.findN_X11_Iter(STRUCTLINK.linkedTo, node);
    }

    /** Returns true if the node is has a connection in either direction.
     */
    public boolean isLinked(Object node) {
        Iterator it = graph.findN_11X_Iter(node, STRUCTLINK.linkedTo);
        if (it.hasNext()) return true;
        it = graph.findN_X11_Iter(STRUCTLINK.linkedTo, node);
        if (it.hasNext()) return true;
        return false;
    }

}
