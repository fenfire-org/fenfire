/*   
Aggregator.java
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
import org.fenfire.swamp.*;
import org.fenfire.swamp.impl.*;
import org.fenfire.swamp.smush.*;
import java.io.*;
import java.util.*;

public class Aggregator implements HTTPUpdater.UpdateListener {

    protected QuadsGraph graph;
    protected HTTPContext httpContext;
    protected Set subscriptions; // URIs
    protected Map namespaces;

    protected HTTPUpdater updater;

    public Aggregator(QuadsGraph graph, Map namespaces,
		      Set subscriptions, int loadInterval,
		      HTTPContext httpContext) {
	this.graph = graph;
	this.namespaces = namespaces;
	this.subscriptions = subscriptions;
	this.httpContext = httpContext;

	this.updater = new HTTPUpdater(httpContext);
	updater.addUpdateListener(this);
	
	for(Iterator i=subscriptions.iterator(); i.hasNext();) {
	    String uri = (String)i.next();
	    subscribe(uri, loadInterval);
	}
    }

    public void start() {
	updater.start();
    }

    public void subscribe(String uri, int loadInterval) {
	System.out.println("Subscribe to "+uri);
	try {
	    HTTPResource r = updater.add(uri, loadInterval);
	    FeedReader.read(r, new OneQuadGraph(graph, uri), namespaces);
	} catch(IOException e) {
	    System.out.println("Error while subscribing to "+uri);
	    e.printStackTrace();
	}
    }

    public void changed(HTTPResource resource) {
	System.out.println("Changed: "+resource.getURI());

	Object context = Nodes.get(resource.getURI());
	graph.rm_AAA1(context);
	try {
	    FeedReader.read(resource, new OneQuadGraph(graph, context), 
			    namespaces);
	} catch(IOException e) {
	    System.out.println("Error while reading "+resource.getURI());
	    e.printStackTrace();
	}

	try {
	    org.nongnu.libvob.AbstractUpdateManager.chg();
	} catch(NullPointerException e) {
	    // AbstractUpdateManager isn't initialized yet
	}
    }

    public void startUpdate(HTTPResource resource) {
	System.out.println("Updating "+resource.getURI());
    }
    public void unchanged(HTTPResource resource) {
	System.out.println("Unchanged: "+resource.getURI());
    }
    public void updateFailed(HTTPResource resource, IOException error) {
	System.out.println("Update of "+resource.getURI()+" failed:");
	error.printStackTrace();
    }


    public static void main(String[] args) throws Exception {
	HTTPContext context = new HTTPContext();
	QuadsGraph graph = new SmushedQuadsGraph();

	Set subscriptions = new HashSet();

	boolean start = false;
	for(int i=0; i<args.length; i++) {
	    if(args[i].equals("-start"))
		start = true;
	    else
		subscriptions.add(args[i]);
	}

	Aggregator agg = new Aggregator(graph, new HashMap(), subscriptions,
					30, context);

	if(start) {
	    agg.start();
	} else {
	    Graph g = new AllQuadsGraph(graph, "foo");
	    StringWriter w = new StringWriter();
	    Graphs.writeTurtle(g, agg.namespaces, w);
	    System.out.println(w.toString());
	}
    }
}
