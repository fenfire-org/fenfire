/*
SpatialViewSettings.java
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
import org.nongnu.libvob.lob.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.util.*;
import org.nongnu.navidoc.util.Obs;
import java.util.*;

public class SpatialViewSettings extends ViewSettings {

    public interface SpatialView extends View {
	/** Return a lob used when the cursor is on this spatial view.
	 *  The lob is rendered into the whole box 
	 *  filled by the structure view, i.e. if the structure view
	 *  is full-screen, the lob will be rendered full-screen.
	 *  If the view only wants to render a node in the center,
	 *  it can use AlignLob (see SimpleSpatialView for an example).
	 */
	Lob getMainviewLob(Cursor cursor);

	/** Return a lob used to show the given node as a buoy.
	 *  The lob's size request should be that of the buoy;
	 *  the lob will be rendered into the buoy box.
	 */
	Lob getBuoyLob(Object node, boolean useFakeFocus);
    }

    public SpatialViewSettings(Set views) {
	super(views);
    }

    private Lob errorLob = Components.label("No matching spatial view found!");

    public Lob getMainviewLob(Cursor cursor) {
	SpatialView v = (SpatialView)getViewByCursor(cursor);
	if(v != null)
	    return v.getMainviewLob(cursor);
	else
	    return errorLob;
    }

    public Lob getBuoyLob(Object node) {
	return getBuoyLob(node, false);
    }

    public Lob getBuoyLob(Object node, boolean useFakeFocus) {
	SpatialView v = (SpatialView)getViewByNode(node);
	if(v != null)
	    return v.getBuoyLob(node, useFakeFocus);
	else
	    return errorLob;
    }
}
