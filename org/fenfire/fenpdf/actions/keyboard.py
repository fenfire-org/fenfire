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


# The key event handlers do not have the same restrictions
# as the event handlers for mouse events -- no need to implement
# any particular interface. 

# Therefore, we'll just use a single class.

import vob
import alph

from org.fenfire.swamp import Nodes
from org import fenfire as ff
from org.fenfire.util import RDFUtil

dbg = 0

def p(*s):
    print 'ff.fenpdf.events.key::', s

# Note that the default is that the old vobscene is reused.
# If an action needs regeneration, it has to explicitly call for
# that by fenPDF.animation.regenerateVs()

class KeyActions:
    def __init__(self, fenpdf):
	self.fenPDF = fenpdf
    def isTypingAbleMain(self, main):
        """ Returns true whether the main node is
        able to type in.
        """
        if isinstance(main, ff.view.buoy.MainNode2D): return 1
        p('not a typing able canvas')
        return 0
    def insertChar(self, main, char):
	"""Insert a character to the accursed node - no creation.

	If no node is accursed, will do nothing.
	"""
        cur = self.fenPDF.uistate.cursor

	acc = cur.getAccursed()

	if dbg: p( "Typing: ", cur, cur.getAccursed())

        offs = cur.getCursorOffset()
	self.fenPDF.structure.alphContent.insertText(acc, offs, char, 1)
	cur.setCursorOffset(offs + 1)

	self.fenPDF.animation.regenerateVS()

	
    def insertCharOrCreate(self, main, char):
	"""Insert a character to the accursed node.

	If no node is accursed, create a new node at cursor.
	"""
        if not self.isTypingAbleMain(main): return 

        cur = self.fenPDF.uistate.cursor
	if cur.getAccursed() == None:
	    node = self.fenPDF.actions.abstract.createNewNode(
		    main.getPlane(),
		    main.getFocus().getPanX(),
		    main.getFocus().getPanY(),
		    )
	    cur.setAccursed(node)

	self.insertChar(main, char)

	self.fenPDF.animation.regenerateVS()

    def moveInsertionCursor(self, main, n):
	"""Move the text insertion cursor by n.

	The number n may be positive or negative.
	If no node is accursed, will do nothing.
	"""
        cur = self.fenPDF.uistate.cursor
	acc = cur.getAccursed()
	if acc == None: return

        offs = cur.getCursorOffset() + n
	if offs < 0: offs = 0
	l = len(self.fenPDF.structure.alphContent.getText(acc))

	if offs > l: offs = l

	cur.setCursorOffset(offs)

	self.fenPDF.animation.regenerateVS()

    def backspace(self, main):
        cur = self.fenPDF.uistate.cursor

	node = cur.getAccursed()
	if node == None: return
        offs = cur.getCursorOffset()

        alphContent = self.fenPDF.structure.alphContent

	if offs >= 1:
	    p('delete:', offs)
	    alphContent.deleteText(node, offs - 1, offs)
	    cur.setCursorOffset(offs - 1)

	self.fenPDF.animation.regenerateVS()

	
    def toggleBgTextureUse(self, value = -1):
	"""Toggle whether fancy paper bgs are used.

	If value is 0 or 1, use it, if -1, toggle.
	"""
	self.fenPDF.views.papers.toggleBg(value)
	self.fenPDF.animation.regenerateVS()

    def changeBgPaperMaker(self):
	"""Change the papermaker, i.e. how the fancy paper is blended.
	"""
	self.fenPDF.views.papers.adjustPaperMakerIndex(1)
	self.fenPDF.animation.regenerateVS()



    def goToHome(self, main):
	# Graa - this certainly doesn't work XXX
	# **AND** breaks encapsulation. Was moved
	# here from fenpdf10.py, and needs fixing XXX
	self.fenPDF.views.buoymanager.singles[1].mainNode = ff.view.buoy.MainNode2D(planeHome, irregu, ctrl)

	self.fenPDF.animation.regenerateVS()

    def save(self):
	self.fenPDF.actions.global.save()
	self.fenPDF.animation.regenerateVS()

    def exit(self):
	# Harsh
        import java
	java.lang.System.exit(43)

    def toggleZoomToSingle(self, main):
	"""Zoom to the currently focused view.

	XXX Doesn't do that yet - always goes to upper view.
	"""
	self.fenPDF.views.getDoubleGeometer().setSingle(
	    not self.fenPDF.views.getDoubleGeometer().getSingle())
	vob.AbstractUpdateManager.chg()
	self.fenPDF.animation.regenerateVS()


    def importPDF(self, main = None):
	"""Show the dialog for importing new PDF files.

	If the given (or upper) main node is on a canvas,
	transclude from all PDF files the topmost part
	of the first page.
	"""
	self.fenPDF.animation.regenerateVS()
	scrollBlocks = ff.fenpdf.importpdf.importPDF_select(
		self.fenPDF.fen,
		self.fenPDF.structure.treeTime)
	p('sc: ',scrollBlocks)
	if not len(scrollBlocks): return
	if not main:
	    main = self.fenPDF.views.getBuoyManager().singles[0].mainNode
	if isinstance(main.getPlane(), alph.PageScrollBlock):
	    self.moveTo(None, scrollBlocks[0])
	    return
	#
	# Now transclude.
	#
	x = main.getFocus().getPanX()
	y = main.getFocus().getPanY()
	for sb in scrollBlocks:
	    node = self.fenPDF.actions.abstract.createNewNode(
		    main.getPlane(),
		    x,
		    y,
		    )
	    page = sb.getPage(0);
	    size = page.getSize();
	    self.fenPDF.structure.ff.setContent(
		node, 
		self.fenPDF.fen.enfMaker.makeEnfilade(
		    page.subArea(0, 0, size.width, size.height / 3)
		    ))
	    x += 100
	    y += 200
	    

    def newCanvas(self):
	canvas = Nodes.N()
	self.fenPDF.structure.canvas2d.makeIntoCanvas(canvas)
	self.fenPDF.structure.treeTime.addLatest(self.fenPDF.fen.graph, canvas)
	self.moveTo(None, canvas)
	self.fenPDF.animation.regenerateVS()

    def moveTo(self, main, newFocus):
	"""Move the focus to a new place.

	main -- the mainNode to move, can be None for default.
	newFocus -- the new focused node.
	"""

	self.fenPDF.views.setFocus(newFocus)
	self.fenPDF.animation.regenerateVS()


    

