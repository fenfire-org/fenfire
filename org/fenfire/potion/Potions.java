/*
Potions.java
 *    
 *    Copyright (c) 2002-2005, Benja Fallenstein
 *    
 *    This file is part of Fenfire.
 *    
 *    Fenfire is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Fenfire is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU Lesser General
 *    Public License along with Fenfire; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *    
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.potion;
import org.fenfire.Cursor;
import org.fenfire.swamp.*;
import org.fenfire.vocab.*;
import org.fenfire.view.ReprView;
import org.nongnu.libvob.lob.LobFont;
import java.util.*;

public class Potions {

    public static Type node = new SimpleType("which node?");
    public static Type property = new SimpleType("which property?");

    public static Command connect = new SimpleCommand(new Object[] {
	"Connect ", node, " on ", property, " to ", node }) {

	    public void execute(Object[] params, Map context) {
		Graph g = (Graph)context.get("graph");
		g.add(params[0], params[1], params[2]);

		// hmmm... it's not too nice to have this here, but how else
		// could we make it work?
		Cursor cursor = (Cursor)context.get("cursor");
		cursor.setRotation(params[0], params[1], params[2]);
	    }
	};

    public static Command connectBackwards = new SimpleCommand(new Object[] {
	"Connect ", node, " backwards on ", property, " to ", node }) {

	    public void execute(Object[] params, Map context) {
		Graph g = (Graph)context.get("graph");
		g.add(params[2], params[1], params[0]);
	    }
	};

    public static Command changeURI = new SimpleCommand(new Object[] {
	"Change URI of ", node, " to ", node }) {

	    public void execute(Object[] params, Map context) {
		Graph g = (Graph)context.get("graph");
		Cursor cursor = (Cursor)context.get("cursor");

		Graphs.changeURI(g, params[0], params[1]);
		cursor.smushed(params[0], params[1]);
	    }
	};

    public static Command bookmark = new SimpleCommand(new Object[] {
	"Bookmark ", node }) {

	    public void execute(Object[] params, Map context) {
		Cursor cursor = (Cursor)context.get("cursor");
		Graph prefs = (Graph)context.get("prefsGraph");

		prefs.add(Nodes.get(""), FF.bookmarks, params[0]);
	    }
	};

    public static Command goTo = new SimpleCommand(new Object[] {
	"Go to ", node }) {

	    public void execute(Object[] params, Map context) {
		Cursor cursor = (Cursor)context.get("cursor");
		cursor.setNode(params[0]);
	    }
	};

    public static FunctionExpression newNode = 
	new SimpleFunction(new Object[] { "a new node" }) {
	    public Object evaluate(Object[] params, Map context) {
		Object node = Nodes.N();

		// hmmm... it's not too nice to have this here, but how else
		// could we make it work?
		Cursor cursor = (Cursor)context.get("cursor");
		cursor.setNode(node);

		return node;
	    }
	}.call();

    public static FunctionExpression newLiteral = 
	new SimpleFunction(new Object[] { "a new literal" }) {
	    public Object evaluate(Object[] params, Map context) {
		return new PlainLiteral("");
	    }
	}.call();


    public static FunctionExpression currentNode = new AbstractFunction.Pattern() {
	    public Head instantiatePattern(Map context) {
		Cursor c = (Cursor)context.get("cursor");
		return nodeFn(c.getNode(), Nodes.toString(c.getNode()));
	    }
	}.call();


    public static Function nodeFn(final Object n, String name) {
	return new AbstractFunction(new Object[] { name }) {
		public List evaluate(List[] params, Map context) {
		    return Collections.singletonList(n);
		}

		public List getLobs(Expression[] params, Map context, 
				    LobFont font) {
		    ReprView reprView = (ReprView)context.get("reprView");
		    return reprView.getLobList(n);
		}
	    };
    }

    public static FunctionExpression node(final Object n, String name) {
	return nodeFn(n, name).call();
    }

    

    public static class SimpleType implements Type {
	protected String question;

	public SimpleType(String question) {
	    this.question = question;
	}

	public String getQuestionString() {
	    return "[" + question + "]";
	}

	public List getQuestionLobs(LobFont font) {
	    return font.text(getQuestionString());
	}
    }
}
