/*
Pannables.java
 *    
 *    Copyright (c) 2004, Matti J. Katila
 *    This file is part of Fenfire.
 *    
 *    Fenfire is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Fenfire is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Fenfire; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 */
/*
 * Written by Matti J. Katila
 */

package org.fenfire.view.management;
import org.fenfire.swamp.cloudberry.*;
import org.fenfire.util.*;
import org.fenfire.modules.init.Settings;

import org.nongnu.libvob.*;
import org.nongnu.libvob.impl.awt.*;
import org.nongnu.libvob.vobs.*;
import org.nongnu.libvob.input.*;
import org.nongnu.libvob.mouse.*;
import java.util.*;

/** Common utilities for panable views.
 */
public class Pannables {
    static private void p(String s) { System.out.println("Panables:: "+s); }

    static public void addActions(Action a, final FServer f, final Pannable p) {
	// from settings get bindings to move view port
	/*
	int button = Settings.getInt(f,
				     ViewPortable.ViewPort,
				     ViewPortable.moveXY,
				     ViewPortable.button,
				     ViewPortable.moveXY_button_default);
	int modifier = Settings.getInt(f,
				       ViewPortable.ViewPort,
				       ViewPortable.moveXY, 
				       ViewPortable.modifier,
				       ViewPortable.moveXY_modifier_default);
	*/
 	a.setListener(1,0, "move view pan", move(f,p));
   }
	
    
    static MousePressListener move(final FServer f, final Pannable p) {
	return new MousePressListener() {
		public MouseDragListener pressed(int x, int y) {
		    return new RelativeAdapter(){
			    public void changedRelative(float x, float y) {
				float zoom = 1.0f;
				if (p instanceof Zoomable)
				    zoom = ((Zoomable)p).getZoom();
				p.setPan(p.getPanX() - x/zoom, 
					 p.getPanY() - y/zoom);
				p.setCoordsysParams();
				f.getScreen().rerender();
			    }
			};
		}
	    };
    }
}
