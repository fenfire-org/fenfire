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
spec.find_patterns = ("11X","1X1","X11", "1XA", "XAA", "X1A")
spec.rm_patterns = ("111", "11A", "A11", "1AA")
spec.one = "111"

quad_spec = Spec()
quad_spec.Graph = "QuadsGraph"
quad_spec.one = "1111"

#quad_spec.find_patterns = [p+'A' for p in spec.find_patterns] + \
#                          [p+'1' for p in spec.find_patterns] + ["111X"]

quad_spec.find_patterns = [
            "111X",

        "11X1", "1X11", "X111",
        "11XA", "1X1A", "X11A",

        "11AX",

        "1XAA", "1XA1",
        #"X1AA", "A1XA", "AX1A", "AA1X", "AAX1",

        "XAAA", "XAA1", #"AXAA", "AAXA", "AAAX",

        "X1AA", "X1A1",
]

quad_spec.rm_patterns = [
	"1111",  # Only these three removes at first...
	"11AA", 
#	"A111", 
#	"1AAA",
	"AAA1",
        "111A", # and this one!
]

#quad_spec.rm_patterns = [p+'A' for p in spec.rm_patterns] + \
#                        [p+'1' for p in spec.rm_patterns]


ARG = ['subj', 'pred', 'obj', 'context']


def arguments(p, additionalArgs=[]):
    """
    Return the arguments in the argument list of a function
    with a particular pattern.

    Examples:
        arguments('11X') == 'Object subj, Object pred'
        arguments('1X11') == 'Object subj, Object obj, Object context'

    additionalArgs -- List of additional arguments the function should have.

    Example:
        arguments('1X1A', ['Obs obs']) == 'Object subj, Object obj, Obs obs'
    """
    
    n = [i for i in range(len(p)) if p[i]=='1']
    return ', '.join(['Object %s' % ARG[i] for i in n]+additionalArgs)

def callArgs(p, additionalArgs=[], null=None, fill=None):
    """
    Return the arguments list for calling a function
    with a particular pattern.

    Examples:
        callArgs('11X') == 'subj, pred'
        callArgs('1X11') == 'subj, obj, context'

    additionalArgs -- List of additional arguments the function should have.

        Example:
            callArgs('1X1A', ['obs']) == 'subj, obj, obs'

    In addition to the basic way, specifying the pattern and optionally
    the additional arguments, there are two additional ways to use
    this function.

    First, if you set 'null' to a string, it will be used in the place
    of omitted parameters. Examples:

        callArgs('111', null='null') == 'subj, pred, obj'
        callArgs('1X1A', null='null') == 'subj, null, obj, null'
        callArgs('11X', null='WILDCARD') == 'subj, pred, WILDCARD'

    This is used to call methods that take a whole triple/quad.

    Second, if you set 'fill' to a string, the argument list
    will be padded with copies of 'fill' to have length (len(p)-1). Examples:

        callArgs('11X', fill='null') == 'subj, pred'
        callArgs('1XA', fill='null') == 'subj, null'
        callArgs('AX1', fill='null') == 'obj, null'
        callArgs('AX1A', fill='null') == 'obj, null, null'

    This is used to call methods in the indices that take a fixed-length
    'key' of length (len(p)-1).

    The 'null' and 'fill' parameters cannot be specified both.
    """

    if null!=None and fill!=None:
        raise Error('Either null or fill can be specified, but not both')
    
    args = []
    for i in range(len(p)):
        if p[i] == '1':
            args.append(ARG[i])
        elif null != None:
            args.append(null)
        else:
            pass

    if fill != None:
        args += [fill] * (len(p)-len(args)-1)

    return ', '.join(args+additionalArgs)



# def constGraph(spec):
#     s = ""
    
#     for p in spec.find_patterns:
#         s += ('Object find1_%s(%s);\n' % (p, arguments(p)))
#         s += ('Iterator findN_%s_Iter(%s);\n' % (p, arguments(p)))

#         s += ('Object find1_%s(%s);\n' % (p, argumentsObs(p)))
#         s += ('Iterator findN_%s_Iter(%s);\n' % (p, argumentsObs(p)))

#     return spec.constGraphTemplate % s


# def graph(spec):
#     s = ""

#     for p in spec.rm_patterns:
#         s += 'void rm_%s(%s);\n' % (p, arguments(p))

#     return spec.graphTemplate % s


# def abstractConstGraph(spec):
#     s = ""

#     for p in spec.find_patterns:
#         s += 'public Object find1_%s(%s) {\n' % (p, arguments(p))
#         s += '    return find1_%s(%s, null);\n' % (p, callArgs(p))
#         s += '}\n'
        
#         s += 'public Iterator findN_%s_Iter(%s) {\n' % (p, arguments(p))
#         s += '    return findN_%s_Iter(%s, null);\n' % (p, callArgs(p))
#         s += '}\n'

#     return spec.abstractConstGraphTemplate % s


def simpleHashGraph(spec):
    s = ""

    for p in spec.find_patterns:
        s += 'Map map_%s = new HashMap();\n' % p

        s += 'public Object find1_%s(%s) {\n' % (p, arguments(p, ['Obs obs']))
        s += '    Iterator i = findN_%s_Iter(%s);\n' % \
                                                (p, callArgs(p, ['obs']))
        s += '    if(!i.hasNext()) return null;\n'
        s += '    Object result = i.next();\n'
        s += '    if(i.hasNext())\n'
        s += '        throw new NotUniqueError(%s);\n' % \
                                                callArgs(p, null='null')
        s += '    return result;\n'
        s += '}\n'
        
        s += 'public Iterator findN_%s_Iter(%s){\n' % \
                                               (p, arguments(p, ['Obs obs']))
        s += '    if(obs != null)\n'
        s += '        observer.addObs(%s);\n' % \
                               callArgs(p, ['obs'], null='observer.WILDCARD')
        s += '\n'
        s += '    Set s = getSet_%s(%s);\n' % (p, callArgs(p))
        s += '    return s.iterator();\n'
        s += '}\n'

        s += 'public Set getSet_%s(%s) {\n' % (p, arguments(p))
        s += '    Map m = map_%s;\n' % p
        s += '    Key key = new Key(%s);\n' % callArgs(p, fill='"ANY"')
        s += '    if(!m.containsKey(key)) m.put(key, new HashSet());\n'
        s += '    return (Set)m.get(key);\n'
        s += '}\n'


    s += 'public void rm_%s(%s) {\n' % (spec.one, arguments(spec.one))
    s += '    checkNode(subj); checkNode(pred); checkNodeOrLiteral(obj);\n'

    for p in spec.find_patterns:
        toremove = p.index('X')
        s += '    getSet_%s(%s).remove(%s);\n' % (p, callArgs(p),
                                                  ARG[toremove])

    s += '    \n'
    s += '    observer.triggerObs(-1, subj, pred, obj);\n'
    s += '}\n'


    s += 'public void add(%s) {\n' % arguments(spec.one)
    s += '    checkNode(subj); checkNode(pred); checkNodeOrLiteral(obj);\n'

    for p in spec.find_patterns:
        toremove = p.index('X')
        s += '    getSet_%s(%s).add(%s);\n' % (p, callArgs(p), ARG[toremove])

    s += '    \n'
    s += '    observer.triggerObs(1, subj, pred, obj);\n'
    s += '}\n'


    return spec.simpleHashGraphTemplate % s


def hashGraph(spec):
    s = ""

    for p in spec.find_patterns:
        s += 'PairMap map_%s = new PairMap();\n' % p

        s += 'public Object find1_%s(%s) {\n' % (p, arguments(p, ['Obs obs']))
        s += '    if(obs != null)\n'
        s += '        observer.addObs(%s);\n' % \
                                callArgs(p, ['obs'], null='observer.WILDCARD')
        s += '\n'
        s += '    try {\n'
        s += '        return map_%s.get(%s);\n' % (p, callArgs(p, fill='null'))
        s += '    } catch(PairMap.NotUniqueException _) {\n'
        s += '        throw new NotUniqueError(%s);\n' % \
                                                  callArgs(p, null='null')
        s += '    }\n'
        s += '}\n'
        
        s += 'public Iterator findN_%s_Iter(%s){\n' % \
                                                (p, arguments(p, ['Obs obs']))
        s += '    if(obs != null)\n'
        s += '        observer.addObs(%s);\n' % \
                                callArgs(p, ['obs'], null='observer.WILDCARD')
        s += '\n'
        s += '    return map_%s.getIter(%s);\n' % (p, callArgs(p, fill='null'))
        s += '}\n'


    s += 'public void rm_%s(%s) {\n' % (spec.one, arguments(spec.one))
    s += '    checkNode(subj); checkNode(pred); checkNodeOrLiteral(obj);\n'

    for p in spec.find_patterns:
        toremove = p.index('X')
        s += '    map_%s.rm(%s, %s);\n' % (p, callArgs(p, fill='null'),
                                           ARG[toremove])

    s += '    \n'
    s += '    observer.triggerObs(-1, subj, pred, obj);\n'
    s += '}\n'


    s += 'public void add(%s) {\n' % arguments(spec.one)
    s += '    checkNode(subj); checkNode(pred); checkNodeOrLiteral(obj);\n'

    for p in spec.find_patterns:
        toremove = p.index('X')
        s += '    map_%s.add(%s, %s);\n' % (p, callArgs(p, fill='null'),
                                            ARG[toremove])

    s += '    \n'
    s += '    observer.triggerObs(1, subj, pred, obj);\n'
    s += '}\n'


    return spec.hashGraphTemplate % s


def quadAdapterGraph(name, findAll):
    # findAll: whether to find triples from all subgraphs
    # or only from the one we add triples to

    if findAll:
        c = 'A'
        callArg = lambda p: callArgs(p, ['obs'])
    else:
        c = '1'
        callArg = lambda p: callArgs(p, ['context', 'obs'])

    s = ""

    for p in spec.find_patterns:
        s += 'public Object find1_%s(%s) {\n' % (p, arguments(p, ['Obs obs']))
        s += '    return graph.find1_%s%s(%s);\n' % (p, c, callArg(p))
        s += '}\n'
        
        s += 'public Iterator findN_%s_Iter(%s){\n' % \
                                                (p, arguments(p, ['Obs obs']))
        s += '    return graph.findN_%s%s_Iter(%s);\n' % (p, c, callArg(p))
        s += '}\n'

    s += 'public void rm_111(Object subject, Object predicate, \n'
    s += '                   Object object) {\n'

    if(findAll):
        s += 'graph.rm_111A(subject, predicate, object);\n'
    else:
        s += 'graph.rm_1111(subject, predicate, object, context);\n'

    s += '}\n'

    return quadAdapterTemplate % (name, name, s)


    

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

        protected void checkNode(Object node) {
            if(!Nodes.isNode(node))
                throw new IllegalArgumentException("Not a node: "+node);
        }

        protected void checkNodeOrLiteral(Object node) {
            if(!Nodes.isNode(node) && !(node instanceof Literal))
                throw new IllegalArgumentException("Not a node or literal: "+node);
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

        protected void checkNode(Object node) {
            if(!Nodes.isNode(node))
                throw new IllegalArgumentException("Not a node: "+node);
        }

        protected void checkNodeOrLiteral(Object node) {
            if(!Nodes.isNode(node) && !(node instanceof Literal))
                throw new IllegalArgumentException("Not a node or literal: "+node);
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


base = 'org/fenfire/swamp/'

def write(filename, contents):
    filename = base+filename
    print 'Write', filename

    contents = '// AUTOGENERATED By makeswamp2.py - DO NOT EDIT.\n'+contents

    #print '='*20, filename, '='*20
    #print contents

    f = open(filename, 'w')
    f.write(contents)
    f.close()

def writeFamily(spec):
    write('impl/SimpleHash%s.java' % spec.Graph, simpleHashGraph(spec))
    write('impl/Hash%s.java' % spec.Graph, hashGraph(spec))


writeFamily(spec)
writeFamily(quad_spec)

for (name, findAll) in [("AllQuadsGraph", 1), ("OneQuadGraph", 0)]:
    write('impl/'+name+'.java', quadAdapterGraph(name, findAll))
