/*
StdObserver.java
 *    
 *    Copyright (c) 2003-2005, Tuomas J. Lukka and Benja Fallenstein
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
 * Written by Tuomas J. Lukka and Benja Fallenstein
 */

package org.fenfire.swamp.impl;
import org.fenfire.util.*;
import org.fenfire.swamp.*;
import org.nongnu.libvob.util.AbstractHashtable;
import org.nongnu.navidoc.util.Obs;
import java.util.*;

/** Implement simple observation functions for triples.
 */
public class StdObserver extends AbstractHashtable {
    private void p(String s) { System.out.println("StdObserver:: "+s); }

    private static final class Wildcard {
	public String toString() { return "WILDCARD"; }
    }

    public static final Object WILDCARD = new Wildcard();

    protected Object[] subj, pred, obj;
    protected Obs[] obs;

    protected Set obsesToTrigger = new HashSet();
    protected int updatesInProgress;
    
    public void startUpdate() { updatesInProgress++; }
    public void endUpdate() {
	updatesInProgress--;
	if(updatesInProgress < 0) { updatesInProgress = 0; return; }

	if(updatesInProgress == 0) {
	    for(Iterator i=obsesToTrigger.iterator(); i.hasNext();)
		((Obs)i.next()).chg();

	    obsesToTrigger.clear();
	}
    }


    public StdObserver() {
	this(128);
    }
    public StdObserver(int size) {
	super(size);

	subj = new Object[size]; 
	pred = new Object[size]; 
	obj = new Object[size];
	obs = new Obs[size];
    }


    protected final int hashCode(Object s, Object p, Object o) {
	return s.hashCode() + (p.hashCode() * 34273) + (o.hashCode() * 23410);
    }
    protected final int hashCode(int entry) {
	return hashCode(subj[entry], pred[entry], obj[entry]);
    }

    protected void expandArrays(int size) {
	Object[] nsubj = new Object[size];
	Object[] npred = new Object[size];
	Object[] nobj = new Object[size];
	Obs[] nobs = new Obs[size];

	System.arraycopy(subj, 0, nsubj, 0, obs.length);
	System.arraycopy(pred, 0, npred, 0, obs.length);
	System.arraycopy(obj, 0, nobj, 0, obs.length);
	System.arraycopy(obs, 0, nobs, 0, obs.length);

	subj=nsubj; pred=npred; obj=nobj; obs=nobs;
    }


    public void addObs(Object s, Object p, Object o, Object context, Obs _) {
	// ignore the context for now...
	addObs(s, p, o, _);
    }

    public void addObs(Object s, Object p, Object o, Obs _) {
	if(_ == null) return;
	if(s == null || p == null || o == null)
	    throw new NullPointerException(""+s+" "+p+" "+o);

	for(int i=first(hashCode(s, p, o)); i>=0; i=next(i))
	    if(obs[i] == _) return; // obs already registered

	int entry = newEntry();
	subj[entry] = s;
	pred[entry] = p;
	obj[entry] = o;
	obs[entry] = _;

	put(entry);
    }

    private void triggerObs_single(int dir, Object o1, Object o2, Object o3,
			    Object s, Object p, Object o) {

	int i = first(hashCode(s, p, o));
	while(i >= 0) {
	    // after we've removed an entry, we cannot ask what its next entry
	    // is, so we need to cache the next entry's index before removing
	    int next = next(i);

	    if(s.equals(subj[i]) && p.equals(pred[i]) && o.equals(obj[i])) {
		if(obs[i] instanceof TripleSetObs) {
		    ((TripleSetObs)obs[i]).chgTriple(dir, o1, o2, o3);
		} else {
		    Obs theObs = obs[i];

		    // remove entry before triggering the obs,
		    // to give it a chance to re-add itself...

		    // note: if the chg() method adds a *new* obs,
		    // one not already in the list, it will go to
		    // the beginning of the linked list, and not be
		    // affected in this iteration. however, if it adds
		    // an obs that is in the linked list, but
		    // hasn't been triggered yet, it won't get added
		    // again, and be removed when triggered in a later 
		    // iteration of this loop. That's not exactly the
		    // right behavior... (XXX)

		    removeEntry(i);

		    if(updatesInProgress == 0)
			theObs.chg();
		    else
			obsesToTrigger.add(theObs);
		}
	    }

	    i = next;
	}
    }

    /** The parameters must not be wildcard!
     */
    public void triggerObs(int dir, Object s, Object p, Object o) {
	triggerObs_single(dir, s, p, o, s, p, o);
	triggerObs_single(dir, s, p, o, s, p, WILDCARD);
	triggerObs_single(dir, s, p, o, s, WILDCARD, o);
	triggerObs_single(dir, s, p, o, s, WILDCARD, WILDCARD);
	triggerObs_single(dir, s, p, o, WILDCARD, p, o);
	triggerObs_single(dir, s, p, o, WILDCARD, p, WILDCARD);
	triggerObs_single(dir, s, p, o, WILDCARD, WILDCARD, o);
	triggerObs_single(dir, s, p, o, WILDCARD, WILDCARD, WILDCARD);
    }

}
