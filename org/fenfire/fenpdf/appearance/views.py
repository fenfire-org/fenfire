# 
# Copyright (c) 2003, Tuomas J. Lukka, Matti Katila
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
import alph
from org import fenfire as ff
from org.fenfire.vocab import RDF, CANVAS2D, TREETIME
from org.nongnu import libvob, navidoc

dbg = 0

def p(*s):
    print 'appearance.views.py:: ',s

class Views(navidoc.util.Obs):
    """Encapsulate the creation of the immutable views and connectors used in FenPDF.

    STRICT ENCAPSULATION: NO MEMBERS SHOULD BE ACCESSED
    FROM OUTSIDE, EXCEPT FOR METHODS WITHOUT UNDERSCORES

    Some members should be placed from outside:

	background -- an object implementing placeBackDrop and placeFinalVob

    The idea is that this class may be subclassed to obtain
    versions that use other node types as well (mm graphs etc)
    """

    def __init__(self, fenPDF, papers, eventHandler,
			home):
	self.fenPDF = fenPDF
	self.fen = self.fenPDF.fen # XXX
	self.papers = papers
	self.textStyle = vob.GraphicsAPI.getInstance(
			    ).getTextStyle("sans", 0, 24)
	self.eventHandler = eventHandler
	self.home = home
	self.buoyManager = None

	self.update()

    def update(self):

	self.functional = ff.functional.SuperFunctional1(
	    self.fenPDF.fen.constgraph,
	    vob.util.PrioritizeBackground(
		vob.AbstractUpdateManager.getInstance(),
		0)
	    )
	self.functional.addSuperLazyObs(self)

	self.functional.cache(
	    "node2scrollBlockAnchor", 1000);
	self.functional.cache(
	    "AccursedDispatching", 10);
	self.functional.cache(
	    "NormalDispatching", 500);
	    # "NormalDispatching", 10);
	self.functional.cache(
	    # "PageNodeView", 200, 1, 10); # superlazy cache as it *is* slow.
	    "PageNodeView", 100, 1, 5.5); # superlazy cache as it *is* slow.
	self.functional.cache(
	    # "PageScroll2Layout", 200, 10); # superlazy cache as it *is* slow.
	    "PageScroll2Layout", 100, 1, 5.5); # superlazy cache as it *is* slow.

	self.papers.update(self.functional)

	self.textFunction = self.functional.createFunctionInstance(
	    "ContentFunction",
	    ff.structure.Ff.ContentFunction,
	    [ self.fenPDF.structure.ff ])


	# XXX Figure out get & create stuffs
	# We are essentially creating a DAG of objects and want to allow
	# extensibility anywhere

	self.pageFunc = self.createPageNodeFunction()

	self.normalNodeContentFunction = self.createNormalNodeContentFunction()
	self.accursedNodeContentFunction = self.createAccursedNodeContentFunction()


	self.multiplexerNodeFunction = self.createMultiplexerNodeContentFunction()

	########## The two View2D objects we will use:
        # select view also
        self.areaSelectView2d =  ff.view.AreaSelectingView2D(None)

	# Canvas
	self.canvasview2d = self.createCanvasView2D()

	# PageScroll
	self.pagescrollview2d = self.createPageScrollView2D()

	########## The corresponding node types

	canvasNodeType = ff.view.buoy.NodeType2D(
		    self.canvasview2d, 
		    ff.view.buoy.MainNode2D.MainNode2DFactory(
			self.fenPDF.events.mouse.getMainMouseMultiplexer()))

	pageNodeType = ff.view.buoy.NodeType2DFull(
		    self.pagescrollview2d, 
		    ff.view.buoy.FisheyeMainNode2D.FisheyeMainNode2DFactory(
			self.fenPDF.events.mouse.getMainMouseMultiplexer()))


	########## The connectors 
	# Transclusion connector

	self.transclusionConnector = ff.view.buoy.TransclusionConnector(
		    self.fen, 
		    self.functional,
		    self.textFunction,
		    self.multiplexerNodeFunction)

	self.transclusionConnector.normalNodeNodeType = \
					canvasNodeType

	self.transclusionConnector.pageImageScrollNodeType = \
					pageNodeType

	# Structlink connector

	self.ppConnector = ff.view.buoy.PPConnector(
		    self.fen, 
		    self.canvasview2d, 
		    canvasNodeType,
		    self.multiplexerNodeFunction)

	if 0: # XXX Make this optional in a reasonable way
	    # TreeTime connector

	    self.ttConnector = ff.view.buoy.TTConnector(self.fen, TREETIME.follows)
	    self.ttConnector.normalNodeNodeType = canvasNodeType
	    self.ttConnector.pageImageScrollNodeType = pageNodeType

	    self.connectors = [self.ppConnector, self.transclusionConnector,
				self.ttConnector]
	else:
	    # Optimization
	    self.connectors = [self.ppConnector, self.transclusionConnector]

	self.doubleGeometer = ff.view.buoy.doublegeometer.DoubleGeometer()
	self.geometryConfiguration = \
	    ff.fenpdf.appearance.fenpdfgeometry.FenPDFGeometryConfiguration()

	if self.buoyManager:
	    # Updating - put the foci back
	    # XXX Encaps buoyManager
            iter = self.buoyManager.iterator()
	    old = iter.next().getMainNode()
	    old2 = iter.next().getMainNode()
	    mainNode = self.createMainNode(old.getPlane())
	    mainNode2 = self.createMainNode(old2.getPlane())
	    if dbg:
		p("FOCI: ",old.getFocus(), old2.getFocus(), \
			    mainNode.getFocus(), mainNode2.getFocus())
	    mainNode.setFocus(old.getFocus())
	    mainNode2.setFocus(old2.getFocus())
	    if dbg:
		p("FOCI: ",old.getFocus(), old2.getFocus(), \
			    mainNode.getFocus(), mainNode2.getFocus())
	else:
	    # Start fresh from home
	    mainNode = self.createMainNode(self.home)
	    mainNode2 = self.createMainNode(self.home)

	self.buoyManager = libvob.buoy.impl.MultiBuoyManagerImpl(
	    [mainNode, mainNode2], self.connectors,
	    self.doubleGeometer,
	    self.geometryConfiguration
	    )


    def getBuoyManager(self):
	return self.buoyManager

    def getDoubleGeometer(self):
	return self.doubleGeometer

    def getAreaSelectView2D(self):
        return self.areaSelectView2d

    def setFocus(self, plane):
	self.buoyManager.getActiveBuoyManager(). \
             replaceMainNodeWith(self.createMainNode(plane))

    def createMainNode(self, plane):
	if isinstance(plane, alph.PageScrollBlock):
	    return ff.view.buoy.FisheyeMainNode2D(plane, 
                       self.pagescrollview2d,
                       ff.view.buoy.AbstractMainNode2D.SimpleFocus(0,0),
		       self.fenPDF.events.mouse.getMainMouseMultiplexer())
	else:
	    return ff.view.buoy.MainNode2D(plane, self.canvasview2d, 
		      self.fenPDF.events.mouse.getMainMouseMultiplexer())
        
    def scene(self, vs):
	"""Render the scene, and return the vobScene.
	"""
	if (not self.fenPDF.animation.animate) or (
	    self.fenPDF.animation.reuseVs):
		vob.AbstractUpdateManager.setNoAnimation()
	    

	if self.fenPDF.animation.reuseVs:
            if dbg: p('reuse vobscene')
	    return self.fenPDF.animation.lastVs

	self.fenPDF.animation.generatedNewScene(vs)

  	self.background.placeBackDrop(vs)

	self.buoyManager.draw(vs)

	self.fenPDF.events.mousemenu.render(vs)

	self.background.placeFinalVob(vs)

	# Render buttons after the final vob to get proper blending
	self.fenPDF.events.buttons.render(vs, 0)

        if self.fenPDF.events.eventHandler.eventGrabber != None:
            self.fenPDF.events.eventHandler.eventGrabber.draw(vs)

        #vs.matcher.dumpSimply()

        # Return absolutely None!
        # Otherwise animation would not work, since we use demoframework.
        #return vs
        return None


    def getConnectors(self):
	return self.connectors

    def getMultiplexerNodeContentFunction(self):
	return self.multiplexerNodeFunction

    def getTextStyle(self):
	return self.textStyle

    def getCanvasView2D(self):
	return self.canvasview2d


    def createAccursedNodeContentFunction(self):

	# accursed text
	accursed_text = self.functional.createFunctionInstance(
		"AccursedTextNodeView",
		ff.view.TextNodeView,
		[
			self.textFunction, 
			self.getTextStyle(), java.awt.Color(0.6, 0, 0)
		])

	accursed_page = self.functional.createFunctionInstance(
	    "AccursedPage",
	    ff.view.VobWrapperFunction,
	    [
		    self.pageFunc,
		vob.vobs.ContinuousLineVob(2,
		    [0,0,0 , 1,0,0 , 1,1,0 , 0,1,0], 1, 100, java.awt.Color.red)
	    ])

	accursed_dispatcher = self.functional.createFunctionInstance(
	    "AccursedDispatching",
	    ff.view.DispatchingNodeView,
	    [
			self.textFunction, accursed_text, 
			accursed_page
	    ])
	return accursed_dispatcher

    def createNormalNodeContentFunction(self):

	# normal text
	normal_text = self.functional.createFunctionInstance(
		"NormalTextNodeView",
		ff.view.TextNodeView,
		[
			self.textFunction, 
			self.getTextStyle(), java.awt.Color.black
		])

	if 0: # XXX Make this optional in a reasonable way

	    normal_page = self.functional.createFunctionInstance(
		"AccursedPage",
		ff.view.VobWrapperFunction,
		[
			self.pageFunc,
		    vob.vobs.ContinuousLineVob(2,
			[0,0,0 , 1,0,0 , 1,1,0 , 0,1,0], 1, 100, java.awt.Color.black)
		])


	    normal_dispatcher = self.functional.createFunctionInstance(
		"NormalDispatching",
		ff.view.DispatchingNodeView,
		[
			    self.textFunction, normal_text, 
			    normal_page
		])

	else:
	    # Speed up rendering: don't put lines around everything.

	    normal_dispatcher = self.functional.createFunctionInstance(
		"NormalDispatching",
		ff.view.DispatchingNodeView,
		[
			    self.textFunction, normal_text, 
			    self.pageFunc
		])


	return normal_dispatcher

    def createMultiplexerNodeContentFunction(self):
	return ff.swamp.MultiplexerNodeFunction(
	    self.normalNodeContentFunction.getCallableFunction(),
	    self.accursedNodeContentFunction.getCallableFunction())

    def createCanvasView2D(self):
	canvasview2d = ff.view.CanvasView2D(
			    self.fen, 
			    self.multiplexerNodeFunction)
        self.contentView2d = canvasview2d
	canvasview2d = self.papers.decorateWithPaperView(canvasview2d)
        canvasview2d = ff.view.View2DList([canvasview2d, self.areaSelectView2d])
	irregu = ff.view.IrregularViewportView2D(canvasview2d)
	return irregu

                                                                    

    def chg(self):
	"""Implement ObjObs for PageNodeFunction.
	We clear Accursed and NodeContent caches for the given entry.
	"""
	self.fenPDF.animation.regenerateVS(0)
	vob.AbstractUpdateManager.chgAfter(1000)
	vob.AbstractUpdateManager.setNoAnimation()


    def createPageNodeFunction(self):

	placeholder = ff.util.DummyPlaceable(
	    vob.vobs.RectBgVob(java.awt.Color.red), 200, 200)
	priorityOffset = 10

	pview = self.functional.createFunctionInstance(
	    "PageNodeView",
	    ff.view.PageNodeView,
	    [
		self.textFunction, 
		self.papers.getSpanImageFactory()
	    ])

	return pview

    def createPageScrollView2D(self):

	priorityOffset = 11
	function = self.functional.createFunctionInstance(
	    "PageScroll2Layout",
	    ff.view.PageScroll2LayoutPureFunction,
	    [
			self.papers.getSpanImageFactory()
	    ])

	pagescrollview2d = ff.view.PageScrollView2D(function.getCallableFunction())

        return ff.view.View2DList([pagescrollview2d, self.areaSelectView2d])
 



