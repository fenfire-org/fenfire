/*   
FenFiction.java
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
package org.fenfire.fenfiction;
import org.nongnu.libvob.*;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.component.*;
import org.nongnu.libvob.impl.LobMain;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.*;
import org.fenfire.swamp.*;
import org.fenfire.lob.*;
import org.fenfire.vocab.*;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class FenFiction extends LobLob {

    public static class ClassType implements BrowserLob.Type {
	protected Graph graph;
	protected Object rdfClass;

	public ClassType(Graph graph, Object rdfClass) {
	    this.graph = graph;
	    this.rdfClass = rdfClass;
	}

	public boolean contains(Object state) {
	    Cursor c = (Cursor)state;
	    Object stateClass = graph.find1_11X(c.getNode(), RDF.type);
	    return stateClass != null && stateClass.equals(rdfClass);
	}

	public boolean equals(Object o) {
	    if(!(o instanceof ClassType)) return false;
	    ClassType t = (ClassType)o;
	    return rdfClass.equals(t.rdfClass) && graph.equals(t.graph);
	}
	public int hashCode() {
	    return rdfClass.hashCode() + graph.hashCode() + 32499;
	}
    }

    public static final String
	FIC = "http://fenfire.org/vocab/2004/11/fenfiction#";

    public static final Object
	//ALL_STORIES = Nodes.get(FIC+"allStories"),
	TITLE = Nodes.get("http://purl.org/dc/elements/1.1/title"), 
	NAME = Nodes.get(FIC+"name"),
	STORY = Nodes.get(FIC+"Story"),
	ALL_STORIES = STORY,
	CHARACTER = Nodes.get(FIC+"Character"),
	ELEMENT = Nodes.get(FIC+"element"),
	NOTE = Nodes.get(FIC+"Note"),
	TEXT = Nodes.get(FIC+"text"),
	RELATION = Nodes.get("http://purl.org/dc/elements/1.1/relation");


    private static final float inf = Float.POSITIVE_INFINITY;


    protected Lob assembleViewLob(Lob heading, Lob body, 
				  Lob leftPane, Lob rightPane) {
	Box outerHBox = new Box(X);

	outerHBox.addRequest(leftPane, 100, 100, inf);
	outerHBox.glue(20, 50, 50);

	Box outerVBox = new Box(Y);
	outerHBox.add(outerVBox);
	
	outerVBox.add(new AlignLob(heading, .5f, .5f, .5f, .5f));

	outerVBox.glue(20, 20, 20);

	outerVBox.add(body);

	outerHBox.glue(20, 50, 50);
	outerHBox.addRequest(rightPane, 100, 100, inf);

	return new FocusLob(new Margin(outerHBox, 20));
    }

    protected Lob viewLob(Lob heading, String msg, ListModel list,
			  Lob backButton) {
	Box hbox = new Box(X);

	Box vbox = new Box(Y);
	hbox.addRequest(vbox, 100, 200, 200);

	if(msg != null)
	    vbox.add(new Label(msg, true));

	vbox.glue(0, 0, inf);

	hbox.glue(30, 30, 30);

	SequenceModel seq = new SequenceModel.ListSequenceModel(list);

	hbox.addRequest(new Box(Y, seq), 100, 200, 200);

	Lob leftPane = new AlignLob(backButton, .5f, 0, .5f, 0);
	return assembleViewLob(heading, hbox, leftPane, NullLob.instance);
    }


    protected class SetCursor extends AbstractAction {
	protected Model state, node;
	public SetCursor(Model state, Model node) {
	    this.state = state; this.node = node;
	}
	public void run() {
	    throw new Error(); //state.set(new Cursor.SimpleCursor(node.get()));
	}

	protected Replaceable[] getParams() {
	    return new Replaceable[] { state, node };
	}
	protected Object clone(Object[] params) {
	    return new SetCursor((Model)params[0], (Model)params[1]);
	}
    }


    protected abstract class AbstractView implements BrowserLob.View {
	protected Graph graph;
	protected RDFLobFactory rlob;
	
	protected AbstractView(Graph graph) { 
	    this.graph = graph; 
	    this.rlob = new RDFLobFactory(new ObjectModel(graph), 
					  Theme.getFont());
	}

	protected ListModel list(Model state, ListModel list, 
				 Object property) {
	    Model param = Parameter.model(ListModel.PARAM);
	    Lob template = rlob.label(param, property);
	    template = new ThemeFrame(template, param);
	    template = new ClickController(template, 1, 
					   new SetCursor(state, param));
	    
	    list = new ListModel.Transform(list, template);
	    
	    return list;
	}

	protected ListModel button(final String caption,
				   final Model state, 
				   final Object type,
				   final Object property) {
	    ListModel list = new ListModel.Simple();
	    list.add(new NoGrowLob(Y, new Button(caption, 
						 new AbstractAction() {
		    public void run() {
			Cursor c = (Cursor)state.get();
			
			Object n = Nodes.N();
			graph.add(n, RDF.type, type);
			if(property != null)
			    graph.add(c.getNode(), property, n);
			throw new Error(); //state.set(new Cursor.SimpleCursor(n));
		    }
		})));
	    list.add(new Glue(Lob.Y, 10, 10, 10));

	    return list;
	}

	protected ListModel end() {
	    ListModel glue = new ListModel.Simple();
	    glue.add(new Glue(Y, 0, 0, inf));
	    return glue;
	}
    }


    protected BrowserLob.Type allStoriesType = new BrowserLob.Type() {
	    public boolean contains(Object state) {
		return ALL_STORIES.equals(((Cursor)state).getNode());
	    }
	};

    protected class AllStoriesView extends AbstractView {
	protected Model headingFont;
	
	protected AllStoriesView(Graph graph) { 
	    super(graph);

	    headingFont = 
		new ObjectModel(new LobFont("SansSerif", java.awt.Font.BOLD, 
					    30, Color.black));
	}
	
	public Set getTypes() {
	    return Collections.singleton(allStoriesType);
	}
	public Lob getViewLob(final Model state) {
	    Lob heading = 
		new KeyLob(new Label("All stories", headingFont),
			   ALL_STORIES);

	    String msg =
		"Please select a story from the right "+
		"or click on the New Story button "+
		"to create a new story.";

	    ListModel stories = rlob.listModel(STORY, new URIComparator());
	    ListModel newStory = button("New Story", state, STORY, null);

	    ListModel list = new ListModel.Concat(newStory,
						  list(state, stories, TITLE), 
						  end());

	    return viewLob(heading, msg, list, NullLob.instance);
	}
    }

    protected abstract class AbstractItemView extends AbstractView {
	private BrowserLob.Type type;

	protected AbstractItemView(Graph graph, Object rdfClass) { 
	    super(graph);
	    this.type = new ClassType(graph, rdfClass);
	}

	public Set getTypes() {
	    return Collections.singleton(type);
	}
    }

    protected class StoryView extends AbstractItemView {
	protected StoryView(Graph graph) { 
	    super(graph, STORY);
	}
	
	public Lob getViewLob(final Model state) {
	    Lob backButton = new Button("All stories", new AbstractAction() {
		    public void run() {
			throw new Error(); //state.set(new Cursor.SimpleCursor(ALL_STORIES));
		    }
		}, new ObjectModel(ALL_STORIES));

	    Model node = Models.adaptMethod(state, Cursor.class, "getNode");

	    Lob heading = rlob.textField(node, TITLE, node);
	    heading = new RequestChangeLob(X, heading, 450, 450, 450);

	    SetModel _elems = rlob.setModel(node, ELEMENT, 1);
	    ListModel elems = rlob.listModel(_elems, new URIComparator());

	    ListModel newCharacter = button("New Character", state,
					    CHARACTER, ELEMENT);

	    ListModel list = new ListModel.Concat(newCharacter,
						  list(state, elems, NAME), 
						  end());

	    return viewLob(heading, null, list, backButton);
	}
    }

    protected class CharacterView extends AbstractItemView {
	protected CharacterView(Graph graph) { 
	    super(graph, CHARACTER);
	}
	
	public Lob getViewLob(final Model state) {
	    Model node = Models.adaptMethod(state, Cursor.class, "getNode");

	    final Model story = rlob.value(node, ELEMENT, -1);
	    Lob label = rlob.label(story, TITLE);

	    Lob backButton = new Button(label, new SetCursor(state, story),
					story);

	    Lob heading = rlob.textField(node, NAME, node);
	    heading = new RequestChangeLob(X, heading, 450, 450, 450);

	    SetModel _elems = rlob.setModel(node, RELATION, 1);
	    ListModel elems = rlob.listModel(_elems, new URIComparator());

	    ListModel newCharacter = button("New Note", state,
					    NOTE, RELATION);

	    ListModel list = new ListModel.Concat(newCharacter,
						  list(state, elems, TITLE), 
						  end());

	    return viewLob(heading, null, list, backButton);
	}
    }

    protected class NoteView extends AbstractItemView {
	protected NoteView(Graph graph) { 
	    super(graph, NOTE);
	}
	
	public Lob getViewLob(final Model state) {
	    Model node = Models.adaptMethod(state, Cursor.class, "getNode");

	    ListModel listHeading = new ListModel.Simple();
	    listHeading.add(new Label("Related items:"));
	    listHeading.add(new Glue(Lob.Y, 10, 10, 10));

	    SetModel _elems = rlob.setModel(node, RELATION, -1);
	    ListModel elems = rlob.listModel(_elems, new URIComparator());

	    ListModel list = new ListModel.Concat(listHeading,
						  list(state, elems, NAME), 
						  end());

	    SequenceModel seq = new SequenceModel.ListSequenceModel(list);
	    
	    Lob related = new Box(Y, seq);


	    Lob heading = rlob.textField(node, TITLE, node);
	    heading = new RequestChangeLob(X, heading, 450, 450, 450);

	    Lob body = rlob.textArea(node, TEXT);
	    
	    return assembleViewLob(heading, body, NullLob.instance, related);
	}
    }

    protected Set getViews(Graph g) {
	Set views = new HashSet();
	views.add(new AllStoriesView(g));
	views.add(new StoryView(g));
	views.add(new CharacterView(g));
	views.add(new NoteView(g));
	return views;
    }

    public FenFiction() {
	final GraphFile gf;

	try {
	    gf = new GraphFile.Turtle(new File("fenfiction.turtle"),
				      new org.fenfire.swamp.impl.HashGraph(),
				      new org.fenfire.swamp.impl.HashGraph());
	} catch(IOException e) {
	    throw new Error(e);
	}

	Graph g = gf.getGraph();

	Lob l = new BrowserLob(null /* XXX!!! new ObjectModel(new Cursor.SimpleCursor(ALL_STORIES))*/, 
			       getViews(g));

	KeyController k = new KeyController(l);
	k.add("Ctrl-S", new AbstractAction() { public void run() {
	    gf.save();
	    System.out.println("Saved.");
	}});

	setDelegate(k);
    }

    public static void main(String[] argv) throws IOException {
	LobMain m = new LobMain(new Color(1, 1, .8f)) {
		protected Lob createLob() { return new FenFiction(); }
	    };
	m.start();
    }
}
