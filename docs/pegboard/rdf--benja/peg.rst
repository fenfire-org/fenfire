====================================================
``rdf--benja``: Use RDF as the structure for Fenfire
====================================================

:Author:	Benja Fallenstein
:Date:		2003-02-17
:Revision:	$Revision: 1.2 $
:Last-Modified: $Date: 2003/04/02 09:59:58 $
:Type:		Architecture
:Scope:		Major
:Status:	Implemented


Because of the patent troubles, we are moving away from
zzstructure. This PEG proposes to use `RDF`_ as the basis
of Gzz's successor, called Fenfire, and outlines a plan
for the move.

.. _RDF: http://www.w3.org/RDF/


Plan
====

We will use the `Jena API`_ for RDF, at least initially.
Jena is on the complex-but-powerful side; see the
`Jena tutorial`_. It has readers and writers for both RDF/XML
and `N-Triples`_, two serialization syntaxes for RDF.
Many Java projects use it, so it seems like a good place to start.
After we have some experience with Jena, we will decide
whether we can keep it, will need to improve it, or
will need to switch.

Things that need to be converted are:

- PP and xupdf.
- The client (needs rewrite).
- Alph (i.e., ``media/``; needs to grow an XML serialization).
- Storm (should express pointers in RDF).

Xupdf depends on Alph; otherwise, we can parallelize pretty well.
The client will start as a generic RDF editor with Alph support
optional, so we can add that later; PP can start with simple
literals and start using Alph later. Once Storm is ready, Xupdf
will switch to it, and it'll be an option for PP and the client.

I am interested in tackling the RDF editor (client) first, which I want
to call the Fenfire Loom; Tuomas wants to convert PP and xupdf.
So let's push both ways for now, and collaborate on the XML format
for ``media/`` at the same time.

Can we do this in the next two weeks, along with splitting the
project? If so, I suggest that we do this, postponing
the Storm conversion till after that date.

\- Benja


.. _Jena API: http://www.hpl.hp.com/semweb/jena.htm
.. _Jena tutorial: http://www.hpl.hp.com/semweb/doc/tutorial/index.html
.. _N-Triples: http://www.w3.org/TR/rdf-testcases/#ntriples
