=============================================================
PEG functional_futureproof_api--tjl: 
=============================================================

:Author:   Tuomas J. Lukka
:Last-Modified: $Date: 2003/09/09 07:20:23 $
:Revision: $Revision: 1.9 $
:Status:   Current

Functions and caching are here to stay with us, as they provide
us with a way to give faster user response times with minimal
code complications. 

However, the caching is currently pretty nasty for the programmer
who *instantiates* the functions and requires active thinking, 
especially in the case of super-lazy
functions (i.e. caches that schedule evaluation only after being
requested the value and return a placeholder).

This PEG provides a future-proof API for handling functions and caching
cleanly.

Issues
======

- How do we specify which functions need to be run in the OpenGL thread?
  How general should we make this ability?

  One important point is that this is a property of the **function**, not
  its instantiation.

  The reasonable alternatives, given this, are 
  
  1) to have separate interfaces
     for the per-function properties and for the ``Function`` 
     to implement them.

     Pros:
	- simple
     Cons:
	- profusion of interfaces

  2) to add, in the ``Functional`` api a Hints object and "getHints"
     or something

     Pros:
	- flexible
     Cons:
	- slightly klunky

  RESOLUTION: 2). this is pretty much as good as we can get. If the Hints
  object is specified flexibly, can be done without loss of generality
  (i.e. can give Id to specific sets of Background objects).



- How should we give hints to the Functional API about which functions
  are slow?

  This may be a property of the function, or a property of the instance.

  RESOLUTION: For now, allow only as part of Hints.


- Do we want/need to specify that a function uses its parameter functions once,
  maybe once, several times?

  RESOLUTION: Not yet. So far, no use for such information has been given.

- Do we want/need to specify that a function uses its parameter functions 
  always with the same parameter that it gets?

  RESOLUTION: Not yet. So far, no use for such information has been given.

- What about slow tasks that want to use OpenGL, such as Mipzip generation?
  Paper generation is much faster.

  RESOLUTION: Nothing for now - they should be run in a thread, and start
  new processes. This is even slower, but OTOH will crash less likely
  (there has been some stability problems when generating lots of mipzips
  that seem like a problem in zlib or NVIDIA drivers ((yeah, right ;))).

- Should we try to do more through reflection, reading parameters &c
  from finished objects? We might be able to map::

    Function node1 = new G("X");
    Function node2 = new F("X", node1);

  to the Functional objects?

  RESOLUTION: Not yet. We can always create a new tag interface for ``Function``
  objects that obey the special restrictions associated with that,
  if any are needed.

- How should we specify placeholder objects for super-lazy caches?
  Placeholders can be on two levels, either the function level or instance level.

  RESOLUTION: For now, only function level. Hints object will have a setPlaceholder
  call.

- Should the Functional API know about NodeFunctions and especially about Swamp
  and observable graphs?

  It would be nice to be clean but OTOH it would be difficult.

  RESOLUTION: For now, Functional will require Swamp. We'll look for a way
  to disentangle them once we have more experience with different implementations
  of ``Functional``.

- How should we handle the dual Function - NodeFunction aspect in the API?
  Single calls that handle it internally or separate calls and FunctionalInstance types?
  Who is aware of the ConstGraph to be used?

  RESOLUTION: The Functional API contains the ConstGraph itself and creates
  Function wrappers for NodeFunctions.

  This may need to be changed later, once we are juggling several graphs 
  at the same time.

- What term should be used for the objects returned by the Functional API?
  They correspond to instances of Functions but need their own methods.

  The first idea, ``Node`` seems confusing in retrospect.

  RESOLUTION: ``FunctionInstance``s. And to make it clearer, the class 
  shall be outside the ``Functional`` class.

Introduction
============

The point of this PEG is to allow for transparent lazy and super-lazy caching
of functions, with (in the future) automatic adjustment of cache locations and sizes
based on actual run-time information. This means that more information about
functions, and parameter functions is required.

This PEG defines a way of creating functions that gives the API maximum information
about the functional structure so created, to allow all these.

As an important example of why this API is needed, consider the NodeFunctions in FenPDF  
that provide the Canvas2D with the Placeables for each node.
The functions could be written as ::

    f(x) = Multiplex( Wrap( PageNodeFunc(x), 1 ), TextNodeFunc(x) )
    g(x) = Multiplex( Wrap( PageNodeFunc(x), 2 ), TextNodeFunc(x) )

The problem is that we want to

1) Cache the result of f(x) and g(x) normally

2) Cache the result of PageNodeFunc(x) super-lazily as it is slower to run.

We do *not* want to cache TextNodeFunc super-lazily, since it is quite fast to run.
However, to allow the last-used nodes to be calculated first, we want to use a LIFO
(last-in-first-out) computation for the nodes. However, if the placeholder
from the super-lazily cached PageNodeFunc is retrieved from the cache of the Multiplex
function, the super-lazy cache's last-used date will remain wrong.

Therefore, it is obvious that the caches need to co-operate. This API allows
a single class to take care of all caches, so this co-operation could be arranged.

Quick glossary
==============

This PEG depends on some concepts that have not yet been explained or published
anywhere. Here are the definitions.

Superlazy caching
    A cache which is lazier than lazy: it does not calculate
    the correct value for the function it is caching even when requested
    but returns a *placeholder* value instead (such as ``null``, or an empty
    rectangle vob or ...) and *schedules* the computation to take place
    at a later time (when the processor would be otherwise idle).

Placeholder values
    Values that look like the real values returned from a function (i.e. are
    of the same type) but are "blank". These values are returned by superlazy
    caches when the true value has not yet been computed.

Hints
    Metainformation about a function, for example whether it takes a long time to run,
    or whether it needs to be run in the OpenGL thread.

Changes to Functions, new API
=============================

Creating functions
------------------

Functions shall no more be created directly by calling the constructor
(it's allowed but will not be cacheable &c).
Instead, a reflective Function creation API shall be used.

This is vital to get the information about the functions to the API
to allow proper caching.

New package
-----------

Create the package ``org.fenfire.functional`` and move all ``Function`` 
and ``NodeFunction`` -related classes there. Naturally, classes
such as ``VobWrapperFunction`` that only **use** or **implement** 
the API shall remain.

The Cache classes shall also be moved to Functional and be deprecated.

GL- and nonGLfunctions
----------------------

In the following, we shall refer to functions that have to be calculated
in the OpenGL thread (i.e. ones that create Vobs) as GLfunctions,
and others as nonGLfunctions.

Loosening purity
----------------

The biggest API change is not an API change *per se* but a semantic change.
So far, ``PureFunction`` and ``PureNodeFunction`` have been required to 
be pure, interface-wise, i.e. their f() *method* may not return values
depending on anything other than the parameter.

To make handling functions convenient, we shall relax this requirement:
a ``PureFunction`` must be pure **iff** all its constructor parameters
that implement ``Function`` are pure, and likewise for ``PureNodeFunction``s.

Allowing exceptions
-------------------

Any object implementing a ``Function`` interface (except for the special Functional
API classes) shall not leak memory or cause crashes or improper results if any of
its constructor parameters that implement the ``Function`` API throw an ``Error``.
The error must pass through the ``Function`` invocation to the caller.

This change is important for the case where a fast GLfunction of a slow nonGLfunction
needs to be calculated: if the slow value has not been cached, it's better
to schedule its calculation in a background thread and throw an exception. That way,
the OpenGL thread is free for other operations during the calculation.

This allows the function evaluation to stop at the first uncached value that is encountered.

The Functional API
------------------

::

    package org.fenfire.functional;

    /** A node in the functional calculation DAG.
     * This class represents (one, or several equivalent) instances
     * of a class implementing Function or NodeFunction.
     * This class wraps the computation so that the implementation
     * of Functional is able to use more information to determine
     * how and when to evaluate what functions.
     * <p>
     * This class is used instead of plain instances of class Function
     * because it is relevant for the Functional API instances to know for
     * which FunctionInstances the actual values of the function are required
     * from outside.
     */
    interface FunctionInstance {
	/** Get a function entry point for this node.
	 */
	Function getCallableFunction();
    }

    /** An object that manages a DAG of Function instances, enabling 
     * transparent and super-lazy caching.
     * This interface allows different implementations, from ones that simply
     * wrap and call the actual function instances to ones that allow
     * transparent run-time data-sensitive caching and super-lazy caching.
     */
    interface Functional {

	/** Hints about a Function class.
	 * Hints tell the Functional API about a Function: is it slow to evaluate,
	 * does it need to be evaluated in the OpenGL thread &c.
	 * <p>
	 * Hints objects are created using HintsMaker.
	 * An empty interface in order to be unmodifiable.
	 * Each class that implements ``Function`` or ``NodeFunction``
	 * that is given to this API shall have a static member ``functionalHints``
	 * of this type.
	 */
	interface Hints {
	}

	/** An interface for creating Hints objects.
	 */
	class HintsMaker {
	    /** This function must be run in a background object
	     * of the given group if it's not run directly.
	     * This is useful for using Libvob OpenGL, since 
	     * OpenGL objects should only be handled in one thread.
	     * Default: null.
	     */
	    void setBackgroundGroup(Object id);

	    /** Whether this function usually consumes considerable time
	     * to generate its output, given all its inputs.
	     * Evaluations of functions given as parameters to this function
	     * are not counted.
	     * Default: not slow.
	     */
	    void setSlow(boolean isSlow);

	    /** Set the placeholder object to be returned if the function value
	     * is not ready yet.
	     * Default: null.
	     */
	    void setPlaceholder(Object o);

	    /** Create the Hints object.
	     */
	    Hints make();
	}

	/** Create a new node in the DAG.
	 * @param id An identifier for the node. Useful for specifying caching for
	 *           some particular implementations..
	 *           Should be stable between invocations.
	 * @param functionClass The class of which the Function (or NodeFunction)
	 *		object should 
	 *	     	be created.
	 * @param parameters The parameters for the constructor of the class.
	 *		These may contain FunctionInstance objects, which will be converted
	 *		to functions or nodefunctions as appropriate.
	 */
	FunctionInstance createFunctionInstance(
	    Object id,
	    Class functionClass,
	    Object[] parameters
	    );
    }

Example: using the API
----------------------

First, define two functions::

    public class G implements PureFunction {
    
	public G(Object bla) {
	}
	public Object f(Object o) {
	    ...
	}
    }

    public class F implements PureFunction {
	static public Functional.Hints functionalHints;
	static {
	    // F needs to be run in the OpenGL thread
	    Functional.HintsMaker maker = 
		new Functional.HintsMaker();
	    maker.setBackgroundGroup("OPENGL");
	    functionalHints = maker.make();
	}
    
	public F(Object bla, Function g) {
	}
	public Object f(Object o) {
	    ...
	}
    }

And construction the functions (``functional`` is an instance of ``Functional``)::

    FunctionInstance node1 = functional.createFunctionInstance(
	"N1",
	G.class, 
	new Object[] {"X"});
    FunctionInstance node2 = functional.createFunctionInstance(
	"N2",
	F.class,
	new Object[] {"Y", node1});

This constructor is "equivalent" to::

    Function node1 = new G("X");
    Function node2 = new F("X", node1);

except that the result may be cached, since ``functional`` knows
the complete structure and is able to deduce (or be told externally)
what to cache. 


Changes to the rest of Fenfire
==============================

Node content
------------

The ``NodeContent`` class is something of a remnant.
It provides the Node content function, so we need to touch
it here, let's upgrade it properly at the same time.

Move it to its rightful place, ``org.fenfire.structure.Ff``,
and add ``getContent`` and ``setContent`` methods, given a Fen.
There's shall also be a PureNodeFunction nested class
``org.fenfire.structure.Ff.ContentFunction`` which allows the user
to create an appropriate node function.

Fen
---

We will not change Fen at this time, except for to remove the ``txtfunc`` member.

