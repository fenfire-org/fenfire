==================================================
PEG: Alter Canvas2D vocab to support transclusions
==================================================

:Authors:       Benja Fallenstein
:Date-Created:  2003-07-03
:Last-Modified: $Date: 2003/07/28 13:54:12 $
:Revision:      $Revision: 1.4 $
:Status:        Incomplete
:Scope:         Minor
:Type:          Interface


The Canvas2D vocabulary (``http://fenfire.org/rdf-v/2003/05/canvas2d``)
specifies properties and classes for placing things on 2D canvases.

This vocabulary does not, however, take into account that
a node's coordinates on some paper are not properties of that node;
e.g., when we have a node "The Fenfire system," then this system
does not have x coordinate 145 and y coordinate -32; rather, a
rendition of the node representing the Fenfire system is placed
at these coordinates.

The failure of taking this into account means that there cannot
be transclusions of a node, i.e. one node cannot be placed
on multiple papers; in Fenfire, transclusion must be supported
for *anything*, i.e., whenever some thing is in a "context,"
it must be possible to place it in more than one context.

In order to make nodes transcludable, the vocabulary 
must be changed.


Issues
======

- Should the definition of the existing namespace be changed,
  or should we create a new namespace?

  RESOLVED: New namespace. Three out of four URIs in the
  namespace cannot be used with their existing definition
  (and changing the definition of an existing URI is
  bad practice); retaining the last URI (``#Canvas``)
  simply isn't worth it.

- Do we need a converter?

  RESOLVED: As save/load hasn't worked for FenPDF so far,
  we can expect that nobody has data in it yet; unless
  somebody speaks up, we'll assume we need no converter.

- [tjl] Couldn't we just resolve this by a more generic facility:
  a clone property. Then, when looking for the node's content you'd always
  follow the clone property.

  This would work for other things beside just nodes on canvases.

- [tjl] Where does the terminology ":Domain:", &c come from?
  Is there a standard somewhere that specifies these?

Changes
=======

Create a new namespace, 
``http://fenfire.org/rdf-v/2003/07/canvas2d-bis``,
containing the following names and definitions:

``http://fenfire.org/rdf-v/2003/07/canvas2d-bis#Canvas``
    The RDF class of spatial 2D canvases.
    Canvases contain (with the ``#contains`` property)
    node transclusions, which may have the ``#x`` and ``#y`` 
    properties, depending on the type of canvas.

``http://fenfire.org/rdf-v/2003/07/canvas2d-bis#NodeOnCanvas``
    A transclusion of a node onto a canvas. This represents
    one instance of a node on a specific canvas. The node
    transcluded is related to this node through the
    ``#transcludes`` property.

    Having this is necessary so that a single node can be
    placed on more than one canvas.

``http://fenfire.org/rdf-v/2003/07/canvas2d-bis#contains``
    A property relating a canvas to the node transclusions
    it contains.

    :Domain:      ``#Canvas``
    :Range:       ``#NodeOnCanvas``
    :Cardinality: ``1:n``

``http://fenfire.org/rdf-v/2003/07/canvas2d-bis#transcludes``
    A property relating a transclusion to the node it is
    a transclusion of.

    :Domain:      ``#NodeOnCanvas``
    :Range:       ``#Resource``
    :Cardinality: ``n:1``

``http://fenfire.org/rdf-v/2003/07/canvas2d-bis#x`` and ``http://fenfire.org/rdf-v/2003/07/canvas2d-bis#y``
    The x and y coordinates of a node on a canvas.
    Note that these are the *default* coordinate
    properties: later on, we might make it possible for a Canvas2D
    to define its own coordinate attributes, which would take
    use close to Ted's floating world ideas.

    The objects in this namespace are literals
    of type `xsd:double`_.

    :Domain:      ``#NodeOnCanvas``
    :Range:       `xsd:double`_
    :Cardinality: ``n:1``

    .. _xsd:double: http://www.w3.org/TR/xmlschema-2/#double

Change ``org.fenfire.vocab.CANVAS2D`` to use this namespace.

Make the necessary changes in the other code so that the
rules of this namespace are respected; in particular,
the coordinate literals need to be of type `xsd:double`_.

\- Benja
