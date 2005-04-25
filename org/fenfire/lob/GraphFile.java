/*   
GraphFile.java
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
package org.fenfire.lob;
import org.fenfire.swamp.*;
import org.fenfire.util.*;
import java.io.*;
import java.util.*;

public interface GraphFile {

    Graph getGraph();
    Map getNamespaces(); // namespace shortname -> namespace uri
    void save(NamespaceMap more_names);

    class Helpers {
	/** Adds new namespaces from another list when they get used. 
	 *  Initially, a file doesn't have namespaces at all. */
	private static void addNewNamespaces(Graph graph, Map namespaces,
					     NamespaceMap more_names) {
	    NamespaceMap nmap = new NamespaceMap();
	    nmap.putAll(namespaces);
	    Iterator preds = graph.findN_AXA_Iter(); // XXX other nodes too?
	    while (preds.hasNext()) {
		String pred = preds.next().toString();
		if (!pred.equals(nmap.getAbbrevString(pred)))
		    continue; // we already have an abbreviation
		String abbrev = more_names.getAbbrevString(pred);
		if (!pred.equals(abbrev)) {
		    String prefix = abbrev.substring(0, abbrev.indexOf(':'));
		    namespaces.put(prefix, more_names.getURIForPrefix(prefix));
		}
	    }
	}
    }

    class Turtle implements GraphFile {
	protected Graph graph;
	protected Map namespaces;
	protected File file;
	
	/*
	public Turtle(File file, Graph graph) {
	    this(file, graph, null);
	}
	*/

	public Turtle(File file, Graph graph, Graph defaultGraph) 
	    throws IOException {

	    this.file = file;
	    this.graph = graph;
	    this.namespaces = new HashMap();

	    if(file.exists()) {
		Graphs.readTurtle(file, graph, namespaces);
	    } else if(defaultGraph == null) {
		throw new FileNotFoundException(""+file);
	    } else {
		graph.addAll(defaultGraph);
	    }
	}

	public Graph getGraph() {
	    return graph;
	}

	public Map getNamespaces() {
	    return namespaces;
	}

	public void save(NamespaceMap more_names) {
	    Helpers.addNewNamespaces(graph, namespaces, more_names);
	    try {
		Graphs.writeTurtle(graph, namespaces, file);
	    } catch(java.io.IOException e) {
		throw new Error(e);
	    }
	}
    }

    class XML implements GraphFile {
	protected Graph graph;
	protected Map namespaces;
	protected File file;
	
	/*
	public XML(File file, Graph graph) {
	    this(file, graph, null);
	}
	*/

	public XML(File file, Graph graph, Graph defaultGraph) 
	    throws IOException {

	    this.file = file;
	    this.graph = graph;
	    this.namespaces = new HashMap();

	    if(file.exists()) {
		Graphs.readXML(file, graph, namespaces);
	    } else if(defaultGraph == null) {
		throw new FileNotFoundException(""+file);
	    } else {
		graph.addAll(defaultGraph);
	    }
	}

	public Graph getGraph() {
	    return graph;
	}

	public Map getNamespaces() {
	    return namespaces;
	}

	public void save(NamespaceMap more_names) {
	    Helpers.addNewNamespaces(graph, namespaces, more_names);
	    try {
		NamespaceMap nmap = new NamespaceMap();
		nmap.putAll(namespaces);
		Graphs.writeXML(graph, file, nmap);
	    } catch(java.io.IOException e) {
		throw new Error(e);
	    }
	}
    }
}
