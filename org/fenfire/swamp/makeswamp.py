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

from swamp_templates import *

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


ARGS = ['subj', 'pred', 'obj', 'context']


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
    return ', '.join(['Object %s' % ARGS[i] for i in n]+additionalArgs)

def callArgs(p, additionalArgs=[]):
    """
    Return the arguments list for calling a function
    with a particular pattern.

    Examples:
        callArgs('11X') == 'subj, pred'
        callArgs('1X11') == 'subj, obj, context'

    additionalArgs -- List of additional arguments the function should have.

        Example:
            callArgs('1X1A', ['obs']) == 'subj, obj, obs'
    """

    args = [ARGS[i] for i in range(len(p)) if p[i]=='1']
    return ', '.join(args+additionalArgs)


def callTupleArgs(p, wildcard='null', additionalArgs=[]):
    """
    Like callArgs, but a wildcard will be used in the place
    of omitted parameters. Examples:

        callTupleArgs('111', null='null') == 'subj, pred, obj'
        callTupleArgs('1X1A', null='null') == 'subj, null, obj, null'
        callTupleArgs('11X', null='WILDCARD') == 'subj, pred, WILDCARD'

    This is used to call methods that take a whole triple/quad.

    wildcard -- the wildcard to use, defaults to 'null'
    additionalArgs -- List of additional arguments the function should have,
                      as in callArgs().
    """

    def choose(cond, v1, v2):
        if cond: return v1
        else: return v2

    args = [choose(p[i]=='1', ARGS[i], wildcard) for i in range(len(p))]
    return ', '.join(args+additionalArgs)

def callIndexArgs(p, wildcard='null', additionalArgs=[]):
    """
    Like callArgs, but pads the argument list with a wildcard
    to have length (len(p)-1). Examples:

        callIndexArgs('11X') == 'subj, pred'
        callIndexArgs('1XA') == 'subj, null'
        callIndexArgs('AX1') == 'obj, null'
        callIndexArgs('AX1A', 'WILD') == 'obj, WILD, WILD'

    This is used to call methods in the indices that take a fixed-length
    'key' of length (len(p)-1).

    wildcard -- the wildcard to use, defaults to 'null'
    additionalArgs -- List of additional arguments the function should have,
                      as in callArgs().
    """
    
    args = [ARGS[i] for i in range(len(p)) if p[i]=='1']
    args += [wildcard] * (len(p)-len(args)-1)
    return ', '.join(args+additionalArgs)




def constGraph(spec):
    s = ""
   
    for p in spec.find_patterns:
        s += 'Object find1_%s(%s);\n' % (p, arguments(p))
        s += 'Iterator findN_%s_Iter(%s);\n' % (p, arguments(p))
        
        s += 'Object find1_%s(%s);\n' % (p, arguments(p, ['Obs obs']))
        s += 'Iterator findN_%s_Iter(%s);\n' % (p, arguments(p, ['Obs obs']))
        
    return spec.constGraphTemplate % s


def graph(spec):
    s = ""

    for p in spec.rm_patterns:
        s += 'void rm_%s(%s);\n' % (p, arguments(p))
        
    return spec.graphTemplate % s


def abstractConstGraph(spec):
    s = ""

    for p in spec.find_patterns:
        s += 'public Object find1_%s(%s) {\n' % (p, arguments(p))
        s += '    return find1_%s(%s);\n' % (p, callArgs(p, ['null']))
        s += '}\n'
        
        s += 'public Iterator findN_%s_Iter(%s) {\n' % (p, arguments(p))
        s += '    return findN_%s_Iter(%s);\n' % (p, callArgs(p, ['null']))
        s += '}\n'

    return spec.abstractConstGraphTemplate % s


def stdObservedConstGraph(spec):
    s = ""

    for p in spec.find_patterns:
        s += 'public Object find1_%s(%s) {\n' % (p, arguments(p))
        s += '    return constgraph.find1_%s(%s);\n' % \
                                                     (p, callArgs(p, ['obs']))
        s += '}\n'
        
        s += 'public Object find1_%s(%s) {\n' % (p, arguments(p, ['Obs obs']))
        s += '    throw new Error("DoubleObs");\n'
        s += '}\n'
        
        s += 'public Iterator findN_%s_Iter(%s) {\n' % (p, arguments(p))
        s += '    return constgraph.findN_%s_Iter(%s);\n' % \
                                                     (p, callArgs(p, ['obs']))
        s += '}\n'

        s += 'public Iterator findN_%s_Iter(%s) {\n' % \
                                                (p, arguments(p, ['Obs obs']))
        s += '    throw new Error("DoubleObs");\n'
        s += '}\n'

    return spec.stdObservedConstGraphTemplate % s

def stdObservedGraph(spec):
    s = ""

    for p in spec.rm_patterns:
        s += 'public void rm_%s(%s) {\n' % (p, arguments(p))
        s += '    graph.rm_%s(%s);\n' % (p, callArgs(p))
        s += '}\n'

    return spec.stdObservedGraphTemplate % s


def delegateGraph(spec):
    s = ""

    for p in spec.find_patterns:
        s += 'public Object find1_%s(%s) {\n' % (p, arguments(p, ['Obs obs']))
        s += '    return find1_%s(%s);\n' % (p, callArgs(p, ['obs']))
        s += '}\n'
        
        s += 'public Iterator findN_%s_Iter(%s) {\n' % \
                                                (p, arguments(p, ['Obs obs']))
        s += '    return findN_%s_Iter(%s);\n' % (p, callArgs(p, ['obs']))
        s += '}\n'


    return spec.delegateGraphTemplate % s


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
                                                callTupleArgs(p)
        s += '    return result;\n'
        s += '}\n'
        
        s += 'public Iterator findN_%s_Iter(%s){\n' % \
                                               (p, arguments(p, ['Obs obs']))
        s += '    if(obs != null)\n'
        s += '        observer.addObs(%s);\n' % \
                               callTupleArgs(p, 'observer.WILDCARD', ['obs'])
        s += '\n'
        s += '    Set s = getSet_%s(%s);\n' % (p, callArgs(p))
        s += '    return s.iterator();\n'
        s += '}\n'

        s += 'public Set getSet_%s(%s) {\n' % (p, arguments(p))
        s += '    Map m = map_%s;\n' % p
        s += '    Key key = new Key(%s);\n' % callIndexArgs(p, '"ANY"')
        s += '    if(!m.containsKey(key)) m.put(key, new HashSet());\n'
        s += '    return (Set)m.get(key);\n'
        s += '}\n'


    s += 'public void rm_%s(%s) {\n' % (spec.one, arguments(spec.one))
    s += '    checkNode(subj); checkNode(pred); checkNodeOrLiteral(obj);\n'

    for p in spec.find_patterns:
        toremove = p.index('X')
        s += '    getSet_%s(%s).remove(%s);\n' % (p, callArgs(p),
                                                  ARGS[toremove])

    s += '    \n'
    s += '    observer.triggerObs(-1, subj, pred, obj);\n'
    s += '}\n'


    s += 'public void add(%s) {\n' % arguments(spec.one)
    s += '    checkNode(subj); checkNode(pred); checkNodeOrLiteral(obj);\n'

    for p in spec.find_patterns:
        toremove = p.index('X')
        s += '    getSet_%s(%s).add(%s);\n' % (p, callArgs(p), ARGS[toremove])

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
                                callTupleArgs(p, 'observer.WILDCARD', ['obs'])
        s += '\n'
        s += '    try {\n'
        s += '        return map_%s.get(%s);\n' % (p, callIndexArgs(p))
        s += '    } catch(PairMap.NotUniqueException _) {\n'
        s += '        throw new NotUniqueError(%s);\n' % callTupleArgs(p)
        s += '    }\n'
        s += '}\n'
        
        s += 'public Iterator findN_%s_Iter(%s){\n' % \
                                                (p, arguments(p, ['Obs obs']))
        s += '    if(obs != null)\n'
        s += '        observer.addObs(%s);\n' % \
                                callTupleArgs(p, 'observer.WILDCARD', ['obs'])
        s += '\n'
        s += '    return map_%s.getIter(%s);\n' % (p, callIndexArgs(p))
        s += '}\n'


    s += 'public void rm_%s(%s) {\n' % (spec.one, arguments(spec.one))
    s += '    checkNode(subj); checkNode(pred); checkNodeOrLiteral(obj);\n'

    for p in spec.find_patterns:
        toremove = p.index('X')
        s += '    map_%s.rm(%s, %s);\n' % (p, callIndexArgs(p),
                                           ARGS[toremove])

    s += '    \n'
    s += '    observer.triggerObs(-1, subj, pred, obj);\n'
    s += '}\n'


    s += 'public void add(%s) {\n' % arguments(spec.one)
    s += '    checkNode(subj); checkNode(pred); checkNodeOrLiteral(obj);\n'

    for p in spec.find_patterns:
        toremove = p.index('X')
        s += '    map_%s.add(%s, %s);\n' % (p, callIndexArgs(p),
                                            ARGS[toremove])

    s += '    \n'
    s += '    observer.triggerObs(1, subj, pred, obj);\n'
    s += '}\n'


    return spec.hashGraphTemplate % s


def quadAdapterGraph(name, findAll):
    # findAll: whether to find triples from all subgraphs
    # or only from the one we add triples to

    if findAll:
        c = 'A'
        myCallArgs = lambda p: callArgs(p, ['obs'])
    else:
        c = '1'
        myCallArgs = lambda p: callArgs(p, ['context', 'obs'])

    s = ""

    for p in spec.find_patterns:
        s += 'public Object find1_%s(%s) {\n' % (p, arguments(p, ['Obs obs']))
        s += '    return graph.find1_%s%s(%s);\n' % (p, c, myCallArgs(p))
        s += '}\n'
        
        s += 'public Iterator findN_%s_Iter(%s){\n' % \
                                                (p, arguments(p, ['Obs obs']))
        s += '    return graph.findN_%s%s_Iter(%s);\n' % (p, c, myCallArgs(p))
        s += '}\n'

    s += 'public void rm_111(Object subject, Object predicate, \n'
    s += '                   Object object) {\n'

    if(findAll):
        s += 'graph.rm_111A(subject, predicate, object);\n'
    else:
        s += 'graph.rm_1111(subject, predicate, object, context);\n'

    s += '}\n'

    return quadAdapterTemplate % (name, name, s)




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
    write('%s.java' % spec.ConstGraph, constGraph(spec))
    write('%s.java' % spec.Graph, graph(spec))
    write('impl/Abstract%s.java' % spec.ConstGraph, abstractConstGraph(spec))
    write('impl/StdObserved%s.java' % spec.ConstGraph,
          stdObservedConstGraph(spec))
    write('impl/StdObserved%s.java' % spec.Graph, stdObservedGraph(spec))
    write('impl/Delegate%s.java' % spec.Graph, delegateGraph(spec))
    write('impl/SimpleHash%s.java' % spec.Graph, simpleHashGraph(spec))
    write('impl/Hash%s.java' % spec.Graph, hashGraph(spec))


writeFamily(spec)
writeFamily(quad_spec)

for (name, findAll) in [("AllQuadsGraph", 1), ("OneQuadGraph", 0)]:
    write('impl/'+name+'.java', quadAdapterGraph(name, findAll))
