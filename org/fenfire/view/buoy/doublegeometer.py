# 
# Copyright (c) 2003, Tuomas J. Lukka, Matti J. Katila
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
from org import fenfire as ff

dbg = 0
def p(*s):
    print 'fenfire.view.doublegeometer::',s

class DoubleGeometer(vob.buoy.FocusViewPortsGeometer):
    """A class for handling the vertically split view in fenpdf.
    """
    def __init__(self):
	self.split = .5
	self.height = 100
	self.width = 100
	self.single = 0
    def _doset(self, vs):

        w,h = vs.size.width, vs.size.height

	#s0 = self.split ** .5
	#s1 = (1-self.split) ** .5

	#mainsize = w * .9
	#w0 = (self.split ** 0.5) * mainsize
	#w1 = ((1-self.split) ** 0.5) * mainsize


        #if self.split < .5:
        #    w0 = mainsize / s0
        #    w1 = mainsize * (1-self.split) / s1
        #    h0 = self.split * h
        #    h1 = (1-self.split+.25) * h
        #else:
        #    w0 = mainsize * self.split / s0
        #    w1 = mainsize / s1
        #    h0 = (self.split+.25) * h
        #    h1 = (1-self.split) * h

        #w0 = mainsize * self.split / s0
        #w1 = mainsize / s1
        #h0 = (self.split+.25) * h
        #h1 = (1-self.split) * h

	if not self.single:

	    h0 = h * .66

	    h1 = h - h0

	    y1 = h0 * 1.03
	    h0 *= .97

	    s0 = 1.
	    s1 = .8

	    vs.coords.setOrthoBoxParams(self.mainbox1, 1,
			    0, 20, s0, s0, w/s0, h0/s0);
	    vs.coords.setOrthoBoxParams(self.mainbox2, 1,
			    0, y1, s1, s1, w/s1, h1/s1);

	else:
	    
	    vs.coords.setOrthoBoxParams(self.mainbox1, 1,
			    0, 0, 1, 1, w, h);


    def setSingle(self, flag):
	"""Set whether to show only topmost view.
	"""
	self.single = flag
    def getSingle(self):
	return self.single
	
    def place(self, vs, coordinatesArray):
	self.height = vs.size.height
	self.width = vs.size.width

	self.mainbox1 = vs.orthoBoxCS(0,"MainFrame1", 0, 0, 0, 0, 0, 0, 0);

	if not self.single:
	    self.mainbox2 = vs.orthoBoxCS(0,"MainFrame2", 0, 0, 0, 0, 0, 0, 0);
	else:
	    self.mainbox2 = -1

	self._doset(vs)

        if len(coordinatesArray) >= 2:
            coordinatesArray[0] = self.mainbox1
            coordinatesArray[1] = self.mainbox2
