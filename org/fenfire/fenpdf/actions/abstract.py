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


from org.fenfire.swamp import Nodes

class AbstractActions:
    """Primitive actions that change the structure.

    For use by the other .actions classes as handy 
    primitives.
    """

    def __init__(self, fenPDF):
	self.fenPDF = fenPDF

    def createNewNode(self, canvas, x, y):
	"""Create an empty node on the canvas at x,y and return it.
	"""
	node = Nodes.N()
	self.fenPDF.structure.canvas2d.placeOnCanvas(
	    canvas, node, x, y)
	self.fenPDF.structure.alphContent.setText(
	    node, "", 1)
	return node


