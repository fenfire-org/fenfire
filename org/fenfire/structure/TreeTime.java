/*
TreeTime.java
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

package org.fenfire.structure;
import java.util.Iterator;
import org.fenfire.vocab.TREETIME;
import org.fenfire.vocab.RDF;
import org.fenfire.swamp.*;

/** Some utility methods for handling TREETIME.
 * This class can be used statically or as an object - 
 * the object will contain the relation to be used.
 */
public class TreeTime {

    private Object relation;

    public TreeTime(Object relation) {
	this.relation = relation;
    }

    public void addLatest(Graph g, Object latest) {
	addLatest(g, relation, latest);
    }

    static public void addLatest(Graph g, Object relation, Object latest) {
	if(relation != TREETIME.follows &&
	    !g.contains(relation, 
		RDF.type, TREETIME.TimeRelation)) 
	    throw new IllegalArgumentException("Not a time relation");
	Iterator i1 = g.findN_11X_Iter(latest, relation);
	if(i1 != null && i1.hasNext())
	    throw new IllegalArgumentException("Already in relation 1");
	Iterator i2 = g.findN_X11_Iter(relation, latest);
	if(i2 != null && i2.hasNext())
	    throw new IllegalArgumentException("Already in relation 1");
	Iterator i = g.findN_X11_Iter(TREETIME.currentOf, relation);
	if(!i.hasNext()) {
	    // Bootstrap: this is the first one
	    g.add(latest, TREETIME.firstOf, relation);
	    g.add(latest, TREETIME.currentOf, relation);
	} else {
	    Object cur = i.next();
	    g.rm_A11(TREETIME.currentOf, relation);
	    g.add(latest, relation, cur);
	    g.add(latest, TREETIME.currentOf, relation);
	}

    }
}
