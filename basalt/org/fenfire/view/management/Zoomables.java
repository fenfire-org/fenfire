/*
Zoomables.java
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

/** Common utilities for view ports.
 */
public class Zoomables {
    static private void p(String s) { System.out.println("Zoomables:: "+s); }

    static public void addActions(Action a, final FServer f, final Zoomable z) {
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
	a.setListener(3, 0, a.VERTICAL, .01f, "zoom in or out", zoom(f,z));
    }
	
    
    static RelativeAxisListener zoom(final FServer f, final Zoomable z) {
	return new RelativeAxisListener() {
		public void changedRelative(float x) {
		    z.setZoom(z.getZoom() + x);
		    z.setCoordsysParams();
		    f.getScreen().rerender();
		}
	    };
    }
}
