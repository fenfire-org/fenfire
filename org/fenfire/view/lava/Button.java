/*
Button.java
 *    
 *    Copyright (c) 2003, Matti Katila
 *    
 *    This file is part of Gzz.
 *    
 *    Gzz is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Gzz is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Gzz; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *    
 */
/*
 * Written by Matti Katila
 */

package org.fenfire.view.lava;
import org.nongnu.libvob.*;
import org.nongnu.libvob.vobs.*;

public class Button {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("Button:: "+s); }

    final private VobScene vs;
    final private float scale;
    final static private TextStyle style =  GraphicsAPI.getInstance().
                                     getTextStyle("sans", 0, 24);
    final private int into;
    final private float offset;
    private float x,y;

    public Button(VobScene v, int into, 
                  float x, float y, float offset) {
	vs = v;
	scale = style.getScaleByHeight(25);
        this.into = into;
        this.x=x; this.y=y;
        this.offset = offset;
    }

    public void add(String caption, Object key) {
	// put the "New paper" -text
	TextVob button = new TextVob(style, caption);
	float width = button.getWidth(scale);
	int frame =
	    vs.orthoBoxCS(into, key, 0,  x,y,  1,1,
			  width, button.getHeight(scale) + button.getDepth(scale));
	int button_cs =
	    vs.scaleCS(frame, key.toString()+"_cs",
		       button.getHeight(scale) + button.getDepth(scale),
		       button.getHeight(scale) + button.getDepth(scale));
	vs.activate(frame);
	vs.map.put(button, button_cs);
        x += width + offset;

        if (dbg) p("Added button: "+caption);
    }
}
