# 
# Copyright (c) 2003, Tuomas J. Lukka, Matti Katila and Benja Fallenstein
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


""" Keyboard events.

This class determines which action to call for which
key.

This class SHALL NOT KNOW ANYTHING about the internals 
of FenPDF!

"""
from org import fenfire as ff
from org.fenfire.spanimages.gl import papermakers
from org.fenfire.swamp import Nodes

dbg = 0

def p(*s):
    print 'ff.fenpdf.events.key::', s

class KeyHandler:
    def key(self, key, main):
        if dbg: p('KEY', key,', v:',main)

        ### normal alphabets etc.
        if len(key) == 1:
	    self.fenPDF.actions.key.insertCharOrCreate(main, key)

	# XXX Need to get rid of these -- 
	# needed for bolding and italics
        elif key == 'Ctrl-B':
	    self.fenPDF.actions.key.toggleBgTextureUse()

        elif key == 'Ctrl-I':
	    self.fenPDF.actions.key.changeBgPaperMaker()

	# XXX Need to change this -- needed for saving!
	elif key == 'Ctrl-S':
	    self.fenPDF.actions.key.toggleZoomToSingle(main)

#	# XXX Illegal extension!
#	elif key == 'Ctrl-R':
#	    self.fenPDF.actions.key.switchFoci()

        elif key == "Home":
	    self.fenPDF.actions.key.goToHome(main)

        elif key == "Ctrl-Q":
            p("going to save");
	    self.fenPDF.actions.key.save()
	    self.fenPDF.actions.key.exit()

        elif key == 'Return':
            if dbg: p('Insert \\n')
	    self.fenPDF.actions.key.insertChar(main, "\n")
	elif key == 'Backspace':
	    self.fenPDF.actions.key.backspace(main)
	elif key == 'Left':
	    self.fenPDF.actions.key.moveInsertionCursor(main, -1)
        elif key == 'Right':
	    self.fenPDF.actions.key.moveInsertionCursor(main, 1)

