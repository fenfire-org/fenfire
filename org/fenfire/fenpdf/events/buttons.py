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


from org import fenfire as ff
import vob

class ActionButtons:
    def render(self, vs, into):
        self.mousebuttons = cs = vs.translateCS(0, "BUTTONS",0,0)
	b = ff.view.lava.Button(vs, cs, 30, 0, 50);
        b.add("[Import PS/PDF]", "IMPORT");
        b.add("[New paper]",     "NEW_PAPER");
	b.add("[Save]", "SAVE");
    def mouseEvent(self, ev, oldVS):
        """ Return true if we have eaten the event ;)"""

        if ev.getType() != ev.MOUSE_CLICKED: return 0;

        key = oldVS.getKeyAt(self.mousebuttons, ev.getX(), ev.getY(), None)
        if key == None: return 0

        if key == 'IMPORT':
	    self.fenPDF.actions.key.importPDF()
            return 1
        elif key == 'NEW_PAPER':
	    self.fenPDF.actions.key.newCanvas()
            return 1
	elif key == "SAVE":
	    self.fenPDF.actions.global.save() 
            return 1
        else:
            p('key', key)
            return 0
    


