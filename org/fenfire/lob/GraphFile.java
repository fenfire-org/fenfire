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
import org.fenfire.vocab.RDFS;
import org.fenfire.util.*;
import java.io.*;
import java.util.*;

public interface GraphFile {
    Graph getGraph();
    Map getNamespaces(); // namespace shortname -> namespace uri
    File getFile();
    void save(NamespaceMap more_names);

    class Helpers {
	public static boolean dbg = false;
	private static void p(String s) { System.out.println("GraphFile:: "+s); }

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

	private static void addURN5Namespaces(Graph g, Map namespaces) {
	    for(Iterator i=g.findN_X1A_Iter(RDFS.label); i.hasNext();) {
		Object o = i.next();
		String uri = Nodes.toString(o);
		if(uri.startsWith("urn:urn-5:")) {
		    String label = null;
		    for(Iterator j=g.findN_11X_Iter(o, RDFS.label); j.hasNext();) {
			Object obj = j.next();
			if(obj instanceof Literal) {
			    label = ((Literal)obj).getString();
			    if(label.trim().equals("")) {
				label = null;
			    } else {
				break;
			    }
			}
		    }

		    if(label == null) continue;

		    String prefix = toPrefix(label);
		    if(namespaces.get(prefix) == null) {
			namespaces.put(prefix, uri);
		    }
		}
	    }
	}

	private static String toPrefix(String label) {
	    StringBuffer prefix = new StringBuffer();
	    boolean hadDash = false;
	    for(int i=0; i<label.length(); i++) {
		char c = label.charAt(i);
		if(('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') ||
		   (prefix.length() > 0 && '0' <= c && c <= '9')) {
		    prefix.append(c);
		    hadDash = false;
		} else if(!hadDash) {
		    if(prefix.length() > 0) // prefixes can't start with -
			prefix.append('-');
		    hadDash = true;
		}
	    }
	    return prefix.toString();
	}

	private static void removeURN5Namespaces(Graph g, Map namespaces) {
	    for(Iterator i=namespaces.entrySet().iterator(); i.hasNext();) {
		Map.Entry e = (Map.Entry)i.next();
		String prefix = (String)e.getKey();
		String uri = (String)e.getValue();

		if(!uri.startsWith("urn:urn-5:")) continue;

		Object node = Nodes.get(uri);
		for(Iterator j=g.findN_11X_Iter(node, RDFS.label); j.hasNext();) {
		    Object object = j.next();
		    if(!(object instanceof Literal)) continue;
		    String label = ((Literal)object).getString();
		    if(prefix.equals(toPrefix(label))) {
			i.remove();
			break;
		    }
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
		Helpers.removeURN5Namespaces(graph, namespaces);
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

	public File getFile() {
	    return file;
	}

	public void save(NamespaceMap more_names) {
	    Helpers.addNewNamespaces(graph, namespaces, more_names);
	    Helpers.addURN5Namespaces(graph, namespaces);
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
		Helpers.removeURN5Namespaces(graph, namespaces);
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

	public File getFile() {
	    return file;
	}

	public void save(NamespaceMap more_names) {
	    Helpers.addNewNamespaces(graph, namespaces, more_names);
	    Helpers.addURN5Namespaces(graph, namespaces);
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
