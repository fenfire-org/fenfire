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


# Helper routines for making "normal" fens

import java

from org import fenfire as ff
from org.nongnu import alph
from org.nongnu import storm

def newFen(graph = None):
    pool = storm.impl.TransientPool(java.util.HashSet())
    myalph = alph.impl.StormAlph(pool)

    fen = ff.Fen()
    if graph == None:
	graph = ff.swamp.impl.HashGraph()
    fen.graph = fen.constgraph = graph
    ff_structure = ff.structure.Ff.create(fen.graph, myalph);
    fen.enfiladeOverlap = ff_structure.getTransclusionIndex()
    return (fen, ff_structure, ff.structure.Ff.ContentFunction(ff_structure))
