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
import com.hp.hpl.mesa.rdf.jena.model.*;
import com.hp.hpl.mesa.rdf.jena.mem.*;
import java.io.*;
import java.util.*;

public class Graphs {
    public static boolean dbg = false;
    public static void p(String s) { System.out.println("swamp.Graphs:: "+s); }

    public static Graph readTurtle(File file, Map namespaces) 
	throws IOException {

	Graph g = new HashGraph();
	TurtleReader.read(new InputStreamReader(new FileInputStream(file),
						"UTF-8"), 
			  g, namespaces, file.getName());
	return g;
    }

    public static void writeTurtle(ConstGraph g, Map namespaces,
				   File f) throws IOException {
	writeTurtle(g, namespaces,
		    new OutputStreamWriter(new FileOutputStream(f), "UTF-8"));
    }

    public static void writeTurtle(ConstGraph g, Map namespaces,
				   Writer w) throws IOException {
	// XXX use namespaces!!!

	for (Iterator i=g.findN_XAA_Iter(); i.hasNext();) {
	    Object subj = i.next();
	    w.write("<");
	    w.write(Nodes.toString(subj));
	    w.write(">\n");

	    for (Iterator j=g.findN_1XA_Iter(subj); j.hasNext();) {
		Object pred = j.next();
		w.write("  <");
		w.write(Nodes.toString(pred));
		w.write(">\n");

		for (Iterator k=g.findN_11X_Iter(subj,pred); k.hasNext();) {
		    Object obj = k.next();

		    if(obj instanceof Literal) {
			w.write("    \"");
			String s = ((Literal)obj).getString();
			for(int p=0; p<s.length(); p++) {
			    char c = s.charAt(p);
			    if(c == '"')
				w.write("\\\"");
			    else if(c == '\\')
				w.write("\\\\");
			    else if(c == '\n')
				w.write("\\n");
			    else
				w.write(c);
			}
			w.write('"');
			
			if(obj instanceof PlainLiteral) {
			    PlainLiteral l = (PlainLiteral)obj;
			    if(l.getLang() != null) {
				w.write("@");
				w.write(l.getLang());
			    }
			} else if(obj instanceof TypedLiteral) {
			    TypedLiteral l = (TypedLiteral)obj;
			    w.write("^^<");
			    w.write(Nodes.toString(l.getType()));
			    w.write(">");
			}
		    } else {
			w.write("    <");
			w.write(Nodes.toString(obj));
			w.write(">");
		    }
			
		    if(k.hasNext())
			w.write(",\n");
		    else if(j.hasNext())
			w.write(";\n");
		    else
			w.write(".\n");
		}
	    }

	    w.write("\n");
	}

	w.close();
    }

    public static Graph readXML(File file) throws IOException {
	return readXML(new InputStreamReader(new FileInputStream(file),
					     "UTF-8"));
    }

    public static Graph readXML(Reader r) throws IOException {
	Model jenaModel = new ModelMem();
	try {
	    jenaModel.read(r, null);
	} catch(com.hp.hpl.mesa.rdf.jena.model.RDFException e) {
	    throw new IOException(""+e);
	}
	    
	return toGraph(jenaModel);
    }

    public static void writeXML(ConstGraph g, File file) throws IOException {
	Model jenaModel = toModel(g);

	try {
	    jenaModel.write(new java.io.FileWriter(file));
	} catch(com.hp.hpl.mesa.rdf.jena.model.RDFException e) {
	    throw new IOException(""+e);
	}
    }

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
}
