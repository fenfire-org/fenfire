=============================================================
PEG model_management--tjl: What are Spaces in the new system?
=============================================================

:Author:   Tuomas J. Lukka
:Last-Modified: $Date: 2003/04/06 13:03:04 $
:Revision: $Revision: 1.4 $
:Status:   Accepted

The Space system was hastily ported from Gzz for the demo deadline.
It needs rethinking.

Issues
======

- What are the relevant concepts in this PEG?

    RESOLVED:
	
	- RDF Model

	- RDF Node

	- Node content

	- Xanalogical transclusions

	- Xanalogical links

- Why the abbreviated name?
    
    RESOLVED: This is a name that will be written SO often
    that acronymizing it is appropriate. All views &c will
    carry an instance of this class.

    Also, the Fen name correctly symbolizes what we have: a swamp
    of data ;)

- Do we need a "home" member in Fen?

    RESOLVED: Not really - not that relevant for RDF

Introduction
============

First, let's reiterate some important differences from Gzz's Space construction.
In Gzz, a Cell was bound to its Space, and Space also provided the facilities for
getting a Cell's content.

In Jena, the situation is dramatically different. Actually, I think it's better;
more natural for RDF at least. RDF nodes and RDF statements are completely 
independent of the Model, and the RDF Model ("space") is just a collection
of Statements, with some optimizations. [ sigh -- was wrong... ]

For Fenfire, we do still need some abstract notion of a node's content; basically,
a mapping between nodes and enfilades, as well an index for use in xu links.
One alternative would be to wrap RDFNodes into Cell-like objects but that would
be very inefficient as well as inelegant.

Changes
=======

I propose the following Fen (FenFire Context) class::

    package org.fenfire;

    class Fen {
	public jena.Model model;

	NodeContent txt;
	
	EnfiladeOverlapIndex enfiladeOverlap;
	XuIndexer xuLinks;
    }

(replace ``jena.`` by ``com.hp.hpl.mesa.rdf.jena.model.``)
This class would be frozen at birth.
at the same time, remove for now IndexManager.

The org.fenfire.NodeContent interface is defined as follows::

    class NodeContent {
	/** Get the vstream in this node.
	 *  Never returns <code>null</code>.
	 */
	Enfilade1D get(RDFNode node);

	/** Set the vstream in this node.
	 */
	void set(RDFNode node, Enfilade1D vstream);
    }

This class would also be frozen.

The classes EnfiladeOverlapIndex and XuIndexer would also be frozen.
