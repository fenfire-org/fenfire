=============================================================
PEG vocabprocess--tjl: process for RDF vocabularies
=============================================================

:Author:   Tuomas J. Lukka
:Last-Modified: $Date: 2003/05/13 03:14:40 $
:Revision: $Revision: 1.14 $
:Status:   Implemented

It seems that vocabularies are easy to create but difficult
to define and maintain. We need more process for vocabularies
which will be put into actual public use.

This PEG changes the current fenfire vocabulary quite a bit,
moves a lot of stuff into lava and creates rules on how 
new vocabulary terms are started and how they end up in the proper
vocab instead of lava.

Issues
======

- Is it ok to have a separate namespace for experimental things?

    RESOLVED: Yes, conversion can be automated / done with inference.
    Any URI in the experimental space should not be widely used
    before being properly defined and accepted.

    It is desirable to be able to *see* from the URI which data is
    stable, which is not, without having to look at the definitions.

    For example, grepping for the experimental ns from a file would
    give you a good idea whether your data is based on stable code.

    The idea is also that conversion wouldn't be too often necessary:
    the move from lava to real wouldn't require too much work, just a PEG
    round.

    The experimental namespace is part of lava, which means
    that we automatically exclude it from production code.


- What about ALPH? How much should we be defining ALPH things
  here, outside Alph proper?

    RESOLVED: Remove & rename. We are talking about fenfire
    things, not alpha things, with content &c.

    We could, in principle, move some of it to Alph, but we don't
    want to depend on Swamp there.

- Should we merge spatial into canvas2d?

    RESOLVED: Yes. Spatial will most likely not 
    be used without canvas2d?  The point is, the x and y coordinates
    are pretty specific to canvases, and while containment + x +
    y is a reasonable structure orthogonal to everything else, 
    separating containnment and x and y to does not seem make sense.

    Also, if it's spatial, it should also have z ;)

    It would be acceptable to have Spatial be a *superclass* 
    of Canvas2D, but this is probably not worth the trouble.

    Another point: the x and y are the *default* coordinate
    attributes, see the discussion on extensibility
    (to be done later) in javadoc.

- What should be the namespace prefix? ``vocabulary``,
  ``rdf`` or ``terms`` or ``rdfv``?

    RESOLVED: ``rdf-v``. For "RDF Vocabulary", which mentions
    the term RDF as well as that this is not just RDF, it's something else.

- Should URIs contain timestamps?

    RESOLVED: [benja:] ::

	> - NEVER use URIs you want to be permanent without a timestamp.
	>
	> Actually the above could be used with a timestamp, but you don't
	> explicitly say so. Please define a full template for namespaces. I
	> suggest::
	>
	>     http://fenfire.org/terms/YYYY/MM/[DD/]namespacename
	>
	> where ``YYYY``, ``MM``, ``DD`` are the date (or month) when the
	> namespace was created, and ``namespacename`` is chosen by the person
	> creating the namespace. (DD is optional.)
	>
	> Items in the namespace would have the form::
	>
	>     http://fenfire.org/terms/YYYY/MM/[DD/]namespacename#id
	>
	> where ``id`` is chosen by the person creating the namespace.

- Should we separate cLink from FF? A separate CLink namespace?

    RESOLVED: Yes. It is sufficiently orthogonal to other structures.

- Should we merge PP (directed links) into FF, or something else? 

    RESOLVED: Move to DLINK, Analogous to CLink and Canvas2D, 
    this property creates its own orthogonal substructure. 

- What should the types of coordx, coordy be? Float? Int? Numeric?

    RESOLVED: Floats. We use them with all coordinate systems anyway.
    If you want gridding, it's something for the UI to provide.

- Should each vocabulary also define its "identity", a RDF node that 
  is used to talk about that vocabulary. This could be useful for describing
  the schema of some data, e.g. "This is spatial canvas data with dLinks".

    RESOLVED: Not yet. While this might be nice, it needs further thinking.

- Where should the RDF schemas the objects be defined?

    RESOLVED: Not yet. A further PEG will address this issue.

- Should the URI names contain the .HTML suffix?

    RESOLVED: No.  It serves zero purpose (except allowing the
    website admin to be lazy) and makes it unintuitive to serve non-HTML
    representations of the namespace (such as RDF Schema).

- Should we stick to the previous convention that namespace
  class names should be all caps?

    RESOLVED: Yes. It says "constant" to C, C++ and Java
    programmers, but leaves the actual field names easier to read.

Changes
=======

Goals of the structure
----------------------

It would be Really Nice if we could structure the vocabularies so
that each class contains a small, self-contained structural universe.
This means that the code implementing access using the vocab. of a certain
class should be relatively independent of the other vocabularies.

For instance, a spatial canvas is a reasonable unit: there is a canvas,
it contains certain nodes at certain locations. However, structlinks
or content links between different canvases do not
actually belong in the same place; they are orthogonal to the spatial
structure.

Currently, our code is pretty well along this structure: CanvasView2D
takes care of the spatial canvas, and PPConnector (name needs to change)
of the structlinks.

The more independent we can make the codes using the different
orthogonal structural pieces, the easier it will be to
slot in new behaviours.

Overall changes 
---------------

Create new package, ``org.fenfire.vocab.lava``.

Move most of the vocabulary entries into lava.

Freeze ``org.fenfire.vocab``. Changes only through PEG process.

Change the prefix ``http://fenfire.org/vocabulary/`` to
``http://fenfire.org/rdf-v/``.  After the prefix, each namespace
shall contain the year and month it was originally defined in,
in the form ``2003/05/``. After that, the name of the namespace,
lowercase.  Finally, the name of the 
resource is specified using ``#`` and the resource name.
So, for instance, FF.content would be
``http://fenfire.org/rdf-v/2003/05/fenfire#content``.


All new words define without PEG go into org.fenfire.vocab.lava
and use the prefix ``http://fenfire.org/EXPERIMENTAL/rdf-v``, after 
which the URI continues as above.

All entries in vocabulary classes shall have their **official**
definitions there, in their javadocs. There shall be no members
or classes without good documentation. This is mandatory for
offical vocabularies and **strongly** recommended for lava
vocabularies.

The vocabulary classes' names shall be, as before, all caps.

Vocabulary changes, prior to freezing
-------------------------------------

ALPH
""""

Remove ``content``, is in FF.

Remove ``clone`` and ``cloneType`` and ``dataType``, 
not current/relevant.

Remove ``xuType``, should be ``xuLinkType``.

Then, we have left ``xuLinkFrom``, ``xuLinkTo``, ``xuLinkType``.
We should probably avoid 'xu' in the permanent names,
just in case. These should be moved to CLINK (defined below)
as CLINK.CLink, CLINK.cLinkFrom,
CLINK.cLinkTo for clink, "content link", a term Ted at some point used.

FF
""

Retain. Javadocs::

    /** RDF Vocabulary of central concepts of Fenfire.
     */
    public class FF {

	static public final String _nsId = 
	    "http://fenfire.org/rdf-v/2003/05/ff";

	/** A property signifying fluid media "content" of a node.
	 * Used as  (node, FF.content, literal) where the literal is
	 * an XML literal containing an enfilade
	 * parseable by alph.
	 * This is analogous to spreadsheet or zzStructure cell contents.
	 */
	static public final Object content;
    }

CONTENTLINK
"""""""""""

A new namespace for Xanalogical content links.
Javadoc::

    /** RDF Vocabulary of content links.
     */
    public class CONTENTLINK {
	static public final String _nsId = 
	    "http://fenfire.org/rdf-v/2003/05/contentlink";

	/** The RDF class for content links. An node which is a content link
	 * must have both the cLinkFrom and cLinkTo properties.
	 */
	static public final Object Link;
	/** The Alph-parseable enfilade XML literal of a content link from-end.
	 */
	static public final Object from;
	/** The Alph-parseable enfilade XML literal of a content link to-end.
	 */
	static public final Object to;
    }


PAPER, SPATIAL
""""""""""""""

Combine to one class, CANVAS2D.

Rename coordX, coordY to x, y.

Javadoc::

    /** RDF Vocabulary of 2D spatial canvases.
     */
    public class CANVAS2D {
	static public final String _nsId = 
	    "http://fenfire.org/rdf-v/2003/05/canvas2d";

	/** The RDF class of spatial 2D canvases.
	 * Canvases contain (with the "contains" property)
	 * nodes, which shall have the "x" and "y" properties.
	 */
	static public final Object Canvas;
	/** The property by which the canvas is connected to
	 * the nodes, as (canvas, contains, node).
	 */
	static public final Object contains;
	/** The x and y coordinates of a node on a canvas.
	 * (node, x, literal), where the literal is parseable
	 * as a floating-point number (similar to Java doubles). 
	 * Note that these are the <em>default</em> coordinate
	 * properties: later on, we might make it possible for a Canvas2D
	 * to define its own coordinate attributes, which would take
	 * use close to Ted's floating world ideas.
	 */
	static public final Object x, y;
    }

PP
""

Remove this class. 
Move association to STRUCTLINK.
PP is really a special-case user interface for a subset of the full
fenfire structure, so it's quite reasonable not to include a special vocabulary for it.

STRUCTLINK
"""""

New class for what used to be called PP connections (directed, typeless connections). 
Javadoc::

    /** RDF vocabulary for directed (so far typeless)
     * one-to-one links.
     */
    public class STRUCTLINK {
	static public final String _nsId = 
	    "http://fenfire.org/rdf-v/2003/05/structlink";

	/** The directed link association.
	 * A and B are linked by the tuple (A, DLINK.dLink, B)
	 */
	static public final Object linkedTo;
    }


RDF
"""

Leave as is, javadoc properly. Javadoc::

    /** RDF vocabulary of central RDF URIs defined outside fenfire.
     */
    public class RDF {

	/** The RDF type attribute. A node's type can be declared 
	 * to be Foo 
	 * by a triple (node, RDF.type, Foo).
	 */
	static public final Object type;
    }


RST
"""

Move to lava

