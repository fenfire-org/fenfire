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
spec.find_patterns = ("11X","1X1","X11", "1XA", "XAA", "X1A")
spec.rm_patterns = ("111", "11A", "A11", "1AA")
spec.one = "111"

quad_spec = Spec()
quad_spec.Graph = "QuadsGraph"
quad_spec.ConstGraph = "QuadsConstGraph"
quad_spec.one = "1111"

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

spec.simpleHashGraphTemplate = """
    package org.fenfire.swamp.impl;
    import org.nongnu.navidoc.util.Obs;
    import org.fenfire.swamp.*;
    import java.util.*;

    public class SimpleHashGraph extends AbstractGraph {
	private StdObserver observer = new StdObserver();

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

        %s

	public boolean contains(Object subj, Object pred, Object obj, Obs o) {
	    if(o != null) observer.addObs(subj, pred, obj, o);
            return map_11X.contains(subj, pred, obj);
	}
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

    void set1_11XA(Object subject, Object predicate, Object object);
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

quad_spec.simpleHashGraphTemplate = """
    package org.fenfire.swamp.impl;
    import org.nongnu.navidoc.util.Obs;
    import org.fenfire.swamp.*;
    import java.util.*;

    public class SimpleHashQuadsGraph extends AbstractQuadsGraph {
	private StdObserver observer = new StdObserver();

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

        %s

	public boolean contains(Object subj, Object pred, Object obj, Object context, Obs o) {
	    if(o != null) observer.addObs(subj, pred, obj, o);
            return map_11X1.contains(subj, pred, context, obj);
	}
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

    public Obs getObserver() { return graph.getObserver(); }
    public boolean contains(Object subj, Object pred, Object obj) {
        return graph.contains(subj,pred,obj, context); }
    public boolean contains(Object subj, Object pred, Object obj, Obs o) {
        return graph.contains(subj,pred,obj, context, o); }
    public void add(Object subject, Object predicate, Object object) {
        graph.add(subject, predicate, object, context);     }

    %s
}
"""
