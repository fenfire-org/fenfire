# 
# Copyright (c) 2003, Tuomas J. Lukka and Matti Katila
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

from __future__ import nested_scopes

import java
from org import fenfire as ff
from org.fenfire.swamp import Nodes
from org.fenfire.vocab import RDF, CANVAS2D


def createDemoSpace(fen, myAlph, treetime, structure_ff):
    """Create a small space using a transclusion and structlinks.
    """
    paperA = Nodes.get("urn:urn-5:P7OMBN+yp3-m-AkulZN-NeaJV9Sl:_1")
    paperHome = Nodes.get("urn:urn-5:COz-fSvNBZRieCdefRTKO2Mgcjmz:_1")

    sc = myAlph.addFile(java.io.File('../alph/testdata/test1.pdf'), 'application/pdf')

    paperB = Nodes.get("urn:urn-5:P7OMBN+yp3-m-AkulZN-NeaJV9Sl:_2")
    paperC = Nodes.get("urn:urn-5:P7OMBN+yp3-m-AkulZN-NeaJV9Sl:_5")
    fen.graph.add(paperHome, RDF.type, CANVAS2D.Canvas);
    fen.graph.add(paperA, RDF.type, CANVAS2D.Canvas);
    fen.graph.add(paperB, RDF.type, CANVAS2D.Canvas);
    fen.graph.add(paperC, RDF.type, CANVAS2D.Canvas);

    if treetime != None:
	treetime.addLatest(fen.graph, paperHome)
	treetime.addLatest(fen.graph, Nodes.get(sc.getID()))
	treetime.addLatest(fen.graph, paperA)
	treetime.addLatest(fen.graph, paperB)
	treetime.addLatest(fen.graph, paperC)

    canvas2d = ff.structure.Canvas2D.create(fen.graph)
    alphContent = ff.util.AlphContent(fen, structure_ff)
    structLink = ff.structure.StructLink.create(fen.graph)

    def newNote(pap, x, y, str):
	node = Nodes.N()
	canvas2d.placeOnCanvas(pap, node, x, y)
	alphContent.setText(node, str, 1)
	return node
	

    noteA1 = newNote(paperA, -30,-30, 'This is a test!')
    noteA2 = newNote(paperA, -60,-70, 'This is a test2!')
    noteA2 = newNote(paperA, -60,-100, 'abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789')
    noteA3 = newNote(paperA, 100, 100, '')
    noteA4 = newNote(paperA, -60,70, 'This is a very long test which is used to see if ')

    fen.graph.add(noteA2, RDF.type, ff.vocab.lava.MINDSTRUCT.Data)

    structLink.link(noteA1, noteA2);

    span = sc.getCurrent().getPage(0).subArea(100, 100, 300, 200);
    structure_ff.setContent(noteA3, fen.enfMaker.makeEnfilade(span))


    noteB1 = newNote(paperB, 1000,500, 'This is an another test!')
    noteA2 = newNote(paperB, 1000,600, 'abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789')

    structLink.link(noteA1, noteB1)

    noteHome = newNote(paperHome, 0, 0, 'HOME')
    noteA2 = newNote(paperHome, 0,100, 'abcdefghijklmnopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789')

    structLink.link(noteHome, 1, noteA1)

    noteH2 = newNote(paperHome, -400, 0, 'foobar')
    noteB2 = newNote(paperB, 1200, 300, 'This is yet another test!')
    structLink.link(noteB2, 1, noteH2)


    noteC1 = newNote(paperC, 0, 0, 'baz')
    noteC2 = newNote(paperC, -200, 50, 'bar')
    noteB3 = newNote(paperB, 1150, 600, 'foo')
    noteA4 = newNote(paperA, -100, -200, 'Lorem ipsum dolor')
    
    structLink.link(noteC1, 1, noteB3)
    structLink.link(noteA4, 1, noteC2)
    

def createEmptySpace(fen, myAlph, treetime, ppActions, structure_ff):
    paper = ppActions.newPaper()
    ppActions.newNote(paper, 0, 0, "HOME")
