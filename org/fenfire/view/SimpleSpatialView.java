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
import java.util.*;

public class SimpleSpatialView implements ViewSettings.SpatialView {

    public Set getTypes() {
	return Collections.singleton(ViewSettings.ALL);
    }

    private static class FooLob extends AbstractMonoLob {
	private Object key;

	public FooLob(Lob content, Object key) {
	    super(content);
	    this.key = key;
	}

	public Object clone(Object[] params) {
	    return new FooLob((Lob)params[0], key);
	}

	public void render(VobScene scene, int into, int matchingParent,
			   float w, float h, float d,
			   boolean visible) {
	    int cs = scene.coords.box(into, w, h);
	    ConnectionVobMatcher m = (ConnectionVobMatcher)scene.matcher;
	    m.add(matchingParent, cs, key);
	    m.link(into, 1, cs, "structure point");
	    content.render(scene, cs, cs, w, h, d, visible);
	}
    }

    public Lob getLob(Cursor c) {
	Object node = c.getNode();

	Lob l = new Label(Nodes.toString(node));
	l = new ThemeFrame(l);
	l = new RequestChangeLob(Lob.X, l, Float.NaN, Float.NaN, 100);
	l = new FooLob(l, node);
	return l;
    }
}
