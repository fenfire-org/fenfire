/*
RDFUtil.java
 *    
 *    Copyright (c) 2003, Matti J. Katila and Tuomas J. Lukka
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
 * Written by Matti J. Katila
 */

package org.fenfire.util;
import org.fenfire.*;
import org.fenfire.swamp.*;
import org.fenfire.vocab.*;

import org.nongnu.alph.*;

import java.util.Iterator;

/** A class for generic RDF utilities.
 * This class shall NOT know anything about specific
 * RDF schemas.
 */
public class RDFUtil {
    public static boolean dbg = false;
    static private void p(String s) { System.out.println("RDFUtil: "+s); }

    /** Create a new resource with given type.
     */
    static public Object N(Graph graph, Object type) {
	Object obj = Nodes.N();
	graph.add(obj, RDF.type, type);

	if(dbg) {
	    p("N: "+graph+" "+obj+" "+type);
	    if (!isNodeType(graph, obj, type)) throw new Error("Impossible!");
	}

	return obj;

    }

    /** Check if node is the type which is asked.
     */
    static public boolean isNodeType(ConstGraph graph, Object node, Object type) {
	if(dbg) p("Isnodetype: "+node+" "+type);
	Iterator it = graph.findN_11X_Iter(node, RDF.type);
	while(it.hasNext()) {
	    Object obj = it.next();
	    if(dbg) p(" entry: "+ obj);
	    if ( obj.equals(type) ) return true;
	}
	if(dbg) p("None matched");
	return false;
    }

    /** Get int attribute (from a literal).
     */
    static public int getInt(ConstGraph graph, Object node, Object pre) {
	Literal lit = (Literal)graph.find1_11X(node, pre);
	return Integer.parseInt(lit.getString() );
    }

    /** Set int attribute (literal).
     */
    static public void setInt(Graph graph, Object node, Object pre, int val) {
	graph.set1_11X(node, pre, new TypedLiteral(val));
    }

    /** Get float attribute (from a literal).
     */
    static public float getFloat(ConstGraph graph, Object node, Object pre) {
	Literal lit = (Literal)graph.find1_11X(node, pre);
	return Float.parseFloat(lit.getString() );
    }

    /** Set float attribute (literal).
     */
    static public void setFloat(Graph graph, Object node, Object pre, float val) {
	graph.set1_11X(node, pre, new TypedLiteral(val));
    }


    /** Checks whether there is a special triple with 
     *  given subject and predicate. Useful for example to 
     *  check if there is a Literal in Graph. 
     */
    static public boolean hasAny11X(Graph graph, Object sub, Object pre) {
	Iterator it = graph.findN_11X_Iter(sub, pre);
	if (it.hasNext()) return true;
	return false;
    }

    static public boolean hasOne11X(Graph graph, Object sub, Object pre) {
	Iterator it = graph.findN_11X_Iter(sub, pre);
	if (it.hasNext()) {
	    it.next();
	    return (!it.hasNext());
	}
	return false;
    }


}

