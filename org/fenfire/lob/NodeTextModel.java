/*
NodeTextModel.java
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
package org.fenfire.lob;
import org.fenfire.swamp.*;
import org.fenfire.util.*;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.component.*;
import java.util.*;

/** NodeTextModel takes a Model containing a node and returns the string
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
public class NodeTextModel extends AbstractModel.AbstractObjectModel {
    private Model node;
    private Graph graph;
    private NamespaceMap nmap;
    private SortedSet textProperties; // the properties we look for, in order
    private Object defaultProperty; // used if text is added to textless node

    private Object cache;

    // XXX these are buggy on Kaffe 1.0.6
    private String preferredLang = Locale.getDefault().getLanguage();
    private String preferredCountry = Locale.getDefault().getCountry()
	                                                 .toLowerCase();

    public NodeTextModel(Graph graph, Model node, NamespaceMap nmap, 
			 Set textProperties, Object defaultProperty) {
	this.graph = graph;
	this.node = node;
	this.nmap = nmap;
	this.textProperties = new TreeSet(textProperties);
	this.defaultProperty = defaultProperty;

	// defaultProperty is expected to be in textProperties
	this.textProperties.add(defaultProperty);

	node.addObs(this);
	nmap.addObs(this);
    }

    public Replaceable[] getParams() {
	return new Replaceable[] { node };
    }
    public Object clone(Object[] params) {
	return new NodeTextModel(graph, (Model)params[0], nmap,
				 textProperties, defaultProperty);
    }

    public void chg() {
	cache = null;
	super.chg();
    }

    /** Finds the first text property that has a literal object present.
     *  @return the property to be used for node text,
     *          or null if there isn't a suitable one
     */
    protected Object getProperty() {
	Object n = node.get();
	
	for(Iterator i=textProperties.iterator(); i.hasNext();) {
	    Object prop = i.next();
	    Iterator j = graph.findN_11X_Iter(n, prop, this);
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
	Iterator i = graph.findN_11X_Iter(n, p, this);
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
    protected String fallback() {
	String s = Nodes.toString(node.get());
		
	if(s.startsWith("anon:") || s.startsWith("bnode:") || s.startsWith("urn:urn-5:"))
	    s = "";
	else if(s.startsWith("mailto:")) 
	    s = s.substring("mailto:".length());
	else if(s.startsWith("tel:")) 
	    s = s.substring("tel:".length());
	else if(nmap != null)
	    s = nmap.getAbbrev(s);

	return s;
    }

    public Object get() {
	if(cache == null) {
	    Object n = node.get(), p = getProperty();
	    if(p == null) 
		cache = fallback();
	    else {
		cache = getLiteral(n, p).getString();
	    }
	}
	return cache;
    }

    public void set(Object value) {
	Object n = node.get(), p = getProperty();
	String s = (String)value;
	Literal l = null;

	if(p != null) {
	    l = getLiteral(n, p);
	    graph.rm_111(n, p, l);
	} else
	    p = defaultProperty;
	graph.add(n, p, Nodes.editLiteral(l, s));
    }
}
