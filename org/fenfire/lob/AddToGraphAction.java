/*   
AddToGraphAction.java
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

public class AddToGraphAction extends AbstractAction {

    protected Model graph;
    protected CollectionModel triples;

    public AddToGraphAction(Model graph, CollectionModel triples) {
	this.graph = graph;
	this.triples = triples;
    }

    protected Replaceable[] getParams() {
	return new Replaceable[] { graph, triples };
    }
    protected Object clone(Object[] params) {
	return new AddToGraphAction((Model)params[0], 
				    (CollectionModel)params[1]);
    }

    public void run() {
	Graph g = (Graph)graph.get();

	for(Iterator i=triples.iterator(); i.hasNext();) {
	    Triple t = (Triple)i.next();
	    g.add(t.getSubject(), t.getPredicate(), t.getObject());
	}
    }
}
