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

# This demo is for FenMM - Mind Mapping on Fenfire

import java, jarray, math
from java.lang import Math

import vob
import org.fenfire as ff

from org.fenfire.vocab import *
from org.fenfire.vocab.lava import *
from org.fenfire.swamp import Nodes
from org.fenfire.fenmm import MMGeometry
from org.fenfire.util.lava import Traversals
from org.nongnu.libvob.util import PUIClipboard
import org.nongnu.alph as alph
import org.nongnu.storm as storm

# save and load
from com.hp.hpl.mesa.rdf.jena.mem import ModelMem
import os.path

def p(*s):
    print 'mindMapView2D', s

dbg = 0

w.setCursor("wait")
vob.putil.demo.usingNormalBindings = 1

print """

     ############################### 
     ##                           ##
     ##  LOADING FenMM1.0 v0.0    ##
     ##                           ##
     ###############################


FenMM is brought to you by the Fenfire team (http://fenfire.org).
 
Fenfire is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
 
sFenfire is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
Public License for more details.
 
You should have received a copy of the GNU General
Public License along with Fenfire; if not, write to the Free
Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
MA  02111-1307  USA
 

"""

# storm pool directory and mindmap file
DIR = 'myFenfire/'
FILE = java.lang.System.getProperty("fenmm.file", DIR+"mindmap.rdf")
POOLDIR = java.lang.System.getProperty("fenmm.pool", DIR)

os.system('mkdir -p '+POOLDIR)

pool = storm.impl.DirPool(java.io.File(POOLDIR), java.util.HashSet())
myalph = alph.impl.StormAlph(pool)

# set flag to load an old graph or creating new
do_load_graph = 0
if os.path.isfile(FILE):
    print 'Loading the RDF graph from the file %s.' % (FILE)
    do_load_graph = 1
else:
    print 'Creating a new RDF graph.'
    do_load_graph = 0

# Now, before reading the xu stuff, render something
# to try to prevent NV driver fallback to software rendering.
vs = w.createVobScene()
vs.put(background((.5, .5, .5)))
w.renderStill(vs, 0)

fen = ff.Fen()

if do_load_graph:
    m = ModelMem()
    m.read(java.io.FileReader(FILE), None);
    fen.constgraph = fen.graph = ff.swamp.Graphs.toGraph(m);
else: fen.graph = fen.constgraph = ff.swamp.impl.HashGraph()

ff_structure = ff.structure.Ff.create(fen.graph, myalph)

# why necessary?
fen.enfiladeOverlap = ff_structure.getTransclusionIndex()
fen.alph = myalph
#

style = vob.GraphicsAPI.getInstance().getTextStyle("sansserif", 0, 24)

functional = ff.functional.SuperFunctional1(
    fen.constgraph,
    vob.AbstractUpdateManager.getInstance()
    )

functional.cache("ContentFunction", 1);
functional.cache("TextNodeView", 500);
functional.cache("NormalTextNodeView", 500);
functional.cache("AccursedTextNodeView", 10);

contentFunction = functional.createFunctionInstance(
    "ContentFunction",
    ff.structure.Ff.ContentFunction,
    [ ff_structure ])

textnodeview = functional.createFunctionInstance(
    "TextNodeView",
    ff.view.TextNodeView,
    [contentFunction, style, java.awt.Color.black, 250.])

# nodeview for normal nodes
normalf = functional.createFunctionInstance(
    "NormalTextNodeView",
    ff.fenmm.WhiteNodeView,
    [textnodeview, java.awt.Color(.6, .6, .6)])

# nodeview for accursed nodes
accursedf = functional.createFunctionInstance(
    "AccursedTextNodeView",
    ff.fenmm.WhiteNodeView,
    [textnodeview, java.awt.Color(.9, .9, .9)])

multiplexer = ff.swamp.MultiplexerNodeFunction(
    normalf.getCallableFunction(),
    accursedf.getCallableFunction())

structLink = ff.structure.StructLink.create(fen.graph)

def i2l(iteratorToList):
    """
    Iterates the given iterator throughout and returns
    its components as a new list.
    """
    list = []
    while iteratorToList.hasNext():
        list.append(iteratorToList.next())
    return list

class Context(ff.view.buoy.AbstractMainNode2D.Context):
    def __init__(self, fen, w, components, multiplexer, style):
        self.fen = fen
        self.w = w
        self.components = components
        self.multiplexer = multiplexer
        self.style = style

        # XXX
        self.ff_structure = ff_structure # XXX
        self.contentFunction = contentFunction # XXX
        self.functional = functional # XXX
        # XXX

        self.rmb_switch = [ 'go', 'link' ]
        self.oldVS = None
        self.replaceVS = None

        self.main = None    # MainNode2D
        self.fastMouseChg = 0  # callback from abstractmainnode2d when called mouse
        self.bgcolor = (.4, .7, 1.0)
        self.c = ff.fenmm.MMTextCursor(style)

    def nextRmb(self):
        rmb = self.rmb_switch
        rmb.append(rmb.pop(0)) 
        rmb = rmb[0]

        if rmb == 'go': self.bgcolor = (.4, .7, 1.0); self.w.setCursor('default')
        elif rmb == 'link': self.bgcolor = (.4, .8, .4); self.w.setCursor('hand')
        else: raise 'Undefined state!'

    def insertText(self, text):        
        if self.c.getAccursed() == None:
            self.c.setAccursed(ff.util.RDFUtil.N(self.fen.graph, MINDSTRUCT.Data))
            self.c.setOffset(0)
            self.components.append(self.c.getAccursed())
        offset = self.c.getOffset()
        self.alphContent.insertText(self.c.getAccursed(), offset, text, 1)
        self.c.setOffset(offset + len(text))

    def deleteText(self):
        if dbg: p('offs:', self.offset)
        accursed = self.c.getAccursed()
        if self.c.getAccursed() == None: return
        offset = self.c.getOffset()
        
        self.alphContent.deleteText(accursed, offset-1, offset)
        self.c.setOffset(offset-1)
        l = len(self.alphContent.getText(accursed))

        offset = self.c.getOffset()
        if offset == 0 and l == 0: self.rmNode(accursed)

    def rmNode(self, obj):
        """
        Removes a node form the graph.

        If the removed node was accursed or centered (set as plane)
        a new accursion or centerint (plane) will be set.
        """

        # store some data from the removed node
        wasLinked = structLink.isLinked(obj)
        wasAccursed = (obj == self.c.getAccursed())
            
        # remove all connections from the node
        # do it so that only the target nodes, not the node to be
        # removed, will be put ont the floating buffer
        neighbour = i2l(self.fen.graph.findN_X11_Iter(STRUCTLINK.linkedTo, obj))
        neighbour.extend(i2l(self.fen.graph.findN_11X_Iter(obj, STRUCTLINK.linkedTo)))
        for node in neighbour: self.rmLinkTo(node, obj)

        # if the removed node was on the floating buffer, remove it
        try: self.components.remove(obj)
        except ValueError: pass
        
        # remove node
        self.fen.graph.rm_111(obj, RDF.type, MINDSTRUCT.Data)

        # at first, look a proper new accursed node or make none accursed
        if obj == self.c.getAccursed():
            for node in self.components:
                if structLink.isLinked(node) != wasLinked: continue
                else: self.c.setAccursed(node)
        if obj == self.c.getAccursed(): self.c.setAccursed(None)
        # then, if the removed node was centered (plane) center its biggest neighbour
        if obj == self.main.getPlane():
            neighbourSet = java.util.HashSet(java.util.Arrays.asList(neighbour))
            comps, largest = Traversals.findComponents(neighbourSet.iterator(), STRUCTLINK.linkedTo,
                                                       self.fen.graph, neighbourSet)
            if largest:
                focus = self.main.getFocus()
                self.main.setNewPlane(largest, focus.getPanX(), focus.getPanY(), focus.getZoom())
                if wasAccursed: self.c.setAccursed(largest)
                try: self.components.remove(largest)
                except ValueError: pass
        # if the removed node had no neighbours, center the biggest floating component
        if obj == self.main.getPlane():
            floatingSet = java.util.HashSet(java.util.Arrays.asList(self.components))
            comps, largest = Traversals.findComponents(floatingSet.iterator(), STRUCTLINK.linkedTo,
                                                       self.fen.graph, floatingSet)
            if largest:
                focus = self.main.getFocus()
                self.main.setNewPlane(largest, focus.getPanX(), focus.getPanY(), focus.getZoom())
                if wasAccursed: self.c.setAccursed(largest)
                try: self.components.remove(largest)
                except ValueError: pass
        # if the removed node was the last node in the graph, create a new Home and center it
        if obj == self.main.getPlane():
            node = ff.util.RDFUtil.N(fen.graph, MINDSTRUCT.Data)
            focus = self.main.getFocus()
            self.alphContent.setText(node, 'Home', 1)
            self.main.setNewPlane(node, focus.getPanX(), focus.getPanY(), focus.getZoom())
            if wasAccursed: self.c.setAccursed(node)

    def rmLinkTo(self, obj, with):
        totRemoved = 0
        links = i2l(self.fen.graph.findN_11X_Iter(obj, STRUCTLINK.linkedTo))
        try:
            for l in links:
                if with == l:
                    self.fen.graph.rm_111(obj, STRUCTLINK.linkedTo, with)
                    totRemoved += 1
            links = i2l(self.fen.graph.findN_11X_Iter(with, STRUCTLINK.linkedTo))
            for l in links:
                if obj == l:
                    self.fen.graph.rm_111(with, STRUCTLINK.linkedTo, obj)
                    totRemoved += 1
        except: pass
        if totRemoved and (not structLink.isLinked(obj) or (structLink.isLinked(obj) \
               and not Traversals.isConnected(obj, STRUCTLINK.linkedTo, with, fen.graph))):
            self.components.append(obj)
        return totRemoved

    def mainNodeToBeRender(self, vs,into, main): pass
    def changeFastAfterMouseEvent(self):
        if dbg: p('Returning', self.fastMouseChg, 'as fast or slow.')
        return self.fastMouseChg
    def setFastAnimation(self):
        self.fastMouseChg = 1
        self.replaceVS = self.oldVS
    def setSlowAnimation(self):
        self.fastMouseChg = 0
        self.replaceVS = None

class Action:
    def __init__(self, fen, context):
        self.fen = fen
        self.context = context

class RMB(Action, vob.mouse.MouseClickListener):
    def clicked(self, x,y):
        self.context.nextRmb()
        self.context.setSlowAnimation()
        
class LMB(Action, vob.mouse.MouseClickListener):
    def clicked(self, x,y):
        self.context.setSlowAnimation()
        vs = self.context.oldVS

        node = None
        nodeCS = vs.getCSAt(0, x,y, None)
        if nodeCS > 0:
            node = vs.matcher.getKey(nodeCS)
            # because enlarged CS are used to make it easier
            # to "hit" the nodes, the real CS must be first
            # digged out
#            parentCS = vs.matcher.getParent(nodeCS)
#            if parentCS > 1:
#                nodeCS = parentCS
#                node = vs.matcher.getKey(nodeCS)
        else:
            self.context.c.setAccursed(None);
            return

        #p('LBM click:', node)

        rmb = self.context.rmb_switch[0]
        main = self.context.main
        cursor = self.context.c
        if rmb == 'go':
            cursor.setAccursed(node)

            ### something easier to allow setOffset(x,y) before rendering
            nodeview = ff.view.TextNodeView(self.context.contentFunction.getCallableFunction(), \
                                            self.context.style, 250.)
            self.context.c.setTextPlaceable(nodeview.f(fen.graph, node))

            # set cursor, transitions between zoomed and normal should be accounted
            into = jarray.zeros(3, 'f')
            vs.coords.transformPoints3(nodeCS, [0,0,0], into)

            scale = main.getFocus().getZoom()

            # scaling
            nodeScale = ff.fenmm.MMGeometry.getTextScale(int(100+into[2]))

            try:
                self.context.components.remove(node)
                self.context.components.append(main.getPlane())
                cursor.setOffset((x-into[0])/nodeScale, (y-into[1])/nodeScale)
            except ValueError:
                cursor.setOffset((x-into[0])/scale/nodeScale, (y-into[1])/scale/nodeScale)
            focus = main.getFocus()

            main.setNewPlane(node, focus.getPanX(), focus.getPanY(), focus.getZoom())

        elif rmb == 'link':
            if node == main.getPlane(): pass
            elif self.context.rmLinkTo(node, main.getPlane()) == 0:
                self.fen.graph.add(main.getPlane(), STRUCTLINK.linkedTo, node)
                try:
                    self.context.components.remove(node)
                    self.context.c.setAccursed(None)
                except ValueError: pass

class ZoomPan(vob.input.RelativeAxisListener, Action, vob.mouse.MousePressListener):
    def pressed(self, x,y):
        return self
    def changedRelative(self, x):
        self.context.main.changeZoom(x)
        self.context.main.chgFast(self.context.oldVS, -1)
        self.context.fastMouseChg = 1
        self.context.setFastAnimation()

class PanMover(Action, vob.mouse.RelativeAdapter, vob.mouse.MousePressListener):
    def pressed(self, x,y):
        return self
    def changedRelative(self, x,y='foo'):
        f = self.context.main.getFocus()
        fX, fY = f.getPanX(), f.getPanY()
        f.setPan(fX-x/f.zoom,
                 fY-y/f.zoom)
        self.context.main.chgFast(self.context.oldVS, -1)
        self.context.setFastAnimation()

class MMScene:
    def __init__(self):
        alphContent = ff.util.AlphContent(fen, ff_structure)
        components, centered = self.compinit(alphContent)

        self.context = Context(fen, w, components, multiplexer, style)
        self.context.alphContent = alphContent

        self.mindMouse = vob.mouse.MouseMultiplexer()
        self.mindMouse.setListener(3, 0, 'Right mouse button switching.', RMB(fen, self.context))
        self.mindMouse.setListener(3, 0, self.mindMouse.VERTICAL, 1.0, \
                          'Zooming the main view.', ZoomPan(fen, self.context))
        self.mindMouse.setListener(1, 0, 'Left mouse button action.', LMB(fen, self.context))
        self.mindMouse.setListener(1, 0,'Moving the pan around or the node if accursed.', \
                          PanMover(fen, self.context))

        self.view = ff.view.lava.mindMapView2D.MindMapView2D(fen, self.context)
        self.main = ff.view.lava.mindMapView2D.MindMapMainNode2D(fen, centered, self.context,
                                                                 self.view, self.mindMouse)
        self.context.main = self.main

        dimensions = w.getSize()
        self.view.filletLength = dimensions.width * 1./4
        self.view.filletWidth = dimensions.height * 1./15
        self.view.depth = 3

        # flag for cursor('wait')
        self.startup = 1 

        ### custom controller support
        self.naxes = 0
        self.calibrating = 0
        
        try:
            self.ps2 = vob.input.impl.PS2MouseDevice( \
                       java.lang.System.getProperty("cc.device", ""), \
                       "main", vob.input.impl.PS2MouseDevice.IMPS_PROTO)

            self.naxes = len(self.ps2.getAxes())
            if self.naxes > 3: self.naxes = 3
            self.axes = [None, None, None]

            if self.naxes >= 1:
                self.axes[0] = vob.input.impl.StandardBoundedFloatModel(1, self.view.filletWidth*2,
                    actionPerformed = lambda x: vob.AbstractUpdateManager.chg())
            if self.naxes >= 2:
                self.axes[1] = vob.input.impl.StandardBoundedFloatModel(1, self.view.filletLength*2,
                    actionPerformed = lambda x: vob.AbstractUpdateManager.chg())
            if self.naxes >= 3:
                self.axes[2] = vob.input.impl.WrappingBoundedFloatModel(0, Math.PI*2, 
                    actionPerformed = lambda x: vob.AbstractUpdateManager.chg())

            for i in range(0,self.naxes):
                self.ps2.getAxes()[i].setMainListener(
                vob.input.BoundedFloatLinearAbsoluteAdapter(self.axes[i]))
        except: pass # java.io.FileNotFoundException: pass
#       if self.naxes >= 3: self.view.initRotation = None

    def compinit(self, alphContent):
        """Finds mindmap components; if none, creates one.
        Returns tuple: the first element is a list of other components but
                       the second element, which is the largest component"""
        # find all nodes of mindmap node type
        nodes1 = fen.graph.findN_X11_Iter(RDF.type, MINDSTRUCT.Data)
        # find subjects of link property (objects are in same components)
        nodes2 = fen.graph.findN_X1A_Iter(STRUCTLINK.linkedTo)
        # search nodes of both finds for components
        nodes = Traversals.concat(nodes1, nodes2)
        comps, largest = Traversals.findComponents(nodes, STRUCTLINK.linkedTo, fen.graph)
        components = []
        if largest == None:
            # No components, so create one for focus
            largest = ff.util.RDFUtil.N(fen.graph, MINDSTRUCT.Data)
            alphContent.setText(largest, 'Home', 1)
        else:
            # convert from java.util.Set to list
            for component in comps.toArray():
                components.append(component)
            components.remove(largest)
        return components, largest

    def scene(self, vs):
        if self.context.replaceVS:
            VobScene = self.context.replaceVS
            self.context.replaceVS = None
            return VobScene
        self.context.oldVS = vs
        
#        if self.view.initRotation and self.naxes >= 3\
#           and Math.abs(self.view.initRotation + self.axes[2].getValue()) > 0.001:
#            if hasattr(self.view, "zoomPanCS"):
#                self.view.initRotation = -self.axes[2].getValue()
#                vs.coords.rotate(self.view.zoomPanCS, self.view.initRotation + self.axes[2].getValue())
#                vob.AbstractUpdateManager.setNoAnimation()
#                return vs
#        else: self.view.initRotation = 0

        # custom controller support
        if self.naxes >= 1: self.view.filletWidth = self.axes[0].getValue()
        if self.naxes >= 2: self.view.filletLength = self.axes[1].getValue()
        if self.naxes >= 3: self.view.initRotation = -self.axes[2].getValue()
        
        accursed = self.context.c.getAccursed()
        # adding accursed to multiplexer
        set = java.util.HashSet()
        if accursed:
            set.add(accursed)
            self.context.multiplexer.setMultiplexerNodes(set)
        else: self.context.multiplexer.setMultiplexerNodes(set)

        vs.put(background(self.context.bgcolor))
        dimensions = w.getSize()
        cs = vs.orthoBoxCS(0, "FenMM",0, 0,0, 1,1, dimensions.width, dimensions.height)
        self.main.renderMain(vs, cs)

        # draw cursor
        if accursed:
            nodeview = ff.view.TextNodeView(self.context.contentFunction.getCallableFunction(), \
                                            style, 250.)
            if hasattr(self.view, "zoomPanCS") \
               and (structLink.isLinked(accursed) or self.context.main.getPlane() == accursed):
                    self.context.c.render(vs, self.view.zoomPanCS, cs, nodeview.f(fen.graph, accursed))
            else:
                self.context.c.render(vs, cs, cs, vs.matcher.getCS(cs, accursed), \
                                      nodeview.f(fen.graph, accursed))

        if self.startup: w.setCursor('default'); self.startup = 0

    def mouse(self, ev):
        if ev.getType() in [vob.VobMouseEvent.MOUSE_CLICKED,
                            vob.VobMouseEvent.MOUSE_DRAGGED]:
            if self.main.mouse(ev, self.context.oldVS):
                vob.AbstractUpdateManager.setNoAnimation()
            vob.AbstractUpdateManager.chg()
        else:
            self.main.mouse(ev, self.context.oldVS)
        
    def key(self, key):
        self.context.setSlowAnimation()
        p("Entered key: ", key)
        if key == 'Escape' or key == 'Tab':
            """Removes the focus from the current accursed node and sets no new accursed node."""
            self.context.c.setAccursed(None)
        elif key == 'Backspace':
            """Removes a character before the cursor."""
            if self.context.c.getAccursed() != None \
                   and self.context.c.getOffset() > 0:
                self.context.deleteText()
        elif key == 'Delete':
            """Removes a character next to the cursor."""
            accursed = self.context.c.getAccursed()
            offset = self.context.c.getOffset()
            if accursed:
                text = self.context.alphContent.getText(accursed)
                if offset < len(text):
                    self.context.c.setOffset(offset+1)
                    self.context.deleteText()
        elif key == "Ctrl-S":
            """Save the structure."""
            p("going to save")
            ff.swamp.writer.write(fen.graph, FILE)
        elif key == "Alt-Q":
            """Quit without saving."""
            java.lang.System.exit(43)
        elif key == "Ctrl-Q":
            """Save the structure and quit."""
            p("going to save")
            ff.swamp.writer.write(fen.graph, FILE)
        elif key == "Return":
            """Add a linebreak."""
            self.context.insertText("\n")
            text = self.context.alphContent.getText(self.context.c.getAccursed())
            # small hack to show the new line on NodeView, without content it would be shrank
            offset = self.context.c.getOffset()
            if len(text) == offset:
                self.context.insertText(" ")
                self.context.c.setOffset(offset)
        elif key == 'Up':
            """Move the cursor to the previous line."""
            self.context.c.moveUp()
	elif key == 'Down':
            """Move the cursor to the next line."""
            self.context.c.moveDown()
	elif key == 'Left':
            """Move the cursor a character to the left."""
            self.context.c.moveLeft()
        elif key == 'Right':
            """Move the cursor a character to the right."""
            self.context.c.moveRight()
        elif key == "Ctrl-HomE":
            """Move the cursor into the beginning of the text."""
            self.context.c.moveBegin()
        elif key == "Ctrl-EnD":
            """Move the cursor to the end of the text."""
            self.context.c.moveEnd()
        elif key == "Ctrl-C" or key == "Ctrl-K":
            """Copy the content of the node into the clipboard."""
            text = self.context.alphContent.getText(self.context.c.getAccursed())
            PUIClipboard.puiCopy(text)
            print 'PUI copied:', PUIClipboard.getText()
        elif key == "Ctrl-V" or key =="Ctrl-Y":
            """Paste the content the clipboard just after the cursor."""
            print 'PUI to paste:', PUIClipboard.getText()
            self.context.insertText(PUIClipboard.getText())
        elif key == "Home" or key == "Ctrl-A":
            """Move the cursor into the beginning of the line."""
            self.context.c.moveBeginLine()
        elif key == "End" or key == "Ctrl-E":
            """Move the cursor to the end of the line."""
            self.context.c.moveEndLine()
        elif key == "Ctrl-2":
            """Set 2D filleting."""
            self.view.depthColor = 0
            self.view.fillet3d = 0
            self.main.floatingView.depthColor = 0
            self.main.floatingView.fillet3d = 0
        elif key == "Ctrl-3":
            """Set 3D filleting."""
            self.view.depthColor = 1
            self.view.fillet3d = 1
            self.main.floatingView.depthColor = 1
            self.main.floatingView.fillet3d = 1
        elif key == "Ctrl-B":
            """Buffer accursed node."""
            if structLink.isLinked(self.context.c.getAccursed()):
                self.context.components.append(self.context.c.getAccursed())
        elif key == "Ctrl-D":
            """Delete accursed node."""
            self.context.rmNode(self.context.c.getAccursed())
        elif key == "Ctrl-0":
            """Set calibrating state of custom controller on / off."""
            if self.calibrating and self.naxes: 
                p("Custom controller calibrated.")
                self.calibrating = 0
                for axe in self.ps2.getAxes():
                    axe.setState(vob.input.InputDeviceManager.STATE_NORMAL)
            elif self.naxes:
                p("Calibrate custom controller. Please, move axes to their extreme positions.")
                self.calibrating = 1
                for axe in self.ps2.getAxes():
                    axe.setState(vob.input.InputDeviceManager.STATE_CALIBRATING)
        elif len(key) == 1:
            """Enter a character into the cursor position."""
            self.context.insertText(key)

        if dbg:
            text = self.context.alphContent.getText(self.context.c.getAccursed())
            print text[0:self.context.c.getOffset()], '[cursor]'
        

currentScene = MMScene()
vob.putil.demo.usingNormalBindings = 0
