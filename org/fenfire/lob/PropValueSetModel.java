/*   
PropValueSetModel.java
 *    
 *    Copyright (c) 2004, Benja Fallenstein.
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
 *
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.lob;
import org.nongnu.navidoc.util.Obs;
import org.nongnu.libvob.layout.*;
import org.fenfire.swamp.*;
import java.util.*;

public class PropValueSetModel extends SetModel.AbstractSetModel {

    protected Model graph, node, property, dir;

    public PropValueSetModel(Graph graph, Model node, Object property, 
			     int dir) {
	this(new ObjectModel(graph), node, new ObjectModel(property),
	     new IntModel(dir));
    }

    public PropValueSetModel(Model graph, Model node, 
			     Model property, Model dir) {
	this.graph = graph;
	this.node = node;
	this.property = property;
	this.dir = dir;

	graph.addObs(this); node.addObs(this); 
	property.addObs(this); dir.addObs(this);
    }

    protected Replaceable[] getParams() {
	return new Replaceable[] { graph, node, property, dir };
    }
    protected Object clone(Object[] params) {
	return new PropValueSetModel((Model)params[0], (Model)params[1],
				     (Model)params[2], (Model)params[3]);
    }

    public Iterator iterator() {
	return new PropValueSetIterator();
    }

    public int size() {
	int i=0;
	for(Iterator iter=iterator(); iter.hasNext(); iter.next()) 
	    i++;
	return i;
    }

    public boolean add(Object value) {
	if(contains(value)) return false;
	
	Graph g = (Graph)graph.get();
	Object node = PropValueSetModel.this.node.get();
	Object prop = PropValueSetModel.this.property.get();
	int dir = PropValueSetModel.this.dir.getInt();
	
	if(dir > 0)
	    g.add(node, prop, value);
	else
	    g.add(value, prop, node);
	
	return true;
    }

    protected class PropValueSetIterator implements Iterator {
	Graph g;
	Object node, prop;
	int dir;
	Iterator iter;
	Object lastValue = null;

	protected PropValueSetIterator() {
	    Obs o = PropValueSetModel.this;

	    g = (Graph)graph.get();
	    node = PropValueSetModel.this.node.get();
	    prop = PropValueSetModel.this.property.get();
	    dir =  PropValueSetModel.this.dir.getInt();

	    if(node == null || prop == null)
		//throw new NullPointerException("Node "+node+", prop "+prop);
		iter = Collections.EMPTY_SET.iterator();
	    
	    else if(dir > 0)
		iter = g.findN_11X_Iter(node, prop, PropValueSetModel.this);
	    else
		iter = g.findN_X11_Iter(prop, node, PropValueSetModel.this);
	}

	public boolean hasNext() { return iter.hasNext(); }
	public Object next() { 
	    lastValue = iter.next(); 
	    return lastValue;
	}

	public void remove() {
	    if(lastValue == null) throw new IllegalStateException();

	    if(dir > 0)
		g.rm_111(node, prop, lastValue);
	    else
		g.rm_111(lastValue, prop, node);
	}
    }

}
