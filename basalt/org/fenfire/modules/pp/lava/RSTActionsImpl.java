/*
RSTActionsImpl.java
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
import org.fenfire.swamp.*;
import java.io.IOException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.Iterator;

import org.nongnu.libvob.lava.placeable.Placeable;

/** The implementation of RSTActions.
 */
public class RSTActionsImpl extends UnicastRemoteObject implements RSTActions {
    public static final boolean dbg = false;
    protected static void p(String s) { System.out.println("RSTActionsImpl: "+s); }

    private Fen fen = null;
    public void setFen(Fen fen) { 
	this.fen = fen; 
    }

    public RSTUtil util;

    public RSTActionsImpl(Fen fen)  throws RemoteException {
	super();
	setFen(fen);
	util = new RSTUtilImpl(fen);
    }



    // --- implement RSTActions
    //


    public Object newRSTCanvas() throws RemoteException 
    { synchronized(fen) {
	if(dbg) p("make a new rst canvas");
	Object canvas = RDFUtil.N(fen.graph, RST.Canvas);
	fen.graph.add(canvas, RDF.type, CANVAS2D.Canvas);
	return canvas;
    }}

    public Object newParagraph(Object canvas, int width, int x, int y) 
	throws RemoteException
    { synchronized(fen) {
	if (! RDFUtil.isNodeType(fen.graph, canvas, RST.Canvas)) 
	    throw new Error("canvas is not canvas! "+canvas);
	if (canvas == null) throw new Error("canvas is null!");
	Object parag = RDFUtil.N(fen.graph, RST.Paragraph);

	fen.graph.set1_11X(parag, RST.width, new TypedLiteral(width) );

	// set coords
	fen.graph.set1_11X(parag, CANVAS2D.x, new TypedLiteral(x) );
	fen.graph.set1_11X(parag, CANVAS2D.y, new TypedLiteral(y) );
	fen.graph.add(canvas, RST.beginParagraph, parag);
	return parag;
    }}

    public void deleteParagraph(Object paragraph) throws RemoteException
    { synchronized(fen) {
	return;
    }}

    public Object newSentence(Object paragraph, int num) 
	throws RemoteException
    { synchronized(fen) {
	if (! RDFUtil.isNodeType(fen.graph, paragraph, RST.Paragraph)) 
	    throw new Error("paragraph is not paragraph! "+paragraph);
	Object senten = RDFUtil.N(fen.graph, RST.Sentence);

	if (num < 0) throw new Error("Num is too small"+num);
	else if (num == 0) {
	    // check if there are any other sentences
	    Object firstSent = fen.graph.find1_11X(paragraph, RST.firstSentence);

	    // put this at first - delete if already set
	    fen.graph.set1_11X(paragraph, RST.firstSentence, senten);

	    if (firstSent != null) {
		// link from new first to the old first
		fen.graph.set1_11X(senten, RST.nextSentence, firstSent);
	    }
	} else if (num > 0) {
	    // find first sentences
	    Object firstSent = fen.graph.find1_11X(paragraph, RST.firstSentence);
	    Object prev=firstSent, next=null;
	    for (int i=0; i<num; i++) { 
		next = fen.graph.find1_11X(prev, RST.nextSentence);
		if (next == null) {
		    // check if we are at last sentence or not
		    if (i != num-1) throw new Error("num is too big");
		} else if (i < num-1) // if not last round
		    prev = next;
	    }
	    // and take care of the next ones.
	    fen.graph.set1_11X(prev, RST.nextSentence, senten);
	    if (next != null)
		fen.graph.set1_11X(senten, RST.nextSentence, next);
	}	    
	// No! - insertNode(senten, Nodes.N(), 0);
	return senten;
    }}

    public void deleteSentence(Object sentence) throws RemoteException
    { synchronized(fen) {
	return;
    }}


    public void insertNode(Object sentence, Object node, int offset)
	throws RemoteException 
    { synchronized(fen) {
	if (! RDFUtil.isNodeType(fen.graph, sentence, RST.Sentence)) 
	    throw new Error("sentence is not sentence! "+sentence);

	if (offset < 0) throw new Error("Offset is too small"+offset);
	else if (offset == 0) {
	    // check if there are any other nodes
	    Object firstNode = fen.graph.find1_11X(sentence, RST.nextNode);

	    // put this at first - delete if already set
	    fen.graph.set1_11X(sentence, RST.nextNode, node);

	    if (firstNode != null) {
		// link from new first to the old first
		fen.graph.set1_11X(node, RST.nextNode, firstNode);
	    }
	} else if (offset > 0) {
	    // find first node
	    Object firstNode = fen.graph.find1_11X(sentence, RST.nextNode);
	    Object prev=firstNode, next=null;
	    for (int i=0; i<offset; i++) { 
		next = fen.graph.find1_11X(prev, RST.nextNode);
		if (next == null) {
		    // check if we are at last nodece or not
		    if (i != offset-1) throw new Error("offset is too big");
		} else if (i < offset-1) // if not last round
		    prev = next;
	    }
	    // and take care of the next ones.
	    fen.graph.set1_11X(prev, RST.nextNode, node);
	    if (next != null)
		fen.graph.set1_11X(node, RST.nextNode, next);
	}	    
	if (! RDFUtil.isNodeType(fen.graph, node, RST.Node)) 
	    fen.graph.add(node, RDF.type, RST.Node);

	Object canvas = util.getCanvas(
	    util.getParagraph(util.getSentence(node))
	    );
	fen.graph.add(canvas, CANVAS2D.contains, node);
    }}

    public void deleteNode(Object sentence, Object node)
	throws RemoteException
    { synchronized(fen) {
	return;
    }}


}
