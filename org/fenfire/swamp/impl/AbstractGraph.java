/*
AbstractGraph.java
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

package org.fenfire.swamp.impl;
import org.fenfire.swamp.*;
import org.nongnu.navidoc.util.Obs;
import java.util.Iterator;
import java.util.ArrayList;

abstract public class AbstractGraph extends AbstractConstGraph implements Graph {
    public Graph getObservedGraph(Obs o) {
	return new StdObservedGraph(this, o);
    }

    public void set1_11X(Object subject, Object predicate, Object object) {
	rm_11A(subject, predicate);
	add(subject, predicate, object);
    }

    public void addAll(Graph g) {
	for (Iterator i=g.findN_XAA_Iter(); i.hasNext();) {
	    Object subj = i.next();
	    for (Iterator j=g.findN_1XA_Iter(subj); j.hasNext();) {
		Object pred = j.next();
		for (Iterator k=g.findN_11X_Iter(subj,pred); k.hasNext();){
		    Object obj = k.next();
		    add(subj, pred, obj);
		}
	    }
	}
    }

    protected void checkNode(Object node) {
	if(!Nodes.isNode(node))
	    throw new IllegalArgumentException("Not a node: "+node);
    }
    
    protected void checkNodeOrLiteral(Object node) {
	if(!Nodes.isNode(node) && !(node instanceof Literal))
	    throw new IllegalArgumentException("Not a node or literal: "+node);
    }

    public void rm_11A(Object e0,Object e1) {
	Iterator i = findN_11X_Iter(e0, e1);
	ArrayList l = new ArrayList();
	while(i.hasNext())
	    l.add(i.next());
	for(i = l.iterator(); i.hasNext();)
	    rm_111(e0, e1, i.next());
    }

    public void rm_A11(Object e1,Object e2) {
	Iterator i = findN_X11_Iter(e1, e2);
	ArrayList l = new ArrayList();
	while(i.hasNext())
	    l.add(i.next());
	for(i = l.iterator(); i.hasNext();)
	    rm_111(i.next(), e1, e2);
    }
    public void rm_1AA(Object e0) {
	while(true) {
	    Iterator i = findN_1XA_Iter(e0);
	    if(!i.hasNext()) return;
	    rm_11A(e0, i.next());
	}
    }
}

