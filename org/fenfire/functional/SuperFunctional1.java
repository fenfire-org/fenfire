/*
SuperFunctional1.java
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
import org.fenfire.util.*;
import org.fenfire.swamp.*;
import org.nongnu.navidoc.util.CachingMap;
import org.nongnu.navidoc.util.Obs;
import org.nongnu.libvob.util.Background;
import java.lang.reflect.*;
import java.util.*;
import org.python.core.*;


/** A first step in evolving Functional impls: single thread, superlazy.
 * This class is VERY single-threaded: all calls must come from
 * the same thread or terrible things will happen.
 */
public class SuperFunctional1 extends DirectFunctional {
    public static boolean dbg = false;
    private void p(String s) { System.out.println(s); }


    class CacheInfo {
	/** Size of the cache.
	 */
	int n;
	/** Whether the cache should be superlazy.
	 */
	boolean superLazy;
	/** Priority for superlazy bg tasks.
	 */
	float priority;
    }

    /** Map: function id to CacheInfo.
     */
    protected Map cacheInfos = new HashMap();

    protected Background background;

    private Set superLazyObses = new HashSet();

    public SuperFunctional1(ConstGraph constGraph, Background background) {
	super(constGraph);
	this.background = background;
    }

    public void addSuperLazyObs(Obs o) {
	superLazyObses.add(o);
    }
    protected void callSuperLazyObses() {
	for(Iterator i = superLazyObses.iterator(); i.hasNext();) {
	    ((Obs)i.next()).chg();
	}
    }

    /** The node ``id`` should be cached using a caching map.
     * This method has to be called before the node is created.
     * @param id the node identifier 
     * @param n the size of caching map that should be used.
     * @param superLazy boolean: whether to cache superlazily
     */
    public void cache(Object id, int n, boolean superLazy, float priority) {
	CacheInfo cacheInfo = new CacheInfo();
	cacheInfo.n = n;
	cacheInfo.superLazy = superLazy;
	cacheInfo.priority = priority;
	cacheInfos.put(id, cacheInfo);
    }
    public void cache(Object id, int n, boolean superLazy) {
	cache(id, n, superLazy, 0);
    }
    public void cache(Object id, int n) {
	cache(id, n, false);
    }

    public FunctionInstance createFunctionInstance(
	Object id,
	Class functionClass,
	Object[] parameters0
	) {
	if(dbg) p("CreateFunctionInstance: "+id+" "+functionClass);
	FunctionInstance directInstance = super.createFunctionInstance(id,
			functionClass, 
			parameters0);
	return wrapFunctionInstance(id, directInstance);
    }

    public FunctionInstance createFunctionInstance_Jython(
	Object id,
	PyClass functionClass,
	PyObject[] parameters0
	) {
	if(dbg) p("CreateFunctionInstance: "+id+" "+functionClass);
	FunctionInstance directInstance = 
	    super.createFunctionInstance_Jython(id, functionClass, 
			parameters0);
	return wrapFunctionInstance(id, directInstance);
    }

    private FunctionInstance wrapFunctionInstance(
	    Object id,
	    FunctionInstance directInstance) { 
	    
	CacheInfo cacheInfo = (CacheInfo)cacheInfos.get(id);
	if(cacheInfo == null) {
	    if(dbg) p("No caching - return direct instance");
	    return directInstance;
	}
	
	if(directInstance instanceof DirectFunctionInstance) 
	    return new CachedFunctionInstance(
		    (PureFunction)(
			    ((DirectFunctionInstance)directInstance).f), 
		    null,
		    cacheInfo);
	else 
	    return new CachedFunctionInstance(
		    null,
		    (PureNodeFunction)(
			    ((DirectNodeFunctionInstance)directInstance).func), 
		    cacheInfo);

    }

    // Override
    protected Object mapParameterToFunction(Object o) {
	if(o instanceof CachedFunctionInstance) 
	    return ((CachedFunctionInstance)o).wrapper;
	return super.mapParameterToFunction(o);
    }
    protected PyObject mapParameterToFunction_Jython(PyObject o) {
	if(o instanceof PyJavaInstance) {
	    Object cfi = o.__tojava__(CachedFunctionInstance.class);
	    if(cfi != Py.NoConversion) 
		return Py.java2py(((CachedFunctionInstance)cfi).wrapper);
	}
	return super.mapParameterToFunction_Jython(o);
    }

    // Who needs to know about accesses to a cache?
    // - all not-yet-calculated superlazy caches that the cached
    //   func needs
    //
    // Who needs to know about RDF graph changes?
    // - all caches that depend on functions there

    /** The Observer that wants to know if a superlazy
     */
    Obs observer_deepwards = null;

    /** Tickers (like observers) that want to know when a 
     * lower-level cache was requested a value.
     */
    Ticker[] tickers_upwards = new Ticker[100];
    int ntickers_upwards = 0;

    interface Ticker {
	void tick();
    }

    protected class CachedFunctionInstance implements FunctionInstance {
	private final PureFunction realFunction;
	private final PureNodeFunction realNodeFunction;

	private final Object placeHolder;
	private final Object errorPlaceHolder;
	
	private class WrapperFunction implements Function, NodeFunction {
	    private CachingMap cache;
	    CacheInfo cacheInfo;
	    public WrapperFunction(CacheInfo cacheInfo) {
		this.cacheInfo = cacheInfo;
		cache = new CachingMap(cacheInfo.n);
	    }
	    private class Entry extends FunctionCacheEntry 
		    implements Runnable, Ticker, CachingMap.Removable {
		boolean closed;

		public Entry(Object input) { super(input); }

		/** The set of Obs object that want to know 
		 * when this entry is re-requested
		 */
		protected Set tickers;

		// Implement Ticker
		public void tick() {
		    if(closed) {
			// Ticking a closed entry means we're a superlazy
			// entry that hasn't been calculated. We *should* be.
			addEntryBack(this);
		    }
		    if(value == DIRTY) 
			background.addTask(this, cacheInfo.priority);
		}
		// Implement CachingMap.Removable
		public void wasRemoved(Object key) {
		    background.removeTask(this);

		    closed = true;

		    value = DIRTY; // allow value to be reclaimed
		}
		// Implement Runnable
		public void run() {
		    if(closed) {
			// This isn't impossible, as there are no guarantees that
			// the priorityqueue got the removal in time
			p("run closed entry");
			return;
		    }
		    if(dbg) p("Entry: run");
		    if(value != DIRTY) return;

		    observer_deepwards = this;

		    if(tickers != null) tickers = null;
		    int startticks = ntickers_upwards;

		    try {
			if(realFunction != null) {
			    this.value = realFunction.f(input);
			} else {
			    ConstGraph graph = constGraph.getObservedConstGraph(
						this);
			    this.value = realNodeFunction.f(graph, input);
			    graph.close();
			}
		    } catch(Throwable t) {
			p("ERROR WHILE EVALUATING FUNCTION "+realNodeFunction
				+" "+realFunction+":" + t);
			t.printStackTrace();
			this.value = ERROR;
		    }

		    if(dbg) p("Entry: called: "+this.input+" -> "+this.value);
		    if(ntickers_upwards > startticks) {
			if(tickers == null) tickers = new HashSet();
			for(int i=startticks; i<ntickers_upwards; i++) {
			    tickers.add(tickers_upwards[i]);
			} 
		    }
		    this.triggerObs();

		    observer_deepwards = null;
		    if(cacheInfo.superLazy) 
			callSuperLazyObses();
		}

		// The real set
		public Object getValue() {
		    if(closed) {
			// This is REALLY screwed up. Should NEVER
			// happen.
			throw new Error("getvalue closed entry");
		    }
		    if(value == ERROR)
			return errorPlaceHolder;
		    // Recalculate
		    if(value == DIRTY) {
			if(dbg) p("Entry: getValue: recalc");
			if(cacheInfo.superLazy) {
			    background.addTask(this, cacheInfo.priority);
			    tickers_upwards[ntickers_upwards++] = this;
			    if(observer_deepwards != null)
				addObs(observer_deepwards);
			    return placeHolder;
			} else {
			    // Stack of active observers
			    Obs saveObs = observer_deepwards;
			    run();
			    observer_deepwards = saveObs;
			}
		    } else {
			if(tickers != null) {
			    for(Iterator it=tickers.iterator(); it.hasNext();)
				((Ticker)it.next()).tick();
			}
		    }
		    if(dbg) p("Entry: getvalue ret: "+value);
		    if(observer_deepwards != null)
			addObs(observer_deepwards);
		    return value;
		}

		// override FunctionCacheEntry to detect closed ones
		public void addObs(Obs o) {
		    if(closed)  {
			throw new Error("addObs closed entry");
		    }
		    super.addObs(o);
		}
		public void triggerObs() {
		    if(closed)  {
			// Do nothing - it's legal to trigger obses in 
			// closed entries
		    }
		    super.triggerObs();
		}
		public void chg() {
		    if(closed)  {
			// Do nothing - it's legal to trigger obses in 
			// closed entries
		    }
		    super.chg();
		}
	    }
	    public Object f(ConstGraph g, Object x) {
		return f(x);
	    }
	    public Object f(Object x) {
		synchronized(cache) {
		    Entry cac = (Entry)cache.get(x);
		    if(cac == null) {
			cac = new Entry(x);
			// Have to get return value first.
			// It might not get cached at all, 
			// i.e. removed at once.
			Object ret = cac.getValue();
			cache.put(x, cac);
			return ret;
		    }
		    return cac.getValue();
		}
	    }
	    private void addEntryBack(Entry entry) {
		// If it's been superseded by another entry, do nothing
		if(cache.get(entry.input) != null) return;
		entry.closed = false;
		cache.put(entry.input, entry);
	    }
	}

	private class PureWrapperFunction 
	    extends WrapperFunction implements PureFunction, PureNodeFunction {
	    public PureWrapperFunction(CacheInfo cacheInfo) {
		super(cacheInfo);
	    }
	}

	private class WrapperWrapper implements Function, NodeFunction {
	    public Object f(ConstGraph g, Object x) {
		return f(x);
	    }
	    public Object f(Object x) {
		if(ntickers_upwards > 0)
		    throw new Error("Can't have tickers on at first entry!");
		Object res =  wrapper.f(x);
		ntickers_upwards = 0;
		return res;
	    }
	}

	private class PureWrapperWrapper 
	    extends WrapperWrapper implements PureFunction, PureNodeFunction {
	}

	private final WrapperFunction wrapper;
	private final WrapperWrapper wrapperWrapper;

	public CachedFunctionInstance(
		    PureFunction real, 
		    PureNodeFunction realNode,
		    CacheInfo cacheInfo) {
	    if(dbg) p("Create Cached instance");
	    realFunction = real;
	    realNodeFunction = realNode;

	    boolean isPure = true;
	    if(realFunction != null &&
	       !(realFunction instanceof PureFunction))
		isPure = false;
	    if(realNodeFunction != null &&
	       !(realNodeFunction instanceof PureNodeFunction))
		isPure = false;

	    if(isPure) {
		wrapper = new PureWrapperFunction(cacheInfo);
		wrapperWrapper = new PureWrapperWrapper();
	    } else {
		wrapper = new WrapperFunction(cacheInfo);
		wrapperWrapper = new WrapperWrapper();
	    }

	    DefaultHints h;
	    try {
		java.lang.reflect.Field field ;
		if(realFunction != null)
		    field = realFunction.getClass().getField("functionalHints");
		else
		    field = realNodeFunction.getClass().getField("functionalHints");
		h = (DefaultHints)field.get(null);
	    } catch(NoSuchFieldException e) {
		h = null;
	    } catch(IllegalAccessException e) {
		throw new Error("Invalid field!");
	    }
	    if(h == null) {
		this.placeHolder = null;
		this.errorPlaceHolder = null;
	    } else {
		this.placeHolder = h.hints.get(Functional.HINT_PLACEHOLDER);
		this.errorPlaceHolder = h.hints.get(Functional.HINT_ERRORPLACEHOLDER);
	    }
	}
	public Function getCallableFunction() {
	    return wrapperWrapper;
	}

    }

}
