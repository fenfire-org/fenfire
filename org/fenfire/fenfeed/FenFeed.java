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
	rss = "http://purl.org/rss/1.0/";

    public static final Object
	CHANNEL = Nodes.get(rss+"channel"),
	ITEM = Nodes.get(rss+"item"),
	ITEMS = Nodes.get(rss+"items"),
	TITLE = Nodes.get(rss+"title"),
	DATE = Nodes.get(rss+"date"),
	DESC = Nodes.get(rss+"description");


    private static final float inf = Float.POSITIVE_INFINITY;

    public FenFeed(Graph g) {
	Model gm = new ObjectModel(g);

	RDFLobFactory rlob = 
	    new RDFLobFactory(gm, Theme.getTextFont());

	SetModel channels = rlob.setModel(CHANNEL, RDF.type, -1);
	Comparator cmp = new PropertyComparator(gm, DATE);

	Model selectedChannel = new ObjectModel();
	if(!channels.isEmpty())
	    selectedChannel.set(channels.iterator().next());

	ListModel items = rlob.containerModel(selectedChannel, ITEMS, 1);

	Model selectedItem = new ObjectModel();
	if(!items.isEmpty())
	    selectedItem.set(items.iterator().next());

	Box hbox = new Box(X);

	ListBox channel_list = rlob.listBox(channels, TITLE, cmp, "channels");
	channel_list.setSelectionModel(selectedChannel);
	hbox.addRequest(channel_list, 250, 250, 250);

	hbox.glue(5, 5, 5);

	Box vbox = new Box(Y);
	hbox.add(vbox);

	ListBox item_list = rlob.listBox(items, TITLE, "items");
	item_list.setSelectionModel(selectedItem);
	vbox.addRequest(item_list, 200, 200, 200);

	vbox.glue(5, 5, 5);

	vbox.add(rlob.textArea(selectedItem, DESC));

	setDelegate(new Margin(hbox, 5));
    }

    public static void main(String[] args) throws IOException {
	HTTPContext context = new HTTPContext();
	QuadsGraph graph = new SmushedQuadsGraph();

	Set subscriptions = new HashSet();

	for(int i=0; i<args.length; i++)
		subscriptions.add(args[i]);

	Aggregator agg = new Aggregator(graph, new HashMap(), subscriptions,
					30, context);

	agg.start();

	final Graph g = new AllQuadsGraph(graph, "foo");

	LobMain m = new LobMain(new Color(1, 1, .8f)) {
		protected Lob createLob() { return new FenFeed(g); }
	    };
	m.start();
    }
}
