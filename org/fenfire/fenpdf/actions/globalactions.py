# 
# Copyright (c) 2003, Matti J. Katila
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


import org.fenfire as ff
import vob, java, jarray

"""
Global actions. These actions implement
global actions, e.g., save and load.
"""
class GlobalActions:
    def __init__(self, fenPDF):
        self.fenPDF = fenPDF
        self.demo = None
    def save(self):
	print "SAVING..."
        ff.swamp.writer.write(self.fenPDF.fen.graph, self.fenPDF.uistate.filename)
	print "DONE"
    

