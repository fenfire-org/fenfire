/*
Linkables.java
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
import org.fenfire.*;
import org.fenfire.modules.init.Settings;

import org.nongnu.libvob.*;
import org.nongnu.libvob.impl.awt.*;
import org.nongnu.libvob.vobs.*;
import org.nongnu.libvob.input.*;
import org.nongnu.libvob.mouse.*;
import org.nongnu.libvob.util.*;
import java.util.*;

/** Common utilities for panable views.
 */
public class Linkables {
    static private void p(String s) { System.out.println("Linkables:: "+s); }

    
    static public interface CallBack {
	void linked(Object node);
    };


    static public void addActions(Action a, final FServer f, 
				  final Object object, 
				  final Object predicate,
				  final CallBack callBack) {
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
 	a.setListener(1, VobMouseEvent.SHIFT_MASK, "link objects", link(f,object,predicate, callBack));
 	a.setListener(3, VobMouseEvent.SHIFT_MASK, "link objects", link(f,object,predicate, callBack));
   }
	
    
    static MousePressListener link(final FServer f, final Object o, final Object pred, final CallBack callBack) {
	return new MousePressListener() {
		public MouseDragListener pressed(int x, int y) {
		    return new MouseDragListener() {
			    public void startDrag(int x, int y) {
				Integer[] i = new Integer[]{
				    new Integer(x), new Integer(y)
				};
				f.environment.request("set link start", i, null);
				f.environment.request("set link end", i, null);
				f.environment.request("rerender", null);
				f.getScreen().rerender();
			    }
			    public void drag(int x, int y) {
				Integer[] i = new Integer[]{
				    new Integer(x), new Integer(y)
				};
				f.environment.request("set link end", i, null);

				VobScene vs = f.getScreen().getVobSceneForEvents();
				Action a = RecursiveVobScenes.findAction(
				    vs, x,y, "link destination");
				
				if (a != null) {
				    Object s = a.request("link destination");
				}
				f.environment.request("rerender", null);
				f.getScreen().rerender();
			    }
			    public void endDrag(int x, int y) {
				Integer[] i = new Integer[]{
				    new Integer(-1), new Integer(-1)
				};
				f.environment.request("set link end", i, null);
				f.environment.request("set link start", i, null);
				VobScene vs = f.getScreen().getVobSceneForEvents();
				Action a = RecursiveVobScenes.findAction(
				    vs, x,y, "link destination");
				
				if (a != null) {
				    Object s = a.request("link destination");

				    if (s != o) {
					Fen[] fen = new Fen[1];
					f.environment.request("fen", fen, null);
					fen[0].graph.add(o, pred, s);
					if (callBack != null) 
					    callBack.linked(o);
					f.getScreen().animate();
				    }
				}
				f.getScreen().switchVS();
			    }
			};
		}
	    };
    }
}
