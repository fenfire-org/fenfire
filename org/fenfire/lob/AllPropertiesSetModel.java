/*   
AllPropertiesSetModel.java
 *    
 *    Copyright (c) 2005, Benja Fallenstein.
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

public class AllPropertiesSetModel extends SetModel.Delegate {

    protected Model graph;

    protected Set cache = new HashSet();
    protected boolean current = false;

    public AllPropertiesSetModel(Graph graph) {
	this(new ObjectModel(graph));
    }

    public AllPropertiesSetModel(Model graph) {
	this.graph = graph;
	graph.addObs(this);
    }

    protected Replaceable[] getParams() {
	return new Replaceable[] { graph };
    }
    protected Object clone(Object[] params) {
	return new AllPropertiesSetModel((Model)params[0]);
    }

    protected Collection getDelegate() {
	if(!current) {
	    Graph g = (Graph)graph.get();

	    cache.clear();
	    for(Iterator i=g.findN_AXA_Iter(this); i.hasNext();)
		cache.add(i.next());

	    current = true;
	}

	return cache;
    }

    public void chg() {
	current = false;
	super.chg();
    }
}
