/*
AppletStormLoader.java
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
import org.nongnu.storm.*;
import org.nongnu.storm.impl.*;
import org.nongnu.storm.references.*;

import java.io.*;
import java.util.*;

import java.awt.*;

/** A loader/initalizer for user to use Storm.
 *
 *  XXX Creates a block signer if not exist.
 */
public class AppletStormLoader implements FServer.RequestHandler {
    private void p(String s) { System.out.println("StormLoader:: "+s); }

    static final public String SIGNER = "storm-ptr-signer";


    private IndexedPool pool;
    private PointerSigner signer;
    private FServer f;
    public AppletStormLoader(FServer f) throws Exception {
	this.f = f;

	if (f.environment.createRequest("storm", this))
	    throw new Error("Storm loader already inited!");
	    
	pool = new TransientPool(new HashSet());

	//signer = new PointerSigner(pool,
	//   new FileInputStream(new File(d, PRIV_KEY_FILENAME)));

	if (f.environment.createRequest(SIGNER, this))
	    throw new Error("Storm signer already inited!");
    }

    public void handleRequest(Object req, Applitude app) {
    }
    public void handleRequest(Object req, Object[] o, Applitude app) {
	if (req.equals("storm"))
	    if (o instanceof StormPool[]) o[0] = pool;
	
	if (req.equals(SIGNER)) {
	    p("signer get: "+signer);
	    if (o instanceof PointerSigner[]) o[0] = signer;
	}
    }
    
    

}
