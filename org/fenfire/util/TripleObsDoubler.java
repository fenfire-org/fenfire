/*
TripleObsDoubler.java
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

package org.fenfire.util;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.TripleSetObs;

/** An easy way to chain Observers into a set - TripleSetObs version.
 */
public class TripleObsDoubler extends ObsDoubler implements TripleSetObs {

    public TripleObsDoubler(Obs o1, Obs o2) {
	super(o1, o2);
    }

    /** If the given observer is a TripleSetObs, return it; if not,
     * call its chg() and return null.
     * Used in the Triple methods below for both our child obses.
     */
    private Obs triple(Obs o) {
	if(o == null) return null;
	if(!(o instanceof TripleSetObs)) {
	    o.chg();
	    return null;
	} 
	return o;
    }

    public void chgTriple(int dir, Object o1, Object o2, Object o3) {
	this.o1 = this.triple(this.o1);
	this.o2 = this.triple(this.o2);
	if(this.o1 != null) ((TripleSetObs)this.o1).chgTriple(dir, o1, o2, o3);
	if(this.o2 != null) ((TripleSetObs)this.o2).chgTriple(dir, o1, o2, o3);
    }

}

