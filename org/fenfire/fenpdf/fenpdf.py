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


"""A central class for fenpdf.
"""

from org import fenfire as ff
import vob, java
from org.fenfire.vocab import TREETIME

def p(*s):
    print 'fenpdf.py::',s


def staticCode():
    """ Static code which is executed when constructing FenPDF class.
    """
    vob.AbstractUpdateManager.fractCalc = vob.AbstractUpdateManager.LinearCalculator(0)
    vob.AbstractUpdateManager.defaultAnimationTime = 1500





def staticCode():
    """ Static code which is executed when constructing FenPDF class.
    """
    vob.AbstractUpdateManager.fractCalc = vob.AbstractUpdateManager.LinearCalculator(0)
    vob.AbstractUpdateManager.defaultAnimationTime = 1500




class _Empty:
    pass

class AnimationState:
    """

    The following fields are public:
	
	lastVs -- The vobscene that has been drawn last.
	reuseVs -- set to true if the change was fast and we can just
		   reuse the last time's vs.
	animate -- The next regeneration should also animate. 
        window  -- The window where program is shown.
    """

    def __init__(self):
	self.reuseVs = 0
	self.lastVs = None
	self.animate = 0

    def regenerateVS(self, callChg = 1):
	"""Set us to regenerate the vobscene for the next frame
	instead of reusing it. 

	params:
	callChg -- Whether to call AbstractUpdateManager.chg() as well,
	           default: yes.
	"""
	self.reuseVs = 0
	if callChg:
	    vob.AbstractUpdateManager.chg()

    def noAnimation(self):
	self.reuseVs = 0

    def generatedNewScene(self, vs):
	"""A new scene was generated, start fresh.
	"""
	self.reuseVs = 1
	self.lastVs = vs
	self.animate = 1

class Cursor(ff.view.lava.Cursor):
    """ The cursor for a node, e.g., text or pgaespan
    (in canvas view). Cursor is like text cursor in text editor,
    i.e., single instance and used to represent the accursed
    node and offset.
    """
    def __init__(self, alphContent, multiplexer):
        ff.view.lava.Cursor.__init__(self, alphContent)
        self.multiplexer = multiplexer
    def setAccursed(self, node):
        ff.view.lava.Cursor.setAccursed(self, node)
        set = java.util.HashSet()
        set.add(node)
        self.multiplexer.setMultiplexerNodes(set)


class UIState_Menu:
    """

    The fields are accessible and are allowed
    to be read, but they
    should only be written to using the 
    UIState_Menu object methods.

	    shown -- If a menu is currently shown.

	    originatingNode -- the node (may be None) 
			       on which the context menu 
			       was clicked open

            originatingTextOffset -- the cursor offset of text
                                     if there are any text othervise None

	    originatingMainNode -- the main view on which
				    the context menu was clicked
				    open. If originatingNode is
				    non-null, this should be too.

	    originatingBuoy -- the buoy on which the context
				menu was clicked open.
				XXX data format?
	    
    """

    def __init__(self):
	self.shown = 0
	self.originatingMainNode = None
        self.originatingTextOffset = None
	self.originatingNode = None
	self.originatingBuoy = None
    def originateFromBuoy(self, buoy):
	self.originatingBuoy = buoy
	self.originatingNode = None
	self.originatingMainNode = None
        self.originatingTextOffset = None
	self.shown = 1
    def originateFromMainNode(self, mainNode, node, offs):
	self.originatingBuoy = None
	self.originatingNode = node
	self.originatingMainNode = mainNode
        self.originatingTextOffset = offs
	self.shown = 1
    def hasTextOffset(self):
        if self.originatingTextOffset == None: return 0
        else: return 1
    def clear(self):
	self.__init__()


class UIState_Scissors:
    """ XXX
    States for text scissors
        begin
            node
            offset
        end
            node
            offset

    Read acces to all.
    Write only trough methods setBegin and setEnd.
    """

    class ScissorsPoint:
        def __init__(self):
            self.node = None
            self.offset = None

    def __init__(self):
        self.begin = self.ScissorsPoint()
        self.end = self.ScissorsPoint()
    def clear(self): self.__init__()
    def setBegin(self, node, offset):
        self.begin.node = node
        self.begin.offset = offset
    def setEnd(self, node, offset):
        self.end.node = node
        self.end.offset = offset
    def isScissored(self, node, offset=None):
        if node == None: return 0
        if self.begin.node == node and \
           self.end.node == node:
            if offset != None:
                if self.begin.offset <= offset and \
                       self.end.offset >= offset or \
                       self.begin.offset >= offset and \
                       self.end.offset <= offset:
                    return 1
            elif offset == None: return 1
        return 0
    def fixOffset(self):
        if self.begin.node != None and self.begin.node == self.end.node:
            if self.begin.offset > self.end.offset:
                tmp = self.begin.offset
                self.begin.offset = self.end.offset
                self.end.offset = tmp

class FenPDF:
    """This class represents a complete fenpdf "application".

    The members that are allowed to be directly used
    by outside classes are:

    fen -- The Fen object used
    views -- An object with the public interface of 
             org.fenfire.fenpdf.appearance.views.Views
    events -- an object with no methods, just fields:

	mouse -- an object like org.fenfire.fenpdf.events.mouse.MouseMapper
	key -- 
	mousemenu --
	eventHandler --

    actions -- an object with no methods, just fields:

	mouse -- an object like org.fenfire.fenpdf.actions.mouse.MouseActions
	key --
	menu --
	abstract --

    structure -- an object with no methods, just fields:

	structLink -- a StructLink object
	alphContent
	canvas2d
	treeTime
	ff -- org.fenfire.structure.Ff

    window -- the GraphicsAPI.window object

    animation -- an AnimationState object

    uistate -- just fields:
	       This object stores **ALL** state about the 
	       user interface interactions, except
	       the focuses of the main nodes, which are
	       inside views.

	       Any selections, cursors, &c are here.
	
	selection -- a Selection object
    
	cursor -- the accursed node or plane

        textscissor -- begin and end scissor offsets and nodes to
                       support text cloud operations 

	menu -- State of the ui context menu.

    extensions -- extensions to fenPDF specification

    Any internal members are prefixed by underscore and should
    not be used by anyone else.
    """

    def __init__(self, fen, treeTime, structure_ff, window, 
		filename):
        staticCode()
	fenPDF = self
	fenPDF.fen = fen
	fenPDF.window = window

	# Default controls for main view
	## see http://himalia.it.jyu.fi/ffdoc/fenfire/pegboard/fenpdf_v1_spec_1--tjl/peg.gen.html and 'Bindings'
	fenPDF.events = _Empty()
	if 1:
	    fenPDF.events.buttons = ff.fenpdf.events.buttons.ActionButtons()
	    fenPDF.events.buttons.fenPDF = fenPDF
	    fenPDF.events.eventHandler = \
		ff.fenpdf.events.eventhandler.EventHandler(fenPDF)
	    fenPDF.events.mouse = ff.fenpdf.events.mouse.MouseMapper()
	    fenPDF.events.key = ff.fenpdf.events.key.KeyHandler()
	    fenPDF.events.mousemenu = ff.fenpdf.events.mousemenu.MouseMenu(fenPDF)

	fenPDF.actions = _Empty()
	if 1:
	    actions = ff.fenpdf.actions
	    fenPDF.actions.mouse = actions.mouse.MouseActions(fenPDF)
	    fenPDF.actions.key = actions.keyboard.KeyActions(fenPDF)
	    fenPDF.actions.menu = actions.menu.MenuActions(fenPDF)
	    fenPDF.actions.abstract = actions.abstract.AbstractActions(fenPDF)
	    fenPDF.actions.global = actions.globalactions.GlobalActions(fenPDF)

	papers = ff.fenpdf.appearance.papers.Papers(fenPDF)

	fenPDF.structure = _Empty()
	fenPDF.animation = AnimationState()

	fenPDF.uistate = _Empty()
	if 1:
	    fenPDF.uistate.filename = filename
	    fenPDF.uistate.menu = UIState_Menu()
	    fenPDF.uistate.textScissors = UIState_Scissors()
	    fenPDF.uistate.selection = ff.fenpdf.fenpdfcontext.Selection()


	fenPDF.structure.ff = structure_ff
	fenPDF.structure.structLink = ff.structure.StructLink.create(fen.graph)
	fenPDF.structure.alphContent = ff.util.AlphContent(fen, structure_ff)
	fenPDF.structure.canvas2d = ff.structure.Canvas2D.create(fen.graph)
	fenPDF.structure.treeTime = treeTime
        fenPDF.extensions = ff.fenpdf.extensions.getExtensions()

	# The first plane is the "firstOf" of the TREETIME follows 
	# relation. 
	iter = fen.constgraph.findN_X11_Iter(TREETIME.firstOf, 
					    TREETIME.follows)
	planeHome = iter.next()
	if planeHome == None: 
	    raise "Error: home plane not found"

	# Check the type of the home plane, just in case
	if not fenPDF.structure.canvas2d.isCanvas(planeHome):
	    raise "Error: home plane not found - wrong type ", planeHome

	# Views must be created last - XXX
	fenPDF.views = \
	   ff.fenpdf.appearance. \
	   views.Views(fenPDF, papers, fenPDF.events.eventHandler, planeHome)

	fenPDF.views.background = \
	    ff.fenpdf.appearance.background.FancyBlueBackground1()

	fenPDF.uistate.cursor = \
	   ff.fenpdf.fenpdf.Cursor(fenPDF.structure.alphContent, 
                           fenPDF.views.getMultiplexerNodeContentFunction())

	fenPDF.events.mouse.fenPDF = fenPDF
	fenPDF.events.mouse.update()

	fenPDF.events.key.fenPDF = fenPDF

    def getVersion(self):
        """ returns the version number of specification of fenpdf.
        """
        return 1.0
    def render(self, vs):
	pass

    def useExtension(self, extname):
        """ Return true if extension is enabled otherwise return false.
        """
        return self.extensions.get(extname, 0)
