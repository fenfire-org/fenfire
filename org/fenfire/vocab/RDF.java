/*
RDF.java
 *    
 *    Copyright (c) 2003, Matti J. Katila
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

package org.fenfire.vocab;
import org.fenfire.swamp.*;

/** RDF vocabulary of central RDF URIs defined outside fenfire.
 */
public class RDF {

    /** The RDF type attribute. A node's type can be declared 
     * to be Foo 
     * by a triple (node, RDF.type, Foo).
     */
    static public Object type;

    static public Object subject;
    static public Object predicate;
    static public Object object;

    static {
	String voc = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	type = Nodes.get(voc + "type");
	subject = Nodes.get(voc + "subject");
	predicate = Nodes.get(voc + "predicate");
	object = Nodes.get(voc + "object");
    }
}
