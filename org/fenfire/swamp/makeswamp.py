# 
# Copyright (c) 2003-2004, Tuomas J. Lukka and Benja Fallenstein
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

# Generate all swamp code

from __future__ import nested_scopes

find_patterns = ("11X","1X1","X11", "1XA", "XAA", "X1A")

constgraph = []

for n in (1,"N"):
    for pattern in find_patterns:
	constgraph.append({
	    "Type": "find", 
	    "NVal": n, 
	    "Pattern": pattern})

graph = []

for pattern in (
	"111",  # Only these three removes at first...
	"11A", 
	"A11", 
	"1AA",
	):
    graph.append({
	"Type": "rm", 
	"Pattern": pattern})

#############################################
# Expand return value type for find functions

def map1(g):
    if(g["NVal"] == "N"):
	g["Return"] = "Iterator"
    return g
    
for g in constgraph: map1(g)

#############################################
# Make function names and prototypes

def copyhash(h):
    def foo(**h):
	return h
    return foo(**h)

def funcName(g):
    pp = []
    for i in range(0,3):
	if g["Pattern"][i] == "1":
	    pp.append("e"+str(i))
    g["Params"] = pp

    if g["Type"] == "find":
	if g["NVal"] == 1:
	    g["FRet"] = "Object"
	    g["FName"] = "find1_" + g["Pattern"]
	else:
	    g["FRet"] = "Iterator"
	    g["FName"] = "findN_"+g["Pattern"]+"_Iter"

    elif g["Type"] == "rm":
	g["FRet"] = "void"
	g["FName"] = "rm_"+g["Pattern"]

	return
	
    else: assert 1==0

for f in constgraph: funcName(f)
for f in graph: funcName(f)

def proto(g, obs=0):
    pars = []
    pars.extend(["Object "+i for i in g["Params"]])
    if obs:
	pars.append("Obs obs")
    return (g["FRet"] + " "+g["FName"]+"("+ 
	    ",".join(pars) +
	    ") ")
    
def decl(g, obs=0):
    return proto(g, obs) + ";\n"

def callObs(g, whatToCall, obs = None, ret = 1):
    ps = []
    ps.extend(g["Params"])
    if obs:
	ps.append(obs)
    if ret:
	retu = "return "
    else:
	retu = ""
    print g, ret, retu
    return ("public "+proto(g) + 
	    "{\n\t "+retu+" "+whatToCall+"."+
	    g["FName"]+"("+",".join(ps) + ");\n}\n")

def noDoubleObs(g):
    return "public "+proto(g, 1) + """{
	throw new Error("DoubleObs");
    }"""+"\n"

nonobs_findprotos = "".join([ decl(g) for g in constgraph])
obs_findprotos =  "".join([ decl(g, 1) for g in constgraph])

nonobs_callobs = "".join([ callObs(g, "this", "null") for g in constgraph])

nonobs_modprotos = "".join([decl(g) for g in graph])

const_nonobs_callother = "".join([ 
		callObs(g, "constgraph", "obs") for g in constgraph])
const_obs_nodouble = "".join([ noDoubleObs(g) for g in constgraph])

graph_callother = "".join([ callObs(g, "graph", ret=0) for g in graph ])


def delegate_proto(g, sync="synchronized ", obs=0):
    ret = ""
    if g["FRet"] in ["Object", "Iterator"]: ret = "return"
    pars = []
    pars.extend(["Object "+i for i in g["Params"]])
    if obs:
        pars.append("Obs o")
        if len(pars) == 1: obs = 'o'
        else: obs = ',o'
    else: obs = ''
    return ("public "+sync + g["FRet"] + " "+g["FName"]+"("+ 
	    ",".join(pars) +
	    ") {\n    "+ret+" graph." + g["FName"]+"("+",".join([i for i in g["Params"]])+ obs+"); }\n\n")
delegate_calls = "".join([delegate_proto(g, "") for g in constgraph])
delegate_calls += "".join([delegate_proto(g, "", 1) for g in constgraph])
synch_delegate_calls = "".join([delegate_proto(g) for g in graph])
synch_delegate_calls += "".join([delegate_proto(g) for g in constgraph])
synch_delegate_calls += "".join([delegate_proto(g,obs=1) for g in constgraph])

def mkFile(a,b):
    f = open(a,"w")
    f.write("// AUTOGENERATED By makeswamp.py - DO NOT EDIT.\n")
    f.write(b)
    f.close()

# mkFile("org/fenfire/swamp/ConstGraph.java", """
# package org.fenfire.swamp;
# import java.util.Iterator;
# import org.nongnu.navidoc.util.Obs;

# /** A non-modifiable RDF graph. The iterators may implement the method
#  *  remove, but it should not be used, as it may cause unspecified behavior.
#  */
# public interface ConstGraph {
#     /** Get a ConstGraph whose queries will return the same
#      * value as the queries for this graph, but will 
#      * set up the Obs for those queries.
#      * When the result of any of those queries changes,
#      * Obs is called immediately.
#      */
#     ConstGraph getObservedConstGraph(org.nongnu.navidoc.util.Obs o);

#     /** This observed graph will not be used any more, and
#      * if desired, may be recycled.
#      * This operation is allowed to be a no-op, and
#      * if the graph this method is called on is not one that
#      * has been returned by getObservedConstGraph, is
#      * defined to be so..
#      */
#     void close();

#     /** If this graph is observed (returned from getObservedConstGraph),
#      * get the observer.
#      */
#     Obs getObserver();
#     /** If this graph is observed (returned from getObservedConstGraph),
#      * get the original.
#      */
#     ConstGraph getOriginalConstGraph();

#     boolean contains(Object e0, Object e1, Object e2);
#     boolean contains(Object e0, Object e1, Object e2, Obs o);

#     %(nonobs_findprotos)s
#     %(obs_findprotos)s

# }

# """ % locals())

# mkFile("org/fenfire/swamp/Graph.java", """
# package org.fenfire.swamp;
# import java.util.Iterator;
# import org.nongnu.navidoc.util.Obs;

# /** A modifiable RDF graph. Existing iterators should not be used after
#  *  the graph is modified, as it may cause unspecified behavior.
#  */
# public interface Graph extends ConstGraph {
#     Graph getObservedGraph(org.nongnu.navidoc.util.Obs o);
#     void set1_11X(Object subject, Object predicate, Object object);

#     %(nonobs_modprotos)s

#     void add(Object subject, Object predicate, Object object);

#     void addAll(Graph g);
# }

# """ % locals())

# mkFile("org/fenfire/swamp/impl/AbstractConstGraph.java", """
# package org.fenfire.swamp.impl;
# import org.nongnu.navidoc.util.Obs;
# import org.fenfire.swamp.*;
# import java.util.Iterator;

# abstract public class AbstractConstGraph implements ConstGraph {

#     public ConstGraph getObservedConstGraph(Obs obs) {
# 	return new StdObservedConstGraph(this, obs);
#     }
#     public void close() { }
#     public Obs getObserver() { return null; }
#     public ConstGraph getOriginalConstGraph() { return null; }
#     public boolean contains(Object e0, Object e1, Object e2) {
# 	return contains(e0, e1, e2, null);
#     }

#     %(nonobs_callobs)s

# }
# """ % locals())

mkFile("org/fenfire/swamp/impl/StdObservedConstGraph.java", """
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

    %(const_nonobs_callother)s
    %(const_obs_nodouble)s


}

""" % locals())

mkFile("org/fenfire/swamp/impl/StdObservedGraph.java", """
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

    %(graph_callother)s

    public void add(Object subject, Object predicate, Object object) {
	graph.add(subject, predicate, object);
    }

    public void addAll(Graph g) {
	graph.addAll(g);
    }
}

""" % locals())


mkFile("org/fenfire/swamp/impl/SynchronizedGraph.java", """
package org.fenfire.swamp.impl;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import java.util.Iterator;

public class SynchronizedGraph implements Graph {
    Graph graph;

    public SynchronizedGraph(Graph graph) {
	this.graph = graph;
    }
    public synchronized void close() { graph.close(); }
    public synchronized Obs getObserver() { return graph.getObserver(); }
    public synchronized ConstGraph getOriginalConstGraph() {
        return graph.getOriginalConstGraph();    }
    public synchronized ConstGraph getObservedConstGraph(Obs o) {
        return graph.getObservedConstGraph(o);    }
    public synchronized boolean contains(Object e0, Object e1, Object e2) {
        return graph.contains(e0,e1,e2); }
    public synchronized boolean contains(Object e0, Object e1, Object e2, Obs o) {
        return graph.contains(e0,e1,e2, o); }
    public synchronized Graph getObservedGraph(org.nongnu.navidoc.util.Obs o) {
        return graph.getObservedGraph(o); }
    public synchronized void set1_11X(Object subject, Object predicate, Object object) {
        graph.set1_11X(subject, predicate, object);   }
    public synchronized void add(Object subject, Object predicate, Object object) {
        graph.add(subject, predicate, object);     }
    public synchronized void addAll(Graph g) {
        graph.addAll(g);     }

    %(synch_delegate_calls)s
}

""" % locals())


mkFile("org/fenfire/swamp/impl/DelegateGraph.java", """
package org.fenfire.swamp.impl;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import java.util.Iterator;

public abstract class DelegateGraph extends AbstractGraph {
    Graph graph;

    public DelegateGraph(Graph graph) {
	this.graph = graph;
    }
    
    public synchronized void close() { graph.close(); }
    public synchronized Obs getObserver() { return graph.getObserver(); }
    public synchronized ConstGraph getOriginalConstGraph() {
        return graph.getOriginalConstGraph();    }
    public synchronized ConstGraph getObservedConstGraph(Obs o) {
        return graph.getObservedConstGraph(o);    }
    public synchronized boolean contains(Object e0, Object e1, Object e2) {
        return graph.contains(e0,e1,e2); }
    public synchronized boolean contains(Object e0, Object e1, Object e2, Obs o) {
        return graph.contains(e0,e1,e2, o); }
    public synchronized Graph getObservedGraph(org.nongnu.navidoc.util.Obs o) {
        return graph.getObservedGraph(o); }
    public synchronized void set1_11X(Object subject, Object predicate, Object object) {
        graph.set1_11X(subject, predicate, object);   }
    public synchronized void add(Object subject, Object predicate, Object object) {
        graph.add(subject, predicate, object);     }
    public synchronized void addAll(Graph g) {
        graph.addAll(g);     }
    public synchronized void rm_111(Object subject, Object predicate, Object object) {
        graph.rm_111(subject, predicate, object);     }


    %(delegate_calls)s
}

""" % locals())

##########################################################3
#
#  Now, the tough part: the actual structure

# Indices: 123, 132, 231

def makeHashGraph(className, classJavadoc, mapClass, rmind, addind, contains,
                  hashIndices):
    hashNames = ["ind_"+"".join([str(i) for i in codes]) 
		    for codes in hashIndices]

    hashDecls = "".join([
	"private %s %s = new %s();\n" % (mapClass, name, mapClass)
	    for name in hashNames])
    
    def makeFind(g):
	code = "public "+ proto(g, 1) + "{\n";
	def ow(i, wild):
	    if g["Pattern"][i] == "1": return "e"+str(i)
	    else: return wild

	code += "if(obs != null) observer.addObs(%s, %s, %s, obs);\n" % tuple(
	    [ow(i, "observer.WILDCARD") for i in range(0,3)])

	notUniqueParams = ",".join([ow(i, "null") for i in range(0,3)])

	indsNeeded = [i for i in range(0,3) if g["Pattern"][i] == "1"]
	indNext = [i for i in range(0,3) if g["Pattern"][i] == "X"][0]

	print g, indsNeeded, indNext

	success = None

        if className == "SimpleHashGraph":
            # Select the index to use
            for i in range(0,len(hashIndices)):
                if len(indsNeeded):
                    firstinds = hashIndices[i][0:len(indsNeeded)]
                    if not min([el in firstinds for el in indsNeeded]) :
                        print "Can't be ", hashNames[i], indsNeeded, firstinds
                        continue
                if hashIndices[i][len(indsNeeded)] != indNext:
                    print "Can't be (because of next set) ",hashNames[i], hashIndices[i], indNext
                    continue

                print "Is: ",hashNames[i]
                success = i
                break

            assert success != None
            ind = i

            curHash = hashNames[ind]
            for i in range(0,3):
                elem = hashIndices[ind][i]
                newHash = "hash"+str(i+1)
                p = g["Pattern"][elem]
                if p == "1":
                    if i == 1: 
                        htype = "HashSet"
                    else:
                        htype = "HashMap"
                    code += """
                        %(htype)s %(newHash)s = (%(htype)s) %(curHash)s.get(e%(elem)s);
                        if(%(newHash)s == null) RETURN_NONE
                    """ % locals()
                    curHash = newHash
                elif p == "X":
                    if i < 2:
                        code += """
                            Set resSet = %(curHash)s.keySet();
                        """ % locals()
                    else:
                        code += """
                            HashSet resSet = %(curHash)s;
                        """ % locals()
                    break
                elif p == "A":
                    assert 1 == 0

            if g["NVal"] == "N":
                code += "return resSet.iterator();\n"
                code = code.replace("RETURN_NONE", "return emptySet.iterator(); ")
            else:
                code += """
                    if(resSet.size() == 0) return null;
                    if(resSet.size() == 1) return resSet.iterator().next();
                    throw new NotUniqueError(%(notUniqueParams)s);
                """ % locals()
                code = code.replace("RETURN_NONE", "return null; ")

        elif className == "HashGraph":
            ind = hashIndices.index(hashGraphIndex(g['Pattern']))

            params = []
            for i in range(3):
                x = g['Pattern'][i]
                if x == '1':
                    params.append('e%s' % i)
                elif x == 'X':
                    pass
                elif x == 'A':
                    params.append('null')

            if g["NVal"] == "N":
                code += """
                    return %s.getIter(%s);
                """ % (hashNames[ind], ', '.join(params))
            else:
                code += """
                    try {
                        return %s.get(%s);
                    } catch(PairMap.NotUniqueException _) {
                        throw new NotUniqueError(%s);
                    }
                """ % (hashNames[ind], ', '.join(params), notUniqueParams)
                                                
        else:
            assert 0


	code += "}\n\n"
	return code

    finds = "".join([makeFind(g) for g in constgraph])


    rm_triple = ""
    add_triple = ""
    for ind in range(0, len(hashIndices)):
	name = hashNames[ind]
        s = []
        
        for i in range(3):
            x = hashIndices[ind][i]
            if x == 'A':
                s.append('null')
            else:
                s.append('e%s' % x)

	s0, s1, s2 = s
        
	rm_triple += """
	    rm_ind(%(name)s, %(s0)s, %(s1)s, %(s2)s);
	""" % locals()
	add_triple += """
	    add_ind(%(name)s, %(s0)s, %(s1)s, %(s2)s);
	""" % locals()

    mkFile("org/fenfire/swamp/impl/%s.java" % className, """
    package org.fenfire.swamp.impl;
    import org.nongnu.navidoc.util.Obs;
    import org.fenfire.swamp.*;

    import java.util.HashMap;
    import java.util.Set;
    import java.util.HashSet;
    import java.util.ArrayList;
    import java.util.Iterator;

    %(classJavadoc)s
    public class %(className)s extends AbstractGraph {

	%(hashDecls)s

	private HashSet emptySet = new HashSet();
	private StdObserver observer = new StdObserver();

	private final void rm_ind(%(mapClass)s ind, Object o1, Object o2, Object o3) {
        %(rmind)s
	}

	private final void add_ind(%(mapClass)s ind, Object o1, Object o2, Object o3) {
        %(addind)s
	}

	public void set1_11X(Object subject, Object predicate, Object object) {
	    rm_11A(subject, predicate);

	    add(subject, predicate, object);
	}

	public boolean contains(Object e0, Object e1, Object e2, Obs o) {
	    if(o != null) observer.addObs(e0, e1, e2, o);

            %(contains)s
	}

	//////////////////
	// Finds

	%(finds)s


	// --------------------------------
	// Methods which modify the structure by calling the single-triple
	// methods below.
	//


	// --------------------------------
	// Methods which actually modify the structure
	//

	public void rm_111(Object e0, Object e1, Object e2) {
            checkNode(e0); checkNode(e1); checkNodeOrLiteral(e2);
	    %(rm_triple)s

	    observer.triggerObs(-1, e0, e1, e2);
	}

	public void add(Object e0, Object e1, Object e2) {
            checkNode(e0); checkNode(e1); checkNodeOrLiteral(e2);
	    %(add_triple)s

	    observer.triggerObs(1, e0, e1, e2);
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
                throw new IllegalArgumentException(\"Not a node: \"+node);
        }

        protected void checkNodeOrLiteral(Object node) {
            if(!Nodes.isNode(node) && !(node instanceof Literal))
                throw new IllegalArgumentException(\"Not a node or literal: \"+node);
        }

    }




    """ % locals())

# makeHashGraph(className="SimpleHashGraph",
#               mapClass="HashMap",
#               classJavadoc="""
#               /** An RDF Graph implemented by HashMaps.
#                * Relatively inefficient but a basic implementation
#                * that can be used as a reference.
#                */
#               """,
#               hashIndices = [(0,1,2),(0,2,1),(1,2,0),(1,0,2)],
#               rmind="""
#               HashMap m2 = (HashMap)ind.get(o1);
#               if(m2 == null) return;
#               HashSet m3 = (HashSet)m2.get(o2);
#               if(m3 == null) return;
#               m3.remove(o3);
#               """,
#               addind="""
#               HashMap m2 = (HashMap)ind.get(o1);
#               if(m2 == null) {
#                   m2 = new HashMap();
#                   ind.put(o1, m2);
#               }
#               HashSet s = (HashSet)m2.get(o2);
#               if(s == null) {
#                   s = new HashSet();
#                   m2.put(o2, s);
#               }
#               s.add(o3);
#               """,
#               contains="""
#               HashMap hash1 = (HashMap) ind_012.get(e0);
#               if(hash1 == null) return false; 
	
#               HashSet hash2 = (HashSet) hash1.get(e1);
#               if(hash2 == null) return false; 
	
#               HashSet resSet = hash2;
#               return resSet.contains(e2);
#               """
#               )


# def hashGraphIndex(pattern):
#     index = [None,None,None]

#     pos = 0
#     for i in range(3):
#         if pattern[i] == 'X':
#             index[2] = i
#         elif pattern[i] == '1':
#             index[pos] = i
#             pos += 1
#         elif pattern[i] == 'A':
#             index[pos] = 'A'
#             pos += 1

#     for el in index: assert el != None

#     return tuple(index)


# makeHashGraph(className="HashGraph",
#               mapClass="PairMap",
#               classJavadoc="""
#               /** A more efficient implementation of Graph
#                *  than SimpleHashGraph. This implementation uses PairMap,
#                *  which is based on AbstractHashtable, for its indices,
#                *  thus avoiding the overhead of creating objects per triple.
#                */
#               """,
#               hashIndices = [hashGraphIndex(p) for p in find_patterns],
#               rmind="""
#               ind.rm(o1, o2, o3);
#               """,
#               addind="""
#               ind.add(o1, o2, o3);
#               """,
#               contains="""
#               return ind_012.contains(e0, e1, e2);
#               """
#               )
