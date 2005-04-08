/*
BuoyConnectorLob.java
 *    
 *    Copyright (c) 2005, Benja Fallenstein and Matti Katila
 *
 *    This file is part of Fenfire.
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
import org.nongnu.libvob.fn.*;
import org.nongnu.libvob.lob.*;
import org.nongnu.libvob.*;
import java.util.*;

public class BuoyConnectorLob extends AbstractDelegateLob {

    protected Object key;
    protected boolean isFocus;
    protected boolean isFakeFocus;

    private BuoyConnectorLob() {}

    public static Lob newInstance(Lob content, Object key,
				  boolean isFocus, boolean isFakeFocus) {
	BuoyConnectorLob l = (BuoyConnectorLob)FACTORY.object();

	if(isFocus && isFakeFocus)
	    throw new IllegalArgumentException("BuoyConnectorLob cannot be focus and fake focus at the same time");

	l.delegate = content;
	l.key = key;
	l.isFocus = isFocus;
	l.isFakeFocus = isFakeFocus;
	return l;
    }

    protected Lob wrap(Lob l) {
	return newInstance(l, key, isFocus, isFakeFocus);
    }

    public void render(VobScene scene, int into, int matchingParent,
		       float d, boolean visible) {
	int context = SpatialContextLob.getSpatialContextCS();

	SizeRequest r = delegate.getSizeRequest();
	int cs = scene.coords.box(into, r.width(), r.height());
	ConnectionVobMatcher m = (ConnectionVobMatcher)scene.matcher;

	Object key = isFakeFocus ? "fake focus" : this.key;

	m.add(matchingParent, cs, key);
	m.link(context, 1, cs, "structure point");
	m.getLink(context, 1, key, "structure point");
	super.render(scene, cs, cs, d, visible);

	if(isFocus)
	    m.setFocus(cs);
	else if(isFakeFocus)
	    ViewThumbnailLinkerLob.connectToFocus(context);
    }

    private static final Factory FACTORY = new Factory() {
	    public Object create() {
		return new BuoyConnectorLob();
	    }
	};
}
