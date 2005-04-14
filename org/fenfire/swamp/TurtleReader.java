/*   
TurtleReader.java
 *    
 *    Copyright (c) 2001-2002, Ted Nelson and Tuomas Lukka
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
 * Based on GZZ1Reader by Tuomas Lukka and Benja Fallenstein
 */
package org.fenfire.swamp;
import org.fenfire.vocab.XSD;
import java.io.*;
import java.util.*;

/** A class for reading RDF/Turtle files.
 *  Final so that smart compilers can inline routines in tight loops.
 */
public final class TurtleReader {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("TurtleReader::"+s); }

    private static final String RDF = 
	"http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    private static final Object 
	RDF_TYPE = Nodes.get(RDF+"type"), 
	RDF_LIST = Nodes.get(RDF+"List"), 
	RDF_FIRST = Nodes.get(RDF+"first"), 
	RDF_NEXT = Nodes.get(RDF+"next"), 
	RDF_NIL = Nodes.get(RDF+"nil");

    public static void read(Reader r, Graph g, Map namespaces,
			    String filename) throws IOException {

	TurtleReader tr = new TurtleReader(r, g, namespaces, filename);
	tr.readStatements();
    }


    /** The size of the buffer blocks we use.
     */
    final int BLOCK = 4096;

    /** The graph that triples should be added to.
     */
    Graph graph;

    /** A mapping from QName prefixes to URI prefixes.
     */
    Map namespaces;

    /** The  we read from.
     */
    Reader reader;

    /** The buffer into which we slurp parts of the file.
     *  All the elements of the buffer are valid; when we have less than
     *  <code>BLOCK</code> bytes left, we create a smaller array. This is
     *  necessary because we want to use Java's subscript checking instead
     *  of adding our own.
     */
    char[] content;

    /** The current position inside the buffer block we currently read.
     *  (The counter is between the previous and the next char.)
     *  <p>
     *  <code>blockPosition + curPosition</code> is the current offset
     *  in the whole file.
     */
    int curPosition = 0;

    /** The position of the block we currently read, in the whole file.
     *  (The counter is the index of the first char of the block.)
     */
    int blockPosition = 0;

    /** A pushback buffer for a single character, allowing us to peek
     *  one character ahead. If smaller than zero, the buffer is empty;
     *  otherwise, the value is the character in the buffer.
     */
    int pushback = -1;

    /** The current line number -- for error messages
     */
    int line = 1;
    int inLine = 1;

    /** The name of the source we're reading from -- for error messages
     */
    String filename;

    /**
     *  @param namespaces A Map used to return the (namespace shortname,
     *         namespace URI) mappings read. The map is cleared before
     *         starting the reading process -- it doesn't set state
     *         for the reader, it's only used to return something.
     */
    public TurtleReader(Reader r, Graph g, Map namespaces, String filename) 
	throws IOException {

	this.reader = r;
	this.graph = g;
	this.filename = filename;
	this.namespaces = namespaces;

	namespaces.clear();

	content = new char[BLOCK];
	nextBlock();
    }


    private char read() throws IOException {
	if(pushback >= 0) {
	    char c = (char)pushback;
	    pushback = -1;
	    return c;
	}

	char c;
	try {
	    c = content[curPosition++];
	} catch(ArrayIndexOutOfBoundsException e) {
	    nextBlock();
	    c = content[curPosition++];
	}
	
	if(c == '\n') { 
	    line++; inLine = 0; 
	} else {
	    inLine++;
	}

	return c;
    }

    private char peek() throws IOException {
	char c = read();
	pushback = c;
	return c;
    }

    
    private void readStatements() throws IOException {
	while(true) {
	    char c;

	    try {
		skipWS();
		c = peek();
	    } catch(EOFException e) {
		return;
	    }

	    if(c == '@')
		readDirective();
	    else if(c == '#')
		readComment();
	    else
		readTriples();
	}
    }

    private void readTriples() throws IOException {
	char c;

	Object subject = readSubject();
	readWS();

	readPredicateObjectList(subject, '.');
    }

    private void readPredicateObjectList(Object subject, char endChar) 
	throws IOException {


	PREDICATES: while(true) {
	    Object predicate = readPredicate();
	    readWS();

	    OBJECTS: while(true) {
		Object object = readObject();
		skipWS();

		graph.add(subject, predicate, object);
		
		char c = read();

		if(c == ',') {
		    skipWS();
		    continue OBJECTS;
		} else if(c == ';') {
		    skipWS();
		    continue PREDICATES;
		} else if(c == endChar) {
		    return;
		} else
		    throw new ParseError(c);
	    }
	}
    }

    private Object readSubject() throws IOException {
	if(isURI()) return readURI();
	if(isQName()) return readQName();
	if(isBlank()) return readBlank();
	throw new ParseError(peek());
    }

    private Object readPredicate() throws IOException {
	if(isURI()) return readURI();
	if(isQName()) return readQName();
	throw new ParseError(peek());
    }

    private Object readObject() throws IOException {
	if(isURI()) return readURI();
	if(isQName()) return readQName();
	if(isBlank()) return readBlank();
	if(isLiteral()) return readLiteral();
	if(isInteger()) return readInteger();
	throw new ParseError(peek());
    }

    private boolean isURI() throws IOException {
	return peek() == '<';
    }
    private boolean isQName() throws IOException {
	char c = peek();
	if('A' <= c && c <= 'Z') return true;
	if('a' <= c && c <= 'z') return true;
	if(c == ':') return true;
	return false;
    }
    private boolean isBlank() throws IOException {
	return peek() == '_' || peek() == '[' || peek() == '(';
    }
    private boolean isLiteral() throws IOException {
	return peek() == '"';
    }
    private boolean isInteger() throws IOException {
	char c = peek();
	return ('0' <= c && c <= '9');
    }


    private Object readURI() throws IOException {
	expect('<');
	int len = readUntil('>'); read();
	return Nodes.get(buf, 0, len);
    }

    private Object readQName() throws IOException {
	// XXX this creates some unnecessary strings...

	String prefix = readStringUntil(NON_NAME); 

	char c = peek();
	if(c == ':')
	    read();
	else if(prefix.equals("a"))
	    return RDF_TYPE;
	else
	    throw new ParseError(c);

	String base = (String)namespaces.get(prefix);
	String rest = readStringUntil(NON_NAME);

	return Nodes.get(base + rest);
    }

    private Object readBlank() throws IOException {
	char c = read();
	if(c == '_') {
	    c = read();
	    if(c != ':') throw new ParseError(c);

	    String id = readStringUntil(NON_NAME);

	    // XXX!!! need to support anon nodes properly in Swamp
	    return Nodes.get("anon:"+id); 
	} else if(c == '[') {
	    Object blank = newAnonNode();

	    skipWS();
	    if(peek() == ']')
		read();
	    else 
		readPredicateObjectList(blank, ']');

	    return blank;
	} else if(c == '(') {
	    return readList();
	} else {
	    throw new ParseError(c);
	}
    }

    private Object readList() throws IOException {
	skipWS();
	if(peek() == ')') { read(); return RDF_NIL; }

	Object list = newAnonNode();
	graph.add(list, RDF_TYPE, RDF_LIST);

	Object elem = readObject();
	graph.add(list, RDF_FIRST, elem);

	Object next = readList();
	graph.add(list, RDF_NEXT, next);
	
	return list;
    }

    private Object readLiteral() throws IOException {
	expect('"');
	String content = readStringUntil('"'); read();
	
	char c = peek();
	if(c == '@') {
	    read();
	    String lang = readStringUntil(NON_NAME);

	    return new PlainLiteral(content, lang);
	} else if(c == '^') {
	    read();
	    expect('^');
	    Object datatype = readPredicate();

	    return new TypedLiteral(content, datatype);
	} else {
	    return new PlainLiteral(content);
	}
    }

    private Object readInteger() throws IOException {
	String s = readStringUntil(NON_NAME);
	return new TypedLiteral(s, XSD._int);
    }

    private void readDirective() throws IOException {
	expect('@');
	String directive = readStringUntil(NON_NAME);
	if(!directive.equals("prefix"))
	    throw new ParseError("Unknown directive '"+directive+"'");

	skipWS();
	String prefix = readStringUntil(':'); read();
	skipWS();
	expect('<');
	String uri = readStringUntil('>'); read();

	skipWS();
	expect('.');
	
	namespaces.put(prefix, uri);
    }

    private void readComment() throws IOException {
	expect('#');
	try {
	    while(read() != '\n');
	} catch(EOFException e) {
	    // EOF inside a comment isn't a problem
	}
    }



    static int anonId = 0;
    private Object newAnonNode() throws IOException {
	// XXX!!! need to support anon nodes properly in Swamp

	anonId++;
	return Nodes.get("anon:"+anonId);
    }


    private void expect(char expected) throws IOException {
	char c = read();
	if(c != expected) throw new ParseError(c);
    }


    private final int NON_NAME = -1;
    private char[] buf = new char[128];

    private void expandBuf() throws IOException {
	char[] nbuf = new char[2*buf.length];
	System.arraycopy(buf, 0, nbuf, 0, buf.length);
	buf = nbuf;
    }

    private int readUntil(int stop) throws IOException {
	int len = 0;
	while(true) {
	    char c = read();

	    if(stop != NON_NAME) {
		if(c == stop) { pushback = c; return len; }
	    } else {
		if(!isNameChar(c)) { pushback = c; return len; }
	    }
	    
	    if(len+1 > buf.length) expandBuf();

	    if(c == '\\') {
		// this is an escape sequence -- read the next char
		c = read();
		if(c == '\\')
		    buf[len] = '\\';
		else if(c == 'n')
		    buf[len] = '\n';
		else if(c == '"')
		    buf[len] = '"';
		else
		    throw new UnsupportedOperationException("Unsupported "+
							    "escape: '\\"+
							    c+"'");
	    } else {
		buf[len] = c;
	    }

	    len++;
	}
    }

    private String readStringUntil(int stop) throws IOException {
	int len = readUntil(stop);
	return new String(buf, 0, len);
    }



    private void readWS() throws IOException {
	char c = read();
	if(!isWhitespace(c)) throw new ParseError(c);
	skipWS();
    }

    private void skipWS() throws IOException {
	try {
	    while(isWhitespace(peek()))
		read();
	} catch(EOFException _) {
	}
    }

    private boolean isWhitespace(char c) {
	return (c == 0x09 || c == 0x0A || c == 0x0D || c == 0x20);
    }

    private boolean isNameChar(char c) throws IOException {
	if('A' <= c && c <= 'Z') return true;
	if('a' <= c && c <= 'z') return true;
	if('0' <= c && c <= '9') return true;
	if(c == '_' || c == '-') return true;
	return false;
    }



    public class ParseError extends IOException {
	private ParseError(char c) {
	    this("Unexpected character '"+c+"'");
	}
	private ParseError(String s) {
	    super(s+" at "+inLine+" in line "+line+" in "+filename);
	}
    }


    void nextBlock() throws IOException {
	if(reader == null)
	    throw new EOFException();
	
	blockPosition += Math.min(curPosition, content.length);
	int n;
	
	try {
	    n = reader.read(content);
	} catch(IOException e) {
	    throw e;
	}
	
	if(n < 0)
	    throw new EOFException();
	else if(n < BLOCK) {
	    char[] ncontent = new char[n];
	    System.arraycopy(content, 0, ncontent, 0, n);
	    content = ncontent;
	}

	curPosition = 0;
    }
}
