/*
CANVAS2D.java
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

package org.fenfire.vocab;
import org.fenfire.swamp.Nodes;


/** RDF Vocabulary of 2D spatial canvases.
 */
public class CANVAS2D {
    static public final String _nsId = 
	"http://fenfire.org/rdf-v/2003/05/canvas2d";

    /** The RDF class of spatial 2D canvases.
     * Canvases contain (with the "contains" property)
     * nodes, which shall have the "x" and "y" properties.
     */
    static public final Object Canvas;

    /** The property by which the canvas is connected to
     * the nodes, as (canvas, contains, node).
     */
    static public final Object contains;
    /** The x and y coordinates of a node on a canvas.
     * (node, x, literal), where the literal is parseable
     * as a floating-point number (similar to Java doubles). 
     * Note that these are the <em>default</em> coordinate
     * properties: later on, we might make it possible for a Canvas2D
     * to define its own coordinate attributes, which would take
     * use close to Ted's floating world ideas.
     */
    static public final Object x, y;

    static {
	Canvas = Nodes.get(_nsId + "#Canvas");
	contains = Nodes.get(_nsId + "#contains");
	x = Nodes.get(_nsId + "#x");
	y = Nodes.get(_nsId + "#y");
    }
}

