/*
PaperMainViewFactory.java
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

package org.fenfire.view.main.impl;
import org.fenfire.view.main.*;
import org.fenfire.swamp.*;
import org.fenfire.util.*;
import org.fenfire.vocab.*;
import org.fenfire.view.management.*;

import org.nongnu.libvob.layout.*;

import java.util.*;


public class PaperMainViewFactory implements MainViewFactory {

    boolean checked = false;
    Object node = null;
    FServer f;
    public PaperMainViewFactory(FServer f) {
	this.f = f;
    }

    public String toString() { return "Paper canvas view"; }



    // impl.
    // XXX simple implementation
    public boolean isViewable(ConstGraph cg, Object node) {
	if (RDFUtil.isNodeType(cg, node, 
			       CANVAS2D.Canvas)) {
	    this.node = node;
	    return true;
	}
	return false;
    }
    
    // impl.
    public Lob product(Object node) {
	if (this.node != node) 
	    throw new Error("not sure whether the "+
			    "view works with given node! "+
			    node);
	return new PaperMainView(f, node);
	//throw new Error(" asdf ");
    }
}
