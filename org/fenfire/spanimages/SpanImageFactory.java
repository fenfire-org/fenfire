/*
SpanImageFactory.java
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

package org.fenfire.spanimages;
import org.fenfire.spanimages.gl.*;
import org.fenfire.spanimages.fuzzybear.*;
import org.nongnu.alph.*;
import org.fenfire.functional.PureFunction;

import org.nongnu.libvob.GraphicsAPI;
import org.nongnu.libvob.impl.gl.GLAPI;

/** The central interface for creating vobs that contain
 * images of ImageSpans.
 * PureFunction Input: ImageSpan; Output: SpanImageVob
 * The backend of this interface takes care of all caching and other
 * operations necessary.
 * <p>
 * The normal size for the SpanImageVob (w and h) is either pixel-for-pixel,
 * if the imagespan contains raster data, or 75 pixels per inch, if
 * the imagespan contains vector data.
 */
public abstract class SpanImageFactory implements PureFunction {
    private static SpanImageFactory instance;

    /** (For tests, mostly) Get a reasonable instance
     * of SpanImageFactory.
     */
    public static SpanImageFactory getDefaultInstance() {
	if(instance == null) {
	    if (GraphicsAPI.getInstance() instanceof GLAPI) {
		PaperMaker paperMaker = new PlainPaperMaker();
		instance = 
		    new MuxSpanImageFactory(
		      new CachingSpanImageFactory(
		        new DefaultSpanImageFactory(
			  new ImageScrollBlockImager(),
			  paperMaker
			  )),
		      new CachingSpanImageFactory(
		        new DefaultSpanImageFactory(
			  new PageScrollBlockImager(),
			  paperMaker
			  ))
		    );
	    } else { // in case of fuzzy bear AWT implementation. 
		instance = new CachingSpanImageFactory(
			        new FuzzySpanImageFactory());
	    }
	}
	return instance;
    }
}


