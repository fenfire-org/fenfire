/*
Demo.java
 *    
 *    Copyright (c) 2004, Benja Fallenstein
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
 * Written by Benja Fallenstein
 */
package org.fenfire.gnowsis;
import org.gnowsis.miniclient.Client;
import org.fenfire.swamp.*;
import java.io.*;

public class Gnowsis {
    private static Client client;

    public static Graph getCBD(Object node) throws Exception {
	if(client == null) client = new Client();

	String uri = Nodes.toString(node);
	Graph g = new org.fenfire.swamp.impl.HashGraph();
	Graphs.readXML(new StringReader(client.runBDesc(uri)), uri, g,
		       new java.util.HashMap());
	return g;
    }

    public static void open(Object node) {
	try {
	    if(client == null) client = new Client();
	    client.execute("", Nodes.toString(node));
	} catch(Exception e) {
	    throw new Error(e);
	}
    }

    public static void main(String[] args) throws Exception {
	Graph g = getCBD("urn:mozilla:email:msg:mailbox://693890@pop.gmx.de/Inbox#375899911");
	StringWriter w = new StringWriter();
	Graphs.writeTurtle(g, new java.util.HashMap(), w);
	System.out.println(w.toString());
    }
}
