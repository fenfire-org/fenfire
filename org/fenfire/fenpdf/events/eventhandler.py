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


import vob

dbg = 0
def p(*s):
    print 'ff.fenpdf.events.eventhandler::', s

class EventHandler:
    def __init__(self, fenPDF):
        self.fenPDF = fenPDF
        self.eventGrabber = None

    def mouse(self, ev, oldvs):
        """ *THE* event handler - if you want to grab the event - grab
        it in the beginning. 
        """
        self.fenPDF.uistate.lastEvent = ev

        if dbg: p(ev)

        if self.eventGrabber != None:
            self.eventGrabber.eventGrabber(ev, oldvs)
            return

        if dbg: p("2")

        if ev.getType() == ev.MOUSE_RELEASED:
            # release event must go to mainMouse MouseMultiplexer to work correctly!
            # mainMouse.deliverEvent(ev)
            # flush implemented...
            self.fenPDF.events.mouse.mainMouse.flush()
            self.fenPDF.events.mouse.buoyMouse.flush()
             
            self.fenPDF.window.setCursor('default')
            if 1: return

        if dbg: p("3")



	buoymanager = self.fenPDF.views.getBuoyManager()

        if dbg: p("3")

        # this is rather ugly
        if ev.getType() == ev.MOUSE_DRAGGED:
            if self.fenPDF.events.mouse.mainMouse.deliverEvent(ev):
                self.fenPDF.animation.reuseVS = 1
                vob.AbstractUpdateManager.chg()
            return

        if dbg: p("4")

	buoy = buoymanager.findIfBuoyHit(oldvs, ev.getX(), ev.getY())
	if buoy != None:
            buoymanager.setActiveBuoyManager(buoymanager.\
                                             getManagerByLastBuoyHit())
            if self.fenPDF.events.mouse.buoyMouse.deliverEvent(ev):
                pass
            return

        if dbg: p("5")

        topmostManager = buoymanager.\
                         findTopmostBuoyManager(oldvs, ev.getX(), ev.getY())

        if dbg: p("6")

        # If the most upper main node is available use it.
        if topmostManager != None:
            topmostMainNode = topmostManager.getMainNode()
            buoymanager.setActiveBuoyManager(topmostManager)
            if self.fenPDF.events.mouse.mainMouse.deliverEvent(ev):
                pass

            return

        if dbg: p('7')
        if self.fenPDF.events.buttons.mouseEvent(ev, oldvs):
            self.fenPDF.animation.regenerateVS()
            return



    def key(self, key):
	self.fenPDF.events.key.key(key, 
                                   self.fenPDF.views.getBuoyManager().\
                                   getActiveBuoyManager().getMainNode())
        vob.AbstractUpdateManager.chg()
        return 1

