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


# Tests which all Graph implementations must pass

import org
from org.fenfire.swamp import Nodes

_uriMaker = org.fenfire.util.URN5Namespace()
_node = [Nodes.get(_uriMaker.generateId()) for i in range(0,20)]


def setUpGraphtest(thegraph):
    global graph
    graph = thegraph

def test_Graph_SingleTriple():
    node = _node

    # Check it isn't there
    assert graph.find1_11X(node[0], node[1]) == None
    assert not graph.findN_11X_Iter(node[0], node[1]).hasNext()

    # Put a triple in there
    graph.set1_11X(node[0], node[1], node[2])
    assert graph.find1_11X(node[0], node[1]) == node[2]
    assert graph.find1_1X1(node[0], node[2]) == node[1]
    assert graph.find1_X11(node[1], node[2]) == node[0]
    assert graph.findN_11X_Iter(node[0], node[1]).hasNext()

    # Remove a different triple
    graph.rm_111(node[0], node[1], node[3])
    graph.rm_11A(node[1], node[0])

    assert graph.find1_11X(node[0], node[1]) == node[2]
    assert graph.find1_1X1(node[0], node[2]) == node[1]
    assert graph.find1_X11(node[1], node[2]) == node[0]

    # Remove the original triple
    graph.rm_11A(node[0], node[1])

    assert graph.find1_11X(node[0], node[1]) == None
    assert graph.find1_1X1(node[0], node[2]) == None
    assert graph.find1_X11(node[1], node[2]) == None


def iterContains(iter, el):
    while iter.hasNext():
        if iter.next() == el: return 1
    return 0

def test_Graph_Triples():
    node = _node
    triples = [(0,1,4),(3,2,1),(2,4,2),(1,2,3),(0,1,0),(1,1,4),(1,2,3)]

    for (s,p,o) in triples:
        graph.add(node[s], node[p], node[o])

    for s in range(6):
        for p in range(6):
            for o in range(6):
                if (s,p,o) in triples:
                    assert graph.contains(node[s],node[p],node[o])

                    assert iterContains(graph.findN_1XA_Iter(node[s]), node[p])
                    assert iterContains(graph.findN_11X_Iter(node[s], node[p]),
                                        node[o])
                else:
                    assert not graph.contains(node[s],node[p],node[o])

                    assert not iterContains(graph.findN_11X_Iter(node[s],
                                                                 node[p]),
                                            node[o])


