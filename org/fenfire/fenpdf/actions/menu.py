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


import vob

from org.fenfire.swamp import Nodes
from org import fenfire as ff
from org.fenfire.util import RDFUtil

dbg = 0

def p(*s):
    print 'ff.fenpdf.actions.menu::', s

# Menu actions will always cause a vobscene regeneration.



class MenuActions:
    def __init__(self, fenPDF):
	self.fenPDF = fenPDF

    def structLinkNodes(self, directionFromMenu):
	"""Make a new structLink between the
	accursed node and the node that the menu
	was clicked out of. The direction is relative
	to the node that originated the menu.
	"""

	node = self.fenPDF.uistate.menu.originatingNode
	if node == None: return

	accursed = self.fenPDF.uistate.cursor.getAccursed()
	if accursed == None: return

	self.fenPDF.structure.structLink.link(node, directionFromMenu, accursed)

    def unlinkBuoy(self):
	"""Unlink a buoy.
	"""

	buoy = self.fenPDF.uistate.menu.originatingBuoy
	#(otherNode, linkId, otherAnchor, into)
	p('Detaching buoy',buoy)
        linkId = buoy.getLinkId()
	if isinstance(linkId, 
		      ff.view.buoy.TransclusionConnector.LinkId):
	    # Transclusion link: delete the node
	    self.fenPDF.structure.structLink.detach(linkId.node)
	    self.fenPDF.structure.canvas2d.removeNode(linkId.node)
	else:
	    a,b = linkId.first, linkId.second
            if dbg: p('dir', buoy.getDirection(), 'linkId', linkId)
            # linkId is a pair which is already in good direction.
	    self.fenPDF.structure.structLink.detach(a, 1, b)

    def deleteNode(self):
	"""Delete a node on a canvas2D.
	"""
	node = self.fenPDF.uistate.menu.originatingNode
	if node == None: return
	self.fenPDF.structure.structLink.detach(node)
	self.fenPDF.structure.canvas2d.removeNode(node)

    def killPlane(self):
	"""Delete a main node.

	XXX HOW TO DELETE FROM TREETIME
	XXX Disabled until treetime made to support this.
	"""
	p("Deleting planes disabled until the treetime code fixed.")
	return

	mainNode = self.fenPDF.uistate.menu.originatingMainNode
	### WRONG BASIS FOR DECISION - SHOULD LOOK AT THE TYPE 
	### OF THE PLANE INSIDE.
	if isinstance(main, ff.view.buoy.MainNode2D):
	    p('KILLING THE PLANE!')
	    self.states.ppActions.deletePaper(main.getPlane())
	    # put a new paper int there
	    self.states.buoymanager.replaceManager(self.states.buoymanager.lastIndex, 
				       ff.view.buoy.MainNode2D(ff.swamp.Nodes.get(self.states.ppActions.newPaper()), 
							       self.states.view2d.irregu, ff.view.buoy.AbstractMainNode2D.SimpleFocus(0,0),1,
							       self.states.mainMouse))
	elif isinstance(main, ff.view.buoy.FisheyeMainNode2D):
	    p('foo fish eye')
	else:
	    p('plaah, unknow main view2d')


    def transclude(self):
	pdfMainNode = self.fenPDF.uistate.selection.getMainNode()
	# XXX Check assertion it's pdf

	area = self.fenPDF.uistate.selection.getXYWH()
	obj = pdfMainNode.getView2D().getSelectedObject(
		pdfMainNode.getPlane(), *area)
	print "Transclude selected: ",pdfMainNode.getPlane(), area, obj
	if obj == None:
	    p("Null??")
	    return
	canvasMainNode = self.fenPDF.uistate.menu.originatingMainNode
	# Check that it really is a canvas
	if not self.fenPDF.structure.canvas2d.isCanvas(canvasMainNode.getPlane()):
	    p("NOT A CANVAS!")
	    return
	# XXX Use oldvs to get location where mouse was clicked
	focus = canvasMainNode.getFocus()
	node = self.fenPDF.actions.abstract.createNewNode(
			canvasMainNode.getPlane(), 
			    focus.getPanX(), focus.getPanY())
	self.fenPDF.structure.ff.setContent(node, obj)
		

