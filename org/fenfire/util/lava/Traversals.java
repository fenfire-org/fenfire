/*
Traversals.java
 *    
 *    Copyright (c) 2003, Tuukka Hastrup
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
 * Written by Tuukka Hastrup
 */

package org.fenfire.util.lava;

import org.fenfire.swamp.ConstGraph;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

/** This class contains utility methods for Swamp RDF graph traversal. 
 */
public class Traversals {
    /** Signals a search collision.
     */
    static class CollisionException extends Exception {
    }

    /** Return type for recurseDFS. 
     */
    static class DFSRet {
	public Object representative;
	public int degree;
    }

    /** Returns an iterator of nodes directly connected to a given node
     *  along a given non-directional property.
     */
    static public Iterator findConnected_Iter(ConstGraph g, Object node, 
				       Object property) {
	return concat(g.findN_X11_Iter(property, node),  // subjects
		      g.findN_11X_Iter(node, property)); // objects
    }

    /** Returns an iterator that iterates given iterators one after the other.
     *  XXX Not unit tested yet!
     */
    public static Iterator concat(final Iterator first, 
				  final Iterator second) {
	return new Iterator() {
		Iterator active = first;
		public boolean hasNext() {
		    if(active.hasNext())
			return true;
		    if(active != second)
			return second.hasNext();
		    return false;
		}
		public Object next() {
		    if(!active.hasNext() && active != second)
			active = second;
		    return active.next();
		}
		public void remove() {
		    active.remove();
		}
	    };
    }

    /** Finds a shortest path between the given nodes, along the given 
     *  nondirected property. Uses BFS.
     *  @return a list of nodes to visit to get from origin (excluded) 
     *          to the target (included). Or an empty list, if target 
     *          is origin. Or null, if there is no path.
     */
    public static List findShortestPath(Object a, Object property, Object b, 
					ConstGraph g) {
	if(a == b)
	    return Collections.EMPTY_LIST; // No path needed to itself

	Set active = new HashSet();
	Map visited = new HashMap(); // maps visited nodes to their parents

	// Initialize the search
	active.add(a);

	try {
	    while(!active.isEmpty()) {
		// Advance the search from one active set to the next
		Set activated = new HashSet();
		Iterator activenodes = active.iterator();
		while(activenodes.hasNext()) {
		    Object node = activenodes.next();
		    Iterator conns = findConnected_Iter(g, node, property);
		    while(conns.hasNext()) {
			Object found = conns.next();
			if(visited.containsKey(found))
			    continue; // Don't re-handle
			activated.add(found);
			visited.put(found, node);
			if(found == b) // If target reached
			    throw new CollisionException(); 
		    }
		}
		active = activated;
	    }
	} catch(CollisionException _) {
	    // Target found, now trace path backwards
	    List path = new LinkedList();
	    Object cur = b;
	    while (cur != a) {
		path.add(0, cur);
		cur = visited.get(cur);
		if(cur == null) // shouldn't happen
		    throw new Error("Broken path!");
	    }
	    return path;
	}

	// The search died out, so there is no path
	return null;
    }

    /** Tests whether a given nondirected property connects the two
     *  nodes given. The method is to run BFS from both nodes simultaneously
     *  and see whether they collide, hoping that one of them dies out
     *  quickly.
     */
    public static boolean isConnected(Object a, Object property, Object b, 
				      ConstGraph g) {
	if(a == b)
	    return true; // A node is always connected to itself
	Set visited1 = new HashSet();
	Set visited2 = new HashSet();
	Set active1 = new HashSet();
	Set active2 = new HashSet();

	// Initialize the searches
	active1.add(a);
	active2.add(b);

	while(!active1.isEmpty() && !active2.isEmpty()) {
	    try {
		// Advance both searches from one active set to the next
		active1 = iterateActive(active1.iterator(), g, property, 
					visited1, new HashSet(), visited2);
		active2 = iterateActive(active2.iterator(), g, property, 
					visited2, new HashSet(), visited1);
	    } catch (CollisionException _) {
		return true; // Collision means there is a connection
	    }
	}

	// One of the searches died out, so there is no connection
	return false;
    }

    /** Iterates active nodes to get the next active set. */
    static Set iterateActive(Iterator active, ConstGraph g, Object property, 
			   Set visited, Set activated, Set obstacles) 
	throws CollisionException {
	while(active.hasNext()) {
	    Object node = active.next();
	    
	    iterateConns(findConnected_Iter(g, node, property), visited, activated, obstacles);
	}
	return activated;
    }

    /** Iterates connections from some node to visit new nodes. 
        XXX Is this really reusable as it is ?-) */
    static void iterateConns(Iterator conns, Set visited, Set activated, 
		      Set obstacles) throws CollisionException {
	while(conns.hasNext()) {
	    Object found = conns.next();
	    if(obstacles.contains(found))
		throw new CollisionException();
	    if(!visited.contains(found)) {
		activated.add(found);
		visited.add(found);
	    }
	}
    }

    /** From given nodes, finds components disconnected along a given 
     *  non-directed property, 
     *  and for each component returns the node of highest degree as a 
     *  representative. Additionally, gives the representative of the 
     *  largest component. 
     *  @return an array: 
     *          the first element is a <code>Set</code> of representatives,
     *          the other element is the representative of largest component
     */
    public static Object[] findComponents(Iterator nodes, Object property, 
					  ConstGraph g) {
	return findComponents(nodes, property, g, null);
    }

    /** From given nodes, finds components disconnected along a given 
     *  non-directed property, 
     *  and for each component returns the node of highest degree as a 
     *  representative. Additionally, gives the representative of the 
     *  largest component. The representatives are tried to be found
     *  in the given candidate node set.
     *  @return an array: 
     *          the first element is a <code>Set</code> of representatives,
     *          the other element is the representative of largest component
     */
    public static Object[] findComponents(Iterator nodes, Object property, 
					  ConstGraph g, Set candidates) {
	Set visited = new HashSet();
	Set components = new HashSet();
	Object largest = null;
	int largestsize = -1;

	while(nodes.hasNext()) {
	    Object node = nodes.next();
	    if(!visited.contains(node)) { // If found a new component
		Object representative;
		int visitedsize = visited.size();
		visited.add(node);
		representative = recurseDFS(node, property, g, visited, 
					    candidates,
					    new DFSRet()).representative;
		int growth = visited.size() - visitedsize;
		if(growth > largestsize) {
		    largestsize = growth;
		    largest = representative;
		}
		if(candidates == null || representative != null)
		    components.add(representative);
		else
		    components.add(node);
	    }
	}
	return new Object[] {components, largest};
    }

    /** Recurses depth-first search from a given node, and finds the node
     *  of highest degree (with most connections).
     *  @return <code>DFSRet</code> object: 
     *          <code>representative</code> is the node of highest degree,
     *          <code>degree</code> is the highest degree
     */
    static DFSRet recurseDFS(Object start, Object property, ConstGraph g, 
			       Set visited, Set candidates, DFSRet ret) {
	Object representative = null;
	int representativeDegree = -1;
	Iterator conns = findConnected_Iter(g, start, property);
	int degree = 0;
	while(conns.hasNext()) {
	    Object found = conns.next();
	    degree++;
	    if(!visited.contains(found)) {
		visited.add(found);
		ret = recurseDFS(found, property, g, visited, candidates, ret);
		if((candidates == null || candidates.contains(found)) 
		   && ret.degree > representativeDegree) {
		    representativeDegree = ret.degree;
		    representative = ret.representative;
		}
	    }
	}
	if(representativeDegree > degree) {
	    ret.representative = representative;
	    ret.degree = representativeDegree;
	} else if (candidates == null || candidates.contains(start)) {
	    ret.representative = start;
	    ret.degree = degree;
	} else {
	    // Not a single node was a candidate, so we have to return null
	    ret.representative = null;
	    ret.degree = -1;
	}
	return ret;
    }
}
