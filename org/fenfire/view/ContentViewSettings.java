/*
ContentViewSettings.java
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
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.util.*;
import org.nongnu.navidoc.util.Obs;
import java.util.*;

public class ContentViewSettings extends ViewSettings {

    public interface ContentView extends View {
	Lob getLob(Object node);
	Lob getPropertyLob(Object prop);
    }

    public ContentViewSettings(Set views) {
	super(views);
    }

    protected Replaceable[] getParams() {
	return new Replaceable[] { };
    }
    protected Object clone(Object[] params) {
	return new ContentViewSettings(views);
    }

    private Lob errorLob = new org.nongnu.libvob.layout.component.Label("No matching content view found!");

    public Lob getLob(Object node) {
	ContentView v = (ContentView)getViewByNode(node);
	if(v != null)
	    return v.getLob(node);
	else
	    return errorLob;
    }

    public Lob getPropertyLob(Object node) {
	ContentView v = (ContentView)getViewByNode(node);
	if(v != null)
	    return v.getPropertyLob(node);
	else
	    return errorLob;
    }
}
