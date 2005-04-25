/*   
Graphs.java
 *    
 *    Copyright (c) 2003-2004 Benja Fallenstein
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
package org.fenfire.swamp;
import org.fenfire.swamp.impl.*;
import org.nongnu.storm.util.URN5Namespace;
import org.fenfire.util.NamespaceMap;
/*
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.model.Statement;
import com.hp.hpl.mesa.rdf.jena.model.Resource;
import com.hp.hpl.mesa.rdf.jena.mem.*;
*/
import org.openrdf.rio.*;
import org.openrdf.model.*;
import org.openrdf.model.impl.*;
import java.io.*;
import java.util.*;

public class Graphs {
    public static boolean dbg = false;
    public static void p(String s) { System.out.println("swamp.Graphs:: "+s); }



    protected static Object node(String bnodeBase, Value node) { 
	if(node instanceof URI)
	    return Nodes.get(((URI)node).getURI());
	else if(node instanceof BNode)
	    return Nodes.get("bnode:"+bnodeBase+((BNode)node).getID());
	else if(node instanceof org.openrdf.model.Literal) {
	    org.openrdf.model.Literal l = (org.openrdf.model.Literal)node;
	    if(l.getDatatype() != null) {
		URI type = (URI)l.getDatatype();
		return new TypedLiteral(l.getLabel(), 
					Nodes.get(type.getURI()));
	    } else {
		return new PlainLiteral(l.getLabel(), l.getLanguage());
	    }
	} else {
	    throw new ClassCastException(""+node);
	}
    }

    protected static Object rioNode(Object node) { 
	if(Nodes.isNode(node)) {
	    return new URIImpl(Nodes.toString(node));
	} else if(node instanceof PlainLiteral) {
	    PlainLiteral l = (PlainLiteral)node;
	    return new LiteralImpl(l.getString(), l.getLang());
	} else {
	    TypedLiteral l = (TypedLiteral)node;
	    URI type = new URIImpl(Nodes.toString(l.getType()));
	    return new LiteralImpl(l.getString(), type);
	}
    }

    private static void setHandlers(Parser parser, final Graph graph, 
				    final Map namespaces) {

	parser.setPreserveBNodeIds(true);

	parser.setNamespaceListener(new NamespaceListener() {
		public void handleNamespace(String prefix, String uri) {
		    namespaces.put(prefix, uri);
		}
	    });

	// bnode base string:
	final String bb = URN5Namespace.generateRandomChars()+":";

	parser.setStatementHandler(new StatementHandler() {
		public void handleStatement(org.openrdf.model.Resource subject,
					    URI predicate, Value object) {
		    graph.add(node(bb, subject), node(bb, predicate), 
			      node(bb, object));
		}
	    });
    }

    private static void parse(Parser parser, InputStream in, String baseURI)
	throws IOException {

	try {
	    parser.parse(in, baseURI);
	} catch(ParseException e) {
	    e.printStackTrace();
	    throw new IOException(""+e);
	} catch(StatementHandlerException e) {
	    throw new IOException(""+e);
	}
    }

    private static void parse(Parser parser, Reader r, String baseURI)
	throws IOException {

	try {
	    parser.parse(r, baseURI);
	} catch(ParseException e) {
	    throw new IOException(""+e);
	} catch(StatementHandlerException e) {
	    throw new IOException(""+e);
	}
    }



    public static void readTurtle(File file, Graph graph, Map namespaces) 
	throws IOException {

	String baseURI = file.toURI().toString();
	readTurtle(new FileInputStream(file), baseURI, graph, namespaces);
    }

    public static void readTurtle(InputStream in, String baseURI,
				  Graph graph, Map namespaces) 
	throws IOException {

	Parser parser = new org.openrdf.rio.turtle.TurtleParser();
	setHandlers(parser, graph, namespaces);

	graph.startUpdate();
	try {
	    parse(parser, in, baseURI);
	} finally {
	    graph.endUpdate();
	}
    }

    public static void readTurtle(Reader r, String baseURI,
				  Graph graph, Map namespaces) 
	throws IOException {

	Parser parser = new org.openrdf.rio.turtle.TurtleParser();
	setHandlers(parser, graph, namespaces);

	graph.startUpdate();
	try {
	    parse(parser, r, baseURI);
	} finally {
	    graph.endUpdate();
	}
    }

    public static void readXML(File file, Graph graph, Map namespaces) 
	throws IOException {

	String baseURI = file.toURI().toString();
	readXML(new FileInputStream(file), baseURI, graph, namespaces);
    }

    public static void readXML(InputStream in, String baseURI,
			       Graph graph, Map namespaces) 
	throws IOException {

	Parser parser = new org.openrdf.rio.rdfxml.RdfXmlParser();
	setHandlers(parser, graph, namespaces);

	graph.startUpdate();
	try {
	    parse(parser, in, baseURI);
	} finally {
	    graph.endUpdate();
	}
    }

    public static void readXML(Reader r, String baseURI,
			       Graph graph, Map namespaces) 
	throws IOException {

	Parser parser = new org.openrdf.rio.rdfxml.RdfXmlParser();
	setHandlers(parser, graph, namespaces);

	graph.startUpdate();
	try {
	    parse(parser, r, baseURI);
	} finally {
	    graph.endUpdate();
	}
    }

    public static void writeTurtle(ConstGraph g, Map namespaces,
				   File f) throws IOException {
	writeTurtle(g, namespaces,
		    new OutputStreamWriter(new FileOutputStream(f), "UTF-8"),
		    f.toURI().toString());
    }

    public static void writeTurtle(ConstGraph g, Map namespaces,
				   Writer w, String baseURI) 
	throws IOException {

	// XXX should get a NamespaceMap instead of a Map
	// order of namespaces is not the same as originally, but random
	NamespaceMap nmap = new NamespaceMap();
	if (namespaces != null)
	    nmap.putAll(namespaces);

	TurtleWriter.writeTurtle(g, nmap, w, baseURI);
    }

    public static void writeXML(ConstGraph g, File file, NamespaceMap nmap) throws IOException {
	RdfDocumentWriter writer = new org.openrdf.rio.rdfxml.RdfXmlWriter(new java.io.FileOutputStream(file));
	writer.startDocument();

	if(nmap != null) {
	    Iterator n = nmap.uriIterator();
	    while(n.hasNext()) {
		String uri = (String) n.next();
		String prefix = nmap.getAbbrevString(uri);
		writer.setNamespace(prefix, uri);
	    }
	}

	for (Iterator i=g.findN_XAA_Iter(); i.hasNext();) {
	    Object subj = i.next();

	    Iterator j = g.findN_1XA_Iter(subj);
	    if(!j.hasNext()) throw new Error();

	    while(j.hasNext()) {
		Object pred = j.next();

		Iterator k = g.findN_11X_Iter(subj,pred);
		if(!k.hasNext()) throw new Error();

		while(k.hasNext()) {
		    Object obj = k.next();

		    writer.writeStatement((Resource)rioNode(subj),
					  (URI)rioNode(pred),
					  (Value)rioNode(obj));
		}
	    }
	}

	writer.endDocument();
    }

    /*
    public static Model toModel(ConstGraph g) {
        try {
            Model m = new ModelMem();
            for (Iterator i=g.findN_XAA_Iter(); i.hasNext();) {
                Object o = i.next();
                Resource sub = m.createResource(Nodes.toString(o));

                for (Iterator j=g.findN_1XA_Iter(o); j.hasNext();) {
                    Object p = j.next();

		    // Convert the property's URI to a pair
		    // (XML namespace, local name) which is
		    // an XML qname; e.g., http://foo/bla is
		    // mapped to ("http://foo/", "bla") and
		    // serialized as e.g. x:bla, where the
		    // x namespace is bound to the URI "http://foo/".
		    String s0 = Nodes.toString(p);
		    boolean hadAlpha = false;
		    int i0 = s0.length()-1;
		    Property prop;
		    while(true) {
			char c0 = s0.charAt(i0);
			if(Character.isLetter(c0) || c0 == '_') hadAlpha = true;
			else if(Character.isDigit(c0)) hadAlpha = false;
			else if(hadAlpha) {
			    prop = m.createProperty(s0.substring(0, i0+1), 
					     s0.substring(i0+1));
			    break;
			} else
			    throw new Error("Cannot serialize URI: "+s0);
			i0--;
			if(i0 < 0)
			    throw new Error("Cannot serialize URI: "+s0);
		    }

                    for (Iterator k=g.findN_11X_Iter(o,p); k.hasNext();) {
                        Object q = k.next();
                        if(q instanceof Literal) {      
                            if(dbg)
				p("Literal! : "+ q);
                            String s = ((Literal)q).getString();
                            sub.addProperty(prop, s);
                        } else {
                            if(dbg)
				p("Resource! : "+ q);
                            Resource ob = m.createResource(Nodes.toString(q));
                            sub.addProperty(prop, ob);
                        }
                    }
                }
            }
            return m;
        } catch(Exception e) {
            throw new Error("Exception converting graph "+e);
        }
    }

    public static Graph toGraph(Model m) {
        try {
            if (dbg) {
		p("toGraph!"); 
                StmtIterator j=m.listStatements(); 
                while(j.hasNext()) {
		    p("And there are: "+ 
                      ((Statement)j.next()).getString() );
                }
            }

            Graph g = new HashGraph();
            StmtIterator i=m.listStatements(); 
            while (i.hasNext()) {
                Statement s = i.next();

		// work around a Kaffe/Jikes bug by using ""+x instead of
		// x.toString() (s.getPredicate().toString() doesn't work
		// with that combination currently because Resource,
		// a superinterface of the Property interface which is the
		// type of s.getPredicate(), defines a toString() method).
                Object sub = Nodes.get(""+s.getSubject());
                Object pred = Nodes.get(""+s.getPredicate());

		if(dbg)
		    p("PRED: '"+s.getPredicate()+"'");

                if(s.getObject() instanceof com.hp.hpl.mesa.rdf.jena.model.Literal) {
		    com.hp.hpl.mesa.rdf.jena.model.Literal l =
			(com.hp.hpl.mesa.rdf.jena.model.Literal)s.getObject();

		    String lang = l.getLanguage();
		    if("".equals(lang)) lang = null;

		    Literal ob = 
			new PlainLiteral(l.getString(), lang);
		    g.add(sub, pred, ob);
		    if(!g.contains(sub,pred,ob)) throw new Error();
                } else {
                    Object ob = Nodes.get(""+s.getObject());
                    g.add(sub, pred, ob);
		    if(!g.contains(sub,pred,ob)) throw new Error();
                }
            }
            return g;
        } catch(Exception e) {
	    e.printStackTrace();
            throw new Error("Exception converting graph");
        }
    }
    */
}
