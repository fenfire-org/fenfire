/*
CachedPureNodeFunction.java
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

package org.fenfire.functional;
import java.util.*;
import org.nongnu.navidoc.util.CachingMap;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.*;
import org.fenfire.swamp.*;
import org.fenfire.util.*;

/** A function which caches its values.
 * In order to work right, the operations
 * performed in the original function
 * are very restricted.
 * First of all, no side effects are allowed.
 * Second, no function whose return value depends
 * on anything except the passed parameters
 * (including the Graph) may be called.
 * <p>
 * As an aside, this class works completely also
 * without a graph, for caching constant functions.
 */
public class CachedPureNodeFunction implements PureNodeFunction {
    private void p(String s) { System.out.println("CachedPureNodeFunction:: "+s); }

    PureNodeFunction f;

    ConstGraph ourGraph;

    CachingMap cache;

    public CachedPureNodeFunction(int n, ConstGraph g, PureNodeFunction f) {
	cache = new CachingMap(n);
	this.ourGraph = g;
	this.f = f;
    }

    public Object f(ConstGraph g, Object node) {
	Obs o = null;
	if(g != ourGraph) {
	    o = g.getObserver();
	    g = g.getOriginalConstGraph();
	    if(g != ourGraph)
		throw new IllegalArgumentException("Called with wrong graph");
	}

	FunctionCacheEntry cac = (FunctionCacheEntry)cache.get(node);
	if(cac == null) {
	    cac = new FunctionCacheEntry(node);
	    cache.put(node, cac);
	}
	if(cac.value == cac.DIRTY) {
	    ConstGraph og = (ourGraph == null ? 
		    null : ourGraph.getObservedConstGraph(cac));
	    //p("Observed constgraph is: "+og);
	    cac.value = f.f(og, node);
	    //p("Observed constgraph was: "+og);

	    //cac.printObses();
	    if(og != null) {
		//p("Observed Constgraph found!");
		og.close();
	    }
	}
	if(o != null) {
	    //System.out.println("Obs added: "+o);
	    cac.addObs(o);
	} 
	return cac.value;
    }
}

