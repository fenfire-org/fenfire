/*
SimpleContentView.java
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
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.component.*;
import java.util.*;

public class SimpleContentView implements ContentViewSettings.ContentView {

    private Map cache = new org.nongnu.navidoc.util.WeakValueMap();
    private Map propCache = new org.nongnu.navidoc.util.WeakValueMap();

    private Graph graph;
    private NamespaceMap nmap;
    private Set textProperties;
    private Object defaultProperty;

    public SimpleContentView(Graph graph, NamespaceMap nmap, 
			     Set textProperties, Object defaultProperty) {
	this.graph = graph;
	this.nmap = nmap;
	this.textProperties = textProperties;
	this.defaultProperty = defaultProperty;
    }

    public Set getTypes() {
	return Collections.singleton(ViewSettings.ALL);
    }

    public Lob getLob(Object node) {
	if(cache.get(node) != null) return (Lob)cache.get(node);
	Lob l = makeLob(node, false);
	cache.put(node, l);
	return l;
    }

    public Lob getPropertyLob(Object node) {
	if(propCache.get(node) != null) return (Lob)propCache.get(node);
	Lob l = makeLob(node, true);
	propCache.put(node, l);
	return l;
    }

    private class ContentModel extends AbstractModel.AbstractObjectModel {
	Model node;

	ContentModel(Model node) {
	    this.node = node;
	    node.addObs(this);
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
		
	    if(s.startsWith("anon:") || s.startsWith("urn:urn-5:"))
		s = s.substring(s.lastIndexOf(":"));
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
	    return ((Literal)graph.find1_11X(n, p, this)).getString();
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

    private Lob makeLob(Object node, boolean isPropertyLob) {
	Model str;

	if(node instanceof Literal)
	    str = new ObjectModel(((Literal)node).getString());
	else
	    str = Models.cache(new ContentModel(new ObjectModel(node)));

	Lob l;

	if(!isPropertyLob) {
	    TextModel text = 
		new TextModel.StringTextModel(str, Theme.getFont());
	    Model cursor = new IntModel(-1);

	    Sequence seq = new Box(Lob.X, text);
	    seq = new TextCursorLob(seq, cursor);
	    
	    Model positionModel = seq.positionModel(Lob.X, cursor); 
	    l = new TextEditController(seq, text, cursor, new IntModel(1));
	    l = new ViewportLob(Lob.X, l, positionModel, new FloatModel(.5f));
	} else {
	    l = new Label(str);
	}

	return l;
    }

}
