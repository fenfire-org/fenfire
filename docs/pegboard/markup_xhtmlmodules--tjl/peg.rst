=============================================================
PEG markup_xhtmlmodularized--tjl: 
=============================================================

:Author:   Tuomas J. Lukka
:Last-Modified: $Date: 2003/04/09 20:02:32 $
:Revision: $Revision: 1.4 $
:Status:   Current

Marked-up text has always been a difficult but focal point for us.  
This PEG proposes using some modules from modularized XHTML
(http://www.w3.org/TR/xhtml-modularization/, http://www.w3.org/TR/xhtml11/)
to accomplish it.

This PEG extends and changes the Alph PEG ``styled_text--benja`` by providing
a concrete proposal of the implementation of the "formatted xanalogical text",
with some different properties.

Issues
======

- How to handle images? The img tag of the image module is not good.
  A new alph element? What properties do we need? Are stylesheets enough?

    RESOLVED: New alph elements: is (imagespan) and ps (pageimagespan).
    These elements work just like HTML's <img> except for the attributes.
    We need the block and the x,y location and w, h. For page spans,
    also the first page and number of pages used. Pagespan layout should
    probably be left to the stylesheet / presentation layers.

- styled_text--benja proposes an object containing a list of characters
  each of which has a list of styles attached (efficiently stored as an
  enfilade). That's not what you're proposing.

  ... more about this issue

- Does using XHTML with the proposed API make styled text too
  difficult to use (too different from strings)?

- How is copy & paste done? Copying over a paragraph boundary, and pasting 
  that somewhere, should insert a paragraph boundary where it is pasted--
  how to represent the copied part as XHTML?

- Doesn't the multiple elements -> one xutext element make it more complex to understand?


Changes
=======

The only module suitable for the current proposal appear to be the Text module,
possibly at first only the subset in Inline Phrasal.

In the future, we should probably consider next the List module, the Presentation module, 
the Style Sheet module, and the Style Attribute module.

For example, the following would be a legal marked-up xanalogical text fragment.
The namespace ``h`` refers to our subset of modularized XHTML and the namespace ``a``
to the alph namespace.

    ...<a:ts b="..." s="15" e="20"/><h:em><a:ts b="..." s="20" e="22"/>
    <a:uts b="..." s="42" t="[his]"/></h:em>...

This shows two text span types, and 
how the ``ts`` span has been split by the onset of the emphasis.

We need to define a suitable API for accessing the text. A miniature variant of DOM,
with xanalogical operators, seems appropriate.

First, some parts of DOM will be disabled for this use; in fact,
only the classes
Attr, CharacterData, DocumentFragment, Element, Node
will be used.

The additional node XuText will be defined; XuText corresponds
to one or more Xanalogical spans. This implies that 
**the alph text elements shall never be seen as DOM elements**.

Also, the interface Node shall be extended by the Xanalogical 
overlap query

    boolean overlaps(SpanCollection coll);

where SpanCollection is some type of overlap interface for a collections
of unordered spans, which Node shall also implement.
