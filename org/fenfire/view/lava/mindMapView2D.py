# 
# Copyright (c) 2003, Matti J. Katila, Asko Soukka
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
import java, jarray
from java.lang import Math

import vob
import org.fenfire as ff
from org.fenfire.vocab import STRUCTLINK, RDF
from org.fenfire.vocab.lava import MINDSTRUCT
from org.fenfire.util.lava import Traversals
from org.fenfire.fenmm import MindNet, MMGeometry

def p(*s):
    print 'mindMapView2D', s

dbg = 0

class MindMapMainNode2D(ff.view.buoy.MainNode2D):
    def __init__(self, fen, centered, context, view2d, controller):
	ff.view.buoy.MainNode2D.__init__(self, centered, view2d, controller);
        self.fen = fen
        self.context = context
	self.structLink = ff.structure.StructLink.create(self.fen.graph)

        style = self.context.style

        nodeview = self.context.functional.createFunctionInstance(
            "TextNodeView",
            ff.view.TextNodeView,
            [self.context.contentFunction, style, java.awt.Color.black, 250.])

        # nodeview for normal nodes
        normalf = self.context.functional.createFunctionInstance(
            "NormalTextNodeView",
            ff.fenmm.WhiteNodeView,
            [nodeview, java.awt.Color(.6, .6, 0)])
        
        # nodeview for accursed nodes
        accursedf = self.context.functional.createFunctionInstance(
            "AccursedTextNodeView",
            ff.fenmm.WhiteNodeView,
            [nodeview, java.awt.Color(.9, .9, .9)])
            
        self.multiplexer = ff.swamp.MultiplexerNodeFunction(
            normalf.getCallableFunction(),
            accursedf.getCallableFunction())

        self.floatingView = MindMapView2D(fen, context)
        self.floatingView.multiplexer = self.multiplexer
        self.floatingView.filletLength = 40
        self.floatingView.filletWidth = 25
        self.floatingView.initRotation = 0
        self.floatingView.depth = 1
        self.floatingView.geometry.init(40, 25, 0, 1)
        
    def renderMain(self, vs, into):
        ff.view.buoy.MainNode2D.renderMain(self, vs, into)
        self.floatingView.matchingParent = into

        accursed = self.context.c.getAccursed()
        # adding accursed to multiplexer
        set = java.util.HashSet()
        if accursed:
            set.add(accursed)
            self.multiplexer.setMultiplexerNodes(set)
        else: self.multiplexer.setMultiplexerNodes(set)

        floating = self.floatingBuffer(vs, into, self.context.components)
        i = floating.keySet().iterator()

        while i.hasNext():
            node = i.next()
            if self.structLink.isLinked(node):
                self.floatingView.net = floating.get(node)
                self.floatingView.drawMindMap(vs)
            else:
                self.place(vs, node, floating.get(node))
            
    def floatingBuffer(self, vs, into, buffer):
        map = java.util.HashMap()
        if len(buffer) == 0: return map

        d = 360.0/len(buffer)
        for i in range(len(buffer)):
            if self.structLink.isLinked(buffer[i]):
                path = java.util.ArrayList(1)
                path.add(buffer[i])
                cs = self.singleCS(vs, into, buffer[i], Math.toRadians(i*d))
                cs = vs.coords.translate(cs, 0,0, 100)
                vs.matcher.add(cs, "Floating_"+buffer[i])
                map.put(buffer[i], self.floatingView.geometry.buildMindNet(vs, cs, path))
            else:
                map.put(buffer[i], self.singleCS(vs, into, buffer[i], Math.toRadians(i*d)))
                
        return map
            
    def singleCS(self, vs, into, node, angle):
        angle += 0.5 * Math.PI
        angle %= 2.0 * Math.PI
        wi, h = vs.size.width, vs.size.height
        a, b = wi/2.3, h/2.3
        x0,y0 = wi/2.0, h/2.0
        bx, by = 1, 1
        
        e = Math.sqrt(1 - b**2/a**2)

        r = a * Math.sqrt( (1 - e**2) / ( 1 - e**2 * Math.cos(angle)**2)) 
        
        x = r * Math.cos(angle) + x0
        y = -r * Math.sin(angle) + y0
         
        cs = vs.orthoBoxCS(into, node+"_FILLET", -100, x-bx/2,y-by/2, 1,1, bx,by)
        return cs

    def place(self, vs, node, into):
        p = self.multiplexer.f(self.fen.graph, node)
        x = - p.getWidth()/2.
        y = - p.getHeight()/2.
        vs.matcher.add(self.floatingView.matchingParent, into, node)
        cs = vs.orthoBoxCS(into,node,0, x, y, 1,1, p.getWidth(), p.getHeight())
        p.place(vs, cs)

#        csEnlarged = vs.coords.orthoBox(into,0, x*2, y*2, 1,1, p.getWidth()*2, p.getHeight()*2)
#        vs.matcher.add(cs, csEnlarged, "Enlarged_"+node)

#        vs.coords.activate(csEnlarged)
        vs.coords.activate(cs)

class MindMapView2D(ff.view.View2D):
    def __init__(self, fen, context):
        self.fen = fen
        self.context = context

        self.multiplexer = self.context.multiplexer

        self.depth = 5
        self.filletWidth = 20
        self.filletLength = 150
        self.initRotation = 0
        
        self.geometry = MMGeometry(self.fen.graph, self.filletLength, self.filletWidth,
                                   self.initRotation, self.depth)

        # fillet set up
        self.angle = 1
        self.thick = 1
        self.drawEdge = 0
        self.drawInside = 1
        self.drawInsideColor = java.awt.Color.blue
        self.depthColor = 1
        self.lines = 0
        self.ellipses = 1
        self.stretched = 1
        self.curvature = 0
        self.sectors = 1
        self.fillets = 1
        self.dice = 10
        self.fillet3d = 1
        self.blend3d = 0
        self.linewidth = 1
        self.perspective = 0
        self.texture = 0
        self.dicelen = 100
        self.tblsize = 20
        self.mode = 0

        self.oldCenter = None

    def render(self, vs, node,
               matchingParent, box2screen, box2plane):
        self.matchingParent = matchingParent

	paper2box = vs.invertCS(box2plane, "minMap_INv")
	paper2screen = vs.concatCS(box2screen, 'mindMap_CONCAT',
                                   paper2box)
        # XXX
        self.zoomPanCS = paper2screen
        
        path = Traversals.findShortestPath((self.oldCenter or node),
                                          STRUCTLINK.linkedTo,
                                          node, self.fen.graph)
        if not path or path.size() == 0:
            path = java.util.ArrayList(1)
            path.add(node)
         
        self.geometry.init(self.filletLength, self.filletWidth,
                           self.initRotation, self.depth)
        self.net = self.geometry.buildMindNet(vs, paper2screen, path)
        self.drawMindMap(vs)
        self.oldCenter = node

    def drawMindMap(self, vs):
        i = self.net.iterator()
        while i.hasNext():
            node = i.next()
            pl = self.net.getPlace(node)
            if pl == None: continue
            c = [ pl.cs ]

            it = self.net.iterator(node)
            while it.hasNext():
                n = it.next()
                pl2 = self.net.getPlace(n)
                if pl2 == None: continue
                c.append(pl2.cs)
                if dbg: p('info:', pl2.cs, pl2.x, pl2.y)
                
            if dbg: p('Fillet coordinates:', c)
            def pc(conns, cs):
                if self.context.main.getPlane() == node:
                    vs.put(vob.putil.misc.getDListNocoords("Color 1 0 0"))
                vs.put(conns, cs+c)

            # draw fillets
            vob.fillet.light3d.drawFillets(self, vs, pc)

            # draw text etc..
            cs = vs.coords.translate(pl.cs, 0,0, -100)
            vs.matcher.add(self.matchingParent, cs, node)

            depth = self.net.getDepth(node)
            if depth < self.depth:
                self.putNodeContent(vs, node, cs)
            else:
                p = self.multiplexer.f(self.fen.graph, node) 
                cs = vs.orthoBoxCS(cs, node, 0, 0, 0, 0, 0, 0, 0)
                p.place(vs, cs)

    def putNodeContent(self, vs, node, into):
        # scaling after depth
        depth = self.net.getDepth(node)
        scale = ff.fenmm.MMGeometry.getScale(depth)
        textScale = ff.fenmm.MMGeometry.getTextScale(depth)

        p = self.multiplexer.f(self.fen.graph, node) 

        x = (self.filletWidth*scale)/2. - (p.getWidth()*textScale)/2.
        y = (self.filletWidth*scale)/2. - (p.getHeight()*textScale)/2.

        cs = vs.orthoBoxCS(into, node,0, x,y, textScale, textScale, p.getWidth(), p.getHeight())
        p.place(vs, cs)


        ## XXX This could work, but setting TextCursor after selecting, would be difficult  
        # set the active coordinate system coverage  
#        if x > -self.filletWidth/2:  
#            x = -self.filletWidth/2
#            w = self.filletWidth*scale*2
#        else: w = p.getWidth()*textScale  
#        if y > -self.filletWidth/2:  
#            y = -self.filletWidth/2
#            h = self.filletWidth*scale*2
#        else: h = p.getHeight()*textScale  
          
#        csEnlarged = vs.coords.orthoBox(into,0, x,y, textScale, textScale, w, h)
#        vs.matcher.add(cs, csEnlarged, "Enlarged_"+node)
        
#        vs.coords.activate(csEnlarged)
        vs.coords.activate(cs)
