/*
MindNet.java
 *    
 *    Copyright (c) 2003, Matti J. Katila, Asko Soukka
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
 * Written by Matti J. Katila, Asko Soukka
 */

/** MindNet stores drawn MindMap connections into a HashMap. 
 * Connections are stored bidirectionally. Connections are
 * restored to avoid drawing the same connection twice and drawing
 * the filleting between right nodes.
 */
package org.fenfire.fenmm;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.Integer;

public class MindNet {

    private final static int INITIAL_CAPACITY = 10;

    private final HashMap net;

    private final HashMap data;

    public MindNet() {
	net = new HashMap();
	data = new HashMap();
    }

    /** Check if node a has been marked to be linked with node b.
     * @param a The first node.
     * @param b The second node.
     * @return True if node a is marked to be linked with node b,
     *         orherwise false.
     */
    public boolean hasBeenLinked(Object a, Object b) {
        if (net.get(a) == null) return false;
	Iterator i = ((ArrayList)net.get(a)).iterator();
        while (i.hasNext()) if (i.next() == b) return true;
        return false;
    }

    /** Mark node a been linked bidirectionally with node b.
     * @param a The first node.
     * @param b The second node.
     */
    public void link(Object a, Object b) {
	linkImpl(a, b);
	linkImpl(b, a);
    }

    public Iterator iterator() { return net.keySet().iterator(); }
    public Iterator iterator(Object a) { 
        if (net.get(a) == null) return null;
	else return ((ArrayList)net.get(a)).iterator();
    }

    private void linkImpl(Object a, Object b) {
        if (net.get(a) == null) net.put(a, new ArrayList(INITIAL_CAPACITY));
        ((ArrayList)net.get(a)).add(b);
    }

    public Object put(Object a, MMPlace pl, int depth) {
	return data.put(a, new MMNode(pl, depth));
    }
    
    public MMNode get(Object a) {
        if (data.get(a) == null) return null;
	return (MMNode)data.get(a);
    }

    public MMPlace getPlace(Object a) {
        if (data.get(a) == null) return null;
	return (MMPlace)((MMNode)data.get(a)).object;
    }

    public int getDepth(Object a) {
        if (data.get(a) == null) return -1;
	return ((MMNode)data.get(a)).depth;
    }
}
