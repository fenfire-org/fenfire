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
 *  This class has a set() method implemented, i.e., it can also be used
 *  to <em>change</em> a node's content. The set() method will use
 *  the text property that is currently used to show the node,
 *  or 'defaultProperty' if the node isn't currently connected to any literal
 *  on any of the text properties.
 */
public class NodeTextModel extends AbstractModel.AbstractObjectModel {
    private Model node;
    private Graph graph;
    private NamespaceMap nmap;
    private Set textProperties;
    private Object defaultProperty;

    public NodeTextModel(Graph graph, Model node, NamespaceMap nmap, 
			 Set textProperties, Object defaultProperty) {
	this.graph = graph;
	this.node = node;
	this.nmap = nmap;
	this.textProperties = textProperties;
	this.defaultProperty = defaultProperty;

	node.addObs(this);
    }

    public Replaceable[] getParams() {
	return new Replaceable[] { node };
    }
    public Object clone(Object[] params) {
	return new NodeTextModel(graph, (Model)params[0], nmap,
				 textProperties, defaultProperty);
    }
    
    Object getProperty() {
	Object n = node.get();
	
	LOOP: 
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

    String fallback() {
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
	Object n = node.get(), p = getProperty();
	if(p == null) return fallback();
	return ((Literal)graph.findN_11X_Iter(n, p, this).next()).getString();
    }

    public void set(Object value) {
	Object n = node.get(), p = getProperty();
	String s = (String)value;

	if(p != null) 
	    graph.rm_11A(n, p);
	else
	    p = defaultProperty;
	graph.add(n, p, new PlainLiteral(s));
    }
}
