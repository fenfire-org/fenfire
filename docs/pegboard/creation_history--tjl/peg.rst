==========================================================================
PEG creation_history--tjl: Object creation order RDF schema
==========================================================================

:Authors:  Tuomas Lukka
:Date-Created: 2003-07-28
:Last-Modified: $Date: 2003/08/04 08:59:53 $
:Revision: $Revision: 1.6 $
:Status:   Accepted
:Stakeholders: benja, mudyc, humppake
:Scope:    Minor
:Type:     Architecture

.. Affect-PEGs:

Time isn't always linear. It can branch. Maybe we should keep track
of the creation of objects as a tree based on versions.

Issues
======

Changes
=======

Create a new namespace, ``http://fenfire.org/rdf-v/2003/07/treetime``.

This namespace shall contain::

    /** An time-like ordering between creation of objects.
     */
    public class TREETIME {
	/** A type that declares a relation to be 
	 * a time-like relation.
	 * This namespace defines one time-like relation,
	 * "follows" for common use, more may be defined
	 * to avoid conflicts, using the TimeRelation class.
	 */
	static public final Object TimeRelation;

	/** (A, follows, B) means that A was created after B.
	 */
	static public final Object follows;

	/** (X, currentOf, follows) means that X is the latest
	 * entity in a "follows" chain. This applies to other
	 * relations, too.
	 */
	static public final Object currentOf;

	/** (X, firstOf, follows) means that X is the root
	 * of a "follows" chain. This applies to other relations,
	 * too.
	 */
	static public final Object firstOf;
    }

These would be used to keep track of in what order canvases
were created and PDF files imported in FenPDF.

When merging, all but one currentOf relations would be deleted.

The treeness arises from merging: merge does not have
to artificially pack the sequences into one sequence but
can just take all "follows" triples from all versions, 
hence the name.


