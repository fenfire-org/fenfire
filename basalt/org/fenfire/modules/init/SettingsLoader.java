/*
SettingsLoader.java
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

package org.fenfire.modules.init;
import org.fenfire.view.management.*;
import org.fenfire.util.*;
import org.fenfire.swamp.*;
import org.fenfire.swamp.impl.*;
import org.fenfire.swamp.cloudberry.*;
import org.nongnu.storm.*;
import org.nongnu.storm.references.*;

import org.python.util.*;
import org.python.core.*;

import java.io.*;



public class SettingsLoader implements FServer.RequestHandler {
    private void p(String s) { System.out.println("SettingsLoader:: "+s); }


    static final private String SETTINGS_PTR_FILENAME = "settings-ptr";

    public void handleRequest(Object req, Applitude app) {
    }
    public void handleRequest(Object req, Object[] o, Applitude app) {
	if (req.equals("settings"))
	    if (o instanceof StormGraph[]) o[0] = stormGraph;
    }

    private StormGraph stormGraph;
    private IndexedPool pool;
    private FServer f;
    public SettingsLoader(FServer f) throws Exception {
	
	this.f = f;
	// get private pool  ...  get settings ptr ..

	if (f.environment.createRequest("settings", this))
	    throw new Error("Settings loader already inited!");


	// private storm pool is just "storm"
	IndexedPool[] p = new IndexedPool[1];
	f.environment.request("storm", (Object[]) p, null);
	if (p[0] == null) throw new Error("pool undefined!");
	pool = p[0];

	String dir = System.getProperty("fenfire.settings.dir");
	if (dir==null) 
	    throw new Error("property 'fenfire.settings.dir' not defined!");
	File d = new File(dir);
	d.mkdirs();
	if (! d.isDirectory()) 
	    throw new Error("directory creation failed!");

	if(!hasSettingsPtr(d)) makeSettingsPtr(d);

	// use pointer to create StormGraph
	BufferedReader reader = new BufferedReader(new FileReader(
	     new File(d, SETTINGS_PTR_FILENAME)));
	PointerId ptr = new PointerId(reader.readLine());


	QuadsGraph[] tmp = new QuadsGraph[1];
	f.environment.request("global graph", tmp, null);
	
	stormGraph = new StormGraph(tmp[0], pool, ptr);
    }


    private boolean hasSettingsPtr(File dir) throws Exception {
	return (new File(dir, SETTINGS_PTR_FILENAME)).exists();
    }
    
    PythonInterpreter interp;

    private void makeSettingsPtr(File dir) throws Exception {
	PointerSigner[] p = new PointerSigner[1];
	f.environment.request(StormLoader.SIGNER, (Object[]) p, null);
	if (p[0] == null) throw new Error("block signer undefined!");
	PointerSigner signer = p[0];
	
	PointerId ptr = signer.newPointer();
	String uri = ptr.getURI();
	p("ptr: "+uri);
	new FileOutputStream(
	    new File(dir, SETTINGS_PTR_FILENAME)).write(uri.getBytes());

	
	// we need to use jython writer :/
	if(interp == null) {
	    interp = new PythonInterpreter();
	    interp.exec("from org.fenfire.swamp import writer\n"+
			"wr = writer.writeToOutputStream");
	}
	Graph graph = new HashGraph();
	//RDFUtil.N(graph, Nodes.get("SETTINGS"));
	BlockOutputStream bos = pool.getBlockOutputStream("application/rdf+xml");
	if(! interp.get("wr").__call__(new PyObject[] {
	    new PyJavaInstance(graph),
	    new PyJavaInstance(bos)
	}).__nonzero__())
	    throw new Error("Writer call aborted.");
	bos.close();

	signer.initialize(ptr, Pointers.hasInstanceRecord, 
			  bos.getBlockId().getURI());
    }

}
