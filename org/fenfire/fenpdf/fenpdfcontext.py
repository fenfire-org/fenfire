# 
# Copyright (c) 2003, Matti J. Katila and Tuomas J. Lukka
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


from org import fenfire as ff
from org.fenfire.swamp import Nodes
import org.nongnu.libvob as vob
import java, jarray

dbg = 0

def p(*s):
    print 'fenpdfcontext.py::', s

global w


class Selection:
    """A selected area

    self.area = x0,y0,x1,y1
    """
    def __init__(self):
	self.area = [0,0,0,0]
	self.mainNode = None
    def getArea(self): return self.area
    def getMainNode(self): return self.mainNode
    def setArea(self, area, mainNode): 
	self.area = area
	self.mainNode = mainNode
    def hasSelection(self):
	a = self.area
	return (a[0]-a[2]) != 0 and (a[1]-a[3]) != 0
    def getXYWH(self):
	"""Return a x, y, width, height tuple.
	"""
	a = self.area
	if a[2] > a[0]:
	    x = a[0]
	    w = a[2]-a[0]
	else:
	    x = a[2]
	    w = a[0]-a[2]
	if a[3] > a[1]:
	    y = a[1]
	    h = a[3]-a[1]
	else:
	    y = a[3]
	    h = a[1]-a[3]
	return (x, y, w, h)
    def isInsideSelect(self, x,y):
        if not self.hasSelection(): return 0
        (x0,y0,w,h) = self.getXYWH()
        if x0 <= x and y0 <= y and \
           x0+w >= x and y0+h >= y: return 1
        return 0
    def removeSelection(self):
        self.__init__()

class CursorRenderer(ff.view.buoy.AbstractMainNode2D.Context):
    """The class responsible for rendering a text cursor
    into a main view.
    """
    def __init__(self, fenpdf):
        self.fenPDF = fenpdf
        
	self.cursorVob = vob.vobs.ContinuousLineVob(5, [0,0,0 , 0,1,0])

    # call back from AbstractMainNode2D
    # chgFast isn't called!!! XXX
    def mainNodeToBeRender(self, vs, into, mainNode):
	
	if dbg:
	    print "MAINNODETOBERENDER",self,vs,into,mainNode

	self.selection = self.fenPDF.views.getAreaSelectView2D()

        buoymanager = self.fenPDF.views.getBuoyManager()
        if mainNode != buoymanager.getActiveBuoyManager().getMainNode(): return

        mainNode = buoymanager.getActiveBuoyManager().getMainNode()
        contentView = mainNode.getView2D().getContentView2D()
        if not isinstance(contentView, ff.view.CanvasView2D): return
        canvas = contentView
        
        xy = jarray.zeros(3, 'f')
        height = self.fenPDF.views.textStyle.getHeight(1)

        # draw cursor
        if not self.fenPDF.uistate.cursor.hasAccursed():
            focus = mainNode.getFocus()
            cs = vs.matcher.getCS(into, 'canvasview_conc')
	    cs = vs.coords.orthoBox(cs,0, focus.getPanX(), focus.getPanY(), 1,1, 1,1)
        else:
            # draw the cursor in somewhere where node is.
            containerCS = canvas.getContainerCS(vs, into)
            cs = vs.matcher.getCS(containerCS, self.fenPDF.uistate.cursor.getAccursed())
            if cs < 0: return

            viewFunction = self.fenPDF.views.getMultiplexerNodeContentFunction()
            pl = viewFunction.f(self.fenPDF.fen.graph, self.fenPDF.uistate.cursor.getAccursed())
            if isinstance(pl, vob.lava.placeable.TextPlaceable):
                pl.getCursorXYY(self.fenPDF.uistate.cursor.getCursorOffset(), xy)
                height = xy[1] - xy[2]
            else: return 
                
        cs = vs.coords.ortho(cs, 0,xy[0],xy[2], 1, height)
        vs.put(self.cursorVob, cs)
        


