==========================================================================
An easier iteration API for Swamp
==========================================================================

:Authors:  Benja Fallenstein
:Created:  2003-09-22
:Status:   Current
:Scope:    Major
:Type:     Interface
:Affect-PEGs: swamp_rdf_api--tjl


As explained in ``swamp_easier--benja``, Swamp must become
easier to use. One problem to solve is that iterating
through triples isn't as easy as it should be, particularly
when you want to iterate e.g. through all triples with
a particular predicate, with any subject and object.

This PEG proposes a way to iterate through a *set of triples*,
without creating a Java object for each triple, by having
a special iterator-like object that has three nodes
at each iteration step (RDF subject, predicate, and object).

This would be returned by the old ``findN_XXX()`` or the proposed
``find()`` methods (see other PEG).


Issues
======

- Name for the ``Iterator``-like thing? Should it be
  ``Triples`` for short, or ``TripleIter`` for clarity?

  RESOLVED: Clarity. ``TripleIter`` isn't too long.

- What should be the names of the fields of ``TripleIter``,
  which contain the subject, predicate, and object
  of the current triple?

  RESOLVED: ``subj``, ``pred``, and ``obj``: Long enough
  to be descriptive, but not as long as the full names
  (``subject`` etc.). I prefer ``sub``, ``pred``, ``ob``
  for pronouncability, but we compromised on the above--
  Tuomas dislikes ``sub`` and ``ob`` because they are
  prefixes in English (``subordinate``, ``obstinate``).


Changes
=======

We shall use an iterator-like object, ``TripleIter``, with the
following API::

    Object subj, pred, obj;

(These are ``null`` when the object hasn't been
initialized, i.e., ``next()`` hasn't been called yet.)

    /** Advance to the next triple. */
    void next();

    /** Whether there are any more triples to iterate through. */
    boolean hasNext();

    /** Indicate that this <code>TripleIter</code> object won't be
     *  used any more.
     *  This shall only be called by the code that has requested
     *  this object from <code>ConstGraph</code> (through 
     *  <code>.get()</code>). It's purpose is to tell the
     *  <code>ConstGraph</code> that it can be re-used for the
     *  next <code>get()</code>; <code>ConstGraph</code> can then
     *  cache <code>TripleIter</code> objects, making life easier
     *  for the garbage collector.
     *  <p>
     *  Calling this method is not obligatory. (If you don't,
     *  this object will be garbage-collected normally.)
     */
    void free();

    boolean loop() {
        if(hasNext()) {
            next();
            return true;
        } else {
            free();
            return false;
        }
    }

The purpose of ``loop()`` is to enable the common loop
pattern, ::

    for(TripleIter i = graph.find(...); i.loop();) {
        // ...
    }

which would otherwise have to be written as::

    TripleIter i;
    for(i = graph.find(...); i.hasNext(); i.next()) {
        // ...
    }
    i.free();

This isn't just harder to read, it also scopes ``i``
wrongly. With the ``loop()`` pattern, the scope of ``i``
is the body of the loop, which is exactly the code
executed before ``free()`` is called.

(This will be expressed in ``TripleIter``'s javadoc.)

\- Benja


