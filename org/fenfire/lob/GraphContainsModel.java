/*   
GraphContainsModel.java
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

public class GraphContainsModel extends AbstractModel.AbstractBoolModel {

    protected Model graph, subj, pred, obj;

    public GraphContainsModel(Model graph, Model subj, Model pred, Model obj) {
	this.graph = graph;
	this.subj = subj;
	this.pred = pred;
	this.obj = obj;

	graph.addObs(this); subj.addObs(this); 
	pred.addObs(this); obj.addObs(this);
    }

    protected Replaceable[] getParams() {
	return new Replaceable[] { graph, subj, pred, obj };
    }
    protected Object clone(Object[] params) {
	return new GraphContainsModel((Model)params[0], (Model)params[1],
				      (Model)params[2], (Model)params[3]);
    }

    public boolean getBool() {
	Graph g = (Graph)graph.get();
	Object s = subj.get(), p = pred.get(), o = obj.get();
	    
	if(s==null || p==null || o==null) return false;
	
	return g.contains(s, p, o, this);
    }

    public void setBool(boolean add) {
	Graph g = (Graph)graph.get();
	Object s = subj.get(), p = pred.get(), o = obj.get();

	if(s==null || p==null || o==null)
	    throw new NullPointerException("spo = ("+s+", "+p+", "+o+")");

	if(add)
	    g.add(s, p, o);
	else
	    g.rm_111(s, p, o);
    }
}
