/*
AbstractQuadsGraph.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka
 *                  2004, Matti J. Katila
 *
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

package org.fenfire.swamp.impl;
import org.fenfire.swamp.*;
import org.nongnu.navidoc.util.Obs;
import java.util.Iterator;
import java.util.ArrayList;

import java.util.*;

abstract public class AbstractQuadsGraph extends AbstractQuadsConstGraph implements QuadsGraph {
    static public boolean dbg = false;
    private void p(String s) { System.out.println("AbstractQuadsGraph:: "+s); }

    public QuadsGraph getObservedGraph(Obs o) {
	return new StdObservedQuadsGraph(this, o);
    }

    protected void checkNode(Object node) {
	if(!Nodes.isNode(node))
	    throw new IllegalArgumentException("Not a node: "+node);
    }
    
    protected void checkNodeOrLiteral(Object node) {
	if(!Nodes.isNode(node) && !(node instanceof Literal))
	    throw new IllegalArgumentException("Not a node or literal: "+node);
    }

    public void rm_11AA(Object e0,Object e1) {
	Iterator i = findN_11XA_Iter(e0, e1);
	Map m = new HashMap();
	while(i.hasNext()) {
	    Object e2 = i.next();
	    List l = new ArrayList();
	    m.put(e2,l);

	    Iterator j = findN_111X_Iter(e0, e1, e2);
	    while(j.hasNext()) {
		l.add(j.next());
	    }
	}

	for (i = m.keySet().iterator(); i.hasNext();) {
	    Object e2 = i.next();
	    List l = (List) m.get(e2);
	    for(int j=0; j<l.size(); j++) {
		rm_1111(e0, e1, e2, l.get(j));
	    }
	}
    }


    public void rm_111A(Object e0, Object e1, Object e2) {
	List l = new ArrayList();

	Iterator i = findN_111X_Iter(e0, e1, e2);
	while(i.hasNext()) {
	    l.add(i.next());
	}

	for(int j=0; j<l.size(); j++) {
	    rm_1111(e0, e1, e2, l.get(j));
	}
    }


    public void rm_AAA1(Object e0) {
	Iterator i = findN_XAA1_Iter(e0);
	Map m = new HashMap();
	while(i.hasNext()) {
	    Object e1 = i.next();
	    if (dbg) p("i1: "+e1);

	    Map m2 = new HashMap();
	    m.put(e1,m2);

	    Iterator j = findN_1XA1_Iter(e1, e0);
	    while(j.hasNext()) {
		Object e2 = j.next();
		if (dbg) p("i2: "+e2);

		List l = new ArrayList();
		m2.put(e2,l);

		Iterator k = findN_11X1_Iter(e1, e2, e0);
		while(k.hasNext()) {
		    Object e3 = k.next();
		    if (dbg) p("i3: "+e3);
		    l.add(e3);
		}
	    }
	}

	for (i = m.keySet().iterator(); i.hasNext();) {
	    Object e1 = i.next();
	    if (dbg) p("o1: "+e1);
	    
	    Map m2 = (Map)m.get(e1);
	    for (Iterator j = m2.keySet().iterator(); j.hasNext();) {
		Object e2 = j.next();
		if (dbg) p("o2: "+e2);
		List l = (List) m2.get(e2);
		for(int n=0; n<l.size(); n++) {
		    Object e3 = l.get(n);
		    rm_1111(e1, e2, e3, e0);
		    if (dbg) {
			p("o3: "+e3);
			p("Node: "+e1+" "+e2+" "+e3+" "+e0);
		    }
		}
	    }
	}
    }


    public void set1_11XA(Object subject, Object predicate, Object object) {
	p("Node: "+subject+" "+predicate+" "+object);
	ArrayList l = new ArrayList();
	Iterator i = findN_11AX_Iter(subject, predicate);
	while(i.hasNext()) 
	    l.add(i.next());

	rm_11AA(subject, predicate);
	
	for(i = l.iterator(); i.hasNext();) {
	    Object o = i.next();
	    p("Ob: "+o);
	    add(subject, predicate, object, o);
	}
    }

}

