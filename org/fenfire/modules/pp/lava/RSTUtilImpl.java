/*
RSTUtilImpl.java
 *    
 *    Copyright (c) 2003, Matti J Katila
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
 * Written by Matti J Katila
 */

package org.fenfire.modules.pp.lava;
import org.fenfire.*;
import org.fenfire.vocab.*;
import org.fenfire.vocab.lava.RST;
import org.fenfire.util.*;
import org.fenfire.functional.*;
import org.fenfire.swamp.*;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.Iterator;

import org.nongnu.libvob.lava.placeable.Placeable;

import org.nongnu.alph.*;

/** The implementation of RSTActions.
 */
public class RSTUtilImpl extends UnicastRemoteObject implements RSTUtil {
    public static boolean dbg = false;
    protected static void p(String s) { System.out.println("RSTUtilImpl: "+s); }

    private Fen fen = null;
    public void setFen(Fen fen) { 
	this.fen = fen; 
    }

    public RSTUtilImpl(Fen fen)  throws RemoteException {
	super();
	setFen(fen);
    }



    public Object getCanvas(Object paragraph)
    { synchronized(fen) {
	if (! RDFUtil.isNodeType(fen.graph, paragraph, RST.Paragraph)) 
	    throw new Error("paragraph is not paragraph! "+paragraph);

	Object canvas = fen.graph.find1_X11(RST.beginParagraph, paragraph);
	if (RDFUtil.isNodeType(fen.graph, canvas, RST.Canvas)) 
	    return canvas;
	else throw new Error("Big mistake, canvas isn't canvas!"+ canvas);
    }}
    public Object getParagraph(Object sentence)
    { synchronized(fen) {
	if (! RDFUtil.isNodeType(fen.graph, sentence, RST.Sentence)) 
	    throw new Error("sentence is not sentence! "+sentence);

	Object curr = sentence;
	Object prev = fen.graph.find1_X11(RST.nextSentence, sentence);
	while (prev != null) {
	    curr = prev;
	    prev = fen.graph.find1_X11(RST.nextNode, curr);
	}
	p("curr: "+curr);
	Object parag = fen.graph.find1_X11(RST.firstSentence, curr);
	if (RDFUtil.isNodeType(fen.graph, parag, RST.Paragraph)) 
	    return parag;
	else throw new Error("paragraph is not paragraph! "+parag);
    }}
    public Object getSentence(Object node)
    { synchronized(fen) {
	if (node == null) throw new Error("node is null!"+ node);
	if (! RDFUtil.isNodeType(fen.graph, node, RST.Node)) 
	    throw new Error("node is not a node! "+node);

	Object sentence = fen.graph.find1_X11(RST.nextNode, node);
	Object prev = sentence;
	while (prev != null) {
	    sentence = prev;
	    prev = fen.graph.find1_X11(RST.nextNode, sentence);
	}
	if (RDFUtil.isNodeType(fen.graph, sentence, RST.Sentence)) 
	    return sentence;
	else throw new Error("sentence is not sentence! "+sentence);
    }}


    /** Generate the nodes' coordinates.
     */
    public void generateBasicSpatialCoords(Object rstCanvas, NodeFunction nodef) 
	throws RemoteException
    { synchronized(fen) {
	if (! RDFUtil.isNodeType(fen.graph, rstCanvas, RST.Canvas)) 
	    throw new Error("canvas is not canvas! "+rstCanvas);
	
	dbg = true;

	if (dbg) p("generate");

	Iterator iter = fen.constgraph.findN_11X_Iter(rstCanvas, RST.beginParagraph);
	while (iter.hasNext() ) {
	    if (dbg) p("paragraph");
	    Object parag = iter.next();
	    int paragX = RDFUtil.getInt(fen.graph, parag, CANVAS2D.x);
	    int paragY = RDFUtil.getInt(fen.graph, parag, CANVAS2D.y);
	    Object senten = fen.graph.find1_11X(parag, RST.firstSentence);
	    for (int iy=0; senten != null; iy++) {
		if (dbg) p("senten, "+iy);
		Object node = fen.graph.find1_11X(senten, RST.nextNode);
		int x = paragX;
		while (node != null) {
		    if (dbg) p("node");
		    int y = paragY + iy*30;

		    // set coords
		    fen.graph.set1_11X(node, CANVAS2D.x, new TypedLiteral(x) );
		    fen.graph.set1_11X(node, CANVAS2D.y, new TypedLiteral(y) );

		    Placeable p = (Placeable)nodef.f(fen.constgraph, node);
		    p("x before: "+x);
		    x += p.getWidth();
		    p("x: "+x+", w: "+p.getWidth()+" x+w: "+(x+p.getWidth()));

		    // XXX Needs to be adjusted for new APIs
		    // p("content: `"+((Enfilade1D)fen.txtfunc.f(fen.constgraph, node)).makeString()+"`");

		    node = fen.graph.find1_11X(node, RST.nextNode);
		}
		senten = fen.graph.find1_11X(senten, RST.nextSentence);
	    }
	}
	if (dbg) p("generate..DONE");
	dbg = false;
	return;
    }}



}
