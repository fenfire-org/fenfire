/*
ContextNodeView.java
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


package org.fenfire.swamp;
import org.fenfire.functional.*;
import org.nongnu.libvob.*;
import java.util.*;

/** This node function is multiplexer for two nodefunctions.
 * Multiplexer definitely is *not* pure node function but 
 * it's good in i.e. focusing nodes.
 * Multiplexing is done if set of nodes contains the function node.
 */
public class MultiplexerNodeFunction implements NodeFunction {

    final private NodeFunction unknown;
    final private NodeFunction known;
    private Set multiplexingNodes = new HashSet();

    /** All functions must be pure node functions.
     * @param unknownNodes Node function for nodes which are not in set of known nodes.
     * @param knownNodes Node function for nodes which are in set of known nodes.
     * @see setMultiplexerNodes
     */
    public MultiplexerNodeFunction(NodeFunction unknownNodes,
				   NodeFunction knownNodes) {
	this.unknown = unknownNodes;
	this.known = knownNodes;
    }

    public void setMultiplexerNodes(Set nodes) {
        this.multiplexingNodes = nodes;
    }

    public Object f(ConstGraph g, Object node) {
	if (multiplexingNodes.contains(node))
	    return known.f(g, node);
	return unknown.f(g, node);
    }
}
