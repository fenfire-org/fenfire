/*   
PageNodeView.java
 *    
 *    Copyright (c) 2003, Benja Fallenstein
 *
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
 *
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.view;
import org.fenfire.spanimages.*;
import org.fenfire.swamp.*;
import org.fenfire.functional.*;
import org.nongnu.alph.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.vobs.*;
import java.awt.Color;

/** A node function returning a vob that shows
 *  the given node as a pageimage or sequence of pages.
 */
public class PageNodeView implements PureNodeFunction {

    final NodeFunction nodeContent;
    final Function spanImageFactory;

    public PageNodeView(NodeFunction nodeContent,
			Function spanImageFactory) {
	this.nodeContent = nodeContent;
	this.spanImageFactory = spanImageFactory;
    }

    public Object f(ConstGraph g, Object node) {
	final Enfilade1D enf = (Enfilade1D)nodeContent.f(g, node);
	return new PageSpanLayout(enf, spanImageFactory);
    }
}
