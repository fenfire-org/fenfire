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


"""Testing utilities for fenpdf.
To be run from inside the test framework only.
"""

import os
from org import fenfire as ff
from org.nongnu import alph
from org.nongnu import storm
from org.fenfire.vocab import RDF, CANVAS2D, TREETIME
import java

def createDemo():
    """Create and return a new FenPDF object using
    the demo space.

    Creates a nonexistent file and a new pool in tmp/.
    """

    # XXX NOT PORTABLE
    os.system("mkdir -p tmp/; rm -rf tmp/pool tmp/test.rdf; mkdir -p tmp/pool")

    POOLDIR="tmp/pool"
    FILE="tmp/test.rdf"

    pool = storm.impl.DirPool(java.io.File(POOLDIR), java.util.HashSet())
    myalph = alph.impl.StormAlph(pool)

    fen = ff.Fen()
    fen.alph = myalph
    fen.constgraph = fen.graph = ff.swamp.impl.HashGraph()

    # Create the enfilade/nodecontent structure
    structure_ff = ff.structure.Ff.create(fen.graph, fen.alph)
    treetime = ff.structure.TreeTime(TREETIME.follows)

    fen.enfiladeOverlap = structure_ff.getTransclusionIndex()

    ff.fenpdf.demospace.createDemoSpace(fen, myalph, treetime, 
	    structure_ff)

    fenPDF = ff.fenpdf.fenpdf.FenPDF(fen, treetime, structure_ff, 
			    ff.test.gfx.win, FILE)

    return fenPDF
