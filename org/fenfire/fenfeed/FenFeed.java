/*   
FenFeed.java
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
package org.fenfire.fenfeed;
import org.fenfire.fenfeed.http.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.component.*;
import org.nongnu.libvob.impl.LobMain;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.gnowsis.Gnowsis;
import org.fenfire.swamp.*;
import org.fenfire.swamp.impl.*;
import org.fenfire.swamp.smush.*;
import org.fenfire.lob.*;
import org.fenfire.vocab.*;
import java.awt.Color;
import java.io.*;
import java.util.*;

public class FenFeed extends LobLob {

    private static final String
	fenfeed = "http://fenfire.org/2004/12/fenfeed#",
	rss = "http://purl.org/rss/1.0/",
	dc = "http://purl.org/dc/elements/1.1/",
	content = "http://purl.org/rss/1.0/modules/content/";

    public static final Object
	CONFIGURATION = Nodes.get(fenfeed+"Configuration"),
	SUBSCRIBED_TO = Nodes.get(fenfeed+"subscribedTo"), // config -> feed
	READ = Nodes.get(fenfeed+"read"); // config -> item (marked as read)

    private static final Object
	CHANNEL = Nodes.get(rss+"channel"),
	ITEM = Nodes.get(rss+"item"),
	ITEMS = Nodes.get(rss+"items"),
	TITLE = Nodes.get(rss+"title"),
	DATE = Nodes.get(rss+"date"),
	DESC = Nodes.get(rss+"description"),
	ENCODED = Nodes.get(content+"encoded"),
	
	DC_DATE = Nodes.get(dc+"date"),
	DC_CREATOR = Nodes.get(dc+"creator"),
	DC_SUBJECT = Nodes.get(dc+"subject");

    private static final Object[]
	ITEM_TEXT = { ENCODED, DESC };


    private static final float inf = Float.POSITIVE_INFINITY;

    // conf = configuration node
    public FenFeed(final Graph g, final Object conf) { 
	Model gm = new ObjectModel(g);
	
	Color col = Color.black;
	Model font = new ObjectModel(new LobFont("Serif", 0, 20, col));
	Model boldFont = 
	    new ObjectModel(new LobFont("Serif", java.awt.Font.BOLD, 20, col));

	RDFLobFactory rlob = new RDFLobFactory(gm, font);
	Comparator cmp = new PropertyComparator(gm, DATE);


	SetModel channels = rlob.setModel(CHANNEL, RDF.type, -1);

	final Model selectedChannel = new ObjectModel();
	final Model selectedItem = new ObjectModel();

	ListModel items = rlob.containerModel(selectedChannel, ITEMS, 1);


	selectedItem.addObs(new Obs() { public void chg() {
	    g.add(conf, READ, selectedItem.get());
	}});

	if(!channels.isEmpty())
	    selectedChannel.set(channels.iterator().next());

	if(!items.isEmpty())
	    selectedItem.set(items.iterator().next());


	ListBox channelList = new ListBox(new ListModel.ListCache(channels)); {
	    channelList.setKey("channels");
	    channelList.setSelection(selectedChannel);

	    // Whether all items in this channel are marked as read
	    Model read = Models.forall(rlob.containerModel("*", ITEMS, 1),
				       rlob.graphContains(conf, READ, "*"));

	    channelList.setTemplate(new Label(rlob.string("*", TITLE), 
					      read.select(font, boldFont)));
	}


	ListBox itemList = new ListBox(items); {
	    itemList.setKey("items");
	    itemList.setSelection(selectedItem);

	    // Whether this item is marked as read
	    Model read = rlob.graphContains(conf, READ, "*");

	    itemList.setTemplate(new Label(rlob.string("*", TITLE),
					   read.select(font, boldFont)));
	}


	TextArea textArea = new TextArea(); {
	    textArea.setKey("body");
	    textArea.setText(rlob.textModel(new Object[] {
		"Title: ", rlob.textsModel(selectedItem, TITLE, null), "\n",
		"Date: ",  rlob.textsModel(selectedItem, DC_DATE, null), "\n",
		"Creator: ",rlob.textsModel(selectedItem,DC_CREATOR,null),"\n",
		"Subject: ",rlob.textsModel(selectedItem,DC_SUBJECT,null),"\n",
		"\n",
	    rlob.textModel(selectedItem, ITEM_TEXT),
	    }));
	}


	Box hbox = new Box(X); {

	    Box vbox = new Box(Y);

	    hbox.addRequest(channelList, 250, 250, 250);
	    hbox.glue(5, 5, 5);
	    hbox.add(vbox); {
		vbox.addRequest(itemList, 200, 200, 200);
		vbox.glue(5, 5, 5);
		vbox.add(textArea);
	    }
	}



	Lob l = new Margin(hbox, 5);
	l = new FocusLob(l);
	
	KeyController kc = new KeyController(l);
	kc.add("Ctrl-Q", new AbstractAction() { public void run() {
	    System.exit(0);
	}});
	kc.add("Ctrl-O", new AbstractAction() { public void run() {
	    Gnowsis.open(selectedItem.get());
	}});

	setDelegate(kc);
    }

    public static void main(String[] args) throws IOException {
	final HTTPContext context = new HTTPContext();
	final QuadsGraph graph = new SmushedQuadsGraph();

	final Graph confGraph = new OneQuadGraph(graph, "foo:fenfeed-conf");

	final File confFile = new File("fenfeed_conf.turtle");
	if(confFile.exists())
	    Graphs.readTurtle(confFile, confGraph, new HashMap());

	Object _configuration = confGraph.find1_X11(RDF.type, CONFIGURATION);
	if(_configuration == null) {
	    _configuration = Nodes.N();
	    confGraph.add(_configuration, RDF.type, CONFIGURATION);
	}
	final Object configuration = _configuration;

	Set subscriptionsModel = 
	    new PropValueSetModel(confGraph, new ObjectModel(configuration), 
				  SUBSCRIBED_TO, 1);

	for(int i=0; i<args.length; i++)
	    subscriptionsModel.add(Nodes.get(args[i]));

	final Set subscriptions = new HashSet(subscriptionsModel);

	Runtime.getRuntime().addShutdownHook(new Thread() { public void run() {
	    try {
		Graphs.writeTurtle(confGraph, new HashMap(), confFile);
	    } catch(IOException e) {
		throw new Error(e);
	    }
	}});

	final Graph g = new AllQuadsGraph(graph, "foo:fenfeed-conf");

	LobMain m = new LobMain(new Color(1, 1, .8f)) {
		protected Lob createLob() { 
		    Aggregator agg = new Aggregator(graph, new HashMap(), 
						    subscriptions, 30,
						    context);

		    return new FenFeed(g, configuration); 
		}
	    };
	m.start();
    }
}
