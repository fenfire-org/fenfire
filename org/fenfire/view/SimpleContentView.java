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
import org.fenfire.Cursor;
import org.fenfire.lob.*;
import org.fenfire.swamp.*;
import org.fenfire.util.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.component.*;
import java.awt.Color;
import java.util.*;

public class SimpleContentView implements ContentViewSettings.ContentView {

    private Map cache = new org.nongnu.navidoc.util.WeakValueMap();
    private Map propCache = new org.nongnu.navidoc.util.WeakValueMap();

    private Graph graph;
    private Cursor cursor;
    private NamespaceMap nmap;
    private Set textProperties;
    private Object defaultProperty;

    private float minPropBrightness, maxPropBrightness;

    public SimpleContentView(Graph graph, Cursor cursor, NamespaceMap nmap, 
			     Set textProperties, Object defaultProperty,
			     float minPropBrightness,
			     float maxPropBrightness) {
	this.graph = graph;
	this.cursor = cursor;
	this.nmap = nmap;
	this.textProperties = textProperties;
	this.defaultProperty = defaultProperty;
	this.minPropBrightness = minPropBrightness;
	this.maxPropBrightness = maxPropBrightness;
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

    private class LiteralTextModel extends AbstractModel.AbstractObjectModel {
	Literal literal;
	LiteralTextModel(Literal literal) { this.literal = literal; }

	public Object get() {
	    return literal.getString();
	}
	public void set(Object o) {
	    Literal nlit = new PlainLiteral((String)o);

	    Object node = cursor.getRotation().getRotationNode();
	    Object prop = cursor.getRotation().getRotationProperty();

	    graph.rm_111(node, prop, literal);
	    graph.add(node, prop, nlit);

	    int textCursor = cursor.textCursor.getInt();
	    cursor.set(nlit, prop, node, -1);
	    cursor.textCursor.setInt(textCursor);
	}
    }

    private Lob makeLob(Object node, boolean isPropertyLob) {
	Model str;

	Model nodeModel = new ObjectModel(node);

	if(node instanceof Literal)
	    str = new LiteralTextModel((Literal)node);
	else
	    str = Models.cache(new NodeTextModel(graph, nodeModel, nmap,
						 textProperties,
						 defaultProperty));

	Lob l;

	if(!isPropertyLob) {
	    TextModel text = 
		new TextModel.StringTextModel(str, Theme.getFont());

	    Model tc = cursor.textCursor;

	    Sequence seq = new Box(Lob.X, text);
	    seq = new TextCursorLob(seq, tc, 
				    cursor.node.equalsModel(nodeModel));
	    
	    Model positionModel = seq.positionModel(Lob.X, tc); 
	    l = new TextEditController(seq, text, tc, new IntModel(1));
	    l = new ViewportLob(Lob.X, l, positionModel, new FloatModel(.5f));
	} else {
	    Model color = 
		new UniqueColorModel(new ObjectModel(node),
				     minPropBrightness, maxPropBrightness);

	    TextStyle ts = ((LobFont)Theme.getFont().get()).getTextStyle();
	    LobFont font = new LobFont(ts, (Color)color.get());

	    TextModel text = 
		new TextModel.StringTextModel(str, new ObjectModel(font));

	    l = new Label(text);
	}

	return l;
    }

}
