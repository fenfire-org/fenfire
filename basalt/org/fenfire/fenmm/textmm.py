# 
# Copyright (c) 2003, Tuukka Hastrup
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

# This demo is for text-mode FenMM - Mind Mapping on Fenfire

import java, jarray

import org.fenfire as ff

from org.fenfire.vocab import *
from org.fenfire.vocab.lava import *
from org.fenfire.swamp import Nodes
from org.fenfire.util.lava import Traversals

import org.nongnu.alph as alph
import org.nongnu.storm as storm

# saving and loading
from com.hp.hpl.mesa.rdf.jena.mem import ModelMem
import os.path

# text UI
from sys import argv, stdin, stdout
from traceback import print_exc

def p(*s):
    print 'textmm', s

dbg = 0

# storm pool directory and mindmap file
DIR = 'myFenfire/'
FILE = java.lang.System.getProperty("fenmm.file", DIR+"mindmap.rdf")
POOLDIR = java.lang.System.getProperty("fenmm.pool", DIR)

try:
    FILE = argv[1]
except IndexError:
    pass

os.system('mkdir -p '+POOLDIR)

pool = storm.impl.DirPool(java.io.File(POOLDIR), java.util.HashSet())
myalph = alph.impl.StormAlph(pool)


fen = ff.Fen()

if os.path.isfile(FILE):
    print 'Loading the RDF graph from the file %s.' % (FILE)
    m = ModelMem()
    m.read(java.io.FileReader(FILE), None);
    fen.constgraph = fen.graph = ff.swamp.Graphs.toGraph(m);
else:
    print 'Creating a new RDF graph.'
    fen.graph = fen.constgraph = ff.swamp.impl.HashGraph()

fen.txt = ff.impl.SimpleNodeContent(fen, myalph);
fen.txtfunc = fen.txt.getNodeFunction()
fen.enfiladeOverlap = fen.txt.getTransclusionIndex()

## Not used now
#structLink = ff.structure.StructLink.create(fen.graph)


def save():
    """Save the structure."""
    print "Saving the RDF graph to file %s." % FILE
    ff.swamp.writer.write(fen.graph, FILE);

def quit():
    java.lang.System.exit(0)


class MMScene:
    def compinit(self):
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
            self.alphContent.setText(largest, 'Home', 1)
        else:
            # convert from java.util.Set to list
            for component in comps.toArray():
                components.append(component)
            components.remove(largest)
        return components, largest

    def __init__(self):
        self.fen = fen
        self.alphContent = ff.util.AlphContent(fen)

        self.components, self.focused = self.compinit()
        
    def text(self, node):
        return self.alphContent.getText(self.fen.graph, node)

    def newNode(self, text):
        node = ff.util.RDFUtil.N(fen.graph, MINDSTRUCT.Data)
        if text != None:
            self.alphContent.setText(node, text, 1)
        self.components.append(node)
        return node

    def addNode(self, text):
        node = self.newNode(text)
        self.link(node)

    def editText(self, text):
        if text == None: text = '' # XXX can alph remove text?
        self.alphContent.setText(self.focused, text, 1)

    def neighbours(self, node):
        iter = Traversals.concat(self.fen.constgraph.findN_11X_Iter(node, STRUCTLINK.linkedTo),
                                 self.fen.constgraph.findN_X11_Iter(STRUCTLINK.linkedTo, node))
        list = []
        while iter.hasNext():
            list.append(iter.next())
        return list

    def focus(self, node):
        if node in self.components:
            self.components.remove(node)
            self.components.append(self.focused)
        self.focused = node

    def link(self, node):
        if node in self.components:
            self.components.remove(node)
        self.fen.graph.add(self.focused, STRUCTLINK.linkedTo, node)

    def unlink(self, node):
        self.fen.graph.rm_111(self.focused, STRUCTLINK.linkedTo, node)
        self.fen.graph.rm_111(node, STRUCTLINK.linkedTo, self.focused)
        if not Traversals.isConnected(self.focused, STRUCTLINK.linkedTo, node, self.fen.constgraph):
            self.components.append(node)

scene = MMScene()


def tellFocus():
    print "You are standing on: 0: "+scene.text(scene.focused)
    print

def tellNeighbours():
    print "You can move to: "
    list = scene.neighbours(scene.focused)
    for i in range(len(list)): print "%d: %s" % (i+1, scene.text(list[i]))
    print

def tellComponents():
    print "In the air around you:"
    list = scene.components
    for i in range(len(list)): print "%d: %s" % (-(i+1), scene.text(list[i]))
    print

def tellCommands():
    keys = cmds.keys()
    keys.sort()
    print keys
    print "What, or where to, shall we? "
    print

def look(arg=None, examineOnly=0):
    if arg == None: arg = '' # they don't let me write arg == null || arg.strip
    if arg.strip() == '':
        tellFocus()
        if not examineOnly:
            tellNeighbours()    
    elif arg.strip() == 'around':
        tellComponents()
    else:
        try:
            i = int(arg)
            if i>0:
                print "%d: %s" % (i, scene.text(scene.neighbours(scene.focused)[i-1]))
            elif i<0:
                print "%d: %s" % (i, scene.text(scene.components[-i-1]))
            else:
                tellFocus()
        except ValueError:
            print "I don't see "+arg

def whereabouts():
    tellComponents()
    look()


cmds = {'quit': lambda x:quit(),
        'save': lambda x:save(),
        'help': lambda x:tellCommands(),
        'examine': lambda str:look(str, examineOnly=1),
        'look': look,
        'lookat': look,
        'whereabouts': lambda x:whereabouts(),
        'new': scene.newNode,
        'add': scene.addNode,
        'write': scene.editText,
        'moveto': lambda str:scene.focus(scene.neighbours(scene.focused)[int(str)-1]),
        'changeto': lambda str:scene.focus(scene.components[-int(str)-1]),
        'link': lambda str:scene.link(scene.components[-int(str)-1]),
        'unlink': lambda str:scene.unlink(scene.neighbours(scene.focused)[int(str)-1]),
        }

postsuccess = {'moveto': look,
               'changeto': whereabouts,
               }

print "*"*20
print """Welcome
You stand, and there is free space around you and under your feet.
You might see some writing, and perhaps by writing and creating more and
by linking pieces, you could make this mirror something you have on your mind.

Just one more remark: remember to save every now and then.
"""
print
whereabouts()
tellCommands()

while 1:
    print '*' * 5
    line = ''
    while line == '':
        print "> ",
        line = stdin.readline().lstrip()[:-1]
        print # there's some odd space character output, this hides it
    try:
        i = int(line)
        if i>0:
            line = "moveto "+line
        elif i<0:
            line = "changeto "+line
    except ValueError:
        pass
    cmdend = line.find(' ')
    cmd, rest = None, None
    if cmdend == -1:
        cmd = line
        rest = None
    else:
        cmd = line[:cmdend]
        rest = line[cmdend:].strip()
    try:
        cmds[cmd](rest)
    except KeyError:
        print "I don't know how to "+cmd
        tellCommands()
    except:
        print_exc()
    else:
        try:
            postsuccess[cmd]()
        except:
            pass
