/*
Ff.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka
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
 * Written by Tuomas J. Lukka
 */

package org.fenfire.structure;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import org.nongnu.alph.Alph;
import org.nongnu.alph.Enfilade1D;
import org.fenfire.swamp.Graph;
import org.fenfire.swamp.ConstGraph;
import org.fenfire.swamp.Literal;
import org.fenfire.swamp.TripleSetObs;
import org.fenfire.vocab.FF;

/** Utility methods for handling RDF structures using 
 * the org.fenfire.vocab.FF vocabulary.
 * This class includes support for getting and setting
 * content and xanalogical indexing.
 */
public class Ff {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("SimpleContent:: "+s); }

    private ConstGraph constGraph;
    private Graph graph;
    private Alph alph;

    private org.nongnu.alph.xml.SpanReader spanReader;

    private javax.xml.parsers.SAXParser saxParser;

    private Enfilade1D.Maker enfMaker = 
	new org.nongnu.alph.impl.Enfilade1DImpl.Enfilade1DImplMaker();
    private Enfilade1D empty = enfMaker.makeEnfilade();

    private org.fenfire.index.impl.EnfiladeOverlapIndex trIndex;

    private ContentFunction contentFunction = new ContentFunction(this);

    private Ff(ConstGraph g, Alph alph) {
	if(g == null) throw new NullPointerException("Graph");
	if(alph == null) throw new NullPointerException("Alph");
	this.constGraph = g;
	this.alph = alph;

	this.spanReader = new org.nongnu.alph.xml.SpanReader(alph);
	try {
	    saxParser = javax.xml.parsers.SAXParserFactory.newInstance().newSAXParser();
	} catch(Exception e) {
	    throw new Error(e);
	}
    }

    private Ff(Graph g, Alph alph) {
	this((ConstGraph)g, alph);
	this.graph = g;
    }


    /** Create a new Canvas2D.
     *  @param alph The Alph to load scroll blocks from.
     *              If <code>null</code>, only urn-5 spans
     *              can be created.
     */
    static public Ff create(ConstGraph g, Alph alph) {
	return new Ff(g, alph);
    }
    /** Create a new Canvas2D.
     *  @param alph The Alph to load scroll blocks from.
     *              If <code>null</code>, only urn-5 spans
     *              can be created.
     */
    static public Ff create(Graph g, Alph alph) {
	return new Ff(g, alph);
    }


    public org.fenfire.index.Index getTransclusionIndex() {
	if(trIndex == null) {
	    trIndex = new org.fenfire.index.impl.EnfiladeOverlapIndex();
	    rereadIndex();
	}
	return trIndex;
    }


    /** Insert an enfilade at the given index of node's current enfilade.
     * @param node The node into whose enfilade to insert the given enfilade
     * @param index index in node's enfilade.
     * @param insertEnf The enfilade to insert.
     */
    public void insert(Object node, int index, Enfilade1D insertEnf) {
	Enfilade1D old = getContent(node);
	Enfilade1D enf = old.sub(0, index);
	enf = enf.plus(insertEnf);
	enf = enf.plus(old.sub(index));
	setContent(node, enf);
    }

    /** Delete a region of a node's content enfilade and return 
     * the deleted region. Throws an error if begin index is 
     * bigger than end index.
     * @param begin The beginning index of the region to be deleted.
     * @param end The index after the region to be deleted.
     * @return The deleted region of enfilade.
     */
    public Enfilade1D deleteRegion(Object node, int begin, int end) {
	if (begin > end) throw new 
	    Error("begin index '"+begin+"' bigger than end index '"+end+"'.");
	Enfilade1D old = (Enfilade1D)getContent(node);
	Enfilade1D enf = old.sub(0, begin);
	enf = enf.plus(old.sub(end));

	setContent(node, enf);
	
	// return the "deleted" enfilade.
	return old.sub(begin,end);
    }


    /** Delete content of a node.
     *<p>
     * Programming note:
     * All nodes, which are in temporary use, should be 
     * cleaned up of the content with deleteContent method.
     */
    public void deleteContent(Object node) {
	Enfilade1D enf = (Enfilade1D)contentFunction.f(constGraph, node);
	Literal lit = literalFromEnfilade(enf);
	graph.rm_111(node, FF.content, lit);
    }
    public void setContent(Object node, Enfilade1D enf) {
	Literal lit = literalFromEnfilade(enf);
	graph.set1_11X(node, FF.content, lit);
    }
    public Enfilade1D  getContent(Object node) {
	return (Enfilade1D)contentFunction.f(constGraph, node);
    }

    /** The content function, function from node to its content.
     */
    public static class ContentFunction implements org.fenfire.functional.PureNodeFunction {
	Ff ff;
	public ContentFunction(Ff ff) {
	    this.ff = ff;
	}

	public Object f(org.fenfire.swamp.ConstGraph graph, Object node) {
	    Object c = graph.find1_11X(node, FF.content);
	    if(dbg) p("Getting content: "+node+" "+c);
	    if(c == null) return ff.empty;

	    if(!(c instanceof Literal))  {
		if(dbg) p("Was not literal: "+c);
		throw new Error("Content not literal");
	    }
	    return ff.enfiladeFromLiteral((Literal)c);
	}
    }


    // Start the real stuff
    private void rereadIndex() {
	if(dbg) p("SNC TRINDEX: Rereading whole index!!!!");
	TripleSetObs obs = new TripleSetObs() {
	    public void chg() { rereadIndex(); }
	    public void chgTriple(int dir, Object o1, Object o2, Object o3) {
		if(dbg) p("SNC TRINDEX: Single triple chgd!!!! ");
		Object content = contentFunction.f(constGraph, o1);
		if(dbg) p("SNC TRINDEX: "+o1+" "+content);
		trIndex.set(dir, o1, content);
	    }
	};
	trIndex.clear();
	for(Iterator i = constGraph.findN_X1A_Iter(FF.content, obs); i.hasNext();) {
	    Object node = i.next();
	    Object content = contentFunction.f(constGraph, node);
	    if(dbg) p("SNC content: "+node+" "+content);
	    trIndex.set(1, node, content);
	}
    }


    /** Create the enfilade from a given literal that
     * needs to be in the Alph XML format.
     */
    public Enfilade1D enfiladeFromLiteral(Literal l) {
	if(dbg) p("EnfiladeFromLiteral: "+l);
	try {
	    String xml = l.getString();
	    if(dbg) p("Parse: "+saxParser+" "+xml+" "+spanReader);
            byte[] bytes = xml.getBytes("UTF-8");
	    saxParser.parse(new ByteArrayInputStream(bytes), 
			    spanReader);

	} catch (Exception e) {
	    e.printStackTrace();
	    p("Exception!: "+e);
	    throw new Error("Get enfilade: parse Exception!");
	} 

	Enfilade1D enf = enfMaker.makeEnfilade(spanReader.getSpans());
	spanReader.clear();
	return enf;
    }

    /** Create a literal in the Alph XML format
     * from an enfilade.
     */
    public Literal literalFromEnfilade(Enfilade1D enf) {
	String ser = org.nongnu.alph.xml.SpanSerializer.serialize(enf);
	if(dbg) p("LiteralFromEnfilade: "+ser);
	return new org.fenfire.swamp.PlainLiteral(ser);
    }


}
