/*
Literal.java
 *    
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
 */
package org.fenfire.swamp;
import org.fenfire.vocab.XSD;

/** A typed RDF literal.
 *  'type' is a node.
 */
public final class TypedLiteral extends Literal {

    private final Object type;

    public TypedLiteral(String string, Object type) {
	super(string);
	if(!Nodes.isNode(type)) 
	    throw new ClassCastException("Not a node: "+type);
	this.type = type;
    }

    public TypedLiteral(String s) {
	this(s, XSD.string);
    }
    public TypedLiteral(int i) {
	this(""+i, XSD._int);
    }
    public TypedLiteral(float f) {
	this(""+f, XSD._float);
    }

    public Object getType() {
	return type;
    }

    public boolean equals(Object o) {
	if(!(o instanceof TypedLiteral)) return false;
	TypedLiteral l = (TypedLiteral)o;
	return string.equals(l.string) && type.equals(l.type);
    }

    public int hashCode() {
	return string.hashCode() + 324891*type.hashCode();
    }

    public String toString() {
	return '"' + string + '"' + "^^<" + type + ">";
    }
}
