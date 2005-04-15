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
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("FeedReader:: "+s); }
    
    public static final String ACCEPT =
	"application/turtle, application/rdf+xml; q=0.9";

    public static void read(HTTPResource res, Graph graph, Map namespaces) 
	throws IOException {

	String baseURI = res.getURI();

	String contentType = res.getContentType();

	// not all publishers send content types... the code below
	// requires contentType to be non-null
	if(contentType == null) contentType = "";

	int i = contentType.indexOf(';');
	if(i >= 0) contentType = contentType.substring(0, i);
	contentType = contentType.trim().toLowerCase();

	if(contentType.equals("application/turtle") ||
	   contentType.equals("application/x-turtle")) {
	    InputStream in = res.getInputStream();
	    Graphs.readTurtle(in, baseURI, graph, namespaces);
	    return;
	} 

	if(contentType.endsWith("/xml") || contentType.endsWith("+xml") ||
	   contentType.equals("text/plain")) {
	    if(dbg) p("---- read arbitrary xml: "+baseURI);

	    Builder b = new Builder();
	    Document xml, transform_xml;

	    try { 
		InputStream in = res.getInputStream();
		xml = b.build(in);	
	    } catch(ParsingException e) {
		if(contentType.equals("text/plain")) {
		    InputStream in = res.getInputStream();
		    Graphs.readTurtle(in, baseURI, graph, namespaces);
		    return;
		} else {
		    e.printStackTrace();
		    throw new IOException("Not well-formed XML. "+e);
		}
	    }

	    Element root = xml.getRootElement();
	    boolean isRDFXML = false;

	    if(contentType.equals("application/rdf+xml")) isRDFXML = true;
	    if(root.getNamespaceURI().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#") && root.getLocalName().equals("RDF")) isRDFXML = true;

	    if(root.getNamespaceURI().equals("")) 
		// probably non-RDF RSS, even if sent as application/rdf+xml
		// (Groklaw does that)
		isRDFXML = false;

	    if(isRDFXML) {
		// seems to be RDF/XML -- cut the chase and read it directly

		if(dbg) p("---- no, read rdf/xml: "+baseURI);
		try {
		    InputStream in = res.getInputStream();
		    Graphs.readXML(in, baseURI, graph, namespaces);
		    if(dbg) p("---- fi: "+baseURI);
		    return;
		} catch(IOException e) {
		    if(dbg) p("---- failed to read as rdf/xml: "+baseURI);
		}
	    }

	    nu.xom.Nodes nodes; 
	    try {
		nodes = getTransform().transform(xml);
	    } catch(XSLException e) {
		throw new IOException(""+e);
	    }

	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    Serializer serializer = new Serializer(bos, "UTF-8");
	    serializer.write(new Document((Element)nodes.get(0)));

	    InputStream bin = new ByteArrayInputStream(bos.toByteArray());
	    Graphs.readXML(bin, baseURI, graph, namespaces);
	    if(dbg) p("---- fi: "+baseURI);

	    return;
	}
	
	throw new IOException("Unhandled content type "+contentType+" when reading "+res.getURI());
    }

    private static XSLTransform rss2rdf_transform;
    private static XSLTransform getTransform() throws IOException {
	if(rss2rdf_transform == null) {
	    try {
		ClassLoader loader = FeedReader.class.getClassLoader();
		String path = "org/fenfire/fenfeed/feed-rss1.0.xsl";
		InputStream in = loader.getResourceAsStream(path);
		
		rss2rdf_transform = new XSLTransform(new Builder().build(in));
	    } catch(ParsingException e) {
		throw new IOException(""+e);
	    } catch(XSLException e) {
		e.printStackTrace();
		throw new IOException(""+e);
	    }
	}

	return rss2rdf_transform;
    }

    public static void main(String[] args) throws Exception {
	String uri = args[0];
	HTTPContext context = new HTTPContext();
	context.setAccept(ACCEPT);

	HTTPResource res = new HTTPResource(uri, context);
	
	Graph graph = new org.fenfire.swamp.impl.HashGraph();
	Map namespaces = new HashMap();

	read(res, graph, namespaces);

	StringWriter w = new StringWriter();
	Graphs.writeTurtle(graph, namespaces, w);
	System.out.println(w.toString());
    }
}
