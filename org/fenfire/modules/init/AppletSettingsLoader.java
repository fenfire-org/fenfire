/*
AppletSettingsLoader.java
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



public class AppletSettingsLoader implements FServer.RequestHandler {
    private void p(String s) { System.out.println("AppletSettingsLoader:: "+s); }


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
    public AppletSettingsLoader(FServer f) throws Exception {
	
	this.f = f;
	// get private pool  ...  get settings ptr ..

	if (f.environment.createRequest("settings", this))
	    throw new Error("Settings loader already inited!");


	// private storm pool is just "storm"
	IndexedPool[] p = new IndexedPool[1];
	f.environment.request("storm", (Object[]) p, null);
	if (p[0] == null) throw new Error("pool undefined!");
	pool = p[0];


	//PointerId ptr = new PointerId(reader.readLine());
	PointerId ptr = null;

	Graph[] tmp = new Graph[1];
	f.environment.request("global graph", (Object[])tmp, null);
	
	//stormGraph = new StormGraph(tmp[0], pool, ptr);
	stormGraph = null;
    }



}
