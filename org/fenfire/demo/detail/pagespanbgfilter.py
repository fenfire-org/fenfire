# 
# Copyright (c) 2003, Janne V. Kujala and Tuomas J. Lukka
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

# PageSpan background filtering demo

from __future__ import nested_scopes
import vob
import java, org
from org import fenfire as ff
from org.nongnu.libvob.gl import GL, GLRen, GLCache, PaperMill
from org.nongnu import alph
from org.nongnu import storm

from vob.putil.demokeys import *

from org.fenfire.spanimages.gl.papermakers import allPaperMakers

class OffsetPaperMill(PaperMill):
    """A PaperMill that delegates to another papermill, adding an offset
    to the seed.

    Useful for changing the paper generated for a given document
    in this demo.
    """
    def __init__(self, mill, offset):
	self.mill = mill
	self.offset = offset
    def getPaper(self, seed):
	return self.mill.getPaper(seed + self.offset)
    def getOptimizedPaper(self, seed, w):
	return self.mill.getOptimizedPaper(seed + self.offset, w);

class OffsetPaperMill(PaperMill):
    """A PaperMill that delegates to another papermill, adding an offset
    to the seed.

    Useful for changing the paper generated for a given document
    in this demo.
    """
    def __init__(self, mill, offset):
	self.mill = mill
	self.offset = offset
    def getPaper(self, seed):
	return self.mill.getPaper(seed + self.offset)
    def getOptimizedPaper(self, seed, w):
	return self.mill.getOptimizedPaper(seed + self.offset, w);

pool = org.nongnu.storm.impl.TransientPool(java.util.HashSet())
myalph = alph.impl.StormAlph(pool)

sc = [
    myalph.addFile(java.io.File('../alph/testdata/test1.pdf'), 'application/pdf'),
    myalph.addFile(java.io.File('testdata/paper.pdf'), 'application/pdf')
    ]

enfMaker = alph.impl.Enfilade1DImpl.Enfilade1DImplMaker()

class Scene:
    def __init__(self):
	self.w = w = vob.putil.demowindow.w
	# self.types = [ (entry[0](), entry[1]) for entry in allPaperMakers ]
	self.types = allPaperMakers 
	self.scrolls = sc

	self.scrollimager = ff.spanimages.gl.PageScrollBlockImager()
	self.spanImageFactory = ff.spanimages.gl.DefaultSpanImageFactory(
					self.scrollimager)
    	self.key = KeyPresses(
            self, 
	    ListIndex("type", "types", 0, "Papermaker", "P", "p"),
	    ListIndex("scroll", "scrolls", 0, "File to use", "F", "f"),
	    SlideLin("x", 0, 50, "x coord", "Right", "Left"),
	    SlideLin("y", 0, 50, "y coord", "Down", "Up"),
	    SlideLin("offset", 0, 1, "seed offset", "S", "s"),
	    SlideLog("scale", 1, "scale", "-", "+"),
	    Action("Time rendering", 't', self.timeRendering)
	)
    def timeRendering(self, *args):
	print "TIME: ", self.w.timeRender(self.vs, 1, 20)
	
    def scene(self, vs):

	print "TEST:"

	vs.map.put(vob.vobs.SolidBackdropVob(java.awt.Color.yellow))

	self.spanImageFactory.paperMaker = self.types[self.type][0](
	    paperOffset = self.offset)
	print self.types[self.type][1]


	layout = ff.view.PageSpanLayout(
		self.scrolls[self.scroll].getCurrent().subSpan(0,2),
		self.spanImageFactory
		)
	#layout.useBg = 0

	nIter = 10
	for i in range(0, nIter):
	    scaled = vs.orthoCS(0, ("X",i), -i, self.x , self.y , self.scale, self.scale)
	    layout.place(vs, scaled)

	self.vs = vs


