/*
CachedNodeFunction.java
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
 * This is a less restricted class than CachedPureNodeFunction,
 * allowing impure node functions and flushing.
 * <p>
 * This class is, of course, far more dangerous and may introduce
 * subtle caching bugs - using it is slightly deprecated,
 * PureNodeFunctions 
 * and CachedPureNodeFunction should be used whenever possible.
 * <p>
 */
public class CachedNodeFunction implements NodeFunction {

    NodeFunction f;

    ConstGraph ourGraph;

    CachingMap cache;

    /** Constructor for default set.
     */
    public CachedNodeFunction(int n, ConstGraph graph, NodeFunction f) {
	this(n,graph,f, DEFAULT);
    }

    /** Constructor for different sets.
     * @param set The set to be flushed when flushing.
     */
    public CachedNodeFunction(int n, ConstGraph graph, NodeFunction f, Object set) {
	cache = new CachingMap(n);
	this.ourGraph = graph;
	this.f = f;
	this.n = n;
        this.add(set, this);
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
	    cac.value = f.f(og, node);
	    if(og != null) og.close();
	}
	if(o != null)
	    cac.addObs(o);
	return cac.value;
    }

    static private HashMap funcsMap = new HashMap();
    static private void add(Object set, Object func) {
	if (funcsMap.get(set) == null)
	    funcsMap.put(set, new ArrayList());
	List l = (List)funcsMap.get(set);
	l.add(func);
    }

    /** Flush all information related to node.
     * This function is needed because we cache impure functions.
     */
    public void flush(Object node) {
	FunctionCacheEntry entry = (FunctionCacheEntry)cache.get(node);
	if(entry == null) return;
	entry.value = entry.DIRTY;
    }

    /** Flush all different sets.
     */
    static public void flushAllSets() {
	for (Iterator it = funcsMap.keySet().iterator(); it.hasNext(); ) {
	    Object set = it.next();
	    flushSet(set);
	}
    }

    /** Flush the default set.
     */
    static public void flushDefaultSet() {
	flushSet(DEFAULT);
    }

    /** Flush the set which is given.
     * Throws an error if set doesn't exist.
     */
    static public void flushSet(Object set) {
	if (funcsMap.get(set) == null)
	    throw new Error("No flush set found: '"+set+"'");

	List l = (List)funcsMap.get(set);
        for (int i=0; i<l.size(); i++) {
            CachedNodeFunction one = 
                (CachedNodeFunction)l.get(i);
            one.flushItAndSmile();
        }
    }


    static private final Object DEFAULT = new Object();

    private final int n;
    private void flushItAndSmile() {
        cache = new CachingMap(n);
    }


}
