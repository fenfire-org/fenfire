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


import vob
from org import fenfire as ff
from org.fenfire.fenpdf import actions

"""Map mouse events to actions

This class contains two MouseMultiplexers, one for
main views and another for buoys.
"""

class MouseMapper:
    def __init__(self):
	self.mainMouse = vob.mouse.MouseMultiplexer()
	self.buoyMouse = vob.mouse.MouseMultiplexer()

    def getMainMouseMultiplexer(self):
	return self.mainMouse

    def update(self):

	# We might like to clear the mainMouse of all
	# bindings here, but that's not implemented
	# yet.
	# self.mainMouse.clear() 

	actions = self.fenPDF.actions

	# With button 1
	self.mainMouse.setListener(1, 0,
                                   'Moving the pan around', 
                                   actions.mouse.GrabPressIfSelectedArea(
            actions.mouse.Pan_Fastest(), actions.mouse.NodeMover())
                                   )
	self.mainMouse.setListener(1, vob.VobMouseEvent.SHIFT_MASK,
			      'Selecting area of main view.',
			      actions.mouse.SelectArea())
	self.mainMouse.setListener(1, vob.VobMouseEvent.CONTROL_MASK,
			      'Move the note.',
			      actions.mouse.NodeMover())
	self.mainMouse.setListener(1, 0, 'Browse to clicked point in the main view',
			      actions.mouse.BrowseClick())

	# With button 3
	self.mainMouse.setListener(3, 0, self.mainMouse.VERTICAL, 1.0, 
			      'Zooming the main view.', 
			      actions.mouse.Zoom())
	self.mainMouse.setListener(3, 0, self.mainMouse.HORIZONTAL, 1.0, 
	  'Changing the size of main view (currently only for pagescroll).', 
			      actions.mouse.ChangeSize())
	self.mainMouse.setListener(3, vob.VobMouseEvent.SHIFT_MASK,
			      'Select a node by click or unselect.',
			      actions.mouse.IfSelectNodeOnPlane())
	self.mainMouse.setListener(3, 0, 
				'Show context menu if available (should be).',
			      actions.mouse.MouseMenu())

	# With wheel
	self.mainMouse.setWheelListener(0, 'Scrolling', \
				   actions.mouse.ScrollWheelPan())


	# Default controls for buoys
	# With button 1
	self.buoyMouse.setListener(1, 0, 'Follow the link buoy.',
			      actions.mouse.BuoyFollowClick())

	# With button 3
	self.buoyMouse.setListener(3, 0, 'Show mouse menu for buoy', \
			      actions.mouse.BuoyMouseMenu())


