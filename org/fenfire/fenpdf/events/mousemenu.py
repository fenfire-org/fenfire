# 
# Copyright (c) 2003, Tuomas J. Lukka and Matti Katila
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

import vob
from org.fenfire.swamp import Nodes
from org import fenfire as ff
import java, jarray
import org.nongnu.libvob.vobs.SelectItemVob as Item

def p(*s):
    print 'ff.fenpdf.events.mousemenu::', s

dbg = 0

class MouseMenu(ff.fenpdf.actions.eventgrabs.Grabber):
    def __init__(self, fenPDF):
        self.fenPDF = fenPDF
	self.selectListVob = None
        self.x, self.y = 0 ,0
        self.w, self.h = .0, .0
        
    def makeMainList(self):
        list = []
	if self.fenPDF.uistate.selection.hasSelection():
	    list.append(Item('Transclude selection (PDF to canvas only)',
		    lambda: self.fenPDF.actions.menu.transclude()))
        if self.fenPDF.uistate.menu.originatingNode:
            list.append(Item('<-----  Link node to left', 
		    lambda: self.fenPDF.actions.menu.structLinkNodes(-1)))
            list.append(Item('Link node to right ----->', 
		    lambda: self.fenPDF.actions.menu.structLinkNodes(1)))
            list.append(Item('Delete this node', 
		    lambda: self.fenPDF.actions.menu.deleteNode()))

        list.append(Item('Destroy this canvas', 
		    lambda: self.fenPDF.actions.menu.killPlane()))

        self.selectListVob = vob.vobs.SelectListVob(list)

    def makeBuoyList(self):
        list = []
        list.append(Item('Unlink buoy', 
            lambda: self.fenPDF.actions.menu.unlinkBuoy()))
        self.selectListVob = vob.vobs.SelectListVob(list)

    def render(self, vs):
	if self.selectListVob:
	    cs = vs.orthoBoxCS(0, 'MOUSE_MENU',-100, self.x, self.y-self.h/2,
			       1,1, self.w, self.h)
	    vs.put(self.selectListVob, cs)

    def showList(self, x, y, who):
        self.fenPDF.events.eventHandler.eventGrabber = self
        self.dragged = 0
        self.notExecuted = 1
	p("showList")
        if who == 'buoymenu':
            self.makeBuoyList()
        elif who == 'mainmenu':
            self.makeMainList()
        else:
            p('no mouse menu list!')
            self.hideList()
            return
        
        self.w, self.h = self.selectListVob.width, self.selectListVob.height
        self.w *= 2
        self.h *= 2
        self.x, self.y = x, y

    def hideList(self):
        self.fenPDF.events.eventHandler.eventGrabber = None
	p("Hidelist")
        if hasattr(self.fenPDF.uistate, 'lastEvent'):
            self.x, self.y = self.fenPDF.uistate.lastEvent.getX(), self.fenPDF.uistate.lastEvent.getY()
        else: self.x, self.y = 0,0
        self.w, self.h = .0, .0

        # what are these?!
	#self.fenPDF.animation.regenerateVS()
	#self.fenPDF.animation.noAnimation()
	#vob.AbstractUpdateManager.chg()

    def eventGrabber(self, ev, oldVS):
        """ Should be called only through eventHandler.eventGrabber
        """

        cs = oldVS.matcher.getCS(0, 'MOUSE_MENU')
        key = oldVS.getKeyAt(cs, ev.getX(), ev.getY(), None)
        if dbg: p('cs', cs, key)
        if cs < 0:
            p('Something WRONG!!, should not happend! How can ',oldVS,'be *WRONG* vobscene??? should not be reused!')
            raise 'Unallowed operation'

        pts = jarray.zeros(3, 'f')
        if dbg: p('mouse', ev.getX(), ev.getY())
        oldVS.coords.inverseTransformPoints3(cs, [ev.getX(), ev.getY(), 0.], pts)
        if dbg: p('transformed', pts)

        if ev.getType() in [ev.MOUSE_PRESSED, ev.MOUSE_DRAGGED]:
            self.selectListVob.preSelect(pts[0], pts[1])

            if ev.getType() == ev.MOUSE_DRAGGED:
                self.dragged = 1
            vob.AbstractUpdateManager.setNoAnimation()
        if ev.getType() in [ev.MOUSE_RELEASED, ev.MOUSE_CLICKED]:
            self.selectListVob.postSelect(pts[0], pts[1])
            
            if key and self.notExecuted:
                key()
                self.notExecuted = 0
            else:
                p('nothing', key)

            if self.dragged or ev.getType() == ev.MOUSE_CLICKED:
                self.fenPDF.events.eventHandler.eventGrabber = None
                self.hideList()
            
        self.fenPDF.animation.regenerateVS()
