==========================================================================
PEG sub_rdf_xml--benja: Sub-RDF/XM
==========================================================================

:Author:  Benja Fallenstein
:Created: 2003-08-18
:Changed: $Date: 2003/08/19 05:59:23 $
:Status:  Current
:Scope:   Major
:Type:    Architecture


I think that it might be useful to have a simplified version of RDF/XML
(the XML serialization of RDF). The full RDF/XML serialization is

- difficult to understand
- difficult to parse
- very difficult to process with tools like XSLT

I think that the first two points are important because it may
well be that the format is simplified in the future-- but at least
in Storm specifications, we will have to be backwards-compatible
because of the persistency commitment. For other parts of Fenfire,
it would also be very good if future versions could read graphs
written now.

XSLT is important because it is a very powerful technology that I think
we will want to use in Storm in the future; for example, when using
Storm with a browser, we could take some RDF data written out by
Fenfire and turn it into a formatted Web page. (Any detailed exploration
of this would require a future peg or dart, though.)

To address these concerns, I propose that we define a subset of
RDF/XML which is still able to serialize all RDF graphs, and define that:

- In both Fenfire and Storm, we only *write* Sub-RDF/XML;
- In Storm, we only *read* Sub-RDF/XML;
- In Fenfire, we read any valid RDF/XML, as before;
- When we define a canonical RDF serialization later, it will be
  a subset of Sub-RDF/XML (with additional rules saying in which order
  the triples appear, which whitespace is to be used, etc.).

I.e., in Fenfire, we're strict in what we send and liberal in what
we accept. In Storm, we're strict both ways, because we have to guarantee
backwards compatibility: Everything we accept now, we *guarantee*
will also be accepted twenty years from now (if Storm is still around).

We *may*, at a later time, create a new version of Sub-RDF/XML with
relaxed requirements. The point is that it's possible to go that way
(allow more alternatives), but, in Storm, impossible to go the other
way around (allow less alternatives than before), because of
the persistency commitment.


Issues
======

.. None so far.


Example
=======

Before we go into the details of the serialization, here's an example.
In triples syntax, the graph we want to serialize is, ::

    <http://example.org/~alice> foaf:mailbox <mailto:alice@example.org>
    <http://example.org/~alice> foaf:knows   <http://example.org/~bob>
    <http://example.org/~alice> foo:name     "Alice Abberson"

    <http://example.org/~bob>   foaf:mailbox <mailto:bob@example.org>
    <http://example.org/~bob>   foaf:knows   <http://example.org/~alice>
    <http://example.org/~bob>   foaf:knows   _:charlie
    <http://example.org/~bob>   foo:name     "Bob Hunk"

    _:charlie                   foo:name     "Charlie Brown"

(The ``_:charlie`` is a blank or "anonymous" node which does not
have a URI.)

A Sub-RDF/XML serialization (with namespaces omitted) would be::

    <rdf:RDF>
        <rdf:Description rdf:about="http://example.org/~alice">

            <foo:name>Alice Abberson</foo:name>

            <foaf:mailbox rdf:about="mailto:alice@example.org"/>
            <foaf:knows   rdf:about="http://example.org/~bob"/>

        </rdf:Description>
        <rdf:Description rdf:about="http://example.org/~bob">

            <foo:name>Bob Hunk</foo:name>

            <foaf:mailbox rdf:about="mailto:bob@example.org"/>
            <foaf:knows   rdf:about="http://example.org/~alice"/>
            <foaf:knows   rdf:nodeID="charlie"/>

        </rdf:Description>
        <rdf:Description rdf:nodeId="charlie">

            <foo:name>Charlie Brown</foo:name>

        </rdf:Description>
    </rdf:RDF>


Definition of Sub-RDF/XML
=========================

The root tag of a Sub-RDF/XML is an ``rdf:RDF`` tag, as in normal
RDF/XML.

Inside this tag, for every node that is a subject in the RDF graph,
there is exactly one ``rdf:Description`` tag. For URI nodes, this
tag has an ``rdf:about`` attribute, with the URI as its value.
For blank nodes, this tag has an ``rdf:nodeId`` attribute,
with a blank node identifier as its value.

There are no other tags inside the ``rdf:RDF`` tag.

Inside each ``rdf:Description`` tag, there is a tag for every triple
that has the node given in the ``rdf:Description`` tag as its subject.
The namespace URI of this tag and the local name of this tag,
concatenated, must be the URI of the triple's property.

For example, if the prefix ``foo`` is bound to the URI
``http://example.org/vocab/1.0/``, then using the tag ``<foo:name>``
would mean that the triple's property is::

    http://example.org/vocab/1.0/name

(This is the same as in all RDF/XML.)

Now, if the object of the triple is a URI node, then the property tag
has an ``rdf:about`` attribute containing the triple's object's URI.
The tag itself is empty. For example::

    <foaf:knows rdf:about="http://example.org/~alice"/>

If the object of the triple is a blank node, then the property tag
has an ``rdf:nodeId`` attribute containing the blank node's identifier.
The tag itself is empty. For example::

    <foaf:knows rdf:nodeId="charlie"/>

If the object of the triple is a literal, then the property tag
has the literal's value as its content. For example::

    <foo:name>Alice Abberson</foo:name>

A property tag containing a literal value can also have an ``xml:lang``
attribute (for literals with language tags), or an ``rdf:type``
attribute (for typed literals; the attribute contains the URI
of the data type).

No attributes except those specified above, and except declarations
of XML namespaces (``xmlns`` and ``xmlns:xxx`` attributes),
shall be used.

XML literals are serialized specially; instead of using an ``rdf:type``
attribute, they use ``rdf:parseType="XMLLiteral"``. For example, ::

    <foo:comment rdf:parseType="XMLLiteral">
        This is <html:em>cool</html:em>, isn't it?
    </foo:comment>

\- Benja
