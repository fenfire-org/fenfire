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

/** An RDF literal.
 *  The implementations are PlainLiteral and TypedLiteral;
 *  no other implementations are allowed.
 */
public abstract class Literal {

    protected final String string;

    /** Package-private because only PlainLiteral and TypedLiteral
     *  should inherit from this.
     */
    Literal(String string) {
	if(string == null) throw new NullPointerException();
	this.string = string;

	if(!(this instanceof PlainLiteral) &&
	   !(this instanceof TypedLiteral))
	    throw new Error("No subclasses of Literal except "+
			    "PlainLiteral and TypedLiteral are allowed");
    }

    public final String getString() {
	return string;
    }
}



