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
from vob.paper.texcache import getCachedTexture

class Background:
    """A class which takes care of the FenPDF background.

    The interface may be implemented in several ways, 
    the most trivial is just to return a SolidBackdropVob with
    a color, but the interesting one is to put a solidBackdropVob
    with null color (not clearing color buffer) and a returning
    a textured quad in the FinalVob.

    """
    def placeBackDrop(self, vs):
	"""This method should be called *before* anything is
	placed in the vobscene.
	"""
	assert 0 == 1
    def placeFinalVob(self):
	"""This method should be called *after* everything is
	placed in the vobscene.
	"""
	pass

class SolidBackground(Background):
    """A solid background with a given color.
    """
    def __init__(self, color):
	self.backdrop = vob.vobs.SolidBackdropVob(color)
    def placeBackDrop(self, vs):
	vs.put(self.backdrop)

class FancyBlueBackground1(Background):
    """A textured blue background.

    The texture is draw *after* the rest of the scene to take
    advantage of hierarchical Z-buffers.

    This means that nothing should be blended on the bare background,
    or funny effects will result.
    """
    def __init__(self):
	# Clear depth & stencil, not color
	# We draw a background texture in the end - better do it
	# there so the overdrawn pixels will be eliminated by
	# various hierarchical Z-buffer solutions.
	self.backdrop = vob.vobs.SolidBackdropVob(None)
	self.bg = getCachedTexture([512,512,0,1,"LUMINANCE", 
			"LUMINANCE", "noise",
		[
		"freq", "30",
		"type", "fBm",
		"bias", "2",
		"scale", "10",
		"fbmgain", ".6",
		"fbmoct", "8",
		"fbmlacu", "1.892",
		]])

	self.bg.setTexParameter("TEXTURE_2D", "TEXTURE_MAX_ANISOTROPY_EXT", "6")

	self.dlist = vob.gl.GLCache.getCallListCoorded("""
	    PushAttrib CURRENT_BIT ENABLE_BIT TEXTURE_BIT
	    Enable TEXTURE_2D
	    Enable COLOR_SUM_EXT
	    Color .1 .1 .2
	    SecondaryColorEXT 0 .85 .9

	    MatrixMode TEXTURE
	    PushMatrix
	    Rotate 45 0 0 1 
	    Scale 5 15 1 
	    Rotate 45 0 0 1 
	    MatrixMode MODELVIEW
	
	    BindTexture TEXTURE_2D %s

	    Begin QUADS
	    TexCoord 0 0
	    Vertex 0 0
	    TexCoord 0 1
	    Vertex 0 1
	    TexCoord 1 1
	    Vertex 1 1
	    TexCoord 1 0
	    Vertex 1 0
	    End

	    MatrixMode TEXTURE
	    PopAttrib
	    PopMatrix
	    MatrixMode MODELVIEW
	""" % self.bg.getTexId())

    def placeBackDrop(self, vs):
	vs.put(self.backdrop)

    def placeFinalVob(self, vs):
	vs.put(self.dlist, 
	    vs.orthoCS(0, "A", 10000, 0, 0, vs.size.width, vs.size.height))
