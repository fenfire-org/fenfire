// (c): Matti J. Katila

package org.fenfire.swamp;
import java.util.*;
import java.io.*;

/** An uniform RDF file format parser and writer for format alike N-Triples.
 * 
 * To speak shortly and say what is different in UniformXN3 and 
 * N-Triples; this file format uses UTF-8 instead of US-ASCII and
 * accept no whitespace or other useless characters. The design goal
 * is to have line based diffs and uniform hash value for any two same
 * graph instead of proof of concept.
 *
 * End of line is marked with \n and not with \r for example.
 * New lines in literals are escaped as \\n.
 *
 * S ::= lines* eof
 * lines ::= uri ' ' uri ' ' ( uri | literal ) '\n'
 * literal ::= plainLit| plainWithLangLit | typedLit 
 * plainLit ::= '"' anyWithNewLinesEscape* '"'
 * plainWithLangLit ::= '"' anyWithNewLinesEscape* '"@' anyWithNewLinesEscape* 
 * typedLit ::= '"' anyWithNewLinesEscape* '"^^<' uri '>'
 */
public class UniformXN3 {

    private static final String utf8 = "UTF-8";
    private final static byte[] space;
    private final static byte[] nl;
    static {
	try {
	    space = " ".getBytes(utf8);
	    nl = "\n".getBytes(utf8);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new Error(e.getMessage());
	}
    }


    static class Triple {
	String a,b;
	Object c;
	Triple(String a_, String b_, Object c_) {
	    a=a_; b=b_; c=c_;
	}
    }

    public static void write(Graph g, File out) throws IOException {
	write(g, new FileOutputStream(out));
    }


    /** Writes a graph to OutputStream.
     */
    public static void write(Graph g, OutputStream out) throws IOException {
	TreeSet all = new TreeSet(new Comparator() {
		public int compare(Object x_, Object y_) {
		    Triple x = (Triple)x_;
		    Triple y = (Triple)y_;
		    
		    int diff = x.a.compareTo(y.a);
		    if (diff != 0) return diff;
		    diff = x.b.compareTo(y.b);
		    if (diff != 0) return diff;
		    diff = x.c.toString().compareTo(y.c.toString());
		    if (diff != 0) return diff;
		    throw new Error("Graph has two same value!");
		}
		public boolean equals(Object x_, Object y_) {
		    return compare(x_, y_) == 0;
		}
	    });
	for (Iterator i=g.findN_XAA_Iter(); i.hasNext();) {
	    Object s=i.next();
	    for (Iterator j=g.findN_1XA_Iter(s); j.hasNext();) {
		Object p=j.next();
		for (Iterator k=g.findN_11X_Iter(s,p); k.hasNext();) {
		    Object o=k.next();
		    all.add(new Triple((String)s,(String)p,o));
		}
	    }	    
	}
	
	for (Iterator i=all.iterator(); i.hasNext();) {
	    Triple t = (Triple)i.next();
	    out.write(t.a.getBytes(utf8));
	    out.write(space);
	    out.write(t.b.getBytes(utf8));
	    out.write(space);
	    out.write(escapeNewLinesAway(t.c.toString()).getBytes(utf8));
	    out.write(nl);
	}
	out.close();
    }

    public static String escapeNewLinesAway(String s) {
	int occur = 0;
	while( (occur = s.indexOf('\n', occur)) != -1) 
	    s = s.substring(0, occur) + "\\n" + s.substring(occur+1);
	return s;
    }
    public static String escapeNewLinesBack(String s) {
	int occur = 0;
	while( (occur = s.indexOf("\\n", occur)) != -1) 
	    s = s.substring(0, occur) + '\n' + s.substring(occur+2);
	return s;
    }

    
    static public void parse(Graph g, InputStream in) throws IOException { 
	Reader r = new InputStreamReader(in, utf8);
	char[] buff = new char[1024];
	char[] buffTmp = new char[1024];
	int count = 0, start = 0, more = 0;
	
	while (true) {

	    // fill buffer
	    more = r.read(buff, start, buff.length - start);
	    if (more != -1) {
		count += more;
	    }
	    if (count == 0) break;

	    //System.out.println("count: "+count);
	    //for (int i=0; i<count; i++) 
	    //if (buff[i] == 0) System.out.println("ARGH");




	    // uniform form is: 
// http://uri.subject.com http://uri.predicate.net http://uri.object.org\n   
// or
// http://uri.subject.com http://uri.predicate.net "Literal"\n   

	    int STATE = 0;
	    int fstSp = -1, sndSp = -1, lastNl = -1;
	    // now find one line.
	    for (int i=0; i<count; i++) {
		//System.out.println("i: "+i+", State: "+STATE+" - '"+buff[i]+"' "+(int)buff[i]);
		switch (STATE) {
		case 0: {
		    // find first space char
		    if (buff[i] == ' ') {
			fstSp = i; 
			STATE++;
		    }
		    break;
		}
		case 1: {
		    // find second space char
		    if (buff[i] == ' ') {
			sndSp = i; 
			STATE++;
		    }
		    break;
		}
		case 2: {
		    // find last new line char
		    if (buff[i] == '\n') {
			lastNl = i; 
			STATE++;
		    }
		    break;
		}
		}
		if (STATE == 3) break;
	    }
	    if (STATE != 3) {
		// we ran out of chars before we got into best state!
		if (more == -1) throw new Error("file corrupted: no format found");
		//System.out.println("\n\n*************\n\n\nexpand to: "+buff.length*2);

		char[] tmp = new char[buff.length * 2];
		buffTmp = new char[buff.length * 2];
		System.arraycopy(buff, 0, tmp, 0, buff.length);
		buff = tmp;
		start = count;
		continue;
	    }

	    //System.out.println("fstSp: "+fstSp+", sndSp: "+sndSp+", lastNl: "+lastNl);

	    if (fstSp > 0 && fstSp < sndSp && sndSp < lastNl) {
		String subj = new String(buff, 0, fstSp);
		String pred = new String(buff, fstSp+1, sndSp-fstSp-1);
		String obj = new String(buff, sndSp+1, lastNl-sndSp-1);
		obj = escapeNewLinesBack(obj);

		
		Object 
		    a = Nodes.get(subj), 
		    b = Nodes.get(pred),
		    c = null;
		if (!obj.startsWith("\""))
		    c = Nodes.get(obj);
		else { // literals
		    // plain
		    if (obj.charAt(obj.length() - 1) == '"')
			c = new PlainLiteral(obj.substring(1, obj.length()-1));

		    // plain with language
		    if (obj.lastIndexOf("\"@") > 0) 
			c = new PlainLiteral(
			    obj.substring(1, obj.lastIndexOf("\"@")),
			    obj.substring(obj.lastIndexOf("\"@") + 2));
		    
		    // typed
		    if (obj.indexOf("\"^^<") > 0 && 
			obj.lastIndexOf('>') == obj.length() -1 )
			c = new TypedLiteral(
			    obj.substring(1, obj.indexOf("\"^^<")),
			    Nodes.get(
				obj.substring(obj.lastIndexOf("\"^^<")+4,
					      obj.length() - 1)));
		}

		//System.out.println(a+" "+b+" "+c);
		//System.out.println("subj: '"+a+"'");
		//System.out.println("pred: '"+b+"'");
		//System.out.println("obj: '"+c+"'");

		g.add(a,b,c);
		//System.out.println("contains? "+g.contains(a,b,c));

		System.arraycopy(buff, lastNl+1, buffTmp, 0, buff.length-lastNl-1);
		char tmp [] = buff;
		buff = buffTmp;
		buffTmp = tmp;
		start = count-lastNl - 1;
		count -= lastNl + 1;
	    }
	    else throw new Error("file corrupted!");
	}
	
    }

}
