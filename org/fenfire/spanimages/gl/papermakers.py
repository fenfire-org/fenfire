# 
# Copyright (c) 2003, Tuomas J. Lukka and Janne Kujala
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


# PaperMaker implementations

from __future__ import nested_scopes;

from org.nongnu.libvob.gl import GL, PaperMill
from vob.putil.nvcode import parseCombiner

from org.fenfire.spanimages.gl import PlainPaperMaker

# A list of paper makers, as tuples:
# (function, description)
#
# The function returns the suitable parameters for
# Functional.createFunctionInstance, i.e.
# type and list of parameters.
#
# Separated by whether the fancy bg is shown or not.
#
# The function is set to None for those that are
# not available.
#
# This is the more rational way to do it than to leave
# them out - this way we can have grayed-out menu entries
# &c.
whitePaperMakers = []
fancyPaperMakers = []


def getPaperMill(paperMill):
    if not paperMill:
	paperMill = PaperMill.getInstance()
    return paperMill


def white(w = None, paperMill = None, 
	    filter = ""):
    return (PlainPaperMaker, [1, """
	TexEnv TEXTURE_ENV TEXTURE_ENV_MODE REPLACE
       Enable TEXTURE_2D
	Disable BLEND
    """ + filter, """
    """, None])

whitePaperMakers.append( (white, "White background") )

# The programs here are best-first
# The following program gives a 30% improvement 
# to rendering speeds on a GF FX 5600
# by using a biased texture unit instead of
# calculating the bias explicitly in the program.

# Replacing some of the MULH:s with MULX makes
# it faster but it stops working - the results are
# not accurate enough.
# On the 5900, 5700 this should be a lot faster...
# It's sad that we can't take advantage of the fixed-point
# units at all except at the very end.

nvBlurProgram = None
if GL.hasExtension("GL_NV_fragment_program"):
    nvBlurProgram = GL.createProgram("""!!FP1.0
	# Get the blurred value of the text texture
	# Texture unit 2 is blurred
	TEX H2, f[TEX1], TEX2, 2D;

	# Get the sharp value of the text texture
	TEX H3, f[TEX1], TEX1, 2D;

	# Map blurred 'text' texture intensity to background blur
	# as follows:
	#   1 -> no bias
	#   0 -> large bias
	DP4H H2, {-10,-10,-10,31}, H2;

	# The derivatives of the paper texture
	DDXH H0.xy, f[TEX0].xyxy;
	DDYH H0.zw, f[TEX0].xyxy;

	# Scale with the DP
	MULH H0, H2, H0;

	# Get the blurred value of the background texture
	TXD H0, f[TEX0], H0.xyxy, H0.zwzw, TEX0, 2D;


	# Compute the final color

	MULX o[COLR], H0, H3;
	END
    """)

def nvFancyBlur(paperMill = None):
    paperMill = getPaperMill(paperMill)
    if not nvBlurProgram:
	print "Nvidia-optimized Fancy blurring is not possible without GL_NV_fragment_program."
	print "Punting to ARB Fancy blur"
	return fancyBlur(paperMill)

    if paperMill == None:
	paperMill = PaperMill.getInstance()

    return (PlainPaperMaker, [2, """
        BindProgram FRAGMENT_PROGRAM_NV %s
	ActiveTexture TEXTURE2
	TexEnv TEXTURE_FILTER_CONTROL TEXTURE_LOD_BIAS 3.8
	ActiveTexture TEXTURE0
        Enable FRAGMENT_PROGRAM_NV
        Disable REGISTER_COMBINERS_NV
        Disable BLEND
    """ % nvBlurProgram.getProgId(),
    """
	ActiveTexture TEXTURE2
	TexEnv TEXTURE_FILTER_CONTROL TEXTURE_LOD_BIAS 0
	ActiveTexture TEXTURE0
    """, [nvBlurProgram], paperMill])

if nvBlurProgram:
    b = nvFancyBlur
else:
    b = None

fancyPaperMakers.append( (b, "(NVIDIA-optimized) Blurring background under text") )


blurProgram = None

if GL.hasExtension("GL_ARB_fragment_program"):
    blurProgram = GL.createProgram("""!!ARBfp1.0
    # Computes color = tex0 * tex1 with
    # tex0 blurred near non-white parts of tex1.

    TEMP coord1;
    MOV coord1, fragment.texcoord[1];
    MOV coord1.w, 3.8;

    # Filter the 'text' texture with and without blurring
    TEMP tex1, tex1b;
    TEX tex1, coord1, texture[1], 2D;
    TXB tex1b, coord1, texture[1], 2D;

    TEMP coord0;
    MOV coord0, fragment.texcoord[0];

    # Map blurred 'text' texture intensity to background LOD bias
    # as follows:
    #   1 -> no bias
    #   0 -> large bias
    DP4 coord0.w, {-7,-7,-7,21}, tex1b;

    # Filter background texture with the proper LOD bias
    TEMP tex0;
    TXB tex0, coord0, texture[0], 2D;

    MUL result.color, tex0, tex1;
    END
    """)

def fancyBlur(paperMill = None):
    paperMill = getPaperMill(paperMill)
    if not blurProgram:
	print "Fancy blurring is not possible without GL_ARB_fragment_program."
	print "Punting to Fancy halo"
	return fancyHalo(paperMill)

    if paperMill == None:
	paperMill = PaperMill.getInstance()

    return (PlainPaperMaker, [1, """
        BindProgram FRAGMENT_PROGRAM_ARB %s
        Enable FRAGMENT_PROGRAM_ARB
        Disable REGISTER_COMBINERS_NV
        Disable BLEND
    """ % blurProgram.getProgId(),
    """
    """, [blurProgram], paperMill])

if blurProgram:
    b = fancyBlur
else:
    b = None

fancyPaperMakers.append( (b, "Blurring background under text") )

class HaloPaperMaker_2tex(PlainPaperMaker):
    def __init__(self, paperMill):
	PlainPaperMaker.__init__(self, 1, 
	     parseCombiner("""
		ActiveTexture TEXTURE0
		Enable TEXTURE_2D
		%(filter)s
		Disable BLEND
		ActiveTexture TEXTURE1
		Enable TEXTURE_2D
		%(filter)s
		TexEnv TEXTURE_FILTER_CONTROL TEXTURE_LOD_BIAS 3
		ActiveTexture TEXTURE0

		Enable REGISTER_COMBINERS_NV
		CONST0 = 0 0 0 .5
		CONST1 = 0 0 0 .15

		# Get luminance multiplied by 12 * CONST1.a
		SPARE0 = ((1-TEX1) . (CONST1.a))*4

		# Multiply by another 3
		SPARE0 = (SPARE0 . (1))*1

		# Limit maximum effect
		EF = SPARE0 * CONST0.a

		# Blend the halo over the texture
		color = EF * (1) + (1 - EF) * TEX0
		alpha = 1
	    """ % { "filter" : "" }),
	    """
	    """,
	    None, paperMill)
    def makePaper(self, img, texgen):
	p = PlainPaperMaker.makePaper(self, img, texgen)
	#print p.getPass(0).getSetupcode()
	p.setNPasses(2)
	pas = p.getPass(1)
	pas.setNTexGens(1)
	pas.putNormalTexGen(0, texgen)
	pas.setSetupcode("""
	    PushAttrib ENABLE_BIT COLOR_BUFFER_BIT TEXTURE_BIT
	    ActiveTexture TEXTURE0
	    Enable TEXTURE_2D
	    Enable BLEND
	    BlendFunc ZERO SRC_COLOR
	    TexEnv TEXTURE_ENV TEXTURE_ENV_MODE REPLACE
	""" )

	pas.setNIndirectTextureBinds(1)
	pas.putIndirectTextureBind(0, "TEXTURE0", "TEXTURE_2D", img.virtualTexture.indirectTexture)

	pas.setTeardowncode("""
	    PopAttrib
	    BindTexture TEXTURE_2D 0
	""")
	return p


def fancyHalo(paperMill = None):
    paperMill = getPaperMill(paperMill)
    if not GL.hasExtension("GL_NV_register_combiners"):
	print "fancy Halo for text not possible without GL_NV_register_combiners"
	print "Punting to standard blend"
	return fancyBlend(paperMill)
    if paperMill == None:
	paperMill = PaperMill.getInstance()
    return (HaloPaperMaker_2tex, [paperMill])

if GL.hasExtension("GL_NV_register_combiners"):
    b = fancyHalo
else:
    b = None

fancyPaperMakers.append( (b, "Brightening (bleaching) background under text") )

def fancyBlend(paperMill = None,
	    filter = ""):
    paperMill = getPaperMill(paperMill)
    return (PlainPaperMaker, [1, 
	"""
	    ActiveTexture TEXTURE0
	    TexEnv TEXTURE_ENV TEXTURE_ENV_MODE REPLACE
	    Enable TEXTURE_2D
	    ActiveTexture TEXTURE1
	    TexEnv TEXTURE_ENV TEXTURE_ENV_MODE MODULATE
	    Enable TEXTURE_2D
	    %s
	    Disable BLEND
	    ActiveTexture TEXTURE0
	""" % filter, "", None, paperMill])

fancyPaperMakers.append( (fancyBlend, "No enhancement, Just rendering text on top of Bg (Not recommended)") )


allPaperMakers = whitePaperMakers + fancyPaperMakers

