=================================
Functional programming in Fenfire
=================================

Some design & implementation notes

Issues
======

- How do we calculate a pure function of a superlazy function and
  cache the result?
    
    SIMPLE RESOLUTION: We don't. We put the superlazy part at the outermost
    edge of the pure function chain.

Introduction
============

Generating a complete view from the RDF graph is a relatively 
computation-intensive task.
Most of the graph remains unchanged between frames, and therefore 
caching partial results can give significant performance gains.

The extreme version of this is the use of scene graphs and applying
modifications to them; however, this gives us an additional layer
where bugs can occur, as there is more state in view code, and we need
to be able to do the same operations through several different changes.

An interesting system we've formulated and started using slowly
during the past few years uses concepts from
functional programming languages and their implementation
to get the best of both worlds.

In functional programming, a function is by definition a **pure** function:
given the same inputs, a function always returns the same value. The return
value cannot depend on anything else except the identity of the function
and the inputs.

These restrictions allow the use of caching and other types of optimizations
for pure functions.

Conventions to be used in Fenfire
(not yet completely obeyed):

    - any pure functions **shall** have the word ``Pure`` in their name.


Node functions
==============

The generalization of pure functions to functions of a part 
of a mutable RDF graph is non-trivial.  Our solution is to allow
*observing* of the RDF graph, i.e. the RDF graph can be requested
to call a callback whenever the result of a given query changes.

The Swamp RDF API has special support for this: the ``ConstGraph`` interface
has the method ``getObservedConstGraph(Obs o)``, which will return 
a graph whose queries will return the same values, but
which automatically sets up the triggers.

This is useful for creating a cache of node function values, since
the cache can simply call the cached function with an observed graph,
without the ``PureNodeFunction`` having to know about it in any way.
This is how ``CachedPureNodeFunction`` is implemented.


Super-lazy functions
====================

Once the first frame has been created, the next frame is faster
due to the caches. However, this doesn't help the first frame
which may lag badly, and other frames that bring a lot of new
things to view.

The innovation here is to use super-lazy functions which,
when a value is requested, return a placeholder and **schedule**
themselves to be calculated in the background at a later date,
using a ``Background`` object.

Now, swamp is not thread-safe so if the background that is used 
uses a separate thread, synchronization has to be taken care of by
the user.


Mixing functions
================

-

Functions used in FenPDF
========================

``SpanImageFactory`` is a mapping from ``ImageSpan``s to ``SpanImageVob``s.

``PageScroll2LayoutPureFunction`` maps ``PageScroll``s to ``PageSpanLayout``s.




Foo ::

    // (c) Tuomas J. Lukka

    package org.fenfire.spanimages;
    import org.fenfire.spanimages.gl.*;
    import org.nongnu.alph.*;

    /** A super-lazy wrapper of SpanImageFactory.
     * @see SuperLazyPureNodeFunction
     * @see SpanImageFactory
     */
    public class SuperLazySpanImageFactory extends SpanImageFactory {

	public SuperLazySpanImageFactory(int n, final SpanImageFactory fact,
			    Obs recalcObs) {
	    superLazyFunc = new SuperLazyPureNodeFunction(n, null,
		new PureNodeFunction() {
		    public Object f(ConstGraph g, Object o) {
			return fact.getSpanImageVob(null,
				(ImageSpan)o);
		    }
		},

	}
	abstract public SpanImageVob getSpanImageVob(ImageSpan s);
    }
