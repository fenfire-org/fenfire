# 
# Copyright (c) 2004, Benja Fallenstein
# Portions Copyright (c) 2003, Tuomas J. Lukka
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

quad_spec.rm_patterns = [p+'A' for p in spec.rm_patterns] + \
                        [p+'1' for p in spec.rm_patterns]


def arguments(p):
    n = len([x for x in p if x=='1'])
    return ', '.join(['Object e%s' % i for i in range(n)])

def argumentsObs(p):
    n = len([x for x in p if x=='1'])
    return ', '.join(['Object e%s' % i for i in range(n)]+["Obs obs"])

def callArguments(p):
    n = len([x for x in p if x=='1'])
    return ', '.join(['e%s' % i for i in range(n)])

def callArguments2(p):
    return ', '.join(['e%s' % i for i in range(len(p)) if p[i]=='1'])

def callArgumentsObs(p):
    n = len([x for x in p if x=='1'])
    return ', '.join(['e%s' % i for i in range(n)]+["obs"])

def callArgumentsNulls(p):
    n = len([x for x in p if x=='1'])
    args = ['e%s' % i for i in range(n)] + ['null']*(len(p)-n)
    return ', '.join(args)

def callArgumentsNulls2(p):
    n = len([x for x in p if x=='1'])
    l = ['e%s' % i for i in range(len(p)) if p[i]=='1'] + ['null']*(len(p)-n-1)
    return ', '.join(l)

def callArgumentsAnys(p):
    n = len([x for x in p if x=='1'])
    args = ['e%s' % i for i in range(n)] + ['"ANY"']*(len(p)-n-1)
    return ', '.join(args)

def callArgumentsAnyNulls(p):
    n = len([x for x in p if x=='1'])
    args = ['e%s' % i for i in range(n)] + ['null']*(len(p)-n-1)
    return ', '.join(args)

def obsArguments(p):
    args = []
    i = 0
    for c in p[:3]:
        if c == '1':
            args.append('e%s' % i)
            i = i+1
        elif c == 'X' or c == 'A':
            args.append('observer.WILDCARD')

    return ', '.join(args)


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
#         s += '    return find1_%s(%s, null);\n' % (p, callArguments(p))
#         s += '}\n'
        
#         s += 'public Iterator findN_%s_Iter(%s) {\n' % (p, arguments(p))
#         s += '    return findN_%s_Iter(%s, null);\n' % (p, callArguments(p))
#         s += '}\n'

#     return spec.abstractConstGraphTemplate % s


def simpleHashGraph(spec):
    s = ""

    for p in spec.find_patterns:
        s += 'Map map_%s = new HashMap();\n' % p

        s += 'public Object find1_%s(%s) {\n' % (p, argumentsObs(p))
        s += '    Iterator i = findN_%s_Iter(%s);\n' % (p, callArgumentsObs(p))
        s += '    if(!i.hasNext()) return null;\n'
        s += '    Object result = i.next();\n'
        s += '    if(i.hasNext())\n'
        s += '        throw new NotUniqueError(%s);\n' % callArgumentsNulls(p)
        s += '    return result;\n'
        s += '}\n'
        
        s += 'public Iterator findN_%s_Iter(%s){\n' % (p, argumentsObs(p))
        s += '    if(obs != null)\n'
        s += '        observer.addObs(%s, obs);\n' % obsArguments(p)
        s += '\n'
        s += '    Set s = getSet_%s(%s);\n' % (p, callArguments(p))
        s += '    return s.iterator();\n'
        s += '}\n'

        s += 'public Set getSet_%s(%s) {\n' % (p, arguments(p))
        s += '    Map m = map_%s;\n' % p
        s += '    Key key = new Key(%s);\n' % callArgumentsAnys(p)
        s += '    if(!m.containsKey(key)) m.put(key, new HashSet());\n'
        s += '    return (Set)m.get(key);\n'
        s += '}\n'


    s += 'public void rm_%s(%s) {\n' % (spec.one, arguments(spec.one))
    s += '    checkNode(e0); checkNode(e1); checkNodeOrLiteral(e2);\n'

    for p in spec.find_patterns:
        toremove = p.index('X')
        s += '    getSet_%s(%s).remove(e%s);\n' % (p, callArguments2(p),
                                                   toremove)

    s += '    \n'
    s += '    observer.triggerObs(-1, e0, e1, e2);\n'
    s += '}\n'


    s += 'public void add(%s) {\n' % arguments(spec.one)
    s += '    checkNode(e0); checkNode(e1); checkNodeOrLiteral(e2);\n'

    for p in spec.find_patterns:
        toremove = p.index('X')
        s += '    getSet_%s(%s).add(e%s);\n' % (p, callArguments2(p), toremove)

    s += '    \n'
    s += '    observer.triggerObs(1, e0, e1, e2);\n'
    s += '}\n'


    return spec.simpleHashGraphTemplate % s


def hashGraph(spec):
    s = ""

    for p in spec.find_patterns:
        s += 'PairMap map_%s = new PairMap();\n' % p

        s += 'public Object find1_%s(%s) {\n' % (p, argumentsObs(p))
        s += '    if(obs != null)\n'
        s += '        observer.addObs(%s, obs);\n' % obsArguments(p)
        s += '\n'
        s += '    try {\n'
        s += '        return map_%s.get(%s);\n' % (p, callArgumentsAnyNulls(p))
        s += '    } catch(PairMap.NotUniqueException _) {\n'
        s += '        throw new NotUniqueError(%s);\n' % callArgumentsNulls(p)
        s += '    }\n'
        s += '}\n'
        
        s += 'public Iterator findN_%s_Iter(%s){\n' % (p, argumentsObs(p))
        s += '    if(obs != null)\n'
        s += '        observer.addObs(%s, obs);\n' % obsArguments(p)
        s += '\n'
        s += '    return map_%s.getIter(%s);\n' % (p, callArgumentsAnyNulls(p))
        s += '}\n'


    s += 'public void rm_%s(%s) {\n' % (spec.one, arguments(spec.one))
    s += '    checkNode(e0); checkNode(e1); checkNodeOrLiteral(e2);\n'

    for p in spec.find_patterns:
        toremove = p.index('X')
        s += '    map_%s.rm(%s, e%s);\n' % (p, callArgumentsNulls2(p),
                                            toremove)

    s += '    \n'
    s += '    observer.triggerObs(-1, e0, e1, e2);\n'
    s += '}\n'


    s += 'public void add(%s) {\n' % arguments(spec.one)
    s += '    checkNode(e0); checkNode(e1); checkNodeOrLiteral(e2);\n'

    for p in spec.find_patterns:
        toremove = p.index('X')
        s += '    map_%s.add(%s, e%s);\n' % (p, callArgumentsNulls2(p),
                                             toremove)

    s += '    \n'
    s += '    observer.triggerObs(1, e0, e1, e2);\n'
    s += '}\n'


    return spec.hashGraphTemplate % s


spec.simpleHashGraphTemplate = """
    package org.fenfire.swamp.impl;
    import org.nongnu.navidoc.util.Obs;
    import org.fenfire.swamp.*;
    import java.util.*;

    public class SimpleHashGraph extends AbstractGraph {
	private StdObserver observer = new StdObserver();

        protected class Key {
            Object e1, e2;
            public Key(Object e1, Object e2) {
                this.e1 = e1; this.e2 = e2;
            }
            public boolean equals(Object o) {
                if(!(o instanceof Key)) return false;
                Key t=(Key)o;
                return e1.equals(t.e1) && e2.equals(t.e2);
            }
            public int hashCode() {
                return e1.hashCode() + 127*e2.hashCode();
            }
        }

        %s

	public void set1_11X(Object subject, Object predicate, Object object) {
	    rm_11A(subject, predicate);
	    add(subject, predicate, object);
	}

	public boolean contains(Object e0, Object e1, Object e2, Obs o) {
	    if(o != null) observer.addObs(e0, e1, e2, o);
            return getSet_11X(e0, e1).contains(e2);
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

	public void set1_11X(Object subject, Object predicate, Object object) {
	    rm_11A(subject, predicate);
	    add(subject, predicate, object);
	}

	public boolean contains(Object e0, Object e1, Object e2, Obs o) {
	    if(o != null) observer.addObs(e0, e1, e2, o);
            return map_11X.contains(e0, e1, e2);
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
            Object e1, e2, e3;
            public Key(Object e1, Object e2, Object e3) {
                this.e1 = e1; this.e2 = e2; this.e3 = e3;
            }
            public boolean equals(Object o) {
                if(!(o instanceof Key)) return false;
                Key t=(Key)o;
                return e1.equals(t.e1) && e2.equals(t.e2) && e3.equals(t.e3);
            }
            public int hashCode() {
                return e1.hashCode() + 127*e2.hashCode() + 2047*e3.hashCode();
            }
        }

        %s

	public boolean contains(Object e0, Object e1, Object e2, Object e3, Obs o) {
	    if(o != null) observer.addObs(e0, e1, e2, o);
            return getSet_11X1(e0, e1, e3).contains(e2);
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

	public boolean contains(Object e0, Object e1, Object e2, Object e3, Obs o) {
	    if(o != null) observer.addObs(e0, e1, e2, o);
            return map_11X1.contains(e0, e1, e3, e2);
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
