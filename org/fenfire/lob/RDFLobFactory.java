/*   
RDFLobFactory.java
 *    
 *    Copyright (c) 2004, Benja Fallenstein.
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
 *
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.lob;
import org.nongnu.navidoc.util.Obs;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.component.*;
import org.fenfire.swamp.*;
import org.fenfire.vocab.*;
import java.util.*;

public class RDFLobFactory {

    protected Model font;
    protected Model graph;

    public RDFLobFactory(Model graph, Model font) {
	this.graph = graph;
	this.font = font;
    }

    private Model model(Object o) {
	if(o == null) return new ObjectModel(null);
	return Nodes.isNode(o) ? new ObjectModel(o) : (Model)o;
    }

    public Model value(Object node, Object property) {
	return value(node, property, 1);
    }

    public Model value(Object _node, Object property, int dir) {
	Model node = model(_node);

	if(!(property instanceof Object[])) {
	    return new PropValueModel(graph, node, model(property), 
				      new IntModel(dir));
	} else {
	    Object[] props = (Object[])property;
	    Model NULL = new ObjectModel(null);

	    Model result = value(node, props[0], dir);

	    for(int i=1; i<props.length; i++) {
		Model m = value(node, props[i], dir);
		result = result.equalsModel(NULL).select(m, result);
	    }

	    return result;
	}
    }

    public Model string(Object node, Object property) {
	return new LiteralStringModel(value(node, property));
    }

    public SetModel setModel(Object node, Object prop, int dir) {
	return new PropValueSetModel(graph, model(node), model(prop),
				     new IntModel(dir));
    }

    public ListModel containerModel(Object node, Object prop, int dir) {
	Model container = new PropValueModel(graph, model(node), model(prop), 
					     new IntModel(dir));
	return new ContainerModel(graph, container);
    }

    public SetModel setModel(Object type) {
	return setModel(type, RDF.type, -1);
    }

    public ListModel listModel(SetModel set, Comparator cmp) {
	CollectionModel c = set;
	c = new SortedSetModel.SortedSetCache(c, cmp);
	return new ListModel.ListCache(c);
    }

    public ListModel listModel(Object type, Comparator cmp) {
	return listModel(setModel(type), cmp);
    }

    public TextModel textModel(String string, boolean lineEnd) {
	TextModel.StringTextModel m =
	    new TextModel.StringTextModel(new ObjectModel(string), font);
	m.setIncludeLineEnd(lineEnd);
	return m;
    }

    public TextModel textModel(Object _node, Object property) {
	Model node = model(_node);
	return textModel(node, property, node);
    }

    public TextModel textModel(Object _node, Object property, Object _key) {
	Model node = model(_node);
	Model key = model(_key);
	Model string = string(node, property);
	return new TextModel.StringTextModel(string, font, key);
    }

    public Label label(Object node, Object property) {
	return new Label(textModel(node, property));
    }
    public Label label(Object node, Object property, boolean linebreaking) {
	return new Label(textModel(node, property), linebreaking);
    }

    public TextField textField(Object node, Object property) {
	return textField(node, property, model(property));
    }
    public TextField textField(Object node, Object property, Model key) {
	return new TextField(textModel(node, property), key);
    }

    public TextArea textArea(Object node, Object property) {
	return textArea(node, property, new ObjectModel(property));
    }
    public TextArea textArea(Object node, Object property, Model key) {
	return new TextArea(textModel(node, property), key);
    }

    public ListBox listBox(CollectionModel collection, Lob template,
			   Comparator cmp, Object key) {
	collection = new SortedSetModel.SortedSetCache(collection, cmp);
	return new ListBox(new ListModel.ListCache(collection), template,
			   new ObjectModel(key));
    }

    public ListBox listBox(CollectionModel collection, Object property,
			   Comparator cmp, Object key) {
	Model m0 = new ObjectModel();

	// prevent NullPointerException... argl
	Iterator i = collection.iterator();
	if(i.hasNext()) m0.set(i.next());

	Lob template = 
	    label(Parameter.model(ListModel.PARAM), property, false);
	return listBox(collection, template, cmp, key);
    }

    public ListBox listBox(ListModel list, Object property, Object key) {
	Model m0 = new ObjectModel();

	// prevent NullPointerException... argl
	if(!list.isEmpty()) m0.set(list.get(0));

	Lob template = 
	    label(Parameter.model(ListModel.PARAM), property, false);
	return new ListBox(list, template, new ObjectModel(key));
    }
}
