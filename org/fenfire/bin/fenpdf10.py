# 
# Copyright (c) 2003, Tuomas J. Lukka, Janne Kujala, Matti J. Katila and Benja Fallenstein
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
# 

# The FenPDF 1.0 !

# Input: in Java properties:
#
# fenpdf.file : The file name. Default: myFenfire/mygraph.rdf
# 
# fenpdf.demo : If the file does not exist, FenPDF creates a new space.
#		If this value is nonzero, it creates a small demo space,
# 		otherwise an empty space. Default: empty
#
# fenpdf.pool : The storm pool directory to use. Default: myFenfire/

dbg = 0

def p(*s):
    print 'fenfire.bin.fenpdf10::',s

import sys
import jarray
import java

print """

     ############################### 
     ##                           ##
     ##  LOADING FenPDF1.0 v0.0   ##
     ##                           ##
     ###############################


FenPDF is brought to you by the Fenfire team (http://fenfire.org).
 
Fenfire is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
 
Fenfire is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
Public License for more details.
 
You should have received a copy of the GNU General
Public License along with Fenfire; if not, write to the Free
Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
MA  02111-1307  USA
 

"""


if dbg:
    for (k,v) in sys.modules.items():
        p( k,"\t",v)

from org import fenfire as ff
from org.fenfire.swamp import Nodes
from org.fenfire.vocab import RDF, CANVAS2D, TREETIME
from org.fenfire.index import XuLink
from org.fenfire.util import Pair

from org.nongnu import alph
from org.nongnu import storm

from org.nongnu.alph.impl import PageImageScroll

from org.fenfire.fenpdf import actions


# save and load
from com.hp.hpl.mesa.rdf.jena.mem import ModelMem
import os.path

import vob

# Read the user-settable properties.
DIR = 'myFenfire/'
FILE = java.lang.System.getProperty("fenpdf.file", DIR+"mygraph.rdf")
POOLDIR = java.lang.System.getProperty("fenpdf.pool", DIR)


# We're using the demo framework here (should eventually move
# out of it) but for now; set the flag to show that
# we don't want 'r' to reload; make the demo framework
# use Ctrl-R instead
vob.putil.demo.usingNormalBindings = 0

# Starting up takes a few seconds; show to the user the friendly
# wait cursor
w.setCursor('wait')

# Now, before reading the xu stuff, render something 
# to try to prevent NV driver
# fallback to software rendering.
# Might be that it only reserves screen memory when starting to render
# and if we reserve it all for textures, ... splat ...
vs = w.createVobScene()
vs.put(background((.8, .4, .9)))
w.renderStill(vs, 0)

### debugs
#ff.view.AreaSelectingView2D.dbg = 1

# Create the pool directory if necessary 
# (XXX NOT PORTABLE)
os.system('mkdir -p '+POOLDIR)

# Create the storm and alph instances
pool = storm.impl.DirPool(java.io.File(POOLDIR), java.util.HashSet())
myalph = alph.impl.StormAlph(pool)


# If the file does exist, load it; otherwise, create
# a new, empty graph, to be saved into that file.
if os.path.isfile(FILE):
    print 'Loading the RDF graph from the file %s.'% FILE
    do_load_graph = 1
else:
    print 'Creating a new RDF graph. File it will be written to: %s' % FILE
    do_load_graph = 0


# Create the fen with swamps.
fen = ff.Fen()
fen.alph = myalph
if do_load_graph:
    m = ModelMem()
    m.read(java.io.FileReader(FILE), None)
    fen.constgraph = fen.graph = ff.swamp.Graphs.toGraph(m)
else:
    fen.constgraph = fen.graph = ff.swamp.impl.HashGraph()

# Create the enfilade/nodecontent structure
structure_ff = ff.structure.Ff.create(fen.graph, fen.alph)
treetime = ff.structure.TreeTime(TREETIME.follows)

fen.enfiladeOverlap = structure_ff.getTransclusionIndex()


# If we want a new space, create a demo space.
if not do_load_graph:
    val = java.lang.System.getProperty("fenpdf.demo")
    if val != None and java.lang.Integer.parseInt(val) != 0:
	ff.fenpdf.demospace.createDemoSpace(fen, myalph, treetime, 
		structure_ff)
    else:
	ff.fenpdf.demospace.createEmptySpace(fen, myalph, treetime, 
		structure_ff)






fenPDF = ff.fenpdf.fenpdf.FenPDF(fen, treetime, structure_ff, w, FILE)

# XXX THIS IS REALLY BAD ARCHITECTURALLY!
cursorRenderer = ff.fenpdf.fenpdfcontext.CursorRenderer(fenPDF)
ff.view.buoy.AbstractMainNode2D.context = cursorRenderer




# fenpdf UI tests go trough this. 
FenPDF_test = 0
if java.lang.System.getProperty('testFenPDF', '0') == '1':
    print 'Test Actions!'
    FenPDF_test = 1
    FenPDF_testUtil = ff.fenpdf.actions.test.\
                      getTest(currentScene, loadScenes, logger, fenPDF)


class Scene:
    def __init__(self):
        w.setCursor('default')
        self.logEvents = 0
    def scene(self, vs):
        scene = fenPDF.views.scene(vs)

        if FenPDF_test:
            FenPDF_testUtil.chg(self)
            if dbg: p('test')
        return scene

    def mouse(self, ev):

        fenPDF.events.eventHandler.mouse(ev, fenPDF.animation.lastVs)

    def key(self, key):
        p(key)

        if key == 'F11':
            self.logEvents = (1-self.logEvents)
            if self.logEvents:
                logger.setRecordFileName('demo.data')
                logger.startRecording()
            else:
                logger.stopRecording()
            return
        elif key == 'F12':
            logger.playRecord('demo.data', self)
            return

	fenPDF.events.eventHandler.key(key)

