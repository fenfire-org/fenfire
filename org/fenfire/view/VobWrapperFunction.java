/*
VobWrapperFunction.java
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

package org.fenfire.view;
import org.fenfire.swamp.*;
import org.fenfire.functional.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.vobs.*;

/** A node function which adds a vob into placeable returned by another node function.
 * A new placeable, which consist of a vob and placeable returned by another function, 
 * is constructed because placeable is an immutable object and is meant to be cached.
 * @see Placeable
 */
public class VobWrapperFunction implements NodeFunction {
    final NodeFunction function;
    final Vob vob;

    /** Wrapper function used to wrap a vob and a placeable object.
     * @param function A function which returns a Placeable object.
     * @param vob The Vob used to place into ortho coordinate system
     *            which is same size as the Placeable returned by 
     *            the function.
     * @see Placeable
     */
    public VobWrapperFunction(NodeFunction function, Vob vob) {
	this.function = function;
	this.vob = vob;
    }
    
    /** @return A Placeable object.
     */
    public Object f(final ConstGraph g, final Object node) {
	final org.nongnu.libvob.lava.placeable.Placeable content = 
	    (org.nongnu.libvob.lava.placeable.Placeable)function.f(g, node);
	if(content == null)
	    return null;
	return new org.nongnu.libvob.lava.placeable.Placeable() {

		public void place(VobScene vs, int into) {
		    content.place(vs, into);
		    int cs = vs.orthoCS(into, "VobWrapper",0, 0,0, 
					content.getWidth(), content.getHeight());
		    vs.put(vob, cs);
		}

		public float getWidth() { return content.getWidth(); }
		public float getHeight() { return content.getHeight(); }
	    };
    }

}
