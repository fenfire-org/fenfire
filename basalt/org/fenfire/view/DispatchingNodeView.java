/*   
DispatchingNodeView.java
 *    
 *    Copyright (c) 2003, Benja Fallenstein and Tuomas J. Lukka
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
 * Written by Benja Fallenstein and Tuomas J. Lukka
 */
package org.fenfire.view;
import org.fenfire.swamp.*;
import org.fenfire.functional.*;
import org.nongnu.alph.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.vobs.*;

/** A node view dispatching to different other
 *  node views depending on a node content's type.
 */
public class DispatchingNodeView implements PureNodeFunction {

    final NodeFunction nodeContent;
    final NodeFunction textView, pageView;

    public static class Pure extends DispatchingNodeView 
	    implements PureNodeFunction {
	public Pure(PureNodeFunction nodeContent,
		    PureNodeFunction textView,
		    PureNodeFunction pageView) {
	    super(nodeContent, textView, pageView);
	}
    }

    public DispatchingNodeView(NodeFunction nodeContent,
			       NodeFunction textView,
			       NodeFunction pageView) {
	this.nodeContent = nodeContent;
	this.textView = textView;
	this.pageView = pageView;
    }

    public Object f(ConstGraph g, Object node) {
	Enfilade1D enf = (Enfilade1D)nodeContent.f(g, node);
	if(enf.length() == 0) return textView.f(g, node);
	Object span = enf.getList().get(0);
	if(span instanceof PageSpan || 
	   span instanceof PageImageSpan)
	    return pageView.f(g, node);
	else
	    return textView.f(g, node);
    }
}
