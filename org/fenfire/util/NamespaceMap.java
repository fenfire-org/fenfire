/*
NamespaceMap.java
 *
 *    Copyright (c) 2003 by Benja Fallenstein
 *
 *    This file is part of Fenfire.
 *    
 *    Fenfire is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Fenfire is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Fenfire; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.util;
import java.io.*;
import java.util.*;
import org.xml.sax.*;

/** A class mapping XML namespace abbreviations like "rdf:" to
 *  namespace URIs.
 */
public class NamespaceMap {
    int n = 0; // number of mappings currently stored
    String[] names = new String[10], uris = new String[10];

    public Map getMappings() {
	Map m = new HashMap();
	for(int i=0; i<n; i++) m.put(names[i], uris[i]);
	return m;
    }
    
    /** Gives an iterator over the URIs that have an abbreviation. 
     *  @return an iterator that gives one URI string at a time
     */
    public Iterator uriIterator() {
	return new Iterator() {
		int i = 0;
		public boolean hasNext() { return i < n; }
		public Object next() { return uris[i++]; }
		public void remove() { throw new Error("Not implemented"); }
	    };
    }

    /** Add a shortname -> namespace mapping.
     *  @param name The short name of the namespace, e.g. "rdfs".
     *  @param uri The URI of the namespace.
     */
    public void put(String name, String uri) {
	if(n+1 > names.length) {
	    String[] nnames = new String[n*2];
	    String[] nuris = new String[n*2];
	    System.arraycopy(names, 0, nnames, 0, n);
	    System.arraycopy(uris, 0, nuris, 0, n);
	    names = nnames; uris = nuris;
	}
	names[n] = name; uris[n] = uri;
	n++;
    }

    public void putAll(Map map) {
	for(Iterator i=map.keySet().iterator(); i.hasNext();) {
	    
	    String qnamePrefix = (String)i.next();
	    String uri = (String)map.get(qnamePrefix);
	    
	    put(qnamePrefix, uri);
	}
    }
    
    /** Get the abbreviation of an RDF resource URI.
     *  If the URI starts with any of the namespace
     *  URIs in this map, an abbreviation is returned
     *  (e.g. "rdf:type"). Otherwise, a full URI
     *  is returned.
     */
    public String getAbbrev(String uri) {
	for(int i=0; i<n; i++) {
	    if(uri.startsWith(uris[i]))
		return names[i] + ":" + uri.substring(uris[i].length());
	}
	return uri;
    }

    /** Load the name -> uri mappings from an XML file.
     */
    public void loadMappings(Reader r) throws IOException, SAXException {
	XMLReader xr = new org.apache.xerces.parsers.SAXParser();
	ContentHandler h = new org.xml.sax.helpers.DefaultHandler() {
		public void startPrefixMapping(java.lang.String prefix,
					       java.lang.String uri) {
		    put(prefix, uri);
		}
	    };
	xr.setContentHandler(h);
	xr.parse(new InputSource(r));
    }

    public Comparator getComparator() {
	return new CompareByAbbrev();
    }

    public class CompareByAbbrev implements Comparator {
	public int compare(Object o1, Object o2) {
	    String s1 = getAbbrev(o1.toString());
	    String s2 = getAbbrev(o2.toString());
	    return s1.compareTo(s2);
	}
    }
}
