/*
FenLoader.java
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
import org.fenfire.view.main.impl.*;
import org.fenfire.util.*;
import org.fenfire.*;
import org.fenfire.swamp.*;
import org.fenfire.swamp.impl.*;
import org.fenfire.swamp.cloudberry.*;
import org.nongnu.storm.*;
import org.nongnu.storm.references.*;

import java.util.*;

/** Some things are deprecated - this is one of those.
 */
public class FenLoader implements FServer.RequestHandler {

    

    public void handleRequest(Object req, Applitude app) { }
    public void handleRequest(Object req, Object[] o, Applitude app) {
	if (req.equals("fen"))
	    if (o instanceof Fen[]) o[0] = fen;
    }

    private Fen fen;
    private FServer f;
    public FenLoader(FServer f) throws Exception {
	
	this.f = f;

	if (f.environment.createRequest("fen", this))
	    throw new Error("Fen loader already inited!");

	Graph[] g = new Graph[1];
	f.environment.request("global graph", g, null);
	
	fen = new Fen();
	fen.constgraph = fen.graph = g[0];
    }

}
