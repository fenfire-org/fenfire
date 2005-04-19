/*
NodeTexter.java
 *    
 *    Copyright (c) 2003-2005, Benja Fallenstein
 *
 *    This file is part of Libvob.
 *    
 *    Libvob is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Libvob is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Libvob; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.view;
import org.fenfire.swamp.*;
import org.fenfire.util.*;
import org.nongnu.libvob.fn.*;
import org.nongnu.libvob.lob.*;
import org.nongnu.navidoc.util.Obs;
import javolution.realtime.*;
import javolution.lang.Text;
import java.util.*;

/** XXX update javadoc.
 *
 *  NodeTextModel takes a Model containing a node and returns the string
 *  we use to represent the node on the screen. For example, if a node
 *  has an rdf:label, NodeTextModel will return that; if it has no rdf:label
 *  or other text property, but it's in a namespace, e.g. Dublin Core,
 *  NodeTextModel will return a namespaced name like "dc:date".
 *  <p>
 *  This class has a set() method implemented, i.e., it can also be
 *  used to <em>change</em> a node's content. The set() method will
 *  use the text triple that is currently used to show the node, or a
 *  new one along 'defaultProperty' if the node isn't currently
 *  connected to any literal on any of the text properties.
 */
public class NodeTexter extends RealtimeObject {
    private Graph graph;
    private NamespaceMap nmap;
    private SortedSet textProperties; // the properties we look for, in order
    private Object defaultProperty; // used if text is added to textless node

    // XXX these are buggy on Kaffe 1.0.6
    private static String preferredLang = Locale.getDefault().getLanguage();
    private static String preferredCountry = Locale.getDefault().getCountry()
	                                                        .toLowerCase();

    public NodeTexter(Graph graph, NamespaceMap nmap, 
		      Set textProperties, Object defaultProperty) {
	this.graph = graph;
	this.nmap = nmap;
	this.textProperties = new TreeSet(textProperties);
	this.defaultProperty = defaultProperty;

	// defaultProperty is expected to be in textProperties
	this.textProperties.add(defaultProperty);
    }

    /** Finds the first text property that has a literal object present.
     *  @return the property to be used for node text,
     *          or null if there isn't a suitable one
     */
    protected Object getProperty(Object n) {
	for(Iterator i=textProperties.iterator(); i.hasNext();) {
	    Object prop = i.next();
	    Iterator j = graph.findN_11X_Iter(n, prop);
	    while(j.hasNext()) {
		Object value = j.next();
		if(value instanceof Literal)
		    return prop;
	    }
	}

	return null;
    }

    /** Finds the first literal among the best present for a given node 
        and predicate. */
    protected Literal getLiteral(Object n, Object p) {
	//  4 right lang, right country
	//  3 right lang
	//  2 without lang
	//  1 wrong lang
	
	//  0 typed literal
	// -1 null
	Object best = null;
	int bestval = -1, val;
	Iterator i = graph.findN_11X_Iter(n, p);
	while (i.hasNext()) {
	    Object o = i.next();
	    if (! (o instanceof Literal))
		continue;
	    
	    if (! (o instanceof PlainLiteral))
		val = 0;
	    else {
		String lang = ((PlainLiteral) o).getLang();
		if (lang == null)
		    val = 2;
		else if (lang.toLowerCase().startsWith(preferredLang)) {
		    val = 3;
		    if (lang.substring(lang.indexOf('-')+1).toLowerCase()
			.equals(preferredCountry))
			val = 4;
		} else val = 1;
	    }
	    if (val > bestval) {
		bestval = val;
		best = o;
	    }
	}
	return (Literal) best;
    }

    /** Provides a fallback text representation for the cases when we didn't
     *  find text content for the node.
     *  @return empty string for anon:, bnode: and urn:urn-5: URIs,
     *          email address or telephone number for mailto: and tel: URIs,
     *          otherwise a URI, abbreviated if it starts with a namespace.
     */
    protected Text fallback(Object node) {
	String uri = Nodes.toString(node);
	Text uri_text = Text.valueOf(uri);
		
	if(uri.startsWith("anon:") || uri.startsWith("bnode:") || 
	   uri.startsWith("urn:urn-5:"))
	    return Text.EMPTY;
	else if(uri.startsWith("mailto:")) 
	    return uri_text.subtext("mailto:".length());
	else if(uri.startsWith("tel:")) 
	    return uri_text.subtext("tel:".length());
	else if(nmap != null)
	    return nmap.getAbbrev(uri);

	return uri_text;
    }

    public Text getText(Object n) {
	Object p = getProperty(n);
	if(p == null) {
	    return fallback(n);
	} else {
	    return Text.valueOf(getLiteral(n, p).getString());
	}
    }

    public void setText(Object n, String value) {
	Object p = getProperty(n);
	String s = (String)value;
	Literal l = null;

	if(p != null) {
	    l = getLiteral(n, p);
	    graph.rm_111(n, p, l);
	} else
	    p = defaultProperty;
	graph.add(n, p, Nodes.editLiteral(l, s));
    }

    public Model model(final Object node) {
	return new Model() {
		public Object get() { 
		    return getText(node); 
		}
		public void set(Object value) { 
		    setText(node, (String)value); 
		}
		public int getInt() {
		    throw new UnsupportedOperationException();
		}
		public void set(int i) {
		    throw new UnsupportedOperationException();
		}

		public javolution.lang.Text toText() {
		    throw new UnsupportedOperationException();
		}
		public boolean move(Realtime.ObjectSpace o) {
		    throw new UnsupportedOperationException();
		}
	    };
    }
}
