/*   
FeedReader.java
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
package org.fenfire.fenfeed;
import org.fenfire.fenfeed.http.*;
import org.fenfire.swamp.*;
import nu.xom.*;
import nu.xom.xslt.XSLTransform;
import nu.xom.xslt.XSLException;
import java.io.*;
import java.util.*;

public class FeedReader {

    public static final String ACCEPT =
	"application/turtle, application/rdf+xml; q=0.9";

    public static void read(HTTPResource res, Graph graph, Map namespaces) 
	throws IOException {

	InputStream in = res.getInputStream();
	String baseURI = res.getURI();

	String contentType = res.getContentType();
	int i = contentType.indexOf(';');
	if(i >= 0) contentType = contentType.substring(0, i);
	contentType = contentType.trim().toLowerCase();

	if(contentType.equals("application/turtle")) {
	    Graphs.readTurtle(in, baseURI, graph, namespaces);
	} else if(contentType.equals("application/rdf+xml")) {
	    Graphs.readXML(in, baseURI, graph, namespaces);
	} else if(contentType.equals("application/rss+xml")) {
	    Builder b = new Builder();
	    Document xml;

	    try { 
		xml = b.build(in);	
	    } catch(ParsingException e) {
		throw new IOException(""+e);
	    }

	    nu.xom.Nodes nodes; 
	    try {
		InputStream tr_in = 
		    new FileInputStream("org/fenfire/fenfeed/feed-rss1.0.xsl");

		XSLTransform transform = new XSLTransform(tr_in);
		nodes = transform.transform(xml);
	    } catch(XSLException e) {
		throw new IOException(""+e);
	    }

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    Serializer serializer = new Serializer(bos, "UTF-8");
	    serializer.write(new Document((Element)nodes.get(0)));
	    
	    InputStream bin = new ByteArrayInputStream(bos.toByteArray());
	    Graphs.readXML(bin, baseURI, graph, namespaces);
	} else {
	    throw new IOException("Unhandled content type: "+contentType);
	}
    }

    public static void main(String[] args) throws Exception {
	String uri = args[0];
	HTTPContext context = new HTTPContext();
	context.setAccept(ACCEPT);

	HTTPResource res = new HTTPResource(uri, context);
	
	QuadsGraph qgraph = new org.fenfire.swamp.impl.SimpleHashQuadsGraph();
	Graph graph = new org.fenfire.swamp.impl.AllQuadsGraph(qgraph, "foo");
	Map namespaces = new HashMap();

	read(res, graph, namespaces);

	QuadsGraph q2 = new org.fenfire.swamp.impl.SimpleHashQuadsGraph();
	org.fenfire.swamp.smush.Smusher.smush(qgraph, q2);
	Graph g2 = new org.fenfire.swamp.impl.AllQuadsGraph(q2, "foo");

	StringWriter w = new StringWriter();
	Graphs.writeTurtle(g2, namespaces, w);
	System.out.println(w.toString());
    }
}
