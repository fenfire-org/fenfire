=============================================================
PEG swamp_rdf_api--tjl: 
=============================================================

:Author:   Tuomas J. Lukka
:Last-Modified: $Date: 2003/08/31 12:28:05 $
:Revision: $Revision: 1.14 $
:Status:   Current (Partially preliminarily implemented [since in its own package])

This document outlines the main issues in the Jena api
currently in use and proposes a lightweight api of our own
to replace it.

Issues
======


- Are there any APIs out there that already support our needs?

- Do we want implicit or explicit observers?
  Gzz used explicit observing, due to having an object per cell. However,
  the tradeoffs are different here.

  The benefits of implicit observing are ease and purity of the functional
  approach: in the explicit approach, forgetting a single obs somewhere will 
  make the code buggy in a potentially dangerous way. However, the implicit
  observing requires wrapper objects for all parts of the API.

    RESOLVED: Implicit observing, since we *can* wrap all parts
    of the API without too much cost. If the API is non-object-oriented,
    in the sense that the individual nodes and statements are not tied 
    to any graph, we only need to wrap O(1) objects.

    Additionally, we can cheaply make "derived" graphs (i.e.
    graph H(G) which is a function of the other graph G), 
    e.g. when implementing the model_versions--tjl stuff.

- How should resources and properties be represented?

    RESOLVED: As hashable Objects with '==' comparison semantics.
    The resources are mapped to and from Strings through a 
    global resource name mapper / compressor.

    We need to save memory and e.g. the URN-5 names
    are too many and too long. The compressor would return either
    strings with a prefix markup (i.e. a funny character and an index
    to a prefix table) or an object with an integer (for the number part)
    and an interned string for the shared part.

    For properties and other such resources, interned strings should be sufficient.

    Of course, the Nodes class will decide at get() time what representation
    to use and that representation will be valid everywhere.

- How should literals be represented?

    RESOLVED: As immutable Literal objects, with several types of accessors.

- What about literal typing? How do we support enfilades?

    RESOLVED: For now, just get the raw string.

- Do we want explicit Statement objects a la Jena?
    
    RESOLVED: No, they force a certain style of implementation which may not be the
    most efficient. We need to minimize the number of Java objects created.
    While an object for every resource and literal is just about unavoidable,
    an object for each statement is not.

- What would be the right characters for the search methods?    

    RESOLVED: 1 for a given object, X for an unknown object. 
    They are visually clearly separate, and X for the unknown is mnemonic.

- Should bags, alts &c be supported explicitly in the API?

    RESOLVED: Not yet. Many issues related e.g. to versioning.

- That's a LOT of methods for all combinations. Couldn't we use wildcards
  or something?

    RESOLVED: No. It would be unnecessary inefficiency to look for them. 
    Remember, this code is *the* inner loop. 

    Quite likely code generation will be used.

    However, we shall also provide less efficient but more comfortable
    versions which take null as the wildcard.

- The non-OO approach is not nice for some code. Could we use OO instead?

    RESOLVED: No. Need for speed.

    HOWEVER, it's pretty simple to build an OO API on top for the less 
    speed-sensitive locations ::

	class OONode {
	    Graph g;
	    Object node;
	    ...
	}

    and use it as ::

	person = OONode(thePersonNode);
	name = person.getProperty(nameProperty);

- What should the RDF model/graph/thing be called? You know, the 
  set of triples?

    RESOLVED: Graph. A nice word, and has images with it that may
    help us leave the word "space" behind.

- how should the methods in Graph be named?

    Open. I'm still flexible about this.

- What should the resource mapper and its methods be called? RMap? 
  Nodes?

    RESOLVED: Nodes. It's the most descriptive.

- Could we use e.g. int instead of Object for resources?

    RESOLVED: Can't do - with ints you won't know if there
    are any external references to it.

    With Object, you can use Weak maps to allow garbage collection
    but also allow the retaining of resource objects between
    different graphs.

- For returning multiple values, should we use Iterators or something
  else?  ::

      <benja> we could use Stepper-like stateful objects, or
      <benja> have iterators with
      <benja> a close() method or so
      <benja> which allows the object to be re-used
      <benja> so that graphs would cache a few, and only create new ones if too many in 
      use simultaneously

  Lots of possibilities.

    RESOLVED: At first, Iterators.
    Suffixing the return type name will help add / change this later.

- What about queries with more than one component? Say, "give me all triples",
  or "give me all property-value pairs for the given subject node"

- Why is the new API called swamp?
    
    RESOLVED: Fenfires spring out of swamps ;)

- Should we also allow a different kind of observation for making functional
  programming efficient? Right now, for each function value you cache, you
  need to create a new Object for the Obs.  

    RESOLVED: Not yet. We may, at some point, want to put in the functional
    part right into swamp itself...

- How are anonymous nodes represented?

- Literals: searching for, languages, uniqueness?

Problems with jena
==================

The most important problem with Jena appears to be that it does not
support observation.

With Gzz, we were moving towards a functional style of programming
where we could easily cache the object given by f(node) since
the node could be observed.

Jena makes this impossible because there are no change listeners.
Wrapping or extending Jena to something that would have them would
be a major task which would result in a more complicated API.

Another issue I (personally) have with Jena is that it tries
to be too object-oriented: I first thought (and liked that thought!)
that Statements and nodes were independent of the model. However,
this was not the case.

Efficiency is also important: in order for Fenfire to work properly,
*ALL* searches within memory must be O(1). Jena makes no guarantees,
since its goal is to support different implementations of Graph.
For us, the different implementations do not matter so much as raw
efficiency of the memory-based implementation. This is quite different
from most RDF uses, since the usual scenario is that there is not too much
RDF (at least so far).

Design
======

All classes in this API shall be in org.fenfire.swamp.

The resource mapper
-------------------

::

    by mudyc: What's the meaning of Resource Mapper?
       tjl: Resource Mapper is used in special cases, i.e.,
            when two different rdf spaces are diffed.
            Now we can share the single resource between them.
       mudyc: So it has nothing to do with Literals?
       tjl: No.

The global resource mapper (has to be global since resources are model-agnostic)
is simple: The name must be short because it's so widely used. ::

    public class Nodes {
	public static Object get(String res);
	public static Object get(String res, int offs, int len);
	public static Object get(char[] res, int offs, int len);

	public static String toString(Object res);

	/** Append the string version of the resource to the given buffer.
	 * In order to avoid creating too many String objects
	 * when serializing a space.
	 */
	public static void appendToString(Object res, StringBuffer buf);

	public static void write(Object res, OutputStream stream) throws IOException;
	public static void write(Object res, Writer stream) throws IOException;
    }

The appendToString method solves one problem we had in Gzz: when saving,
too many Strings were created for object names. Similarly, having the
toModel method overloaded with different parameter types allows the most
efficient creation of resources without conversions.

We *may* want to make Nodes internally redirectable in the future to
allow alternate implementations; the static interface will not change.

The graph object
----------------

The ShortRDF class shows what a mess the query functions
can easily become.  To avoid this, we'll drop the semantics
(subject,predicate,object) for now and name all methods according to a
general scheme. ::

    public interface ConstGraph {
	Object find1_11X(Object subject, Object predicate);
	Object find1_X11(Object predicate, Object subject);
	...
	Iterator findN_11X_Iter(Object subject, Object predicate);
	...
    }

    public interface Graph extends ConstGraph {
	void set1_11X(Object subject, Object predicate, Object object);
	void set1_X11(Object subject, Object predicate, Object object);
	...

	void rm_1XX(Object subject);
	void rm_11X(Object subject, Object predicate);
	void rm_X11(Object predicate, Object object);
	...

	/** Add the given triple to the model.
	 */
	void add(Object subject, Object predicate, Object object);
    }

The functions are built by the following format:
first, the actual function type:

    find1
	Find a *single* triple fitting the given parts and return the
	part marked X. If there is none, null is returned. If there are
	more than one, an exception is thrown.

	Only a single X may be used.
      
    findN
	Return an iterator iterating through the triples fitting the
	given parts, and return. Even if there are none, the iterator
	is created.  Only a single X may be used.

	For instance, ::

	    findN_1XA(node)

	returns all properties that the node has, and

	    findN_XAA()

	finds all nodes that are the subject of any triple.

    set1
	Remove the other occurrences of the matching triples, replace
	them with the given new one. For example, if triples (a,b,c)
	and (a,b,d) and (a,e,d) are in the model, then after ::

	    set1_11X(a, b, g)

	the model will have the triples (a,b,g) and (a,e,d).  Only a
	single X may be used (restriction may be lifted in the future).
	Only 1 and X may be used.

    rm
	Remove the matching triples from the model. Any amount of As
	may be used.

and, after an underscore, the parameter scheme:

    1
	Given
    X
	Requested / set

    A
	Ignored - may be any

The uniqueness exception
------------------------

For debugging and possibly cool code hacks, the following error gives
enough information to understand what was not unique. ::

    public class NotUniqueError extends Error {
	public final Object subject;
	public final Object predicate;
	public final Object object;
    }

The wildcards are set to null.

For example, if the user calls ::

    graph.find1_11X(foo, bar);

and there are the triples (foo, bar, baz) and (foo, bar, zip) in the model,
then ::

    NotUniqueError(foo, bar, null)

is generated.

Observing
---------

Observing is a part of ConstGraph:::

    public ConstGraph getObservedConstGraph(Obs o);

    /** This observed graph will not be used any more, and
     * if desired, may be recycled by the ObservableGraph.
     * This operation is allowed to be a no-op.
     */
    public void close();

    Object find1_11X(Object subject, Object predicate, Obs o);
    Object find1_X11(Object predicate, Object subject, Obs o);
    ...
    Iterator findN_11X_Iter(Object subject, Object predicate, Obs o);
    ...


The find methods with Obses are included in ObservableGraph because 
this allows the cheap default implementation of ObservedGraph.
In an autogenerated implementation, ObservedGraph would also be generated
for efficiency.

Literals
--------

For literals, we shall use immutable literal objects.

