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
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.component.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.util.*;
import org.nongnu.navidoc.util.Obs;
import java.awt.Color;
import java.util.*;

public class SimpleSpatialView implements SpatialViewSettings.SpatialView {

    private ContentViewSettings contentViewSettings;
    private Color bgColor;
    private Color nodeBorderColor;
    private Color literalBorderColor;

    private Map cache = new org.nongnu.navidoc.util.WeakValueMap();

    public SimpleSpatialView(ContentViewSettings contentViewSettings) {
	this(contentViewSettings, new Color(.85f, .85f, .8f),
	     new Color(.56f, .425f, 1), new Color(.85f, .425f, 0));
    }

    public SimpleSpatialView(ContentViewSettings contentViewSettings,
			     Color bgColor, Color nodeBorderColor,
			     Color literalBorderColor) {
	this.contentViewSettings = contentViewSettings;
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
	return getBuoyLob(cursor.getNode());
    }

    public Lob getBuoyLob(Object node) {
	if(cache.get(node) != null) return (Lob)cache.get(node);

	Model cs = new IntModel();

	Color borderColor = 
	    (node instanceof Literal) ? literalBorderColor : nodeBorderColor;

	Lob l = contentViewSettings.getLob(node);
	l = new BuoyConnectorLob(l, node, cs);
	l = new Frame(l, bgColor, borderColor, 2, 3, false, false, true);
	l = new RequestChangeLob(Lob.X, l, Float.NaN, 100, 100);
	l = new SpatialContextLob(l, cs);
	l = new Stamp(l);
	cache.put(node, l);
	return l;
    }
}
