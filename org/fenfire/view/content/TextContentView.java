/*
TextContentView.java
 *    
 *    Copyright (c) 2003-2005, Benja Fallenstein
 *                  2005, Matti J. Katila
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
 * Written by Benja Fallenstein and Matti J. Katila
 */
package org.fenfire.view.content;
import org.fenfire.view.*;
import org.fenfire.Cursor;
import org.fenfire.lob.*;
import org.fenfire.swamp.*;
import org.fenfire.util.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.fn.*;
import org.nongnu.libvob.lob.*;
import javolution.realtime.*;
import java.awt.Color;
import java.util.*;

public class TextContentView implements ContentViewSettings.ContentView {
    private static void p(String s) { System.out.println("TextContentView:: "+s); }


    private Map propCache = new org.nongnu.navidoc.util.WeakValueMap();

    private Graph graph;
    private Cursor cursor;
    private NamespaceMap nmap;
    private Set textProperties;
    private Object defaultProperty;

    private NodeTexter texter;

    public TextContentView(Graph graph, Cursor cursor, NamespaceMap nmap, 
			     Set textProperties, Object defaultProperty) {
	this.graph = graph;
	this.cursor = cursor;
	this.nmap = nmap;
	this.textProperties = textProperties;
	this.defaultProperty = defaultProperty;
	
	this.texter = new NodeTexter(graph, nmap, textProperties,
				     defaultProperty);
    }

    public Set getTypes() {
	return Collections.singleton(ViewSettings.ALL);
    }

    public Lob getLob(Object node) {
	//if(cache.get(node) != null) return (Lob)cache.get(node);
	Lob l = makeLob(node, false);
	//cache.put(node, l);
	return l;
    }

    private class LiteralTextModel extends RealtimeObject implements Model {
	public Object get() {
	    Object n = cursor.getNode();
	    if(n instanceof Literal) {
		//p("ret "+n); 
		return ((Literal)n).getString(); 
	    } else { 
		//p("ret ''"); 
		return ""; 
	    }
	}
	public void set(Object o) {
	    Object n = cursor.getNode();
	    if(!(n instanceof Literal)) return;

	    Literal literal = (Literal)n;
	    Literal nlit = Nodes.editLiteral(literal, (String)o);

	    Object node = cursor.getRotation().getRotationNode();
	    Object prop = cursor.getRotation().getRotationProperty();

	    graph.rm_111(node, prop, literal);
	    graph.add(node, prop, nlit);

	    //p("removed "+node+" "+prop+" "+literal);
	    //p("added "+nlit);

	    int textCursor = cursor.textCursor;
	    cursor.set(nlit, prop, node, -1);
	    cursor.textCursor = textCursor;
	}

	public int getInt() {
	    throw new UnsupportedOperationException();
	}
	public void set(int value) {
	    throw new UnsupportedOperationException();
	}
    }

    private Lob makeLob(Object node, boolean isPropertyLob) {
	if(node instanceof Literal) {
	    throw new Error();
	    /*
	    Model nodeModel = new ObjectModel(node);

	    Model str = new LiteralTextModel();
	    TextModel text = 
		new TextModel.StringTextModel(str, Theme.getFont());

	    Breaker br = new Breaker(Lob.X, text);

	    Model tc = cursor.textCursor;
	    Model lineModel = br.lineModel(tc);

	    Sequence seq = br;
	    seq = new TextCursorLob(br, tc, 
				    cursor.node.equalsModel(nodeModel));
	    
	    Model positionModel = seq.positionModel(Lob.Y, tc); 
	    Lob l = new TextEditController(seq, text, tc, lineModel);
	    l = new ViewportLob(Lob.Y, l, positionModel, new FloatModel(.5f));

	    str = new LiteralStringModel(nodeModel);
	    TextModel ntext = 
		new TextModel.StringTextModel(str, Theme.getFont());
	    br = new Breaker(Lob.X, ntext);
		
	    Lob l2 = new TextEditController(br, text, tc, lineModel);

	    Model lobModel = cursor.node.equalsModel(nodeModel).select(
		 new ObjectModel(l), new ObjectModel(l2));

	    return new ModelLob(lobModel);
	    */
	}

	return Components.label(texter.getText(node));

	/*
	Lob l;

	TextModel text = 
	    new TextModel.StringTextModel(str, Theme.getFont());
	
	Model tc = cursor.textCursor;
	tc = cursor.node.equalsModel(nodeModel).select(tc, new IntModel(-1));
	
	Breaker br = new Breaker(Lob.X, text);
	Model lineModel = br.lineModel(tc);
	
	Sequence seq = br;
	seq = new TextCursorLob(br, tc, 
				cursor.node.equalsModel(nodeModel));
	
	Model positionModel = seq.positionModel(Lob.Y, tc); 
	l = new TextEditController(seq, text, tc, lineModel);
	l = new ViewportLob(Lob.Y, l, positionModel, new FloatModel(.5f));
	return l;
	*/
    }

}
