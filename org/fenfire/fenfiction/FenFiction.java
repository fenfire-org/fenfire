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
import org.fenfire.swamp.*;
import org.fenfire.lob.*;
import org.fenfire.vocab.*;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class FenFiction extends LobLob {

    private static final String 
	fic = "http://fenfire.org/vocab/2004/11/fenfiction#",
	allStories = "AllStories";

    public static final Object
	TITLE = Nodes.get("http://purl.org/dc/elements/1.1/title"), 
	NAME = Nodes.get(fic+"name"),
	STORY = Nodes.get(fic+"Story"),
	CHARACTER = Nodes.get(fic+"Character"),
	ELEMENT = Nodes.get(fic+"element");


    private static final float inf = Float.POSITIVE_INFINITY;


    protected class State {
	protected Object item, screen;

	protected State(Object item, Object screen) {
	    this.item = item; this.screen = screen;
	}
    }


    protected Lob viewLob(Lob heading, String msg, ListModel list,
			  Lob backButton) {
	Box outerHBox = new Box(X);

	Lob leftPane = new AlignLob(backButton, .5f, 0, .5f, 0);
	outerHBox.addRequest(leftPane, 100, 100, inf);

	Box outerVBox = new Box(Y);
	outerHBox.add(outerVBox);
	
	outerVBox.add(new AlignLob(heading, .5f, .5f, .5f, .5f));

	outerVBox.glue(20, 20, 20);

	Box hbox = new Box(X);
	outerVBox.add(hbox);

	Box vbox = new Box(Y);
	hbox.addRequest(vbox, 100, 200, 200);

	if(msg != null) {
	    Lob label = new Label(msg, true);
	    vbox.add(new KeyLob(label, "HELP"));
	}

	vbox.glue(0, 0, inf);

	hbox.glue(30, 30, 30);

	SequenceModel seq = new SequenceModel.ListSequenceModel(list);

	hbox.addRequest(new Box(Y, seq), 100, 200, 200);

	outerHBox.glue(0, 0, inf);

	return new FocusLob(new Margin(outerHBox, 20));
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
	    template = new ThemeFrame(template);
	    template = new ClickController(template, 1, 
					   new Model.Change(state, param));
	    template = new KeyLob(template, param);
	    
	    list = new ListModel.Transform(list, template);
	    
	    return list;
	}

	protected ListModel button(final String caption,
				   final Model state, 
				   final Object type,
				   final Object property) {
	    ListModel list = new ListModel.Simple();
	    list.add(new NoGrowLob(Y, new KeyLob(new Button(caption, 
						 new AbstractAction() {
		    public void run() {
			Object n = Nodes.N();
			graph.add(n, RDF.type, type);
			if(property != null)
			    graph.add(state.get(), property, n);
			state.set(n);
		    }
		}), "New "+type)));
	    list.add(new Glue(Lob.Y, 10, 10, 10));

	    return list;
	}

	protected ListModel end() {
	    ListModel glue = new ListModel.Simple();
	    glue.add(new Glue(Y, 0, 0, inf));
	    return glue;
	}
    }


    protected class AllStoriesView extends AbstractView {
	protected Model headingFont;
	
	protected AllStoriesView(Graph graph) { 
	    super(graph);

	    headingFont = 
		new ObjectModel(new LobFont("SansSerif", java.awt.Font.BOLD, 
					    20, Color.black));
	}
	
	public boolean accepts(Object state) {
	    return allStories.equals(state);
	}
	public Lob getViewLob(final Model state) {
	    Lob heading = 
		new KeyLob(new Label("All stories", headingFont),
			   "AllStories");

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
	private Object type;

	protected AbstractItemView(Graph graph, Object type) { 
	    super(graph);
	    this.type = type;
	}

	public boolean accepts(Object state) {
	    return graph.find1_11X(state, RDF.type).equals(type);
	}
    }

    protected class StoryView extends AbstractItemView {
	protected StoryView(Graph graph) { 
	    super(graph, STORY);
	}
	
	public Lob getViewLob(final Model state) {
	    Lob backButton = new Button("All stories", new AbstractAction() {
		    public void run() {
			state.set(allStories);
		    }
		}, new ObjectModel("AllStories"));

	    Lob heading = rlob.textField(state, TITLE, state);
	    heading = new RequestChangeLob(X, heading, 450, 450, 450);

	    SetModel _elems = rlob.setModel(state, ELEMENT, 1);
	    ListModel elems = rlob.listModel(_elems, new URIComparator());

	    ListModel newCharacter = button("New Character", state,
					    CHARACTER, ELEMENT);

	    ListModel list = new ListModel.Concat(newCharacter,
						  list(state, elems, TITLE), 
						  end());

	    return viewLob(heading, null, list, backButton);
	}
    }

    public FenFiction() {
	final GraphFile gf;

	try {
	    gf = new GraphFile.Turtle(new File("fenfiction.turtle"),
				      new org.fenfire.swamp.impl.HashGraph());
	} catch(IOException e) {
	    throw new Error(e);
	}

	Graph g = gf.getGraph();

	ListModel views = new ListModel.Simple();
	views.add(new AllStoriesView(g));
	views.add(new StoryView(g));

	Lob l = new BrowserLob(new ObjectModel(allStories), views);

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
