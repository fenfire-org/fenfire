==============================
``urn5s_as_properties--benja``
==============================

:Author:   Benja Fallenstein
:Last-Modified: $Date: 2003/04/25 08:40:11 $
:Revision: $Revision: 1.1 $
:Status:   Current

In the RDF/XML serialization of RDF, all properties are
turned into XML tags by separating them into an XML
namespace and a local part. For example,
``http://example.org/foo`` could become::

    <ex:foo xmlns:ex="http://example.org/"/>

When properties cannot be split like this, RDF graphs
using these properties cannot be serialized in RDF/XML.
Unfortunately, this apparently includes urn-5s:
The colon can't occur in an XML tag in a namespace,
and a tag's name cannot start with a digit.

Even if we decide not to use RDF/XML, it would still
be *very* bad for interoperability if our graphs couldn't
be converted to RDF/XML. And we do want user-created
nodes to be usable as properties.

Therefore, add an additional underscore
before the index in urn-5s, like this::

    urn:urn-5:cf7O9Un3XBIVdh3rYqH0us169zo-:_12

Then we can serialize these as properties::

    <ex:_12 xmlns:ex="urn:urn-5:cf7O9Un3XBIVdh3rYqH0us169zo-:"/>

The urn-5 registration allows for this.

\- Benja