# 
# Copyright (c) 2004, Benja Fallenstein
# Portions Copyright (c) 2003, Tuomas J. Lukka
# Portions Copyright (c) 2004, Matti J. Katila
# 
# This file is part of Fenfire.
# 
# Fenfire is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
# 
# Fenfire is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
# Public License for more details.
# 
# You should have received a copy of the GNU General
# Public License along with Fenfire; if not, write to the Free
# Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
# MA  02111-1307  USA
# 

class Spec: pass

spec = Spec()
spec.Graph = "Graph"
spec.ConstGraph = "ConstGraph"
#spec.find_patterns = ("11X","1X1","X11", "AX1", "1XA", "XAA", "X1A")
#spec.rm_patterns = ("111", "11A", "A11", "1AA", "A1A", "AA1")
spec.one = "111"


# use all possible patterns
spec.find_patterns = []
spec.rm_patterns = []
for a in "1A":
    for b in "1A":
        spec.find_patterns.extend(['X'+a+b, a+'X'+b, a+b+'X'])
        for c in "1A": spec.rm_patterns.append(a+b+c)

print spec.find_patterns
print spec.rm_patterns
    

quad_spec = Spec()
quad_spec.Graph = "QuadsGraph"
quad_spec.ConstGraph = "QuadsConstGraph"
quad_spec.one = "1111"

quad_spec.find_patterns = [p+'A' for p in spec.find_patterns] + \
                          [p+'1' for p in spec.find_patterns] + \
                          ["111X", "AAAX"]

quad_spec.rm_patterns = [p+'A' for p in spec.rm_patterns] + \
                        [p+'1' for p in spec.rm_patterns]

spec.constGraphTemplate = """
package org.fenfire.swamp;
import java.util.Iterator;
import org.nongnu.navidoc.util.Obs;

/** A non-modifiable RDF graph. The iterators may implement the method
 *  remove, but it should not be used, as it may cause unspecified behavior.
 */
public interface ConstGraph {
    /** Get a ConstGraph whose queries will return the same
     * value as the queries for this graph, but will 
     * set up the Obs for those queries.
     * When the result of any of those queries changes,
     * Obs is called immediately.
     */
    ConstGraph getObservedConstGraph(org.nongnu.navidoc.util.Obs o);

    /** This observed graph will not be used any more, and
     * if desired, may be recycled.
     * This operation is allowed to be a no-op, and
     * if the graph this method is called on is not one that
     * has been returned by getObservedConstGraph, is
     * defined to be so..
     */
    void close();

    /** If this graph is observed (returned from getObservedConstGraph),
     * get the observer.
     */
    Obs getObserver();
    /** If this graph is observed (returned from getObservedConstGraph),
     * get the original.
     */
    ConstGraph getOriginalConstGraph();

    boolean contains(Object e0, Object e1, Object e2);
    boolean contains(Object e0, Object e1, Object e2, Obs o);

    %s
}
"""

spec.graphTemplate = """
package org.fenfire.swamp;
import java.util.Iterator;
import org.nongnu.navidoc.util.Obs;

/** A modifiable RDF graph. Existing iterators should not be used after
 *  the graph is modified, as it may cause unspecified behavior.
 */
public interface Graph extends ConstGraph {
    Graph getObservedGraph(org.nongnu.navidoc.util.Obs o);

    void startUpdate();
    void endUpdate();

    void set1_11X(Object subject, Object predicate, Object object);
    void add(Object subject, Object predicate, Object object);
    void addAll(Graph g);

    %s
}
"""

spec.abstractConstGraphTemplate = """
package org.fenfire.swamp.impl;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import java.util.Iterator;

abstract public class AbstractConstGraph implements ConstGraph {

    public ConstGraph getObservedConstGraph(Obs obs) {
	return new StdObservedConstGraph(this, obs);
    }
    public void close() { }
    public Obs getObserver() { return null; }
    public ConstGraph getOriginalConstGraph() { return null; }
    public boolean contains(Object e0, Object e1, Object e2) {
	return contains(e0, e1, e2, null);
    }

    %s
}
"""

spec.abstractGraphTemplate = """
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

    %s
}
"""

spec.simpleHashGraphTemplate = """
    package org.fenfire.swamp.impl;
    import org.nongnu.navidoc.util.Obs;
    import org.fenfire.swamp.*;
    import java.util.*;

    public class SimpleHashGraph extends AbstractGraph {
	private StdObserver observer = new StdObserver();

        public void startUpdate() { observer.startUpdate(); }
        public void endUpdate() { observer.endUpdate(); }

        protected class Key {
            Object k1, k2;
            public Key(Object k1, Object k2) {
                this.k1 = k1; this.k2 = k2;
            }
            public boolean equals(Object o) {
                if(!(o instanceof Key)) return false;
                Key t=(Key)o;
                return k1.equals(t.k1) && k2.equals(t.k2);
            }
            public int hashCode() {
                return k1.hashCode() + 127*k2.hashCode();
            }
        }

        %s

	public boolean contains(Object subj, Object pred, Object obj, Obs o) {
	    if(o != null) observer.addObs(subj, pred, obj, o);
            return getSet_11X(subj, pred).contains(obj);
	}
    }
"""

spec.hashGraphTemplate = """
    package org.fenfire.swamp.impl;
    import org.nongnu.navidoc.util.Obs;
    import org.fenfire.swamp.*;
    import java.util.*;

    public class HashGraph extends AbstractGraph {
	private StdObserver observer = new StdObserver();

        public void startUpdate() { observer.startUpdate(); }
        public void endUpdate() { observer.endUpdate(); }

        %s

	public boolean contains(Object subj, Object pred, Object obj, Obs o) {
	    if(o != null) observer.addObs(subj, pred, obj, o);
            return map_11X.contains(subj, pred, obj);
	}
    }
"""

spec.stdObservedConstGraphTemplate = """
package org.fenfire.swamp.impl;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import java.util.Iterator;

public class StdObservedConstGraph implements ConstGraph {
    ConstGraph constgraph;
    Obs obs;

    public StdObservedConstGraph(ConstGraph graph, Obs obs) {
	this.constgraph = graph;
	this.obs = obs;
    }

    public ConstGraph getObservedConstGraph(Obs obs) {
	throw new Error("DoubleObs");
    }

    public void close() { }

    public Obs getObserver() {
	return obs;
    }

    public ConstGraph getOriginalConstGraph() {
	return constgraph;
    }

    public boolean contains(Object e0, Object e1, Object e2) {
	return constgraph.contains(e0, e1, e2, obs);
    }
    public boolean contains(Object e0, Object e1, Object e2, Obs o) {
	throw new Error("DoubleObs");
    }

    %s
}
"""

spec.stdObservedGraphTemplate = """
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

    
    public void startUpdate() { graph.startUpdate(); }
    public void endUpdate() { graph.endUpdate(); }


    public void set1_11X(Object subject, Object predicate, Object object) {
	graph.set1_11X(subject, predicate, object);
    }

    %s

    public void add(Object subject, Object predicate, Object object) {
	graph.add(subject, predicate, object);
    }

    public void addAll(Graph g) {
	graph.addAll(g);
    }
}
"""

spec.delegateGraphTemplate = """
package org.fenfire.swamp.impl;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import java.util.Iterator;

public abstract class DelegateGraph extends AbstractGraph {
    Graph graph;

    public DelegateGraph(Graph graph) {
	this.graph = graph;
    }

    public void startUpdate() { graph.startUpdate(); }
    public void endUpdate() { graph.endUpdate(); }
    
    public void close() { graph.close(); }
    public Obs getObserver() { return graph.getObserver(); }
    public ConstGraph getOriginalConstGraph() {
        return graph.getOriginalConstGraph();    }
    public ConstGraph getObservedConstGraph(Obs o) {
        return graph.getObservedConstGraph(o);    }
    public boolean contains(Object e0, Object e1, Object e2) {
        return graph.contains(e0,e1,e2); }
    public boolean contains(Object e0, Object e1, Object e2, Obs o) {
        return graph.contains(e0,e1,e2, o); }
    public Graph getObservedGraph(org.nongnu.navidoc.util.Obs o) {
        return graph.getObservedGraph(o); }
    public void set1_11X(Object subject, Object predicate, Object object) {
        graph.set1_11X(subject, predicate, object);   }
    public void add(Object subject, Object predicate, Object object) {
        graph.add(subject, predicate, object);     }
    public void addAll(Graph g) {
        graph.addAll(g);     }
    public void rm_111(Object subject, Object predicate, Object object) {
        graph.rm_111(subject, predicate, object);     }


    %s
}
"""

quad_spec.constGraphTemplate = """
package org.fenfire.swamp;
import java.util.Iterator;
import org.nongnu.navidoc.util.Obs;

/** A non-modifiable RDF graph. The iterators may implement the method
 *  remove, but it should not be used, as it may cause unspecified behavior.
 */
public interface QuadsConstGraph {
    /** Get a QuadsConstGraph whose queries will return the same
     * value as the queries for this graph, but will 
     * set up the Obs for those queries.
     * When the result of any of those queries changes,
     * Obs is called immediately.
     */
    QuadsConstGraph getObservedConstGraph(org.nongnu.navidoc.util.Obs o);

    /** This observed graph will not be used any more, and
     * if desired, may be recycled.
     * This operation is allowed to be a no-op, and
     * if the graph this method is called on is not one that
     * has been returned by getObservedConstGraph, is
     * defined to be so..
     */
    void close();

    /** If this graph is observed (returned from getObservedConstGraph),
     * get the observer.
     */
    Obs getObserver();
    /** If this graph is observed (returned from getObservedConstGraph),
     * get the original.
     */
    QuadsConstGraph getOriginalConstGraph();

    boolean contains(Object e0, Object e1, Object e2, Object context);
    boolean contains(Object e0, Object e1, Object e2, Object context, Obs o);

    %s
}
"""

quad_spec.graphTemplate = """
package org.fenfire.swamp;
import java.util.Iterator;
import org.nongnu.navidoc.util.Obs;

/** A modifiable RDF graph. Existing iterators should not be used after
 *  the graph is modified, as it may cause unspecified behavior.
 */
public interface QuadsGraph extends QuadsConstGraph {
    QuadsGraph getObservedGraph(org.nongnu.navidoc.util.Obs o);

    void startUpdate();
    void endUpdate();

    void add(Object subject, Object predicate, Object object, Object context);

    %s
}
"""

quad_spec.abstractConstGraphTemplate = """
package org.fenfire.swamp.impl;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import java.util.Iterator;

abstract public class AbstractQuadsConstGraph implements QuadsConstGraph {

    public QuadsConstGraph getObservedConstGraph(Obs obs) {
	return new StdObservedQuadsConstGraph(this, obs);
    }
    public void close() { }
    public Obs getObserver() { return null; }
    public QuadsConstGraph getOriginalConstGraph() { return null; }
    public boolean contains(Object e0, Object e1, Object e2, Object e3) {
	return contains(e0, e1, e2, e3, null);
    }

    %s
}
"""

quad_spec.abstractGraphTemplate = """
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

    %s
}
"""

quad_spec.simpleHashGraphTemplate = """
    package org.fenfire.swamp.impl;
    import org.nongnu.navidoc.util.Obs;
    import org.fenfire.swamp.*;
    import java.util.*;

    public class SimpleHashQuadsGraph extends AbstractQuadsGraph {
	private StdObserver observer = new StdObserver();

        public void startUpdate() { observer.startUpdate(); }
        public void endUpdate() { observer.endUpdate(); }

        protected class Key {
            Object k1, k2, k3;
            public Key(Object k1, Object k2, Object k3) {
                this.k1 = k1; this.k2 = k2; this.k3 = k3;
            }
            public boolean equals(Object o) {
                if(!(o instanceof Key)) return false;
                Key t=(Key)o;
                return k1.equals(t.k1) && k2.equals(t.k2) && k3.equals(t.k3);
            }
            public int hashCode() {
                return k1.hashCode() + 127*k2.hashCode() + 2047*k3.hashCode();
            }
        }

        %s

	public boolean contains(Object subj, Object pred, Object obj, Object context, Obs o) {
	    if(o != null) observer.addObs(subj, pred, obj, o);
            return getSet_11X1(subj, pred, context).contains(obj);
	}
    }
"""

quad_spec.hashGraphTemplate = """
    package org.fenfire.swamp.impl;
    import org.nongnu.navidoc.util.Obs;
    import org.fenfire.swamp.*;
    import java.util.*;

    public class HashQuadsGraph extends AbstractQuadsGraph {
	private StdObserver observer = new StdObserver();

        public void startUpdate() { observer.startUpdate(); }
        public void endUpdate() { observer.endUpdate(); }

        %s

	public boolean contains(Object subj, Object pred, Object obj, Object context, Obs o) {
	    if(o != null) observer.addObs(subj, pred, obj, o);
            return map_11X1.contains(subj, pred, context, obj);
	}
    }
"""

quad_spec.stdObservedConstGraphTemplate = """
package org.fenfire.swamp.impl;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import java.util.Iterator;

public class StdObservedQuadsConstGraph implements QuadsConstGraph {
    QuadsConstGraph constgraph;
    Obs obs;

    public StdObservedQuadsConstGraph(QuadsConstGraph graph, Obs obs) {
	this.constgraph = graph;
	this.obs = obs;
    }

    public QuadsConstGraph getObservedConstGraph(Obs obs) {
	throw new Error("DoubleObs");
    }

    public void close() { }

    public Obs getObserver() {
	return obs;
    }

    public QuadsConstGraph getOriginalConstGraph() {
	return constgraph;
    }

    public boolean contains(Object e0, Object e1, Object e2, Object e3) {
	return constgraph.contains(e0, e1, e2, e3, obs);
    }
    public boolean contains(Object e0, Object e1, Object e2,
                            Object e3, Obs o) {
	throw new Error("DoubleObs");
    }

    %s
}
"""

quad_spec.stdObservedGraphTemplate = """
package org.fenfire.swamp.impl;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import java.util.Iterator;

public class StdObservedQuadsGraph extends StdObservedQuadsConstGraph
         implements QuadsGraph {
    QuadsGraph graph;

    public StdObservedQuadsGraph(QuadsGraph graph, Obs obs) {
	super(graph, obs);
	this.graph = graph;
	this.obs = obs;
    }

    public QuadsGraph getObservedGraph(Obs obs) {
	throw new Error("DoubleObs");
    }

    public void startUpdate() { graph.startUpdate(); }
    public void endUpdate() { graph.endUpdate(); }

    %s

    public void add(Object subject, Object predicate, Object object, Object quad) {
	graph.add(subject, predicate, object, quad);
    }
}
"""

quad_spec.delegateGraphTemplate = """
package org.fenfire.swamp.impl;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import java.util.Iterator;

public abstract class DelegateQuadsGraph extends AbstractQuadsGraph {
    QuadsGraph graph;

    public DelegateQuadsGraph(QuadsGraph graph) {
	this.graph = graph;
    }

    public void startUpdate() { graph.startUpdate(); }
    public void endUpdate() { graph.endUpdate(); }

    public void close() { graph.close(); }
    public Obs getObserver() { return graph.getObserver(); }
    public QuadsConstGraph getOriginalConstGraph() {
        return graph.getOriginalConstGraph();    }
    public QuadsConstGraph getObservedConstGraph(Obs o) {
        return graph.getObservedConstGraph(o);    }
    public boolean contains(Object e0, Object e1, Object e2, Object e3) {
        return graph.contains(e0,e1,e2, e3);
    }
    public boolean contains(Object e0, Object e1, Object e2, Object e3, Obs o) {
        return graph.contains(e0,e1,e2,e3, o);
    }
    public QuadsGraph getObservedGraph(org.nongnu.navidoc.util.Obs o) {
        return graph.getObservedGraph(o); }
    public void add(Object subject, Object predicate, Object object, Object context) {
        graph.add(subject, predicate, object, context);     }
    public void rm_1111(Object subject, Object predicate, Object object, Object context) {
        graph.rm_1111(subject, predicate, object, context);     }


    %s
}
"""


quadAdapterTemplate = """
package org.fenfire.swamp.impl;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import java.util.Iterator;

public class %s extends AbstractGraph {
    protected QuadsGraph graph;
    protected Object context;

    public %s(QuadsGraph graph, Object context) {
	this.graph = graph;
        this.context = context;
    }

    public void startUpdate() { graph.startUpdate(); }
    public void endUpdate() { graph.endUpdate(); }

    public Obs getObserver() { return graph.getObserver(); }
    public boolean contains(Object subj, Object pred, Object obj) {
        return graph.findN_111X_Iter(subj,pred,obj).hasNext(); }
    public boolean contains(Object subj, Object pred, Object obj, Obs o) {
        return graph.findN_111X_Iter(subj,pred,obj,o).hasNext(); }
    public void add(Object subject, Object predicate, Object object) {
        graph.add(subject, predicate, object, context);     }

    %s
}
"""


smushedGraphTemplate = """
package org.fenfire.swamp.smush;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import org.fenfire.swamp.impl.*;
import java.util.Iterator;

public abstract class SmushedQuadsGraph_Gen extends AbstractQuadsGraph {
    protected QuadsGraph unsmushed = new HashQuadsGraph();
    protected QuadsGraph smushed = new HashQuadsGraph();

    protected abstract Object get(Object node);

    public void startUpdate() { smushed.startUpdate(); }
    public void endUpdate() { smushed.endUpdate(); }

    public boolean contains(Object s, Object p, Object o, Object c, Obs obs) {
        return smushed.contains(get(s), get(p), get(o), c, obs);
    }

    %s
}
"""
