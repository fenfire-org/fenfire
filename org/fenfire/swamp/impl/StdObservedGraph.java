// AUTOGENERATED By makeswamp.py - DO NOT EDIT.

package org.fenfire.swamp.impl;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import java.util.Iterator;

public class StdObservedGraph extends StdObservedConstGraph implements Graph {
    Graph graph;

    public StdObservedGraph(Graph graph, Obs obs) {
	super(graph, obs);
	this.graph = graph;
	this.obs = obs;
    }

    public Graph getObservedGraph(Obs obs) {
	throw new Error("DoubleObs");
    }

    public void set1_11X(Object subject, Object predicate, Object object) {
	graph.set1_11X(subject, predicate, object);
    }

    public void rm_111(Object e0,Object e1,Object e2) {
	  graph.rm_111(e0,e1,e2);
}
public void rm_11A(Object e0,Object e1) {
	  graph.rm_11A(e0,e1);
}
public void rm_A11(Object e1,Object e2) {
	  graph.rm_A11(e1,e2);
}
public void rm_1AA(Object e0) {
	  graph.rm_1AA(e0);
}


    public void add(Object subject, Object predicate, Object object) {
	graph.add(subject, predicate, object);
    }

    public void addAll(Graph g) {
	graph.addAll(g);
    }
}

