/*
CachingSpanImageFactory.java
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
import org.nongnu.libvob.util.CacheControl;
import org.fenfire.spanimages.*;
import org.nongnu.navidoc.util.*;
import org.nongnu.alph.*;
import java.util.*;

/** A SpanImageFactory which caches the results of another SpanImageFactory
 * into a SoftValueMap.
 */

public class CachingSpanImageFactory extends SpanImageFactory {

    private Map cache = Collections.synchronizedMap(
		    new CachingMap(1000));
//		    new SoftValueMap());

    public SpanImageFactory factory;
    CacheControl.Listener listener;

    public CachingSpanImageFactory(SpanImageFactory factory) {
	this.factory = factory;
	listener = CacheControl.registerCache(this, 
			"SpanImage cache for "+factory);
    }

    public void clear() {
	cache.clear();
    }

    public Object f(Object s) {
	SpanImageVob ret = (SpanImageVob)cache.get(s);
	if(ret == null) {
	    listener.startMiss(s);
	    ret = (SpanImageVob)factory.f(s);
	    cache.put(s, ret);
	    listener.endMiss(s);
	} else 
	    listener.hit(s);
	return ret;
    }
}
