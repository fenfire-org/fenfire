/*
MMNode.java
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

/** MMNode structure stores a Node and its related MM spesific information.
 * The drawing depth is quite important factor in FenMM structure view.
 * Since nodes are not necessarily drawn in depth order, it should be
 * stored with node.
 */
package org.fenfire.fenmm;

public class MMNode {

    public final int depth;
    public final Object object;

    public MMNode(Object object, int depth) {
	this.object = object;
	this.depth = depth;
    }

    public Object getObject() { return object; }
    public int getDepth() { return depth; }
}
