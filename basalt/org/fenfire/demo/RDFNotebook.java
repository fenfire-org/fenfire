/*   
RDFNotebook.java
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
package org.fenfire.demo;
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

public class RDFNotebook extends LobLob {

    public static final Object
	NOTE = Nodes.get("http://fenfire.org/2004/10/20/RDFNotebook/Note"),
	DATE = Nodes.get("http://purl.org/dc/elements/1.1/date"),
	TITLE = Nodes.get("http://fenfire.org/2004/10/20/RDFNotebook/title"),
	TEXT = Nodes.get("http://fenfire.org/2004/10/20/RDFNotebook/text");


    private static Literal newDateLiteral() {
	Date d = new Date();
	String s = org.nongnu.storm.util.DateParser.getIsoDate(d);
	return new TypedLiteral(s, XSD.dateTime);
    }


    public RDFNotebook(final GraphFile file) {
	Model graphModel = new ObjectModel(file.getGraph());

	RDFLobFactory rlob = 
	    new RDFLobFactory(graphModel, Theme.getTextFont());
	Comparator dateCmp = new PropertyComparator(graphModel, DATE);

	SetModel notes = rlob.setModel(NOTE, RDF.type, -1);

	Object note1 = file.getGraph().findN_X11_Iter(RDF.type, NOTE).next();
	Model selectedNote = new ObjectModel(note1);


	final Object NEW_DATE_LITERAL = new Object();

	SetModel newNoteTriples = new SetModel.Simple();
	Model newNote = Parameter.model(CreateURIAction.NEW_NODE);
	Model dateLiteral = Parameter.model(NEW_DATE_LITERAL);
	newNoteTriples.add(new Triple(newNote, RDF.type, NOTE));
	newNoteTriples.add(new Triple(newNote, TITLE, 
				      new PlainLiteral("New note")));
	newNoteTriples.add(new Triple(newNote, DATE, dateLiteral));
	newNoteTriples.add(new Triple(newNote, TEXT,
				      new PlainLiteral("")));

	Action createNote = new AddToGraphAction(graphModel, newNoteTriples);

	createNote = new AbstractAction.Inline(createNote) {
		public void run(Object[] p) {
		    Action action = (Action)p[0];

		    Map params = new HashMap();
		    params.put(NEW_DATE_LITERAL, 
			       new ObjectModel(newDateLiteral()));
		    
		    Action a = (Action)action.instantiateTemplate(params);
		    a.run();
		}
	    };

	createNote = new Action.Concat(createNote,
				       new Model.Change(selectedNote, newNote));
	createNote = new CreateURIAction(createNote);

	Action saveAction = new AbstractAction() { public void run() {
	    file.save();
	}};
	Action quitAction = new AbstractAction() { public void run() {
	    System.exit(0);
	}};


	Box outerVBox = new Box(Y);

	Menu menubar = new Menu(X);
	outerVBox.add(new KeyLob(menubar, "MENUBAR"));

	Menu filemenu = menubar.addMenu("File");
	filemenu.add("Save", saveAction);
	filemenu.add("Quit", quitAction);

	Menu notemenu = menubar.addMenu("Note");
	notemenu.add("New note", createNote);
	//notemenu.add("Delete note", deleteNote);


	outerVBox.glue(5, 5, 5);

	Box hbox = new Box(X);
	outerVBox.add(hbox);

	hbox.glue(5, 5, 5);

	ListBox noteList = new ListBox(rlob.listModel(notes, dateCmp)); {
	    noteList.setTemplate(rlob.label("*", TITLE));
	    noteList.setKey("NOTES");
	    noteList.setSelection(selectedNote);
	}

	hbox.addRequest(noteList, 100, 250, 250);
	hbox.glue(5, 5, 5);

	Box vbox = new Box(Y);
	hbox.add(vbox);

	TextField titleField = rlob.textField(selectedNote, TITLE);
	vbox.add(titleField);

	vbox.glue(5, 5, 5);


	TextModel dateTM = 
	    new TextModel.Concat(rlob.textModel("Date: ", false), 
				 rlob.textModel(selectedNote, DATE));

	vbox.add(new Label(dateTM));

	vbox.glue(5, 5, 5);

	TextArea bodyArea = rlob.textArea(selectedNote, TEXT);
	// XXX -- should just be vbox.add()...
	vbox.addRequest(bodyArea, 30, 100, Float.POSITIVE_INFINITY);

	hbox.glue(5, 5, 5);

	outerVBox.glue(5, 5, 5);

	setDelegate(new FocusLob(outerVBox));
    }

    public static void main(String[] argv) throws IOException {
	Graph defaultGraph = new org.fenfire.swamp.impl.HashGraph();

	Object note1 = Nodes.N();
	defaultGraph.add(note1, RDF.type, NOTE);
	defaultGraph.add(note1, TITLE, new PlainLiteral("Note #1"));
	defaultGraph.add(note1, DATE, newDateLiteral());
	defaultGraph.add(note1, TEXT, new PlainLiteral(""));

	Graph graph = new org.fenfire.swamp.impl.HashGraph();

	final File file = new File("rdf_notebook_graph.turtle");
	final GraphFile gf = new GraphFile.Turtle(file, graph, defaultGraph);

	LobMain m = new LobMain(new Color(1, 1, .8f)) {
		protected Lob createLob() { return new RDFNotebook(gf); }
	    };
	m.start();
    }
}
