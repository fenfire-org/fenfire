# 
# Copyright (c) 2003, Benja Fallenstein
# This file is part of Fenfire.
# 
# Fenfire is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
# 
# Fenfire is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
# Public License for more details.
# 
# You should have received a copy of the GNU General
# Public License along with Fenfire; if not, write to the Free
# Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
# MA  02111-1307  USA
# 

from __future__ import nested_scopes

"""
An RDF/XML writer for Swamp ConstGraphs.
"""

LITERAL = 0
URI     = 1


import re
from org.fenfire.swamp import Nodes, Literal
from java.lang import Character

import java.io


# Hardcoded namespace prefixes: Namespaces that are assigned
# a human-readable namespace name. This is somewhat hackish,
# but works for now. Add new namespaces here when needed.
hardcoded_prefixes = {
    'http://www.w3.org/1999/02/22-rdf-syntax-ns#': 'rdf',
    'http://fenfire.org/rdf-v/2003/05/ff#': 'ff',
    'http://fenfire.org/rdf-v/2003/05/structlink#': 'structlink',
    'http://fenfire.org/rdf-v/2003/05/canvas2d#': 'canvas2d',
    'http://fenfire.org/rdf-v/2003/07/treetime#': 'treetime',
}

def writeToOutputStream(graph, output):
    g = write(graph, '/dev/null')
    w = java.io.PrintWriter(java.io.OutputStreamWriter(output))
    for i in g:
        w.println(i)
    w.flush()
    return "ok"

def write(graph, filename):
    """
    Write a ConstGraph to a file, given the filename.
    """

    out = []
    def w(s):
	"""Add a string to the output.
	"""
	out.append(s)
    
    (triples, namespace_uris) = readTriples(graph)

    (uriByPrefix, prefixByURI) = assignNamespacePrefixes(namespace_uris)
    prefixes = uriByPrefix.keys()
    prefixes.sort()

    w("<rdf:RDF\n")
    for prefix in prefixes:
        w("  xmlns:%s='%s'\n" % (prefix, escape(uriByPrefix[prefix])))
    w(">\n\n")

    subjects = triples.keys(); subjects.sort()
    for subject in subjects:
        w("  <rdf:Description rdf:about='%s'>\n" % escape(subject))
        properties = triples[subject].keys(); properties.sort()
        for property in properties:
            (namespace_uri, local_name) = property
            prop_tag = "%s:%s" % (prefixByURI[namespace_uri], local_name)
            objects = triples[subject][property]; objects.sort()
            for (object_type, object_str) in objects:
                if object_type == URI:
                    w("    <%s rdf:resource='%s'/>\n" %
                                 (prop_tag, escape(object_str)))
                else:
                    w("    <%s>%s</%s>\n" %
                                 (prop_tag, escape(object_str), prop_tag))

        w("  </rdf:Description>\n\n")
                    

    w("</rdf:RDF>\n")
    writer = java.io.OutputStreamWriter(java.io.FileOutputStream(filename),
                                        "UTF-8")
    for str in out:
	writer.write(str)
    writer.close()
    return out

def readTriples(graph):
    """
    Return a structure of the following type::

         ({subject_uri:
             {(property_namespace_uri,
               property_local_name):
                   [
                       (URI, object_uri)
                       --or--
                       (LITERAL, object_literal_content)
                   ]
             }
          },
          [namespace_uri]
         )

    The [namespace_uri] list is a list of namespace URIs used.
    """
    
    bySubject = {}
    namespaces = {} # Map used namespace URIs to ``None`` to emulate set.

    i = graph.findN_XAA_Iter()
    while i.hasNext():
        subject_node = i.next()
        subject = Nodes.toString(subject_node)
        byProperty = bySubject[subject] = {}
        j = graph.findN_1XA_Iter(subject_node)
        while j.hasNext():
            property_node = j.next()
            property = genQName(Nodes.toString(property_node))
            namespaces[property[0]] = None
            objects = byProperty[property] = []
            k = graph.findN_11X_Iter(subject_node, property_node)
            while k.hasNext():
                o = k.next()
                if isinstance(o, Literal):
                    # XXX -- language tags and data types!!!!!!!!!
                    objects.append((LITERAL, o.getString()))
                elif Nodes.isNode(o):
                    objects.append((URI, Nodes.toString(o)))
                else:
                    raise Error("Not a literal nor node: %s" % o)

    return (bySubject, namespaces.keys())
    

def genQName(uri):
    """
    Split URI in namespace URI and local name; return
    a ``(namespace_uri, local_name)`` pair.
    """

    hadLetter = 0

    i = len(uri)-1
    while i >= 0:
        if Character.isLetter(uri[i]) or uri[i] == '_':
            hadLetter = 1
        elif Character.isDigit(uri[i]) or uri[i] in ('-', '.'):
            hadLetter = 0
        else:
            if not hadLetter:
                raise Error("Cannot serialize graph because of "
                            "unserializable property URI: %s" % uri)

            return (uri[:i+1], uri[i+1:])

        i = i - 1


def assignNamespacePrefixes(namespace_uris):
    """
    Given a list of namespace URIs, return a mapping
    from namespace prefixes to namespace URIs, as well
    as the reverse mapping (i.e., a 2-tuple of dictionaries).

    The RDF namespace URI is always included.
    """

    uris = ['http://www.w3.org/1999/02/22-rdf-syntax-ns#']
    uris.extend(namespace_uris)
    uris.sort()

    byPrefix = {}
    byURI = {}
    counter = 1

    for uri in uris:
        if hardcoded_prefixes.has_key(uri):
            prefix = hardcoded_prefixes[uri]
        else:
            prefix = "NS%s" % counter
            counter = counter + 1

        byPrefix[prefix] = uri
        byURI[uri] = prefix

    return (byPrefix, byURI)


def escape(str):
    """
    XML-escape a string.
    """

    str = re.sub("&", "&amp;", str)
    str = re.sub("<", "&lt;", str)
    str = re.sub(">", "&gt;", str)
    str = re.sub("'", "&apos;", str)
    str = re.sub('"', "&quot;", str)

    return str
