/*
Canvas2D.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka and Matti Katila
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
 * Written by Tuomas J. Lukka and Matti Katila
 */


package org.fenfire.structure;
import java.util.Iterator;
import org.fenfire.vocab.RDF;
import org.fenfire.vocab.CANVAS2D;
import org.fenfire.swamp.*;
import org.fenfire.util.RDFUtil;
import java.util.*;

/** Some utility methods for handling canvas2d's.
 */
public class Canvas2D {

    ConstGraph constGraph;
    Graph graph;

    private Canvas2D(ConstGraph g) {
	this.constGraph = g;
    }

    private Canvas2D(Graph g) {
	this.graph = g;
	this.constGraph = g;
    }

    /** Copy the iterator into a set, then return an iterator 
     * into the set.
     * XXX Generalize into utility routine
     */
    private Iterator copyIterator(Iterator it) {
	Set s = new HashSet();
	while(it.hasNext()) s.add(it.next());
	return s.iterator();
    }

    /** Create a new Canvas2D.
     */
    static public Canvas2D create(ConstGraph g) {
	return new Canvas2D(g);
    }
    static public Canvas2D create(Graph g) {
	return new Canvas2D(g);
    }


    /** Return true if the given node is a canvas.
     */
    public boolean isCanvas(Object node) {
	return RDFUtil.isNodeType(constGraph, node, 
				    CANVAS2D.Canvas);
    }

    /** Get an iterator over the nodes on the canvas.
     */
    public Iterator getNodesOn(Object canvas) {
	return constGraph.findN_11X_Iter(canvas, CANVAS2D.contains);
    }

    /** Get the canvas (if any) that the given node is on.
     * If the node is (erroneously) on several canvases, return them all.
     * @return The canvas, or null if none.
     */
    public Object getCanvas(Object node) {
	Iterator it = constGraph.findN_X11_Iter(CANVAS2D.contains, node);
	if(it.hasNext()) return it.next();
	return null;
    }

    /** Get the location of a node on the canvas.
     * If the node given is not valid, will throw something.
     * @param node The node whose coordinates we want. 
     * @param into A 2-element float array into which to place them.
     */
    public void getCoordinates(Object node, float[] into) {
	into[0] = RDFUtil.getFloat(constGraph, node, CANVAS2D.x);
	into[1]= RDFUtil.getFloat(constGraph, node, CANVAS2D.y);
    }

    /** Make the given node be a Canvas object.
     */
    public void makeIntoCanvas(Object node) {
	graph.add(node, RDF.type, CANVAS2D.Canvas);
    }

    /** Place the given node on this canvas.
     */
    public void placeOnCanvas(Object canvas, Object node, float x, float y) {
	graph.add(canvas, CANVAS2D.contains, node);
	setCoordinates(node, x, y);
    }

    /** Set the coordinates for a node.
     */
    public void setCoordinates(Object node, float x, float y) {
	// delete all old coords
	graph.rm_11A(node, CANVAS2D.x);
	graph.rm_11A(node, CANVAS2D.y);
	// XXX
	graph.set1_11X(node, CANVAS2D.x, new TypedLiteral(x) );
	graph.set1_11X(node, CANVAS2D.y, new TypedLiteral(y) );
    }

    /** Remove the node from the canvas and remove the coordinates.
     */
    public void removeNode(Object node) {
        Object canvas = graph.find1_X11(CANVAS2D.contains, node);
        graph.rm_111(canvas, CANVAS2D.contains, node);
    }

    /** Delete a canvas, and all nodes' placements within it.
     */
    public void deleteCanvas(Object canvas) {
	Iterator it = copyIterator(getNodesOn(canvas));
	while(it.hasNext())
	    removeNode(it.next());
	graph.rm_111(canvas, RDF.type, CANVAS2D.Canvas);
    }


}
