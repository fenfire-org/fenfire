/*
AbstractNodeType2D.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka and Benja Fallenstein
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
 * Written by Tuomas J. Lukka and Benja Fallenstein
 */

package org.fenfire.view.buoy;
import org.fenfire.view.View2D;
import org.fenfire.util.Pair;
import org.fenfire.util.Triple;
import org.nongnu.libvob.*;
import org.nongnu.libvob.impl.DefaultVobMatcher;
import org.nongnu.libvob.buoy.*;

public abstract class AbstractNodeType2D implements BuoyViewNodeType {
    protected View2D view2d;
    protected AbstractMainNode2D.Factory mainNodeFactory;

    public View2D getView2D() { return view2d; }

    public AbstractNodeType2D(View2D view2d, 
		AbstractMainNode2D.Factory factory) {
	this.view2d = view2d;
	this.mainNodeFactory = factory;
    }

    public BuoyViewMainNode createMainNode(Object linkId, 
					   Object anchor) {
	System.out.println("AbstractNodeType2D:: Object anchor: "+anchor);
	View2D.Anchor a = (View2D.Anchor)anchor;
	return mainNodeFactory.create(a.plane, view2d,
				      a.x+a.w/2f,
				      a.y+a.h/2f,
				      1);
    }
}
