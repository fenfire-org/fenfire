/*
PagePool.java
 *    
 *    Copyright (c) 2005, Matti J. Katila
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


package org.fenfire.spanimages;
import org.fenfire.spanimages.fuzzybear.*;
import org.fenfire.swamp.*;

import org.nongnu.libvob.*;
import org.nongnu.libvob.lob.*;
import org.nongnu.libvob.impl.awt.*;


/** Pool for page documents.
 */
public class PagePool {
    private static void p(String s) { System.out.println("PagePool:: "+s); }

    protected PageRequests requests = null;

    
    protected void request(Object node) {
	requests.request((String) node);
    }

    protected Lob get(Object node, int page) {
	return requests.getOnePage(node, page);
    }
    protected Lob getFull(Object node, int page) {
	return requests.getWholeDocument(node);
    }



    static protected PagePool instance;
    static protected PagePool getInstance() {
	if (instance == null) {
	    throw new Error("Uninitialized!");
	}
	return instance;
    }
    static public void init(Graph g, WindowAnimation anim) {
	if (instance == null) {
	    instance = new PagePool();
	    if (GraphicsAPI.getInstance() instanceof AWTAPI)
		instance.requests = new PageRequests(g, anim);
	    else throw new Error("unreadable error -- see soursce");
	}
    }


    static public Lob oneFullPage(Object node, int page) {
	PagePool pool = getInstance();
	pool.request(node);
	Lob l;
	l = pool.get(node, 1);
	return l;
    }

    static public Lob fullDocument(Object node, float lod) {
	PagePool pool = getInstance();
	pool.request(node);
	Lob l;
	l = pool.getFull(node, 1);
	return l;
    }



}
