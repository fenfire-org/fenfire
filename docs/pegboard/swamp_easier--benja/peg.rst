==========================================================================
An easier API for Swamp
==========================================================================

:Authors:  Benja Fallenstein
:Created:  2003-09-22
:Status:   Current
:Scope:    Major
:Type:     Interface
:Affect-PEGs: swamp_rdf_api--tjl


Tuomas always makes the point that Swamp must be fast,
because it is called in the inner loops of Fenfire.

But Swamp must also be easy to use, because it is
the API that everyone hacking Fenfire will have to learn
in order to do anything, so it is vital that it doesn't
have a steep learning curve.

(Besides, easy-to-read and easy-to-use APIs are of course
the right thing to have anyway.)

Part of the original proposal in this PEG is split off
into ``swamp_easier_iteration--benja`` because mudyc
requested it.


Issues
======

- Should we keep the current methods, and just add those
  proposed in this PEG? There is a lot of code using the
  current methods; we could just deprecate them for now.

  RESOLVED: No. The point is to *simplify* the API;
  adding more variants doesn't do that. 

  Deprecating the current methods but not changing the code
  that uses them adds to the confusion, rather than making
  that code simpler.

  (I have volunteered to change the existing code
  if this PEG is accepted.)

- What should happen in ``getObject()`` etc.
  if there is more than one triple of the requested form?

  RESOLVED: Do the same as currently: throw
  ``NotUniqueException``. There are some problems
  associated with that (see mailing list discussions),
  but they are out of scope for this PEG.

- What should be the name of the method returning
  a ``TripleIter``? ``get()``, for symmetry with
  the Collections API and the other functions;
  ``find()``, similar to what we have now; or
  ``query()`` for similarity with e.g. Aaron Swartz'
  Python API for RDF?

  RESOLVED: ``find()``. Tuomas explains:

      I feel better about ``find()``, since it 

      1. feels lighter than query
      2. feels heavier than get, as it should - we don't *necessarily*
         have all indices ready.

- Should you be able to query just subjects, i.e. ignoring objects,
  having them ``null`` in ``TripleIter`` and not getting duplicates?

  RESOLVED: No-- this is what ``getSubjects()`` etc. is for;
  working with a ``Set`` is more useful and consistent in these cases 
  than working with a ``TriplesIter`` (and having one of its elements
  ``null``, i.e. not really iterating through *triples*, etc.).


A flavor of the API
===================

First of all, we need a good way for iterating
through a set of triples. I propose the following
interface::

    for(TripleIter i = graph.get(_, RDF.type, _); t.loop();) {
        System.out.println(i.subj+" is instance of "+i.obj);
    }

I.e., have our own iterator-like thing, which iterates
through a set of *triples*-- rather than nodes-- but doesn't
need to create objects for every one of these triples.

For good measure, here's how the above code would look
in the current API::

    for(Iterator i=graph.findN_X1A(RDF.type); i.hasNext();) {
        Object sub = i.next();
        for(Iterator j=graph.findN_11X(sub, RDF.type); j.hasNext();) {
            Object ob = j.next();
            System.out.println(sub+" is instance of "+t.ob);
        }
    }

However, to be fair, my code isn't how it would look
when efficiency is at a premium. (Then again, when I print
to the console inside the loop, efficiency isn't at a
premium anyway... but whatever...) The *fast* version
would look like this [#speed]_::

    for(TripleIter t = graph.find_X1X(RDF.type); t.loop();) {
        System.out.println(t.sub+" is instance of "+t.ob);	
    }

Not quite as straight-forward, but still better than
what we have now.

In Jython, the loop would look like this::

    t = graph.find(_, RDF.type, _)

    while t.loop():
        print "<%s> is instance of <%s>" % (t.sub, t.ob)

A bit different than in Java, but still recognizable.


Changes
=======

We'll make it a convention that classes using the API
have this at the top::

    static final Object _ = null;

You don't have to have this, but it makes things easier to read.


``ConstGraph``
--------------

The current methods for finding triples shall be removed
from ``ConstGraph`` and be replaced by the following API::

    /** Get an iterator through all triples in the graph
     *  matching a certain pattern.
     *  If <code>subject</code>, <code>predicate</code> and/or
     *  <code>object</code> are given, the triples must match these.
     *  If any of the parameters is <code>null</code>,
     *  any node will match it.
     */
    TripleIter find(Object subject, Object predicate, Object object);

    // Versions that don't allow wildcards (``null``)
    TripleIter find_XX1(Object predicate, Object object);
    TripleIter find_1X1(Object subject, Object object);
    ...

    /** Get the subject of the triple matching a certain pattern.
     *  If <code>subject</code>, <code>predicate</code> and/or
     *  <code>object</code> are given, the triple must match these.
     *  If any of the parameters is <code>null</code>,
     *  any node will match it.
     *  @returns The subject of the triple, if there is one,
     *           or <code>null</code> if there is no such triple.
     *  @throws  NotUniqueException if there is more than one
     *           matching triple in the graph.
     */
    Object getSubject(Object subject, Object predicate, Object object)
        throws NotUniqueException;

    Object getSubject_X1X(Object predicate) throws NotUniqueException;
    ...

Note: The reason for having ``subject`` as a parameter
for ``getSubject()`` is that it's easier to read. It will
almost always be "``_``" (i.e., ``null``). It shall work
consistently, though: If a subject is given, and there is
such a triple in the graph, return that subject; otherwise,
return ``null``.

    /** Get the subjects of all triples matching a certain pattern.
     *  If <code>subject</code>, <code>predicate</code> and/or
     *  <code>object</code> are given, the triple must match these.
     *  If any of the parameters is <code>null</code>,
     *  any node will match it.
     *  <p>
     *  The set is backed by the graph (i.e., changing the graph
     *  changes the set, e.g. if the last triple with a given
     *  subject is removed from the graph, that subject
     *  disappears from the set). The set is <em>not</em> modifiable
     *  (e.g. the <code>add()</code> and <code>remove()</code> methods 
     *  throw <code>UnsupportedOperationException</code>).
     */
    Set getSubjects(Object subject, Object predicate, Object object);

Backing is generally used in the Collections API, and allows
for lighter implementations of the method. For example,
when using ``new TreeSet(graph.getSubjects(_, _, _))`` to get
a *sorted* set of all subjects in a graph, it would be quite
wasteful if ``getSubjects()`` created a ``HashSet`` only to have
it discarded after being used in the constructor of ``TreeSet``.

    Set getSubjects_XX1(Object object);
    ...

    // getObject(), getObjects() similarly
    // getPredicates() similarly

``getPredicate()`` is essentially useless, so we don't
have it. This is symmetric with not having ``setPredicate()``,
below. (If you need something to the same effect,
you can use ``find()`` manually.)

``getPredicates()`` is useful, mostly for
getting *all* predicates used in a graph.

Note that we don't have ``A`` in the function variants
any more, just ``1`` and ``X``, with ``X`` being equivalent
to passing ``null`` in that position to the generic method.

(E.g., ``getSubjects_XXX()`` is equivalent to
``getSubjects(_, _, _)``, returning the set of all subjects
in the graph.)


``TripleIter``
--------------

For the API of the iterator-like object, ``TripleIter``,
see ``swamp_easier_iteration--benja``.


``Graph``
---------

The current methods for adding, changing and removing triples
shall be removed from ``Graph`` and replaced by::

    /** Add a triple to this graph. */
    void add(Object subject, Object predicate, Object object);

    /** Remove all triples matching a certain pattern from this graph.
     *  If <code>subject</code>, <code>predicate</code> and/or
     *  <code>object</code> are given, the triple must match these.
     *  If any of the parameters is <code>null</code>,
     *  any node will match it.
     */
    void remove(Object subject, Object predicate, Object object);

    void remove_X1X(Object predicate);
    void remove_1XX(Object subject);
    ...

    /** Replace all triples with the given predicate and object
     *  with the given triple.
     */
    void setSubject(Object subject, Object predicate, Object object);

    /** Replace all triples with the given subject and predicate
     *  with the given triple.
     */
    void setObject(Object subject, Object predicate, Object object);

We don't have ``setPredicate()`` because it is essentially useless
and potentially harmful-- someone using it almost certainly
intended to do something else.

This is never a problem because the ``setXXX()`` methods
are only a convenience. You can always do::

    graph.remove(_, predicate, _);
    graph.add(subject, predicate, object);

if you *do* happen to have some esoteric use for it.


Conclusion
==========

I believe this API will be substantially simpler to use 
than the one we have at the moment, and not lose
anything w.r.t. speed. In fact, it may speed things up
in the future, because we can cache the ``TripleIter`` objects.

\- Benja


.. [#speed] The speed difference between ``find(_, RDF.type, _)``
   and ``find_X1X(RDF.type)`` is that ``find()`` has to check
   for ``null`` in each of the arguments (that's three ``jnz``
   instructions) and do one method call. (If we can get the compiler
   to inline the ``find_XXX()`` variants, the method call goes away.)
   This may actually be fine even in an inner loop. (The
   hashtable lookups inside the loop will probably not be as cheap!)

   One might think that all fields of ``TripleIter``
   (``subj``, ``pred``, ``obj``) need to be fetched for each
   iteration, but that's actually not true: Only those that are
   different from the previous iteration need to be fetched.
   (The implementation of the iterator can easily know
   which those are.)

   The only situation where this makes a speed difference
   is something like::

       for(TripleIter i = graph.find(_, RDF.type, _); i.loop();) {
           System.out.println("Has an rdf:type: "+i.subj);
       }

   where fetching the ``obj`` each time is superfluous.
   This situation is not expected to be frequent enough
   to be a problem.