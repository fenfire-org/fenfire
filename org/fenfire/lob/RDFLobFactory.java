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
import java.util.Comparator;

public class RDFLobFactory {

    protected Model font;
    protected Model graph;

    public RDFLobFactory(Model graph, Model font) {
	this.graph = graph;
	this.font = font;
    }

    public Model value(Model node, Object property) {
	return value(node, property, 1);
    }

    public Model value(Model node, Object property, int dir) {
	return new PropValueModel(graph, node, property, dir);
    }

    public Model string(Model node, Object property) {
	return new LiteralStringModel(value(node, property));
    }

    public SetModel setModel(Object node, Object prop, int dir) {
	Model nm = Nodes.isNode(node) ? new ObjectModel(node) : (Model)node;
	Model pm = Nodes.isNode(prop) ? new ObjectModel(prop) : (Model)prop;

	return new PropValueSetModel(graph, nm, pm, new IntModel(dir));
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

    public TextModel textModel(Model node, Object property) {
	Model string = string(node, property);
	return new TextModel.StringTextModel(string, font, node);
    }

    public Label label(Model node, Object property) {
	return new Label(textModel(node, property));
    }
    public Label label(Model node, Object property, boolean linebreaking) {
	return new Label(textModel(node, property), linebreaking);
    }

    public TextField textField(Model node, Object property) {
	return textField(node, property, new ObjectModel(property));
    }
    public TextField textField(Model node, Object property, Model key) {
	return new TextField(textModel(node, property), key);
    }

    public TextArea textArea(Model node, Object property) {
	return textArea(node, property, new ObjectModel(property));
    }
    public TextArea textArea(Model node, Object property, Model key) {
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
	// doesn't throw a NullPointerException... argl
	Model m0 = new ObjectModel(collection.iterator().next());

	Lob template = 
	    label(Parameter.model(ListModel.PARAM), property, false);
	return listBox(collection, template, cmp, key);
    }
}
