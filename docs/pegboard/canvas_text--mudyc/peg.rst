======================================================
``canvas_text--mudyc``: Spatial canvas text enchaments
======================================================

:Author:   	Matti Katila
:Date:		2003-05-02
:Stakeholders: 
:Revision:	$Revision: 1.5 $
:Last-Modified: $Date: 2003/05/09 14:00:43 $
:Type:		User Interface, Spatial canvas RDF structure, Text format
:Status: 	Current


.. contents::


I do like to see improvements in text editing/handling in
spatial canvases. Current model is based on pp's way
to handle text and that is too limited even for pp.
There are at least three disagreeing circumtances:
wysiwyg, text editability, spatial positions.
This enchament propouse is meant to be compromise of
these three circumtances for our own best.


Issues
------

- Would Nile like text editability be too complex for pp?

    RESOLVED:
    Yes and no. Word/Sentence/Paragraph modes would not be 
    set by default. Paragraph thinking already solves much 
    of the current problems of pp text editability also.

- If we have associated a node [foobar] and
  someone likes to split it as a [foo], [ ], [bar].
  What we do for the rdf propertys while splitting?

    RESOLVED:
    We do copy all propertys from [foobar] to [foo] and [bar],
    except the property 'nextNode' which is set like:

    Old: ::

       [prev] --nextNode--> [foobar] --nextNode--> [next]

    New: ::

       [prev] --nextNode--> [foo] --nextNode--> [ ] -->
 
          >>--nextNode--> [bar] --nextNode--> [next]

- Why sentence hasn't words but nodes?

    RESOLVED:
    If one of the future goals is rst generated presentation
    we need to be able to say where there are spaces and it's 
    easier to not present unwanted spaces if those can be 
    identified with ' ' or such.

- What to do with emptys (spaces like [ ]) in rst generated view?
    
    RESOLVED:
    That's the point, we don't show them but we do have to show them 
    in text mode view.

- What to do if someone wants to associate a piece of a sentence but 
  more than one node?

    RESOLVED:
    We don't support that or we associate nodes one by one.


RST as a inspirer
-----------------

I do like very much of reStructuredText philosophy.
It is very handly to edit in *text mode* but of course
looks fine in *generated mode* where we are used to 
read it. Normally you can't edit text in the generated 
mode but I see this as a lack. At least I usually would 
like to edit typos found in generated text.


Nile as a inspirer
------------------

As discussed with Asko about rst like canvases he quite 
fast informed me about Nile. After looking to Nile I
needed to come back to design board. Yes, we absolutely
need nodes, sentences of nodes and paragraphs as a primitives. 


What's it all about?
--------------------

This proposal is all about:

    - spatial placing of different paragraphs into one canvas.
    - one paragraph includes sentences one after the other.
    - sentence includes nodes(as words, letters, spaces, numbers etc.) 
      one after the other.
    - nodes are contained to canvas.

Nodes' coordinates are generated because of they are additive to
paragraph's coordinates. So, a node isn't spatially placed but a paragraph is.

One sentence usually has words, numerals and such. We must also 
count spaces to be included into sentences because we should 
reach for real text editability when we really dominate the text buffer.
So the following text ``This is a sentence.`` would be splitted to nodes
marked by '[ ]': ::

      1      2    3     4    5    6       7
    [This], [ ], [is], [ ], [a], [ ], [sentence.]

Now we are able to link any of the seven nodes with pp association.
Of course we don't like even try to link empty - [ ] -nodes to anywhere but
it would look very silly to link [foobar ] to somewhere. 
Problems also occur if one adds a space and word after 'r'. 
[foobar ] would have to be splitted and now [foobar] and [ ] would be linked!


Changes to structure
--------------------

Current structure: ::

    (Paper)  --PAPER.contains---> (A note)

    (A note) --SPATIAL.coordsX--> literal
    (A note) --SPATIAL.coordsY--> literal

Proposal addition to this: ::

    (Paper) --RST.beginParagraph--> (RST.Paragraph)

    (RST.Paragraph) --SPATIAL.coordsX--> literal
    (RST.Paragraph) --SPATIAL.coordsY--> literal

    (RST.Paragraph) --RST.firstSentence--> (RST.Sentence)

    (RST.Sentence)  --RST.nextSentence-->  (RST.Sentence)
    (RST.Sentence)  --RST.nextNode-->      (A note)

    (A note)        --RST.nextNode-->      (A note)

Again, (A note)s' coordinates are generated because of they are 
additive to RST.Paragraph's coordinates.


Goal
----

The first goal for FenPDF and PP is text editability good enough
and spatial placing for text - both of these as soon as possible.


Future goal
-----------

RST generating of text build up of the paragraphs placed on canvas.
One issue would be how text format is handled and does this proposal
approach it in different manner. 

