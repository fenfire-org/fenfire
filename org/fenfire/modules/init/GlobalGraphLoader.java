/*
GlobalGraphLoader.java
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
import org.fenfire.swamp.impl.*;
import org.fenfire.swamp.cloudberry.*;
import org.fenfire.swamp.*;


public class GlobalGraphLoader implements FServer.RequestHandler {

    private QuadsGraph qg;
    private Graph g;
    private FServer f;
    public GlobalGraphLoader(FServer f) {
	this.f = f;

	this.qg = new HashQuadsGraph();
	this.g = new GraphToQuadsGraph(qg){
		Object q = Nodes.N();
		protected Object getQuad() { return q; }
	    };
	if (f.environment.createRequest("global graph", this))
	    throw new Error("Storm loader already inited!");
    }



    public void handleRequest(Object req, Applitude app) {
    }
    public void handleRequest(Object req, Object[] o, Applitude app) {
	if (req.equals("global graph")) {
	    if (o instanceof QuadsGraph[]) o[0] = this.qg;
	    if (o instanceof Graph[]) o[0] = this.g;
	}
    }
}
