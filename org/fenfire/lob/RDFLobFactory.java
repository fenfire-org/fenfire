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
	if(o == null) return new ObjectModel(null); // not always appropriate?
	if(o.equals("*")) return Parameter.model(ListModel.PARAM);
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

    public TextModel textsModel(Object _node, Object property) {
	Model node = model(_node);
	return textsModel(node, property, node);
    }

    public TextModel textModel(Object node, Object property, Object _key) {
	Model key = model(_key);
	Model string = string(node, property);
	return new TextModel.StringTextModel(string, font, key);
    }

    public TextModel textsModel(Object node, Object property, Object _key) {
	Model key = model(_key);
	SetModel set = setModel(node, property, 1);
	ListModel list = new ListModel.ListCache(set);

	Model param = Parameter.model(ListModel.PARAM);
	Model strTempl = Models.adaptMethod(param, Object.class, "toString");
	TextModel.StringTextModel templ = 
	    new TextModel.StringTextModel(strTempl, font, key);
	templ.setIncludeLineEnd(false);
	list = new ListModel.Transform(list, templ);

	TextModel comma = textModel(", ", false);
	list = new ListModel.Join(list, new ObjectModel(comma));

	return new TextModel.Concat(list);
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
	return new ListBox(new ListModel.ListCache(collection), 
			   "template", template,
			   "key", new ObjectModel(key));
    }

    public ListBox listBox(CollectionModel collection, Object property,
			   Comparator cmp, Object key) {
	Lob template = 
	    label(Parameter.model(ListModel.PARAM), property, false);
	return listBox(collection, template, cmp, key);
    }

    public ListBox listBox(ListModel list, Object property, Object key) {
	Lob template = 
	    label(Parameter.model(ListModel.PARAM), property, false);
	return new ListBox(list, "template", template, 
			   "key", new ObjectModel(key));
    }
}
