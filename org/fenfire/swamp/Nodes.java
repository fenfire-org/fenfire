/*
Nodes.java
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

package org.fenfire.swamp;
import org.fenfire.util.URN5Namespace;

/** Mapping between URI strings and RDF nodes.
 * One of swamp's main goals is efficiency, both in time and space.
 * Space efficiency is not possible if we fix the node representation
 * to be strings, as there may be a lot of nodes with long URIs.
 * Therefore, nodes are currently opaque Objects (for best extensibility).
 * <p>
 * The implementation may change in the future without changing the Nodes API.
 * <p>
 * The following guarantees are given about RDF Nodes returned from
 * the get() or N() methods of this class:
 * <ul>
 * <li> They can be compared using the == operator.
 * <li> Their hash code will be the same as of the URI string and is therefore
 *      stable between invocations.
 * </ul>
 */
public class Nodes {
    public static Object get(String res) {
	return res.intern();
    }
    public static Object get(char[] res, int offs, int len) {
	return get(new String(res, offs, len));
    }

    private static URN5Namespace my_namespace = new URN5Namespace();
    public static URN5Namespace getNamespace() { return my_namespace; }

    /** Mint a 'new' node.
     *  This mints (creates) a new random urn-5 URI
     *  and returns the node corresponding to it.
     */
    public static Object N(URN5Namespace namespace) {
	return get(namespace.generateId());
    } 

    public static Object N() {
	return N(my_namespace);
    }

    public static String toString(Object res) {
	return (String)res;
    }

    /** Try to find out whether the given object is a node.
     *  Due to the current implementation of nodes, we cannot
     *  say with certainty whether a given object is a node;
     *  however, if this method returns <code>false</code>,
     *  we know for sure that the object is <em>not</em> a node.
     *  Otherwise, we can hope that it is one <code>:-)</code>.
     */
    public static boolean isNode(Object o) {
	if(!(o instanceof String)) return false;
	String s = (String)o;
	return s == s.intern();
    }

    /** Append the string version of the resource to the given buffer.
     * In order to avoid creating too many String objects
     * when serializing a space.
     */
    //	public static void appendToString(Object res, StringBuffer buf);

    /** Simulates editing a literal. As literals are immutable, a new
     *  literal is created with new content, but otherwise it should
     *  be equal to the old one. If the old one is null, a new 
     *  plain literal is created without language.
     *
     *  @param literal a literal that acts as model for the result
     *  @param content new content for the result
     *  @return a new literal with the given content
     */
    public static Literal editLiteral(Literal literal, String content) {
	if (literal == null || literal instanceof PlainLiteral) {
	    String lang = null;
	    if (literal != null)
		lang = ((PlainLiteral) literal).getLang();
	    return new PlainLiteral(content, lang);
	} else if (literal instanceof TypedLiteral) {
	    return new TypedLiteral(content, 
				    ((TypedLiteral) literal).getType());
	} else {
	    throw new Error("No subclasses of Literal except "+
			    "PlainLiteral and TypedLiteral are supported");
	}
    }
}
