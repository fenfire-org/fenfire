=====================================================
``SwampTutorial.rst``: Short tutorial / FAQ for swamp
=====================================================

:Authors:   	Matti J. Katila and Tuomas J. Lukka
:Date:		2003-04-15T15:53:31Z

.. contents::

What is Swamp?
==============

Swamp is our RDF API, designed for SPEED, SPEED and SPEED 
and also some flexibility. It is general and does not depend
on the rest of Fenfire or the other projects (alph &c) at all.
(the Obs interface and some such things are used, but they're
trivial classes, not really dependencies).

See the peg swamp_rdf_api--tjl.

What is Fen?
============

Fen is the central "model" class in fenfire. In addition to a swamp,
it contains objects for dealing with nodes' contents and xanalogical
indexing, for searching nodes with transclusions.

Here, there are dependencies to Alph with the xanalogical media.

A node (with content) is conceptually similar to a zzStructure cell.
Not all RDF resources are nodes.




How to set up Fen with swamps?
------------------------------

For example: ::

    fen = new Fen();
    fen.constgraph = fen.graph = new HashGraph();
    fen.txt = new SimpleNodeContent(fen);

Or for speed you probably want caching: ::
 
    fen.txt = new CachingNodeContent(new SimpleNodeContent(fen));

How do I add a triplet in the Swamp?
------------------------------------

Adding triplet is easy, just: ::

    fen.graph.add(sub, pred, obj)

If you want to add attribute (remove all other 
possibilities) you can use set1_11X instead: ::

    fen.graph.set1_11X(sub, pred, obj)


How are Literals used?
----------------------

::

    < mudyc> hmm.. so how literal is done?
    < mudyc> like the coords of a note?
    <@tuomasl2> graph.add(foo,bar, Nodes.getStringLiteral(""+42))
    < mudyc> and other direction?
    <@tuomasl2> x = graph.find1_11X(foo,bar)
    <@tuomasl2> ((Literal)x).getTextString()

(note that this will change at some point for efficiency: we shall
accept Integer, Float &c objects as literals, I think -- tjl)

How do I make a new node?
-------------------------

If you want a new identity, 
use shortcut method Nodes.N()  ::

    Nodes.N()  if URN-5 Namespace
    also N(Namespace..)

If you know the uri you want, use ::

    Nodes.get(uri)


How to make a triplet?
----------------------

There is no special "triplet" object in the API at the moment.

::

    < mudyc> tuomasl2: how do i do a new triplet?
    <@tuomasl2> mudyc: if the last element is unique, set1_11X

otherwise you should use graph.add method: ::

    fen.graph.add(sub, pred, onj);

See "How do I add a triplet" above.

Find nodes
----------

::

    < mudyc> after this: Iterator findN_11X_Iter(Object e0,Object e1) ; what does the iterator
             return after iter.next()?
    < mudyc> Triplet?
    <@tuomasl2> no, no Triplet objects here yet
    <@tuomasl2> it returns e2

