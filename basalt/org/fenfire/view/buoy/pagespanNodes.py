# 
# Copyright (c) 2003, Tuomas J. Lukka
# 
# This file is part of fenfire.
# 
# fenfire is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
# 
# fenfire is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
# Public License for more details.
# 
# You should have received a copy of the GNU General
# Public License along with fenfire; if not, write to the Free
# Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
# MA  02111-1307  USA
# 
# 


from __future__ import nested_scopes

"""A pagespan scroll shown in a buoy view.

An interesting point: if this is from a transclusion, we
want to show the *whole* scrollblock always, in small scale.
If this is from a xanadu link, we want to show the linked-to
area.
"""

# The coordinate systems used inside a single node/mainnode:
# all are matching-children of the "into" coordinate system.
# 
# "LAYOUT" - the coordinate system in which the pagespan layout
#            is rendered into.
# "VIEWPORT" - the edges of the viewport, w.r.t. LAYOUT

import jarray
import java

from org import fenfire 
from org.nongnu import alph 
from org.nongnu import libvob as vob



def placeFramed(vs, data, paperCS, viewport, cull=-1, linemode = 0):
    class FrameR(java.lang.Runnable):
	def run(rself):
	    vs.map.put(data.irregu.frame, paperCS, viewport)
    class ContentR(java.lang.Runnable):
	def run(rself):
	    vs.map.put(data.irregu.content, paperCS, viewport)
    class LayoutR(java.lang.Runnable):
	def run(rself):
	    data.layout.place(vs, paperCS, .05, 100, cull)
    
    # vob.gl.Stencil.drawStenciled(vs, ContentR(), None, FrameR(), LayoutR(), 1)

    if linemode:
	vs.put(vob.gl.GLCache.getCallList("""
	    PushAttrib POLYGON_BIT
	    PolygonMode FRONT_AND_BACK LINE
	"""))

    LayoutR().run()

    if linemode:
	vs.put(vob.gl.GLCache.getCallList("""
	    PopAttrib
	"""))



class ScrollblockData:
    def __init__(self, scrollBlock):
	self.layout = fenfire.view.PageSpanLayout(scrollBlock.getCurrent())
	self.layout.flags = 2
	self.layout.diceLength = 40
	self.irregu = vob.putil.effects.IrreguFrame(0, 0, 
			self.layout.w, self.layout.h, 50, 200)


scrollblockDatas = vob.util.CachingMap(40)
def getScrollblockData(scrollBlock):
    data = scrollblockDatas.get(scrollBlock)
    if data == None:
	data = ScrollblockData(scrollBlock)
	scrollblockDatas.put(scrollBlock, data)
    return data

def makeEnf(span):
    return alph.impl.Enfilade1DImpl.theMaker.makeEnfilade(span)

class AbstractPageSpanNodeType(vob.buoy.BuoyViewNodeType):
    """Just do the stuff that would be same for both
    buoy types.
    """
    def __init__(self, scrollBlockLinker):
	self.scrollBlockLinker = scrollBlockLinker
    def createMainNode(self, linkId, anchorSpan, listener):
	return PageSpanMainNode(self, anchorSpan, listener)
size = jarray.zeros(2, 'f')

class WholePageSpanNodeType(AbstractPageSpanNodeType):
    def getSize(self, linkId, anchorSpan, wh):
	sb = anchorSpan.getScrollBlock();
	data = getScrollblockData(sb)
	wh[0] = data.layout.w
	wh[1] = data.layout.h
	return data
	
    def renderBuoy(self, vs, into, linkId, anchorSpan, data):
	if data == None: 
	    data = getScrollblockData(anchorSpan.getScrollBlock())
	# For now, we'll just squish to fit
	unit = vs.unitSqCS(into, "UNIT")
	scaled = vs.coords.scale(unit, 
		    1.0 / data.layout.w, 1.0 / data.layout.h)
	vs.matcher.add(into, scaled, "LAYOUT")
	data.layout.place(vs, scaled, .05, 50)

	return into

class AnchorPageSpanNodeType(AbstractPageSpanNodeType):
    def getSize(self, linkId, anchorSpan, wh):
	data = getScrollblockData(anchorSpan.getScrollBlock())
	extents = data.layout.getExtents(anchorSpan, None)
	wh[0] = extents[2]
	wh[1] = extents[3]
	return data
    def renderBuoy(self, vs, into, linkId, anchorSpan, data):

	if data == None: 
	    data = getScrollblockData(anchorSpan.getScrollBlock())
	vs.coords.getSqSize(into, size)
	extents = data.layout.getExtents(anchorSpan, None)
	print "ExtentsInit: ", [i for i in extents]
	print "size: ", [s for s in size]
	scalex = size[0] / extents[2] 
	scaley = size[1] / extents[3] 
	scale = min(scalex, scaley)

	# Shift it so we hit the given cs exactly
	# extents[0] -= .5 * (scalex / scale - 1) * extents[2]
	# extents[1] -= .5 * (scaley / scale - 1) * extents[3]
	extents[2] *= scalex / scale 
	extents[3] *= scaley / scale 

	print "Extents: ", [i for i in extents]

	paperCS = vs.orthoCS(into, "paper", 0, -extents[0] * scale,
				-extents[1] * scale, scale, scale)

	viewport = vs.coords.ortho(0, 0, extents[0], extents[1], extents[2], extents[3])

	data.layout.request(extents[0]+.5*extents[2],
			extents[1]+.5*extents[3],
			.5 * (extents[2]+extents[3]),
			.9, .2, 1000)

	vs.matcher.add(into, viewport, "VIEWPORT")

	placeFramed(vs, data, paperCS, viewport, into)
	# LayoutR().run()


	# XXX!!!!
	# Irregu!
	# find edges of span
	# should place only that region surrounded by irregu
	return into

size = jarray.zeros(3, 'f')

class PageSpanMainNode(vob.buoy.BuoyViewMainNode):
    fisheye = vob.view.FisheyeState(
        1.1, .1, 5, .1, 500
	)
    fisheye.curmag = 1.75
    fisheye.cursize = 200
    def __init__(self, nodetype, anchorSpan, listener):
	self.nodetype = nodetype
	self.listener = listener
	self.scrollBlock = anchorSpan.getScrollBlock()
	self.enf = makeEnf(self.scrollBlock.getCurrent())
	self.size = jarray.zeros(2, "f")

	self.data = getScrollblockData(self.scrollBlock)

	xywh = self.data.layout.getExtents(anchorSpan, None)


	self.fisheye.setCenter(xywh[0] + .5 * xywh[2],
				xywh[1] + .5 * xywh[3])

	dk = vob.putil.demokeys

	self.keystroke = dk.KeyPresses(self,
	    dk.Toggle("linemode", 0, "(debug) Draw only edges of polygons", "l"),
	    )
#if key == "Ctrl-H":
#    import org
#    org.fenfire.util.PageSpanPaper.withHalo = not org.fenfire.util.PageSpanPaper.withHalo 

    def _linkEndCS(self, enf, key):
	"""Make a coordinate system for the link end and return it.
	"""
	for repr in alph.util.EnfUtil.getScrollBlockRepresentatives(enf):
	    if repr.getScrollBlock() == self.scrollBlock:
		xywh = self.data.layout.getExtents(repr, None)
		return self.vs.orthoBoxCS(self.shift, key, -20,
				xywh[0], xywh[1], 1, 1,xywh[2], xywh[3])
		    
	raise "HELP!"

    def renderMain(self, vs, into):
	self.vs = vs
	vs.coords.getSqSize(into, size)
	# print "SQ:", size[0], size[1]
	
	self.scale = size[1] / self.data.layout.h 
	self.ctr = vs.translateCS(into, "ORIGIN", .5 * size[0],
		    .5 * size[1])
	self.scale = vs.scaleCS(self.ctr, "SCALE", self.scale, self.scale)

	self.shift = self.fisheye.getCoordsys(vs, self.scale, "TR")
	vs.matcher.add(into, self.shift, "LAYOUT")

	w,h = self.data.layout.w, self.data.layout.h
	shi = .2
	viewport = vs.coords.ortho(0,  0, -shi*w, -shi*h, (1+2*shi)*w, (1+2*shi)*h)
	vs.matcher.add(into, viewport, "VIEWPORT")

	placeFramed(vs, self.data, self.shift, viewport, linemode = self.linemode)
	
	print "Links ",self.nodetype.scrollBlockLinker.enfiladeOverlap, \
		    self.nodetype.scrollBlockLinker.xuIndex 

	if self.nodetype.scrollBlockLinker.enfiladeOverlap != None:
	    matches = (self.nodetype.scrollBlockLinker
				.enfiladeOverlap.getMatches(self.enf))
	    print "EnfOver"
	    for m in matches:
		print "eomatch: ",m
		enf = m.space.getCellTexter().getEnfilade(m, None)
		linkId = vob.util.Pair(m, self.scrollBlock)
		thisEndCS = self._linkEndCS(enf, linkId)
		self.listener.link(-1, thisEndCS, 
			self.nodetype.scrollBlockLinker.cellNodeType,
			linkId, m)

	if self.nodetype.scrollBlockLinker.xuIndex != None:
	    xuIndex = self.nodetype.scrollBlockLinker.xuIndex
	    for dir, index, thisEndName, otherEndName in [
			(1, xuIndex.getForwardIndex(), "from", "to"),
			(-1, xuIndex.getBackwardIndex(), "to", "from")]:
		print "xu ",dir
		for xulink in index.getMatches(self.enf):
		    print xulink
		    myenf = getattr(xulink, thisEndName)
		    thisEndCS = self._linkEndCS(myenf, xulink)
		    endenf = getattr(xulink, otherEndName)
		    for repr in alph.util.EnfUtil.getScrollBlockRepresentatives(endenf):
			self.listener.link(dir, thisEndCS,
				self.nodetype.scrollBlockLinker.xulinkPageSpanNodeType,
				xulink, repr)
	self.doReq()

    def doReq(self):
	self.data.layout.request(self.fisheye.curx, self.fisheye.cury, 
			    self.data.layout.w, 1, .8, 2000)

    def moveTo(self, x, y):
        if x < 0: x = 0
        if y < 0: y = 0
        if x > self.data.layout.w: x = self.data.layout.w
        if y > self.data.layout.h: y = self.data.layout.h
        self.fisheye.setCenter(x, y)
        vob.AbstractUpdateManager.chg()
        self.doReq()

    def mouse(self, mouseEvent, oldVS):
	if self.fisheye.event(mouseEvent):
	    self.fisheye.setCoordsysParams()
	    self.doReq()
	    return 1
	if mouseEvent.getID() == mouseEvent.MOUSE_CLICKED:
	    # print "MOUSE PSPMAIN ",mouseEvent
	    size[0] = mouseEvent.getX()
	    size[1] = mouseEvent.getY()
	    size[2] = 0
	    tmp = jarray.zeros(3, 'f')
	    oldVS.coords.inverseTransformPoints3(self.ctr, size, tmp)
	    # print "Ev - ctr: ",mouseEvent, tmp[0], tmp[1], tmp[2]
	    oldVS.coords.inverseTransformPoints3(self.scale, size, tmp)
	    # print "Ev - scale: ",mouseEvent, tmp[0], tmp[1], tmp[2]

	    oldVS.coords.inverseTransformPoints3(self.shift, size, size)
	    # print "Ev: ",size[0], size[1], size[2]
	    # print "Self.xy was",self.x,self.y
	    oldVS.coords.transformPoints3(self.shift, size, tmp)
	    # print "Retransformed:", tmp[0], tmp[1], tmp[2]
	    x = size[0]
	    y = size[1]
            self.moveTo(x, y)
            # print 'move to attribs: ',x,':',y
	    # self.setShift(oldVS)
	    return 0
	return 0

