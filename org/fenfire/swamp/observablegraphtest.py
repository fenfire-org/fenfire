# 
# Copyright (c) 2003, Tuomas J. Lukka
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


# Tests which all ObservableGraph implementations must pass

from __future__ import nested_scopes

import org
from org.fenfire.swamp import Nodes, PlainLiteral
from org.fenfire.functional import NodeFunction, CachedNodeFunction

_uriMaker = org.fenfire.util.URN5Namespace()
_node = [Nodes.get(_uriMaker.generateId()) for i in range(0,20)]

dbg = 0

class _Obs(org.nongnu.navidoc.util.Obs):
    def __init__(self):
	self.t = 0
    def chg(self):
	self.t += 1

class _TripleObs(org.fenfire.swamp.TripleSetObs):
    def __init__(self):
	self.t = 0
	self.c = []
    def chg(self):
	self.t += 1
    def chgTriple(self, *args):
	self.c.append(args)

def setUpObservableGraphTest(thegraph):
    global graph
    graph = thegraph

def test_ObservableGraph_ObsOnce():
    node = _node

    o1 = _Obs()
    o2 = _Obs()
    assert graph.find1_11X(node[0], node[1], o1) == None
    assert graph.find1_11X(node[0], node[1], o2) == None

    graph.set1_11X(node[0], node[1], node[2])

    assert o1.t == 1
    assert o2.t == 1
    assert graph.find1_11X(node[0], node[1]) == node[2]

    graph.rm_11A(node[0], node[1])

    assert o1.t == 1
    assert o2.t == 1

    graph.set1_11X(node[0], node[1], node[2])
    assert o1.t == 1
    assert o2.t == 1

    assert graph.find1_11X(node[0], node[1], o1) == node[2]
    assert graph.find1_11X(node[0], node[1], o2) == node[2]

    graph.rm_11A(node[0], node[1])

    assert o1.t == 2
    assert o2.t == 2
    assert graph.find1_11X(node[0], node[1]) == None

    graph.set1_11X(node[0], node[1], node[2])

    assert o1.t == 2
    assert o2.t == 2

def test_ObservableGraph_TripleObs():
    node = _node
    to1 = _TripleObs()
    graph.set1_11X(node[0], node[1], node[2])
    assert graph.find1_11X(node[0], node[1], to1) == node[2]
    graph.set1_11X(node[0], node[1], node[3])

    assert to1.t == 0
    if dbg:
	print "CHANGES: ", to1.c
    assert len(to1.c) == 2
    assert to1.c[0][0] == -1
    assert to1.c[0][3] == node[2]

    assert to1.c[1][0] == 1
    assert to1.c[1][3] == node[3]


class _ObsInChg(org.nongnu.navidoc.util.Obs):
    def __init__(self, obs, s, p):
        self.obs, self.s, self.p = obs, s, p
        self.t = 0

    def chg(self):
        self.t += 1
        graph.find1_11X(self.s, self.p, self.obs)
        
        
    

def test_ObservableGraph_add_obs_in_chg():
    """
    Test adding an observer in the chg() method of another observer.

    This needs to be tested because it changes the set of observers
    during triggering.
    """

    node = _node

    o2 = _Obs()
    o1 = _ObsInChg(o2, node[0], node[1])

    assert graph.find1_11X(node[0], node[1], o1) == None
    assert o1.t == o2.t == 0

    graph.add(node[0], node[1], node[2])

    assert o1.t == 1 and o2.t == 0

    graph.rm_111(node[0], node[1], node[2])

    assert o1.t == 1 and o2.t == 1

    graph.add(node[0], node[1], node[2])
    
    assert o1.t == 1 and o2.t == 1



def test_ObservableGraph_Func():
    node = _node
    class Func(NodeFunction):
	def f(self, g, x):
	    self.calls += 1
	    literal = g.find1_11X(x, node[0])
	    if dbg: print "F: Got literal",literal
	    if literal == None: return None
	    return literal.getString()
	
    of = Func()
    of.calls = 0
    cf = CachedNodeFunction(10, graph, of)

    def doit(f):
	if dbg: print "F now:",f
	assert f.f(graph, node[1]) == None
	assert f.f(graph, node[2]) == None
	assert f.f(graph, node[3]) == None
	assert f.f(graph, node[4]) == None

	graph.set1_11X(node[4], node[0], PlainLiteral("X"))

	assert f.f(graph, node[4]) == "X"
	assert f.f(graph, node[1]) == None

	graph.set1_11X(node[4], node[0], PlainLiteral("Y"))
	graph.set1_11X(node[1], node[0], PlainLiteral("Z"))
	graph.set1_11X(node[3], node[0], PlainLiteral("Q"))

	assert f.f(graph, node[4]) == "Y"
	assert f.f(graph, node[1]) == "Z"
	assert f.f(graph, node[2]) == None
	assert f.f(graph, node[3]) == "Q"

	graph.rm_11A(node[3], node[0])

	assert f.f(graph, node[4]) == "Y"
	assert f.f(graph, node[1]) == "Z"
	assert f.f(graph, node[2]) == None
	assert f.f(graph, node[3]) == None

	graph.rm_11A(node[4], node[0])
	graph.rm_11A(node[1], node[0])

	assert f.f(graph, node[1]) == None
	assert f.f(graph, node[2]) == None
	assert f.f(graph, node[3]) == None
	assert f.f(graph, node[4]) == None

    doit(of)
    assert of.calls == 18
    of.calls = 0
    doit(cf)
    assert of.calls < 16, of.calls

    of.calls = 0
    cf = CachedNodeFunction(10, graph, of)

    class Func2(NodeFunction):
	def __init__(self, func):
	    self.func = func
	def f(self, g, x):
	    self.calls += 1
	    t = self.func.f(g, x)
	    if t == None: return None
	    return "_"+t

    of2 = Func2(cf)
    of2.calls = 0
    cf2 = CachedNodeFunction(3, graph, of2)

    assert cf2.f(graph, node[1]) == None
    assert of.calls == 1
    assert of2.calls == 1
    assert cf2.f(graph, node[1]) == None
    assert of.calls == 1
    assert of2.calls == 1
    graph.set1_11X(node[1], node[0], PlainLiteral("Z"))
    assert cf2.f(graph, node[1]) == "_Z"
    assert of.calls == 2
    assert of2.calls == 2
    assert cf2.f(graph, node[1]) == "_Z"
    assert of.calls == 2
    assert of2.calls == 2

