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


"""Methods that deal with importing PS/PDF files for FenPDF
"""

import sys
import java
from org import fenfire as ff
from org.nongnu import storm

def p(*s):
    print 'importpdf.py::', s

def importPDF_select(fen, treetime):
    """Show a file selection dialog and import a file.

    Returns a sequence of ScrollBlock objects imported.
    """
    
    file = selectFile("Select PS/PDF file to import, or directory for all files in it")
    p("ps/pdf to be added: ",file)

    if isinstance(file, storm.BlockId):
        id = file
        addToTreetime(fen, treetime, id)
        return [fen.alph.getScrollBlock(id.getURI())]
    
    if file == None or not file.exists():
        print "File doesn't exist"
        return []

    lastFile = None
    if file.isDirectory():
	blocks = []
	for f in file.listFiles():
	    try:
		if f.isDirectory(): continue
		blocks.append(
		    importPDF(fen, treetime, f)
		)
	    except :
		print "EXCEPTION LOADING",f,":",sys.exc_info()
	blocks = [b for b in blocks if b != None ]
	return blocks
    else:
	return [importPDF(fen, treetime, file)]

def selectFile(string):
    """Select a file using an AWT dialog, with the given prompt.

    Returns a java.io.File or an org.nongnu.storm.BlockId object
    (the latter if the filename parses as a Storm id).
    """
    f = java.awt.Frame()
    fd = java.awt.FileDialog(f, string,
				 java.awt.FileDialog.LOAD)
    fd.setModal(1)
    fd.show()  #// now wait until user makes a desicion
    fd.hide()

    if fd.getDirectory() == None:
        return None
    fileName = fd.getDirectory()+fd.getFile()

    try:
        pos = fileName.lower().index("vnd-storm-")
        return storm.BlockId(fileName[pos:])
    except ValueError:
        return java.io.File(fileName)

def importPDF(fen, treetime, file):
    """Really import a PDF file from the given java.io.File
    object.

    If treetime is non-None, adds as the latest of that
    treetime object.

    BUGS: assumes PDF
    """

    f = open(str(file), 'rb')
    type = f.read()[0:4]
    f.close()
    
    if type.endswith('PDF'): p('Importing PDF'); scrollBlock = fen.alph.addFile(file, 'application/pdf')
    elif type.endswith('PS'): p('Importing PostScript'); scrollBlock = fen.alph.addFile(file, 'application/postscript')
    elif type.endswith('PNG'): p('PNG not yet supported!'); scrollBlock = None
    elif type.startswith('GIF'): p('GIF not yet supported!'); scrollBlock = None
    # XXX JPEG?!?
    elif type.startswith('\xff\xd8\xff\xe0'): p('JPEG not yet supported!'); scrollBlock = None

    if scrollBlock == None: return

    addToTreetime(fen, treetime, scrollBlock.getID())

    return scrollBlock


def addToTreetime(fen, treetime, id):
    if treetime != None:
	try:
	    treetime.addLatest(fen.graph, 
			ff.swamp.Nodes.get(id))
	except:
	    p("Exception in treetime ",sys.exc_info())

