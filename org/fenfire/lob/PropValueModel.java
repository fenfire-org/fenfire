/*   
PropValueModel.java
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

public class PropValueModel extends AbstractModel.AbstractObjectModel {

    protected Model graph, node, property, dir;

    public PropValueModel(Model graph, Model node, Object property, int dir) {
	this(graph, node, new ObjectModel(property),
	     new IntModel(dir));
    }

    public PropValueModel(Model graph, Model node, Model property, Model dir) {
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
	return new PropValueModel((Model)params[0], (Model)params[1],
				  (Model)params[2], (Model)params[3]);
    }

    private Object value;
    private boolean valueIsCurrent;

    public void chg() {
	valueIsCurrent = false;
	super.chg();
    }

    public Object get() {
	if(!valueIsCurrent) {
	    Graph g = (Graph)graph.get();
	    Object node = this.node.get();
	    Object prop = this.property.get();
	    int dir = this.dir.getInt();
	    
	    if(node == null || prop == null) 
		value = null;
	    else if(dir > 0)
		value = g.find1_11X(node, prop, this);
	    else
		value = g.find1_X11(prop, node, this);

	    valueIsCurrent = true;
	}

	return value;
    }

    public void set(Object value) {
	Graph g = (Graph)graph.get();
	Object node = this.node.get();
	Object prop = this.property.get();
	int dir = this.dir.getInt();

	if(dir > 0) {
	    g.rm_11A(node, prop);
	    if(value != null)
		g.add(node, prop, value);
	} else {
	    g.rm_A11(prop, node);
	    if(value != null)
		g.add(value, prop, node);
	}
    }
}
