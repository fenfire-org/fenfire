/*
PageScroll2LayoutPureFunction.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka
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
 * Written by Tuomas J. Lukka
 */

package org.fenfire.view;
import org.nongnu.alph.*;
import org.fenfire.spanimages.*;
import org.fenfire.functional.PureFunction;
import org.fenfire.functional.Function;

/** A (non-node)function which takes a PageScrollBlock and returns
 * a PageSpanLayout.
 * This function is pure *iff* the SpanImageFactory
 * is not changed. A planned change to org.fenfire.spanimages
 * should make spanimagefactories immutable.
 */

public class PageScroll2LayoutPureFunction  implements PureFunction {

    private final Function spanImageFactory;
    public PageScroll2LayoutPureFunction(Function spanImageFactory) {
	this.spanImageFactory = spanImageFactory;
    }

    public Object f(Object input) {
	PageScrollBlock block = (PageScrollBlock)input;
	return new PageSpanLayout(
	    (PageSpan)block.getCurrent(), spanImageFactory);
    }
}

