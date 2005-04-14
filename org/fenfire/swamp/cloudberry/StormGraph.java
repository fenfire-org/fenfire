/*
StormGraph.java
 *    
 *    Copyright (c) 2004, Matti J. Katila
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
 */
/*
 * Written by Matti J. Katila
 */

package org.fenfire.swamp.cloudberry;
import org.fenfire.swamp.*;
import org.fenfire.swamp.impl.*;
import org.fenfire.vocab.RDF;

import org.nongnu.storm.*;
import org.nongnu.storm.references.*;

import org.python.util.*;
import org.python.core.*;

import java.util.*;
import java.io.*;

/** An RDF graph used to store in storm pointer.
 */
public class StormGraph extends AllQuadsGraph {
    private void p(String s) { System.out.println("StormGraph:: "+s); }
    
    final PointerId ptr;
    final IndexedPool pool;
    
    final Object pred = 
    Nodes.get("http://org.fenfire.swamp.cloudberry.StormGraph#belongsTo.html");

    // import the rdf into the system
    public StormGraph(QuadsGraph g, IndexedPool pool, 
		      PointerId ptr) throws Exception {
	super(g, Nodes.N());
	this.pool = pool;
	this.ptr = ptr;


	// test case..
	if (pool != null && ptr != null) {
	    // take the latest version..
	    Reference ref = ((PointerIndex)pool.getIndex(PointerIndex.uri)
			     ).getMostCurrent(ptr, Pointers.VERSION_PROPERTIES);
	    
	    
	    p("ptr: "+ptr);
	    String URI = ref.get("_:this", Pointers.version);
	    Block b = pool.get(new BlockId(URI));
	    p("id: "+b.getId());
	    p("ct: "+b.getId().getContentType());
	    BufferedReader buf = new BufferedReader(
						    new InputStreamReader(b.getInputStream(), "US-ASCII"));
	    String s = buf.readLine();
	    while (s != null) {
		p("-> "+s);
		s = buf.readLine();
	    }
	    
	    Graph graph = new HashGraph();

	    Graphs.readXML(b.getInputStream(), b.getId().toString(),
			   graph, new HashMap());
	}
    }

    public void remove() {
	graph.rm_AAA1(context);
    }
    
    PythonInterpreter interp;
    
    public void flush(PointerSigner signer) throws Exception {
	Graph graph = new HashGraph();
	for(Iterator i=super.findN_X11_Iter(pred, context); i.hasNext(); ) {
	    Object t = i.next();
	    Object s,p,o;
	    s = super.find1_11X(t, RDF.subject);
	    p = super.find1_11X(t, RDF.predicate);
	    o = super.find1_11X(t, RDF.object);
	    graph.add(s,p,o);
	}


	// we need to use jython writer :/
	if(interp == null) {
	    interp = new PythonInterpreter();
	    interp.exec("from org.fenfire.swamp import writer\n"+
			"wr = writer.writeToOutputStream");
	}
	BlockOutputStream bos = pool.getBlockOutputStream("application/rdf+xml");
	if(! interp.get("wr").__call__(new PyObject[] {
	    new PyJavaInstance(graph),
	    new PyJavaInstance(bos)
	}).__nonzero__())
	    throw new Error("Writer call aborted.");
	bos.close();

	signer.updateNewest(ptr, bos.getBlockId().getURI());

    }

}
