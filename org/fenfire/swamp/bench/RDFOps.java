/*
RDFOps.java
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

package org.fenfire.swamp.bench;
import org.fenfire.swamp.*;
import org.fenfire.swamp.impl.*;
import org.fenfire.util.*;

/** Benchmark methods for various RDF operations.
 * For use from jython: set the attributes and then call
 * the method.
 */
public class RDFOps {

    public int nrounds;

    public int circleSize;

    public Graph graph;
    URN5Namespace namespace = new URN5Namespace();
    
    Object[] nodes;

    public RDFOps(int nnodes) {
	nodes = new Object[nnodes];
	for(int i=0; i<nnodes; i++)
	    nodes[i] = Nodes.get(namespace.generateId());
    }

    public void traverseCircle_prepare() {
	// Make a circle
	for(int i=0; i<circleSize; i++) 
	    graph.add(nodes[i+1], nodes[0], nodes[i+2]);
	graph.add(nodes[circleSize+1], nodes[0], nodes[1]);
    }

    public void traverseCircle_find1() {
	Object node = nodes[1];
	for(int i=0; i<nrounds; i++) {
	    node = graph.find1_11X(node, nodes[0]);
	    if(node == null) throw new NullPointerException();
	}
    }

}
