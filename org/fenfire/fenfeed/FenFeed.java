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
	ENCODED = Nodes.get(content+"encoded");

    private static final Object[]
	ITEM_TEXT = { ENCODED, DESC };


    private static final float inf = Float.POSITIVE_INFINITY;

    // conf = configuration node
    public FenFeed(final Graph g, final Object conf) { 
	Model gm = new ObjectModel(g);
	
	Color col = Color.black;
	Model font = new ObjectModel(new LobFont("Serif", 0, 20, col));
	Model boldFont = new ObjectModel(new LobFont("Serif", 
						     java.awt.Font.BOLD, 
						     20, col));

	final RDFLobFactory rlob = new RDFLobFactory(gm, font);

	SetModel channels = rlob.setModel(CHANNEL, RDF.type, -1);
	Comparator cmp = new PropertyComparator(gm, DATE);

	Model selectedChannel = new ObjectModel();
	if(!channels.isEmpty())
	    selectedChannel.set(channels.iterator().next());

	ListModel items = rlob.containerModel(selectedChannel, ITEMS, 1);

	final Model selectedItem = new ObjectModel();

	selectedItem.addObs(new Obs() { public void chg() {
	    g.add(conf, READ, selectedItem.get());
	}});

	if(!items.isEmpty())
	    selectedItem.set(items.iterator().next());

	Box hbox = new Box(X);

	Model channel = Parameter.model(ListModel.PARAM);
	ListModel cis = rlob.containerModel(channel, ITEMS, 1);// channel items
	Model allRead = new FunctionModel(cis) { public Object f(Object o,
								 Obs obs) {
	    boolean allRead = true;

	    for(Iterator i=((Collection)o).iterator(); i.hasNext();) {
		if(!g.contains(conf, READ, i.next(), obs))
		    allRead = false;
	    }

	    return allRead ? Boolean.TRUE : Boolean.FALSE;
	}};
	Model channelFont = allRead.select(font, boldFont);
	Lob chTemplate = new Label(rlob.string(channel, TITLE), channelFont);

	ListBox channel_list = 
	    new ListBox(rlob.listModel(channels, cmp), chTemplate, 
			new ObjectModel("channels"));
	channel_list.setSelectionModel(selectedChannel);
	hbox.addRequest(channel_list, 250, 250, 250);

	hbox.glue(5, 5, 5);

	Box vbox = new Box(Y);
	hbox.add(vbox);


	SetModel itemsRead = rlob.setModel(conf, READ, 1);

	Model item = Parameter.model(ListModel.PARAM);
	Model wasRead = itemsRead.containsModel(item);
	Model itemFont = wasRead.select(font, boldFont);
	Lob template = new Label(rlob.string(item, TITLE), itemFont);

	ListBox item_list = new ListBox(items, template, 
					new ObjectModel("items"));
	item_list.setSelectionModel(selectedItem);
	vbox.addRequest(item_list, 200, 200, 200);

	vbox.glue(5, 5, 5);

	vbox.add(rlob.textArea(selectedItem, ITEM_TEXT));

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
