/*
FunctionCacheEntry.java
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

package org.fenfire.functional;
import java.util.*;
import org.fenfire.*;
import org.fenfire.util.*;
import org.nongnu.navidoc.util.Obs;

/** A useful building block class for function caches.
 * This class represents a single cache entry, which may
 * be valid or DIRTY at any given time.
 * <p>
 * Triggering chg() of the Obs interface on a FunctionCacheEntry
 * sets the value to DIRTY, and calls all associated Obses
 * (added through the addObs method).
 * The obses are removed after being triggered.
 */

public class FunctionCacheEntry implements Obs {
    /** The object to represent that this cache entry is dirty,
     * and needs to be recalculated.
     */
    static final public Object DIRTY = new Object();
    /** The object to represent that calculating this cache entry
     * yielded an error and thus we don't have a value
     * but recalculation is not possible either.
     */
    static final public Object ERROR = new Object();

    /** The input this cache entry is for.
     * Stored here so that it may be used as the hash key.
     */
    public final Object input;

    /** The value of the cache.
     * Either DIRTY or the real value.
     */
    public Object value = DIRTY;

    /** The set of Obs object that observe this cache entry.
     */
    protected Set obses;

    public FunctionCacheEntry(Object input) {
	this.input = input;
    }

    protected void releaseAll() {
	value = null;
	obses = null;
    }

    void printObses() {
	if (obses != null)
	    for (Iterator i = obses.iterator(); i.hasNext();) {
		System.out.println("Obses are: "+i.next());
	    }
    }

    public void addObs(Obs o) {
	if(obses == null) obses = new HashSet();
	obses.add(o);
    }
    public void triggerObs() {
	if(obses != null) {
	    for(Iterator i = obses.iterator(); i.hasNext();) {
		((Obs)i.next()).chg();
	    }
	    obses = null;
	}
    }
    public void chg() {
	//System.out.println("CHG! old value: "+value);
	value = DIRTY;
	triggerObs();
    }
}


