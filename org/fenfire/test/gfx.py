# 
# Copyright (c) 2003, Tuomas J. Lukka
# 
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
# 


from jarray import array, zeros

from java.awt import Color
from org.nongnu.libvob import *
from org.nongnu.libvob.vobs import *

import org

class GLNeeded:
    pass

def needGL():
    if org.nongnu.libvob.GraphicsAPI.getInstance().getTypeString() != "gl":
	raise GLNeeded()

print "Init test.gfx"

_didRender = 0

_realwin = org.nongnu.libvob.GraphicsAPI.getInstance().createWindow()
_realwin.setLocation(0, 0, 600, 600)

if org.nongnu.libvob.GraphicsAPI.getInstance().getTypeString() == "gl":
    from org.nongnu.libvob.gl import GL
    if GL.workaroundStupidBuggyAtiDrivers:
	# Sorry, ATI doesn't let us use pbuffers on R300 except in FireGL.
	# Because of that, don't put another window in front when using
	# this.
	win = _realwin
    else:
	win = org.nongnu.libvob.GraphicsAPI.getInstance().createStableOffscreen(500, 500)
	from org.nongnu.libvob.gl import GL, GLCache, GLRen
	_buf = GL.createByteVector(500*500*3)
	_drawbufvs = _realwin.createVobScene()
	_drawbufvs.map.put(SolidBackdropVob(Color(0, 0, 0.2)))
	_drawbufvs.map.put(GLCache.getCallList("""
	    Disable TEXTURE_2D
	    Color 1 1 1 1
	"""))
	cs = _drawbufvs.translateCS(0, "tr", 0, 501)
	_drawbufvs.map.put(
	    GLRen.createDrawPixels(
		500, 500,
		"RGB", "UNSIGNED_BYTE", _buf),
		cs)
else:
    win = _realwin

# print "GW: ",win

def failUnless(b, msg=None):
    if not b:
	raise str(("FU ",msg))

def getvs():
    return win.createVobScene()

def render(vs):
    global _didRender
    _didRender = 1
    win.renderStill(vs, 0)
    if win != _realwin:
	_buf.readFromBuffer(win.getRenderingSurface(), 
		    "FRONT", 0, 0, 500, 500,
			"RGB", "UNSIGNED_BYTE")
	_realwin.renderStill(_drawbufvs, 0)
	

def getAvgColor(x, y, w, h):
    
    colors = win.readPixels(x, y, w, h)
    color = org.nongnu.libvob.util.ColorUtil.avgColor(colors)
    return [c*255 for c in color.getComponents(None)]

def checkAvgColor(x, y, w, h, color, delta=10):
    real = getAvgColor(x, y, w, h)
    msg = str((color, real, ":", x, y, w, h))

    for i in range(0,3):
	if abs(color[i]-real[i]) > delta:
	    raise msg


def checkNotAvgColor(x, y, w, h, color, delta=10):
    real = getAvgColor(x, y, w, h)
    msg = str((color, real, ":", x, y, w, h))

    for i in range(0,3):
	if abs(color[i]-real[i]) > delta:
	    return

    raise msg


def checkNoTrans(vs, cs):
    """Check that a transformation is singular with the 
    current coords.
    """
    src = array([0,0,0], 'f')
    dst = vs.coords.transformPoints3(cs, src, None)
    failUnless(dst == None)

def checkTrans(vs, cs, srclist, dstlist, delta=0, alsoRender = 1):
    """Check that a transformation works a certain way.
    """
    src = array(srclist, 'f')
    dst = vs.coords.transformPoints3(cs, src, None)
    failUnless(dst != None)
    for i in range(0, len(src)):
	if abs(dst[i]-dstlist[i]) > delta:
	    raise str([srclist, dstlist, dst, i, dst[i], dstlist[i]])
    if alsoRender:
	for i in range(0, len(src), 3):
	    vs.map.clear()
	    vs.map.put(SolidBackdropVob(Color.red))
	    d = TestSpotVob(src[i], src[i+1], src[i+2])
	    vs.map.put(d, cs)
	    render(vs)
	    checkNotAvgColor(
		int(dstlist[i])-1, int(dstlist[i+1])-1,
		3, 3, (255, 0, 0), delta=50)

def checkInterp(vs1, vs2, i, fract, cs, srclist, dstlist, delta=0):
    src = array(srclist, 'f')
    dst = zeros(len(src), 'f')
    if not vs1.coords.transformPoints3_interp(i, vs2.coords, fract, 0, cs, src, dst):
	raise str(("transformpoints for checkinterp not done!", vs1, vs2, cs))
    for i in range(0, len(src)):
	if abs(dst[i]-dstlist[i]) > delta:
	    raise str([srclist, dstlist, dst, i, dst[i], dstlist[i]])


