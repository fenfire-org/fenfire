/*
TREETIME.java
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

/** An time-like ordering between creation of objects.
 */
public class TREETIME {
    static public final String _nsId = 
	"http://fenfire.org/rdf-v/2003/07/treetime";

    /** A type that declares a relation to be 
     * a time-like relation.
     * This namespace defines one time-like relation,
     * "follows" for common use, more may be defined
     * to avoid conflicts, using the TimeRelation class.
     */
    static public final Object TimeRelation;

    /** (A, follows, B) means that A was created after B.
     */
    static public final Object follows;

    /** (X, currentOf, follows) means that X is the latest
     * entity in a "follows" chain. This applies to other
     * relations, too.
     */
    static public final Object currentOf;

    /** (X, firstOf, follows) means that X is the root
     * of a "follows" chain. This applies to other relations,
     * too.
     */
    static public final Object firstOf;

    static {
	TimeRelation = Nodes.get(_nsId + "#TimeRelation");
	follows = Nodes.get(_nsId + "#follows");
	currentOf = Nodes.get(_nsId + "#currentOf");
	firstOf = Nodes.get(_nsId + "#firstOf");
    }
}

