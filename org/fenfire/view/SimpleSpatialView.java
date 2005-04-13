/*
SimpleSpatialView.java
 *    
 *    Copyright (c) 2004-2005, Benja Fallenstein and Matti Katila
 *
 *    This file is part of Libvob.
 *    
 *    Libvob is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Libvob is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Libvob; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *
 */
/*
 * Written by Benja Fallenstein and Matti Katila
 */
package org.fenfire.view;
import org.fenfire.Cursor;
import org.fenfire.swamp.*;
import org.nongnu.libvob.fn.*;
import org.nongnu.libvob.lob.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.util.*;
import org.nongnu.navidoc.util.Obs;
import java.awt.Color;
import java.util.*;

public class SimpleSpatialView implements SpatialView {

    private ReprView reprView;
    private Color bgColor;
    private Color nodeBorderColor;
    private Color literalBorderColor;

    public SimpleSpatialView(ReprView reprView) {
	this(reprView, new Color(.85f, .85f, .8f),
	     new Color(.56f, .425f, 1), new Color(.85f, .425f, 0));
    }

    public SimpleSpatialView(ReprView reprView,
			     Color bgColor, Color nodeBorderColor,
			     Color literalBorderColor) {
	this.reprView = reprView;
	this.bgColor = bgColor;
	this.nodeBorderColor = nodeBorderColor;
	this.literalBorderColor = literalBorderColor;
    }

    public Set getTypes() {
	return Collections.singleton(ViewSettings.ALL);
    }

    public boolean showBig() {
	return false;
    }

    public Lob getMainviewLob(Cursor cursor) {
	return getLob(cursor.getNode(), 150, true, true, false);
    }

    public Lob getBuoyLob(Object node, boolean useFakeFocus) {
	return getLob(node, 75, false, false, useFakeFocus);
    }

    private Lob getLob(Object node, float maxY, boolean align,
		       boolean useFocus, boolean useFakeFocus) {
	Color borderColor = 
	    (node instanceof Literal) ? literalBorderColor : nodeBorderColor;

	Lob l = reprView.getLob(node);
	l = Lobs.margin(l, 3);
	l = Lobs.clip(l);
	l = BuoyConnectorLob.newInstance(l, node, useFocus, useFakeFocus);
	l = Lobs.frame(l, bgColor, borderColor, 2, 0, false);
	
	if(l.getLayoutableAxis() == Axis.X) {
	    SizeRequest r = l.getSizeRequest();
	
	    float size = 125;
	    if(size < r.minW) 
		size = r.minW;

	    l = l.layoutOneAxis(size);
	} else {
	    l = Lobs.request(l, 125, -1, -1, -1, -1, maxY);
	}

	l = SpatialContextLob.newInstance(l, "simple context");

	if(align)
	    l = Lobs.align(l, .5f, .5f, .5f, .5f);

	return l;
    }
}
