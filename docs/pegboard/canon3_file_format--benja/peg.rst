=========================================================
``canon3_file_format``: A canonical, N3-based file format
=========================================================

:Author:	Benja Fallenstein
:Date:		2003-04-01
:Revision:	$Revision: 1.6 $
:Last-Modified: $Date: 2003/07/05 21:43:48 $
:Type:		Architecture
:Scope:		Major
:Status:	Irrelevant


We need a canonical file format for storing data in CVS
(canonical so that diffs will only show the differences
in structure, not changes because one RDF writer
chose to order triples differently than another writer
or so). This format could also be a potential candidate
for storing versions of RDF graphs in Storm.

This PEG specifies such a format.


Issues
======

- Does this also cover bags and sequences? Reification?

   RESOLVED: Of course. All RDF structures (anything
   that can be serialized as triples) can be
   represented as Canon3.

- Do we really need a new format?

   RESOLVED: None of the existing formats are canonical.

- How compatible is this with N3 and NTriples?
  What are the differences?

   RESOLVED: NTriples is encoded in US-ASCII and
   doesn't allow for multi-line literals. N3 cannot
   refer to anonymous nodes. An N3 processor
   will be able to read any Canon3 file that does
   not contain anonymous nodes (except if the
   Unicode LINE SEPARATOR character is used,
   which is not allowed by N3).

   (Anonymous nodes in Canon3 are represented
   as in NTriples.)
   
- Should the encoding allowed to be different?
  
   RESOLVED: No, since that would lose both
   canonicality and compatibility with N3.

- Is UTF-8 always sufficient?

   RESOLVED: UTF-8 can represent all of Unicode and
   RDF uses Unicode only; therefore, yes.

- Is quoting with three quotes really what we want?

   RESOLVED: Multiline literals is really what
   we want-- imagine you have a 1K HTML document
   as a literal and the encoder puts it all
   in one line. (Also, with multiline literals,
   CVS's diffs are more useful.)

   Multiline literals are enclosed in three quotes in N3.

- Does the specification need to talk about equal triples
  occuring in the same graph? Can the same triple
  occur twice, according to the RDF spec?

   RESOLVED: There are tools which allow a single triple
   to occur twice. Therefore, the spec should be clear
   about the topic.

- Why `Normalization Form C`_?

   RESOLVED: Because it's required by N3, and because
   it's the standard on the Web (http://www.w3.org/TR/charmod/).

- Does it allow for the different newline conventions?

   RESOLVED: Yes. (Normalization Form C only specifies that
   composite characters like umlauts are stored in
   composited, not decomposited form. See the spec.)

- Wouldn't it be easier to produce the serialization format 
  for each triple, and then put those into lexical order? 
  Or if the parts must be compared 
  separately, could we compare serializations of those parts?

   RESOLVED: We assume that a Canon3 writer usually operates 
   on an in-memory representation of an RDF graph. That
   makes it easy to sort triples in unencoded, and hard
   to sort them in the encoded way. It's also more scalable:
   Sorting on the serializations would mean having to
   generate the whole serialization in memory first,
   before writing anything to the disk.

   This is also the reason we compare literals prior to
   encoding them, not after.

   (Also note that simply sorting the *lines*
   wouldn't work anyway, because of multiline literals.)


Specification
=============

The name of the format is *Canon3*. This version is identified
by the URI <http://fenfire.org/2003/Canon3/1.0>. It is related to
both `Notation 3`_ and `NTriples`_. Canon3 files
are encoded as UTF-8, normalized to Unicode `Normalization Form C`_.
They obey the following grammar::

    document ::= header (triple)*
    header ::= "# Canon3 <http://fenfire.org/2003/Canon3/1.0/>" NEWLINE
    triple ::= subject " " property " " object "." NEWLINE
    subject ::= URItoken | anonNode
    property ::= URItoken
    object ::= URItoken | anonNode | literal
    URItoken ::= "<" URIref ">"
    anonNode ::= "_:" [A-Za-z][A-Za-z0-9]*
    literal ::= #x22 #x22 #x22 string #x22 #x22 #x22 qualifiers
    qualifiers ::= ("@" language)? ("^^" type)?
    type ::= URItoken

A conforming processor must not accept faulty
Canon3 files.

The ``NEWLINE`` token may be any of CR, LF, CRLF, and
the Unicode LINE SEPARATOR (U+2028).
This is necessary for CVS, to be useful across platforms.
In contexts where the specific form used matters,
the newline character is LF. (In particular, when computing
a content hash-- e.g., when creating a Canon3 Storm block.)
It would be nicer to use LINE SEPARATOR, but that
would be an incompatibility with N3.

A ``string`` is any UTF-8 character sequence 
encoded in the following way:

- Double any backslash in the string.
- Insert a backslash before the first of any three
  consecutive double quotes (#x22) in the string.
  (This means: In a sequence of three or more
  double quote characters, instert a backslash
  before all but the last two double quotes).

For example, the string ``f\oo"""""ba"r`` becomes
``f\\oo\"\"\"""ba"r``.

Strings may contain newlines. Like all of Canon3,
they are encoded in Normalization Form C.
They are enclosed in triple double quotes
(see production ``literal``).

The triples must be ordered. Two triples are compared
by comparing their subjects, properties, and objects
in this order. Each of these parts is compared
as follows:

- Literals are lower than (go before) URIrefs,
  which go before anonymous nodes.
- URIrefs are compared character-by-character,
  in the form as defined in [RFC 2396]
  (i.e., *after* Unicode characters outside
  the ASCII range have been escaped).
  Characters are compared by Unicode code point
  value.
- Literals are compared character-by-character
  in their unescaped form (i.e., before the
  backslash escaping defined below). If the
  strings of two literals are equal, first
  the language tag and then the data type,
  if any, are compared in the same manner.
  Literals without language tags/data types
  go before literals with them (if the
  contents of the literals are equal).
- Anonymous nodes are compared by their
  internal identifiers (the stuff following
  the ``_:``), also character-by-character.
  
A triple may only be listed once; if there are two
equal triples in the graph to be serialized, this
triple must occur only once in the serialization.

``URIref`` is one of the following:

1. An `RDF URI reference`_ encoded in UTF-8 (Normalization
   Form C) as the rest of Canon3.
2. An RDF URI reference with everything before the
   fragment identifier (if any) omitted. This refers
   to the current document (in the case of the empty
   string) or to a fragment of it (e.g., ``#foo``).

``language`` is a Language-Tag as defined by [RFC 3066].
If present, ``language`` and ``type`` indicate
the `language tag and data type`_ of a literal.

Here's an example Canon3 file::

    # Canon3 <http://fenfire.org/2003/Canon3/1.0/>
    <> <http://example.org/name> """Foobar
    An example Canon3 "document\""""@en.
    <> <http://example.org/name> """Foobar
    Ein Beispiel eines Canon3-"Dokumentes\""""@de.
    <> <http://example.org/isa> <http://example.org/document>.
    <#Foo> <http://example.org/name> """Foo fragment identifier""".
    <http://example.org> <urn:x-files:rating> """7"""^^<http://www.w3.org/2001/XMLSchema#int>.
    <http://example.org> <urn:x-foo:related> <urn:x-foo:rittlefricks>.

We will register a MIME type for Canon3.

\- Benja


.. _Normalization Form C: http://www.unicode.org/unicode/reports/tr15/
.. _NTriples: http://www.w3.org/TR/rdf-testcases/#ntriples
.. _Notation 3: http://www.w3.org/DesignIssues/Notation3.html
.. _RDF URI reference: http://www.w3.org/TR/rdf-concepts/#section-Graph-URIref
.. _language tag and data type: 
   http://www.w3.org/TR/rdf-concepts/#section-Literals