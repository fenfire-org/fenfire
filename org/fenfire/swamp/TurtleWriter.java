/*   
TurtleWriter.java
 *    
 *    Copyright (c) 2005, Benja Fallenstein and Tuukka Hastrup
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
 */
/*
 * Written by Benja Fallenstein and Tuukka Hastrup
 */
package org.fenfire.swamp;
import org.fenfire.swamp.impl.*;
import org.fenfire.util.NamespaceMap;
import java.io.*;
import java.util.*;

public final class TurtleWriter {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("TurtleWriter::"+s); }

    private static Iterator sort(Iterator i) {
	return sort(i, COMPARATOR);
    }

    private static Iterator sort(Iterator i, Comparator c) {
	List list = new ArrayList();
	while(i.hasNext()) list.add(i.next());
	if (c==null)
	    Collections.sort(list);
	else
	    Collections.sort(list, c);
	    
	return list.iterator();
    }

    public static void writeTurtle(ConstGraph g, NamespaceMap nmap,
				   Writer w, String baseURI) 
	throws IOException {

	for(Iterator i=sort(nmap.uriIterator(), null); i.hasNext();) {
	    String uri = (String)i.next();
	    w.write("@prefix "+nmap.getAbbrevString(uri)+" <"+uri+">.\n");
	}
	w.write("\n");

	for (Iterator i=sort(g.findN_XAA_Iter()); i.hasNext();) {
	    Object subj = i.next();

	    Iterator j = sort(g.findN_1XA_Iter(subj));
	    if(!j.hasNext()) throw new Error();

	    writeNode(w, baseURI, nmap, "", subj, "\n");

	    while(j.hasNext()) {
		Object pred = j.next();

		Iterator k = sort(g.findN_11X_Iter(subj,pred));
		if(!k.hasNext()) throw new Error();

		writeNode(w, baseURI, nmap, "  ", pred, "\n");

		while(k.hasNext()) {
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
			    writeNode(w, baseURI, nmap, "^^", l.getType(), "");
			}
		    } else {
			writeNode(w, baseURI, nmap, "    ", obj, "");
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

    /** Writes a given node in Turtle's resource format.
     *  @param w    the writer to use
     *  @param baseURI the URI that is written out as &lt;&gt;
     *  @param nmap defined namespaces
     *  @param pre  some text to be written before the node
     *  @param res  the node to be written
     *  @param post some text to be written after the node
     *  @throws IOException if writing fails
     */
    protected static void writeNode(Writer w, String baseURI, 
				    NamespaceMap nmap, 
				    String pre, Object res, String post) 
	throws IOException {
	w.write(pre);
	String uri = Nodes.toString(res);
	String abbrev = nmap.getAbbrevString(uri);
	if (uri.equals(baseURI))
	    w.write("<>");
	else if (abbrev == uri) { // if the uri wasn't abbreviated
	    w.write('<');
	    w.write(uri);
	    w.write('>');
	} else w.write(abbrev);
	w.write(post);
    }

    public static final Comparator COMPARATOR = new Comparator() {
	    public int compare(Object a, Object b) {
		if(Nodes.isNode(a)) {
		    if(Nodes.isNode(b)) {
			return Nodes.toString(a).compareTo(Nodes.toString(b));
		    } else if(b instanceof Literal) {
			return 1;
		    } else {
			throw new IllegalArgumentException("not a swamp node: "+b);
		    }
		} else if(a instanceof PlainLiteral) {
		    if(Nodes.isNode(b)) {
			return -1;
		    } else if(b instanceof PlainLiteral) {
			PlainLiteral p = (PlainLiteral)a, q = (PlainLiteral)b;
			int i = p.getString().compareTo(q.getString());
			if(i != 0) return i;

			if(p.getLang() == null) {
			    if(q.getLang() == null)
				return 0;
			    else
				return 1;
			} else {
			    if(q.getLang() == null)
				return -1;
			    else
				return p.getLang().compareTo(q.getLang());
			}
		    } else if(b instanceof TypedLiteral) {
			return 1;
		    } else {
			throw new IllegalArgumentException("not a swamp node: "+b);
		    }
		} else if(a instanceof TypedLiteral) {
		    if(Nodes.isNode(b)) {
			return -1;
		    } else if(b instanceof PlainLiteral) {
			return -1;
		    } else if(b instanceof TypedLiteral) {
			TypedLiteral s = (TypedLiteral)a, t = (TypedLiteral)b;

			int i = Nodes.toString(s.getType()).compareTo(Nodes.toString(t.getType()));
			if(i != 0) return i;
			return s.getString().compareTo(t.getString());
		    } else {
			throw new IllegalArgumentException("not a swamp node: "+b);
		    }
		} else {
		    throw new IllegalArgumentException("not a swamp node: "+a);
		}
	    }
	};
}
