/*
RingMindNet.java
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
 *    You should have received a copy of the GNU Lesser General
 *    Public License along with Fenfire; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 */
/*
 * Written by Matti J. Katila
 */

/** RingMindNet stores the network of MindMap connections.
 */
package org.fenfire.fenmm;

import java.util.*;

public interface RingMindNet {

    static interface Node {
	Object getNode();
	int parentIndex();
	int getDepth();
	Object getParent();
	boolean hasChilds();
	/* side of neightbour. positive is clockwise.
	Object getNeightbour(int side);
	 */
	int getFirst();
	int getLast();
    }

    Node getNode(Object node);


    /**
     * <pre>
     *  center -- a --b
     *        \      /
     *          ---c 
     * </pre>
     * example returns b and c leafs computed to summ, i.e., two.
     */
    int getLeafCount();

    /** @return All leafs of this mind map.
     */
    List getLeafs();



    void constructNet(Object centerNode);

    /** Check if node a has been marked to be linked with node b.
     * @param a The first node.
     * @param b The second node.
     * @return True if node a is marked to be linked with node b,
     *         orherwise false.
     */
    //boolean hasBeenLinked(Object a, Object b);

    /** Mark node a been linked bidirectionally with node b.
     * @param a The first node.
     * @param b The second node.
     */
    //void link(Object a, Object b);

    Iterator iterator(Object a);

    List getRing(int depth);

    
    int maxDepth();


    Iterator iterateAllNodes();

}
