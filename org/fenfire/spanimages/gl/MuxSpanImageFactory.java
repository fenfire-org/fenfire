/*
MuxSpanImageFactory.java
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

package org.fenfire.spanimages.gl;
import org.fenfire.spanimages.*;
import org.nongnu.alph.*;

/** A spanimagefactory that redirects pageimage and image spans
 * to different factories.
 */
public class MuxSpanImageFactory extends SpanImageFactory {
    private final SpanImageFactory imageFactory;
    private final SpanImageFactory pageImageFactory;

    public MuxSpanImageFactory(SpanImageFactory imageFactory,
				SpanImageFactory pageImageFactory) {
	this.imageFactory = imageFactory;
	this.pageImageFactory = pageImageFactory;
    }

    public Object f(Object s) {
	if(s instanceof PageImageSpan)
	    return pageImageFactory.f(s);
	else
	    return imageFactory.f(s);
    }
}



