/*
ViewPortable.java
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

package org.fenfire.view.management;

import org.fenfire.swamp.Nodes;
import org.nongnu.libvob.VobMouseEvent;

/** 
 */
public interface ViewPortable {
    static final public String ID = "viewportable";
    ViewPort getViewPort();



    // view port settings vocabular.

    static final public Object ViewPort = Nodes.get("http://fenfire.org/rdf-v/2004/08/28/ViewPortable#ViewPort");
    static final public Object button = Nodes.get("http://fenfire.org/rdf-v/2004/08/28/ViewPortable#button");
    static final public Object modifier = Nodes.get("http://fenfire.org/rdf-v/2004/08/28/ViewPortable#modifier");

    static final public Object moveXY = Nodes.get("http://fenfire.org/rdf-v/2004/08/28/ViewPortable#moveXY");
    static final public int moveXY_button_default = 1;
    static final public int moveXY_modifier_default = VobMouseEvent.CONTROL_MASK;

    static final public Object resize = Nodes.get("http://fenfire.org/rdf-v/2004/08/28/ViewPortable#resize");
    static final public int resize_button_default = 3;
    static final public int resize_modifier_default = VobMouseEvent.CONTROL_MASK;

}
