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


import java
import vob
from org import fenfire as ff

dbg = 0

def p(*s):
    print 'ff.fenpdf.appearance.papers::', s

class Papers:
    """Encapsulate the selection of background drawing methods.
    """
    def __init__(self, fenPDF):
	self.fenPDF = fenPDF

	# Boot the indices to existing paperMakers
	self.booting = 1
	self.useBg = 0
	self.whitePaperMakerIndex = -1
	self.adjustPaperMakerIndex(1)
	self.useBg = 1
	self.fancyPaperMakerIndex = -1
	self.adjustPaperMakerIndex(1)
	self.booting = 0

	# ScrollBlockImagers do not change -
	# they also cache the textures, better
	# keep them here.
	self.scrollBlockImager = ff.spanimages.gl.PageScrollBlockImager()


    def toggleBg(self, useBg = -1):
	"""Toggle (or set) whether to use background textures.
	"""
	if useBg == -1:
	    self.useBg = not self.useBg
	else:
	    self.useBg = useBg

	self.fenPDF.views.update()
	
    def adjustPaperMakerIndex(self, delta):
	if not self.useBg:
	    self.whitePaperMakerIndex += delta
	    self.whitePaperMakerIndex %= \
		len(ff.spanimages.gl.papermakers.whitePaperMakers)
	    # If it's unavailable, take the next one
	    if ff.spanimages.gl.papermakers\
		.whitePaperMakers[self.whitePaperMakerIndex][0]\
		    == None: 
		    print "Not available: ", \
		      ff.spanimages.gl.papermakers\
		           .whitePaperMakers[
			      self.whitePaperMakerIndex][0]
		    self.adjustPaperMakerIndex(delta)
	else:
	    self.fancyPaperMakerIndex += delta
	    self.fancyPaperMakerIndex %= \
		len(ff.spanimages.gl.papermakers.fancyPaperMakers)
	    # If it's unavailable, take the next one
	    if ff.spanimages.gl.papermakers\
		.fancyPaperMakers[self.fancyPaperMakerIndex][0]\
		    == None: 
		    print "Not available: ", \
		      ff.spanimages.gl.papermakers\
		           .fancyPaperMakers[
			      self.fancyPaperMakerIndex][0]
		    self.adjustPaperMakerIndex(delta)

	if dbg: p("Pmis: ",
		self.useBg,
		self.whitePaperMakerIndex,
		self.fancyPaperMakerIndex)

	if not self.booting:
	    self.fenPDF.views.update()

    def update(self, functional):
	"""Update the objects inside this class.

	For functioninstances, use the given functional
	object.
	"""

	# The function to create papers.
	if self.useBg:
	    self.paperFunc = functional.createFunctionInstance(
		"PaperMill",
		ff.util.PaperMillFunction,
		[ vob.paper.papermill.ThePaperMill(),
		    java.lang.Boolean(1)
		  ])
	else:
	    self.paperFunc = functional.createFunctionInstance(
		"ConstantPaper",
		ff.functional.ConstantFunction,
		[ vob.gl.SpecialPapers.solidPaper(java.awt.Color.white)])

	# self.spanImageFactory.poolManager.DICELENGTH = 10
	makerEntry = None
	if self.useBg:
	    makerEntry = ff.spanimages.gl.papermakers\
			.fancyPaperMakers[
				self.fancyPaperMakerIndex]
	else:
	    makerEntry = ff.spanimages.gl.papermakers\
			.whitePaperMakers[
			    self.whitePaperMakerIndex]
	p("Set paper maker: ", makerEntry)

	assert makerEntry != None

	if makerEntry[0] != None:
	    args = makerEntry[0]()
	    #
	    # Kludgeish: currently Functional can't
	    # create both Java and Jython function instances
	    # with the same method; we hope we can later
	    # improve that.
	    #
	    if args[0].__class__ == java.lang.Class:
		paperMaker = functional.createFunctionInstance(
		    "PaperMaker", *args)
	    else:
		paperMaker = functional.createFunctionInstance_Jython(
		    "PaperMaker", *args)
	    if dbg: print "Maker:", paperMaker
	else:
	    raise 'FIX ME! Got None makeEntry.'

	self.spanImageFactory = \
	    functional.createFunctionInstance(
		"SpanImageFactory",
	        ff.spanimages.gl.DefaultSpanImageFactory,
		[self.scrollBlockImager,
		paperMaker])

    def getSpanImageFactory(self):
	return self.spanImageFactory

    def decorateWithPaperView(self, view2d):
	paperView = ff.view.PaperView2D(
	    self.paperFunc.getCallableFunction(), view2d)
	paperView.paperScaling = .1
	p("decorateWithPaperView", self.useBg)
	return paperView



