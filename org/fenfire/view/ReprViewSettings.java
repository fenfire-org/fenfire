/*
ReprViewSettings.java
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

public class ReprViewSettings extends ViewSettings implements ReprView {

    public ReprViewSettings(Set views) {
	super(views);
    }

    private Lob errorLob = Components.label("No matching content view found!");

    public Lob getLob(Object node) {
	ReprView v = (ReprView)getViewByNode(node);
	if(v != null)
	    return v.getLob(node);
	else
	    return errorLob;
    }

    public List getLobList(Object node) {
	ReprView v = (ReprView)getViewByNode(node);
	if(v != null)
	    return v.getLobList(node);
	else
	    return Lists.list(errorLob);
    }
}
