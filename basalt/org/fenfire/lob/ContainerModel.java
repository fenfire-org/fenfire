/*   
ContainerModel.java
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

public class ContainerModel extends ListModel.AbstractListModel {
    private static final String BASE = 
	"http://www.w3.org/1999/02/22-rdf-syntax-ns#_";

    protected Model graph, container;

    public ContainerModel(Graph graph, Model container) {
	this(new ObjectModel(graph), container);
    }

    public ContainerModel(Model graph, Model container) {
	this.graph = graph;
	this.container = container;

	graph.addObs(this); container.addObs(this);
    }

    protected Replaceable[] getParams() {
	return new Replaceable[] { graph, container };
    }
    protected Object clone(Object[] params) {
	return new ContainerModel((Model)params[0], (Model)params[1]);
    }

    protected Object prop(int i) {
	return Nodes.get(BASE+i);
    }

    public int size() {
	Graph g = (Graph)graph.get();
	Object c = container.get();

	if(c == null) return 0;

	int n = 0;
	while(g.find1_11X(c, prop(n+1), this) != null) n++;
	return n;
    }

    public Object get(int i) {
	Graph g = (Graph)graph.get();
	Object c = container.get();

	if(c == null) throw new NullPointerException("container is null");

	return g.find1_11X(c, prop(i+1), this);
    }

    public Object set(int i, Object value) {
	if(value == null) throw new NullPointerException();

	Graph g = (Graph)graph.get();
	Object c = container.get();

	Object o = get(i);

	if(c == null) throw new NullPointerException("container is null");

	g.rm_11A(c, prop(i+1));
	g.add(c, prop(i+1), value);

	return o;
    }

    public boolean add(Object value) {
	if(value == null) throw new NullPointerException();

	Graph g = (Graph)graph.get();
	Object c = container.get();

	if(c == null) throw new NullPointerException("container is null");

	g.add(c, prop(size()+1), value);
	return true;
    }

    public Object remove(int i) {
	Graph g = (Graph)graph.get();
	Object c = container.get();

	if(c == null) throw new NullPointerException("container is null");

	Object o = get(i);

	for(int j=i; j<size(); j++) {
	    set(j, get(j+1));
	}
	g.rm_11A(c, prop(size()+1));

	return o;
    }
}
