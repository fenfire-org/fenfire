# 
# Copyright (c) 2003, Matti J. Katila
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


import java, jarray
import org.nongnu.libvob as vob
import org.fenfire as ff

dbg = 0

def p(*s):
    print 'ff.fenpdf.actions.eventgrabs::', s

class Grabber(ff.fenpdf.events.MouseEventGrabber):
    def draw(self, vs): return

    def createFlyingNode(self, oldVS, node, ev, fenPDF):
        transCS = oldVS.translateCS(0,'NODE_CS', ev.getX(), ev.getY())
        viewFunction = fenPDF.views.getMultiplexerNodeContentFunction()
        placeable = viewFunction.f(fenPDF.fen.graph, node)
        if placeable == None: return
        nodeCS = oldVS.orthoBoxCS(transCS,
                                  node, -1000,0,0, 1,1,
                                  placeable.getWidth(),
                                  placeable.getHeight())
        placeable.place(oldVS, nodeCS)

    def setFlyingNodeZoom(self, vs, node, fenPDF, ev):
        buoymanager = fenPDF.views.getBuoyManager()
        manager = buoymanager.findTopmostBuoyManager(vs, ev.getX(), ev.getY())
        mainNode = manager.getMainNode()
        if mainNode != None:
            zoom = mainNode.getFocus().getZoom()
        else:
            zoom = 1

        cs = vs.matcher.getCS('NODE_CS')
        cs = vs.matcher.getCS(cs, node)
        viewFunction = fenPDF.views.getMultiplexerNodeContentFunction()
        placeable = viewFunction.f(fenPDF.fen.graph, node)
        vs.coords.setOrthoBoxParams(cs, -1000, 0,0, zoom, zoom,
                                    placeable.getWidth(), placeable.getHeight())

    def mainNodeOfEvent(self, vs, ev, fenPDF):
        buoymanager = fenPDF.views.getBuoyManager()
        manager = buoymanager.findTopmostBuoyManager(vs, ev.getX(), ev.getY())
        if manager == None: return None
        return manager.getMainNode()


    def isCanvas(self, mainNode, fenPDF):
        return  mainNode != None and fenPDF.structure.canvas2d.isCanvas(mainNode.getPlane())

    def getNodeCSbyNode(self, mainNode, fenPDF, vs, node):
        box2screen = mainNode.getBox2Screen()
        plane = mainNode.getPlane()
        if mainNode.getView2D().getContentView2D() == None:
            return None
        return mainNode.getView2D(). \
               getContentView2D().getNodeCS(vs,\
               node, plane, box2screen, box2screen, 0)



class NodeMoverActionGrabber(Grabber):
    def __init__(self, fenpdf, x,y,node, canvas):
        self.fenPDF = fenpdf
        self.x, self.y = x,y
        self.node = node
        self.canvas = canvas
        self.ev = None
        self.drewMainNode = None

    def draw(self, vs):
        if self.ev == None: return
        
        mainNode = self.mainNodeOfEvent(vs, self.ev, self.fenPDF)
        if not self.isCanvas(mainNode, self.fenPDF):
            self.createFlyingNode(vs, self.node, self.ev, self.fenPDF)
        self.drewMainNode = mainNode
        
    def eventGrabber(self, ev, vs):
        self.ev = ev

        mainNode = self.mainNodeOfEvent(vs, ev, self.fenPDF)
        self.fenPDF.window.setCursor('move');

        # if there has been some kind of change!
        if self.drewMainNode != mainNode:
            self.fenPDF.structure.canvas2d.removeNode(self.node)
            if self.isCanvas(mainNode, self.fenPDF):
                canvas = mainNode.getPlane()
                self.fenPDF.structure.canvas2d.placeOnCanvas(canvas, self.node, ev.getX(), ev.getY())
            self.fenPDF.animation.regenerateVS()
        else:
            if self.isCanvas(mainNode, self.fenPDF):
                # set coordinates if inside of some foci
                xy = mainNode.getXYHit(vs, ev.getX(), ev.getY())
                self.fenPDF.structure.canvas2d.setCoordinates(self.node, xy[0], xy[1])
                mainNode.chgFast(vs, -1)
            else:
                vs.coords.setTranslateParams(vs.matcher.getCS('NODE_CS'),
                                             ev.getX(), ev.getY())
            self.fenPDF.animation.reuseVS = 1
            self.fenPDF.animation.animate = 0

        if ev.getType() != ev.MOUSE_DRAGGED:
            if not self.isCanvas(mainNode, self.fenPDF):
                if self.canvas != None:
                    self.fenPDF.structure.canvas2d.placeOnCanvas(self.canvas, self.node, self.x, self.y)
                else:
                    self.fenPDF.structure.ff.deleteContent(self.node)
                p('placed on fen, using default')

            # remove selection!
            self.fenPDF.uistate.selection.removeSelection()
            
            # temporarily solution, flush drags away...
            self.fenPDF.events.mouse.mainMouse.flush()
            self.fenPDF.events.eventHandler.eventGrabber = None
            self.fenPDF.animation.regenerateVS()
            self.fenPDF.window.setCursor('default');

        vob.AbstractUpdateManager.chg()

class TextCloud(Grabber):
    """ Very buggy and unhealthy coded text cloud.

    Text has been put into canvas when coming in here.
    """
    def __init__(self, fenpdf, canvas, origNode, origOffset, node,x,y):
        self.fenPDF = fenpdf
        self.originalCanvas = canvas
        self.originalNode = origNode
        self.originalOffset = origOffset
        self.node = node
        self.x, self.y = x,y

        """ tipNode has content for a tip, i.e., dragged text
        cloud inserted into the underlying node's text.
        'Final content if you drop the text now'
        """
        self.tipNode = ff.swamp.Nodes.N()
        self.tipOffset = None

        self.ev = None
        self.drewMainNode = None


    def getNodeOnPlane(self, vs, ev, ignoreNode):
        mainNode = self.mainNodeOfEvent(vs, ev, self.fenPDF)
        if self.isCanvas(mainNode, self.fenPDF):
            cs = mainNode.getBox2Screen()
            ints = vs.coords.getAllCSAt(cs, ev.getX(), ev.getY())
            #p('ints:', ints)
            for i in ints:
                node = vs.matcher.getKey(i)
                if node != ignoreNode:
                    return node
        else:
            raise 'not on canvas'

    def draw(self, vs):
        if self.ev == None: return
        
        mainNode = self.mainNodeOfEvent(vs, self.ev, self.fenPDF)
        if not self.isCanvas(mainNode, self.fenPDF):
            self.createFlyingNode(vs, self.node, self.ev, self.fenPDF)
        self.drewMainNode = mainNode

        viewFunction = self.fenPDF.views.getMultiplexerNodeContentFunction()
        placeable = viewFunction.f(self.fenPDF.fen.graph, self.tipNode)
        cs = vs.orthoBoxCS(0, 'TipNode', -1000,50,50, 1,1,
                           placeable.getWidth(), placeable.getHeight()) 
        placeable.place(vs, cs)


    def eventGrabber(self, ev, vs):
        self.ev = ev

        mainNode = self.mainNodeOfEvent(vs, ev, self.fenPDF)

        # if there has been some kind of change!
        if self.drewMainNode != mainNode:
            self.fenPDF.structure.canvas2d.removeNode(self.node)
            if self.isCanvas(mainNode, self.fenPDF):
                canvas = mainNode.getPlane()
                self.fenPDF.structure.canvas2d.placeOnCanvas(canvas, self.node, self.x, self.y)
            p('regenerate')
            self.fenPDF.animation.regenerateVS()
        else:
            if self.isCanvas(mainNode, self.fenPDF):
                # set coordinates if inside of some foci
                xy = mainNode.getXYHit(vs, ev.getX(), ev.getY())
                self.fenPDF.structure.canvas2d.setCoordinates(self.node, xy[0], xy[1])
                mainNode.chgFast(vs, -1)
            else:
                vs.coords.setTranslateParams(vs.matcher.getCS('NODE_CS'),
                                             ev.getX(), ev.getY())
            self.fenPDF.animation.reuseVS = 1
            self.fenPDF.animation.animate = 0

        # if there are a change by offset!
        if self.isCanvas(mainNode, self.fenPDF):
            node = self.getNodeOnPlane(vs, ev, self.node)

            if node != None:
                #p('there\'s a node')
                viewFunction = self.fenPDF.views.getMultiplexerNodeContentFunction()
                placeable = viewFunction.f(self.fenPDF.fen.graph, node)
                if isinstance(placeable, vob.lava.placeable.TextPlaceable):
                    #p('textplaceable')
                    cs = self.getNodeCSbyNode(mainNode, self.fenPDF, vs, node)
                    ptsIn = jarray.array([ev.getX(),ev.getY(),0], 'f')
                    ptsOut = jarray.zeros(3, 'f')
                    vs.coords.inverseTransformPoints3(cs, ptsIn, ptsOut)
                    offset = placeable.getCursorPos(ptsOut[0], ptsOut[1])
                    if self.tipOffset != offset:
                        p('regenerate')
                        self.fenPDF.animation.regenerateVS()
                    self.tipOffset = offset
            
                    self.fenPDF.structure.ff.setContent(self.tipNode, \
                         self.fenPDF.structure.ff.getContent(node))
                    self.fenPDF.structure.ff.insert(self.tipNode, offset, \
                         self.fenPDF.structure.ff.getContent(self.node))
                else: self.hideTipNode(vs)
            else: self.hideTipNode(vs)


        if ev.getType() != ev.MOUSE_DRAGGED:
            # XXX others also ?
            self.fenPDF.structure.canvas2d.removeNode(self.node)
            if not self.isCanvas(mainNode, self.fenPDF):
                self.fenPDF.structure.ff.insert(self.originalNode, self.originalOffset,
                                                self.fenPDF.structure.ff.getContent(self.node))
            else:
                node = self.getNodeOnPlane(vs, ev, self.node)
                viewFunction = self.fenPDF.views.getMultiplexerNodeContentFunction()
                placeable = viewFunction.f(self.fenPDF.fen.graph, node)

                # check if there are text under the mouse cursor
                if node != None and isinstance(placeable, vob.lava.placeable.TextPlaceable):
                    self.fenPDF.structure.canvas2d.removeNode(self.node)
                    cs = self.getNodeCSbyNode(mainNode, self.fenPDF, vs, node)
                    ptsIn = jarray.array([ev.getX(),ev.getY(),0], 'f')
                    ptsOut = jarray.zeros(3, 'f')
                    vs.coords.inverseTransformPoints3(cs, ptsIn, ptsOut)
                    offset = placeable.getCursorPos(ptsOut[0], ptsOut[1])
                    self.fenPDF.structure.ff.insert(node, offset, \
                        self.fenPDF.structure.ff.getContent(self.node))
                    
                # there's no text so just drop the text on canvas
                else:
                    xy = mainNode.getXYHit(vs, ev.getX(), ev.getY())
                    self.fenPDF.structure.canvas2d.placeOnCanvas(mainNode.getPlane(), self.node, xy[0], xy[1])


            # temporarily solution, flush drags away...
            self.fenPDF.events.mouse.mainMouse.flush()
            self.fenPDF.events.eventHandler.eventGrabber = None
            self.fenPDF.animation.regenerateVS()

        vob.AbstractUpdateManager.chg()
            
    def hideTipNode(self, vs):
        cs = vs.matcher.getCS('TipNode')
        if cs < 2: return
        vs.coords.setOrthoBoxParams(cs, 0, 0,0,0,0,0,0)

