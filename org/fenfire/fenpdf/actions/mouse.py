# 
# Copyright (c) 2003, Matti Katila, Benja Fallenstein and Tuomas J. Lukka
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

"""
Mouse actions. These actions implement
various mouse listening things.
"""

from __future__ import nested_scopes
from org import fenfire as ff
from org.fenfire.vocab import CANVAS2D, RDF
import org.nongnu.libvob as vob
import jarray

dbg = 0

def p(*s):
    print 'ff.fenpdf.actions.mouse::', s

# Define the actions.

def defineActions(fenPDF):
    def getMainNode():
	return fenPDF.views.getBuoyManager().getActiveBuoyManager().getMainNode()
    def getOldVS():
	return fenPDF.animation.lastVs
    def getNodeCS(x,y):
        """ Return node's coordinate system by given coordinates
        found from underlying main node.
        """
        box2screen = getMainNode().getBox2Screen()
        plane = getMainNode().getPlane()
        if getMainNode().getView2D().getContentView2D() == None:
            return -1
        return getMainNode().getView2D(). \
               getContentView2D().getNodeCS(getOldVS(),\
               x,y, plane, box2screen, box2screen, 0, None)
    def getNodeOnPlane(x,y):
        """ Return node from underlying main node
        found by given coordinates. Node may be null.
        """
        cs = getNodeCS(x,y)
        if dbg: p('cs', cs)
        if cs < 1: return None
        return getOldVS().matcher.getKey(cs)
    def getNodeCSbyNode(node):
        """ Return given node's coordinate system from underlying main node.
        """
        box2screen = getMainNode().getBox2Screen()
        plane = getMainNode().getPlane()
        if getMainNode().getView2D().getContentView2D() == None:
            return None
        return getMainNode().getView2D(). \
               getContentView2D().getNodeCS(getOldVS(),\
               node, plane, box2screen, box2screen, 0)

    def getOffset(node, x,y):
        """ Get the text offset in screen coordinates if placeable
        is textplaceable.
        """
        viewFunction = fenPDF.views.getMultiplexerNodeContentFunction()
        placeable = viewFunction.f(fenPDF.fen.graph, node)
        if isinstance(placeable, vob.lava.placeable.TextPlaceable):
            cs = getNodeCSbyNode(node)
            ptsIn = jarray.array([x,y,0], 'f')
            ptsOut = jarray.zeros(3, 'f')
            getOldVS().coords.inverseTransformPoints3(cs, ptsIn, ptsOut)
            offs = placeable.getCursorPos(ptsOut[0], ptsOut[1])
            return offs
        return -1

    def getOffsetXYY(node, canvasCS, offset):
        """ Get the x, y top and t bottom of textplaceable offset
        in canvas coordinates.
        """
        viewFunction = fenPDF.views.getMultiplexerNodeContentFunction()
        placeable = viewFunction.f(fenPDF.fen.graph, node)
        retArray = []
        if isinstance(placeable, vob.lava.placeable.TextPlaceable):
            cs = getNodeCSbyNode(node)
            xyy = jarray.zeros(3, 'f')
            placeable.getCursorXYY(offset, xyy)
            ptsIn = jarray.array([xyy[0],xyy[1],0], 'f')
            ptsOut = jarray.zeros(3, 'f')
            getOldVS().coords.transformPoints3(cs, ptsIn, ptsOut)
            getOldVS().coords.inverseTransformPoints3(canvasCS, ptsOut, ptsOut)
            retArray.append(ptsOut[0])
            retArray.append(ptsOut[1])

            ptsIn = jarray.array([xyy[0],xyy[2],0], 'f')
            getOldVS().coords.transformPoints3(cs, ptsIn, ptsOut)
            getOldVS().coords.inverseTransformPoints3(canvasCS, ptsOut, ptsOut)
            #retArray.append(ptsOut[0])
            retArray.append(ptsOut[1])
        return retArray
        
    def getTextWidthEdges(node, canvasCS):
        """ Get left and rigth edges' coordinates of text placeable in
        canvas coordinates.
        """
        viewFunction = fenPDF.views.getMultiplexerNodeContentFunction()
        placeable = viewFunction.f(fenPDF.fen.graph, node)
        retArray = []
        if isinstance(placeable, vob.lava.placeable.TextPlaceable):
            cs = getNodeCSbyNode(node)
            ptsOut = jarray.zeros(3, 'f')
            ptsIn = jarray.array([0,0,0], 'f')
            getOldVS().coords.transformPoints3(cs, ptsIn, ptsOut)
            getOldVS().coords.inverseTransformPoints3(canvasCS, ptsOut, ptsOut)
            retArray.append(ptsOut[0])

            ptsIn = jarray.array([placeable.getWidth(),0,0], 'f')
            getOldVS().coords.transformPoints3(cs, ptsIn, ptsOut)
            getOldVS().coords.inverseTransformPoints3(canvasCS, ptsOut, ptsOut)
            retArray.append(ptsOut[0])
        return retArray

# Inside the action, we have the following symbols usable:
#  fenPDF - the main FenPDF object
#  getMainNode() - get the main node that was hit last
#  getOldVS() - get the previous vobScene.

# Note that the default is that the old vobscene is reused.
# If an action needs regeneration, it has to explicitly call for
# that by fenPDF.animation.regenerateVs()

    ######## Actions to control pan of main nodes etc.

    class ScrollWheelPan(vob.input.RelativeAxisListener):
	"""Scroll vertically using the scroll wheel.
	"""
        def __init__(self):
            self.mul = 13
	def changedRelative(self, d):
	    f = getMainNode().getFocus()
	    fX, fY = f.getPanX(), f.getPanY()
	    f.setPan(fX, fY - d*self.mul/f.zoom)
	    getMainNode().setZoomPan(getOldVS())
            getMainNode().chgFast(getOldVS(), -1)
            fenPDF.animation.reuseVS = 1
            vob.AbstractUpdateManager.chg()    

    class Pan_Fastest(vob.mouse.MousePressListener,
                      vob.mouse.RelativeAdapter):
	"""Pan quickly with an ad hoc formula.
	"""
	def pressed(self, x, y): return self
        def changedRelative(self, x,y):
	    mainNode = getMainNode()
            f = mainNode.getFocus()
	    if isinstance(mainNode, ff.view.buoy.FisheyeMainNode2D):
		x *= 2
		y *= 2
            fX, fY = f.getPanX(), f.getPanY()
            f.setPan(fX-x/f.zoom,
                     fY-y/f.zoom)
            mainNode.setZoomPan(getOldVS())
        def startDrag(self, x,y):
            vob.mouse.RelativeAdapter.startDrag(self,x,y)
            fenPDF.window.setCursor('move')
        def endDrag(self, x,y):
            vob.mouse.RelativeAdapter.endDrag(self,x,y)
            fenPDF.window.setCursor('default')
            

    class Pan_BestQuality(vob.mouse.MouseDragListener):
	"""General panning.

	This class uses the real coordsys which is a great
	advantage for fisheye views, yielding a nicer-quality
	motion.

	This is slow because getXYHit is currently slow - it
	creates the vobscene's internal coordsyses twice
	and there's a lot of them currently.

	When we move to 
	"""
	def startDrag(self, x,y):
	    self.oldxy = getMainNode().getXYHit(getOldVS(), x, y)
	    p('old', self.oldxy)
	def drag(self, x, y):
	    mainNode = getMainNode()
	    curxy = mainNode.getXYHit(getOldVS(), x, y)
	    f = mainNode.getFocus()
	    fX, fY = f.getPanX(), f.getPanY()
	    f.setPan(fX-(curxy[0] - self.oldxy[0]),
		     fY-(curxy[1] - self.oldxy[1]))
	    mainNode.setZoomPan(getOldVS())
        def startDrag(self, x,y):
            vob.mouse.RelativeAdapter.startDrag(self,x,y)
            fenPDF.window.setCursor('move')
        def endDrag(self, x,y):
            vob.mouse.RelativeAdapter.endDrag(self,x,y)
            fenPDF.window.setCursor('default')



    class BrowseClick(vob.mouse.MouseClickListener):
	def clicked(self, x, y):
	    if dbg: p('move pan slow')
	    obj = getNodeOnPlane(x,y)
            if dbg: p('node', obj)
	    fenPDF.uistate.cursor.setAccursed(obj)
	    getMainNode().moveToPoint(int(x),int(y), getOldVS())


            # clear selection!
            fenPDF.uistate.selection.removeSelection()
            fenPDF.views.getAreaSelectView2D().clear()

            viewFunction = fenPDF.views.getMultiplexerNodeContentFunction()
            placeable = viewFunction.f(fenPDF.fen.graph, obj)
            if dbg: p(placeable)
            if obj != None and isinstance(placeable, vob.lava.placeable.TextPlaceable):
                cs = getNodeCS(x,y)
                ptsIn = jarray.array([x,y,0], 'f')
                ptsOut = jarray.zeros(3, 'f')
                getOldVS().coords.inverseTransformPoints3(cs, ptsIn, ptsOut)
                offs = placeable.getCursorPos(ptsOut[0], ptsOut[1])
                fenPDF.uistate.cursor.setCursorOffset(offs)
	    fenPDF.animation.regenerateVS()


    class Zoom(vob.input.RelativeAxisListener):
	def changedRelative(self, x):
	    getMainNode().changeZoom(x)
	    getMainNode().chgFast(getOldVS(), -1)

    class ChangeSize(vob.input.RelativeAxisListener):
	def changedRelative(self, x):
	    getMainNode().changeSize(x)
	    getMainNode().chgFast(getOldVS(), -1)

    class IfSelectNodeOnPlane(vob.mouse.MouseClickListener):
	def clicked(self, x, y):
	    obj = getNodeOnPlane(x,y)
	    fenPDF.uistate.cursor.setAccursed(obj)
	    fenPDF.animation.regenerateVS()

    class MouseMenu(vob.mouse.MouseClickListener):
	def clicked(self, x, y):
	    obj = getNodeOnPlane(x,y)

            viewFunction = fenPDF.views.getMultiplexerNodeContentFunction()
            placeable = viewFunction.f(fenPDF.fen.graph, obj)
            if dbg: p(placeable)
            offs = None
            if obj != None and isinstance(placeable, vob.lava.placeable.TextPlaceable):
                cs = getNodeCS(x,y)
                ptsIn = jarray.array([x,y,0], 'f')
                ptsOut = jarray.zeros(3, 'f')
                getOldVS().coords.inverseTransformPoints3(cs, ptsIn, ptsOut)
                offs = placeable.getCursorPos(ptsOut[0], ptsOut[1])
                p(offs)

	    fenPDF.uistate.menu.originateFromMainNode(getMainNode(), obj, offs)

	    fenPDF.events.mousemenu.showList(x,y,'mainmenu')
	    fenPDF.animation.regenerateVS()
	    vob.AbstractUpdateManager.setNoAnimation()

    ####### Actions to control nodes on main node plane
	    
    class NodeMover(vob.mouse.RelativeAdapter,
		    vob.mouse.MousePressListener):
	def pressed(self, x,y):
	    self.set(getNodeOnPlane(x,y),x,y)
	    return self
	def set(self, node, x,y):
            self.evX, self.evY = x, y
	    self.node = node
	def changedRelative(self, x,y):
	    f = getMainNode().getFocus()
	    fX, fY = f.getPanX(), f.getPanY()

            if self.node == None:
                p('No node!!!')
                return

	    g = fenPDF.fen.graph
            self.canvas =  fenPDF.structure.canvas2d.getCanvas(self.node)
            if self.canvas != None:
                self.startPointX = ff.util.RDFUtil.getFloat(g, self.node, CANVAS2D.x)
                self.startPointY = ff.util.RDFUtil.getFloat(g, self.node, CANVAS2D.y)
            else:
                self.startPointX = self.startPointY = None
            
            # find out offset
            viewFunction = fenPDF.views.getMultiplexerNodeContentFunction()
            placeable = viewFunction.f(fenPDF.fen.graph, self.node)
            if isinstance(placeable, vob.lava.placeable.TextPlaceable):
                cs = getNodeCS(self.evX,self.evY)
                ptsIn = jarray.array([self.evX,self.evY,0], 'f')
                ptsOut = jarray.zeros(3, 'f')
                getOldVS().coords.inverseTransformPoints3(cs, ptsIn, ptsOut)
                offs = placeable.getCursorPos(ptsOut[0], ptsOut[1])

                if fenPDF.useExtension('TextCloud') and fenPDF.uistate.textScissors.isScissored(self.node, offs):
                    fenPDF.uistate.textScissors.fixOffset()
                    node = ff.swamp.Nodes.N()
                    enf = fenPDF.structure.ff \
                          .deleteRegion(self.node,
                                        fenPDF.uistate.textScissors.begin.offset,
                                        fenPDF.uistate.textScissors.end.offset)
                    fenPDF.structure.ff.setContent(node, enf)
                    xy = getMainNode().getXYHit(getOldVS(), self.evX, self.evY)
                    fenPDF.structure.canvas2d.placeOnCanvas(self.canvas, node, xy[0], xy[1])

                    grabber = ff.fenpdf.actions.eventgrabs.TextCloud(
                        fenPDF, self.canvas, self.node,
                        fenPDF.uistate.textScissors.begin.offset,
                        node, self.evX, self.evY)
                    fenPDF.uistate.textScissors.clear()
                else:
                    grabber = ff.fenpdf.actions.eventgrabs.NodeMoverActionGrabber(
                        fenPDF, self.startPointX, self.startPointY, self.node, self.canvas)
            else:
                grabber = ff.fenpdf.actions.eventgrabs.NodeMoverActionGrabber(
                    fenPDF, self.startPointX, self.startPointY, self.node, self.canvas)
            fenPDF.events.eventHandler.eventGrabber = grabber
                    
	    fenPDF.animation.noAnimation()
            fenPDF.animation.regenerateVS()

            # clear selection!
            fenPDF.uistate.selection.removeSelection()

            # clear selection!
            fenPDF.uistate.selection.removeSelection()

    ####### Actions to control selection (Ctrl pressed)

    class SelectArea(vob.mouse.MouseDragListener,
		     vob.mouse.MousePressListener):
	def pressed(self, x,y):
	    """ dumb version of listener """
	    return self
	def startDrag(self, x,y):
	    self.oldxy = getMainNode().getXYHit(getOldVS(), x, y)
	    self.node = getNodeOnPlane(x,y)
            if self.node != None:
                offs = getOffset(self.node, x,y)
                if dbg: p('old', self.oldxy, self.node, offs)
                fenPDF.uistate.textScissors.setBegin(self.node, offs)
	def drag(self, x, y):
            if self.node != None:
                offs = getOffset(self.node, x,y)
                fenPDF.uistate.textScissors.setEnd(self.node, offs)
                canvasCS = ff.view.CanvasView2D.getContainerCS(getOldVS(),
                             getMainNode().getBox2Screen())
                # XXX very bad encapsulation!
                canvasCS = getOldVS().matcher.getCS(getMainNode(). \
                            getBox2Screen(), 'AreaSelectingView_concat')
                if dbg: p(getOffsetXYY(self.node, canvasCS, offs))

            select = fenPDF.views.getAreaSelectView2D()
            select.setCurrentPlane(getMainNode().getPlane())
            select.clear()
	    xy = getMainNode().getXYHit(getOldVS(), x,y)
            if not fenPDF.uistate.textScissors.isScissored(self.node):
                select.setArea(self.oldxy[0], self.oldxy[1], xy[0], xy[1])
            else:
                if dbg: p('begin',fenPDF.uistate.textScissors.begin.offset,
                          'end', fenPDF.uistate.textScissors.end.offset)
                begin = getOffsetXYY(self.node, canvasCS,
                                     fenPDF.uistate.textScissors.begin.offset)
                end = getOffsetXYY(self.node, canvasCS,
                                   fenPDF.uistate.textScissors.end.offset)
                edge = getTextWidthEdges(self.node, canvasCS)
                select.setTextArea(begin[0], begin[1], begin[2],
                              end[0], end[1], end[2],
                              edge[0], edge[1])
            
            fenPDF.uistate.selection.setArea(
                (self.oldxy[0], self.oldxy[1], xy[0], xy[1]),
                getMainNode())

	    getMainNode().chgFast(getOldVS(), -1)
	def endDrag(self, x,y):
	    self.drag(x,y)


    class GrabPressIfSelectedArea(vob.mouse.MousePressListener):
        def __init__(self, notSelected, selected):
            self.normal = notSelected
            self.selected = selected
        def pressed(self, x, y):
            main = getMainNode()
            xy = jarray.zeros(2, 'f')
	    xy = getMainNode().getXYHit(getOldVS(), x,y)

            
            if fenPDF.uistate.selection.isInsideSelect(xy[0], xy[1]):
                p('selected')
                select = fenPDF.views.getAreaSelectView2D()
                select.clear()
                # text selection needs still adjustments.
                #offs = getOffset(node, x,y)
                node = getNodeOnPlane(x,y)
                if node == None:
                    main = getMainNode()
                    area = fenPDF.uistate.selection.getXYWH()
                    enf = main.getView2D().getSelectedObject(
                        main.getPlane(), *area)
                    p('transclude', enf)
                    if enf != None:
                        node = ff.swamp.Nodes.N()
                        fenPDF.structure.ff.setContent(node, enf)
                    else: return self.normal
                if node == None: return self.normal
                self.selected.set(node, x,y)
                return self.selected
            return self.normal


    ##############################
    ####   Double view action  ###
    ##############################


    class AdjustDoubleViewSeparator(vob.mouse.MouseDragListener,
                                    vob.mouse.RelativeAdapter):
	def pressed(self, x,y):
	    """ dumb version of listener """
	    return self
        def changed(self, x,y):
            pass

    #######################################################################
    #######################################################################
    #####                                                             #####
    #####   Actions for buoys.                                        #####
    #####                                                             #####
    #######################################################################
    #######################################################################

	
    class BuoyMouseMenu(vob.mouse.MouseClickListener):
	def clicked(self, x, y):
	    buoymanager = fenPDF.views.getBuoyManager()
	    buoy = buoymanager.getLastFoundBuoy()
            p('hit',buoy)
	    fenPDF.uistate.menu.originateFromBuoy(buoy)
	    fenPDF.events.mousemenu.showList(x,y, 'buoymenu')
	    fenPDF.animation.regenerateVS()
	    vob.AbstractUpdateManager.setNoAnimation()

    class BuoyFollowClick(vob.mouse.MouseClickListener):
	def clicked(self, x,y):
	    buoymanager = fenPDF.views.getBuoyManager()
            buoy = buoymanager.getLastFoundBuoy()
	    p('hit',buoy)
	    buoymanager.getActiveBuoyManager().moveFocusTo(buoy)
	    fenPDF.animation.regenerateVS()

    # Return a map of symbols
    res = { }
    for sym in locals().keys():
	if sym[0].isupper():
	    res[sym] = locals()[sym]
    return res

class MouseActions:
    def __init__(self, fenPDF):
	actions = defineActions(fenPDF)
	for sym in actions.keys():
	    setattr(self, sym, actions[sym])


