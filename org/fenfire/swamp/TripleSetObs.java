/*
TripleSetObs.java
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
import org.nongnu.navidoc.util.Obs;

/** Refine the Obs interface by defining 
 * set operations for triples.
 * <p>
 * This alters the semantics of Obs a little:
 * first, if chg() is called, the Obs was removed
 * and things have been changed so total regeneration
 * is necessary.
 * However, if addTriple or rmTriple is called, 
 * the Obs WILL NOT BE REMOVED, but will simply
 * keep being updated incrementally.
 * <p>
 * This is so that casting a TripleSetObs to Obs will always
 * yield correct (if inefficient) results.
 */
public interface TripleSetObs extends Obs {

    /** A triple was added or removed.
     * @param dir 1 if added, -1 if removed.
     */
    void chgTriple(int dir, Object o1, Object o2, Object o3);
}
