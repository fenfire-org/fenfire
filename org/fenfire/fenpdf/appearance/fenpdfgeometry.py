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


import vob, java
from org import fenfire as ff

class FenPDFGeometryConfiguration(vob.buoy.BuoyGeometryConfiguration):
    """This class decides everything about how buoys and main views
    in FenPDF are placed.
    """
    def __init__(self):

	self.nadirManager = vob.buoy.NadirManager(.5, 3, "NADIRMANAGER")

	w = .75
	h = 1
	self.buoyMainViewGeometer = vob.buoy.impl.RatioMainGeometer(
					.5-w/2., .5-h/2., w, h)

	self.buoySizer = vob.buoy.impl.AspectBuoySizer(400, 400, 1.5)

	w = .9
	h = 1.05
	self.normalBuoyGeometer = vob.buoy.impl.RatioBuoyOnCircleGeometer(
				    .5-w/2., .5-h/2., w, h)
	self.normalBuoyGeometer.nadirManager = self.nadirManager

	self.treetimeBuoyGeometer = vob.buoy.impl.FlatMarginGeometer()
        
        self.setOfGeometers = java.util.HashSet()
        self.setOfGeometers.add(self.normalBuoyGeometer)
        self.setOfGeometers.add(self.treetimeBuoyGeometer)

    def getMainViewGeometer(self, node):
	return self.buoyMainViewGeometer

    def getSizer(self, node, connector):
	return self.buoySizer

    def getGeometer(self, node, connector):
	# print "GetGeometer:",connector
	if isinstance(connector, ff.view.buoy.TTConnector):
	    # print "WAS TT!"
	    return self.treetimeBuoyGeometer
	else:
	    return self.normalBuoyGeometer

    def getGeometers(self, node):
        return self.setOfGeometers


