/*
ConnectorLob.java
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

/** Connect something to the focus, in ConnectionVobMatcher
 */
public class ConnectorLob extends AbstractDelegateLob {

    protected Object prop;
    protected Object key;
    protected int dir;

    private ConnectorLob() {}

    public static Lob newInstance(Lob content, Object prop, Object key,
				  int dir) {
	ConnectorLob l = (ConnectorLob)FACTORY.object();
	l.delegate = content;
	l.prop = prop;
	l.key = key;
	l.dir = dir;
	return l;
    }

    protected Lob wrap(Lob l) {
	return newInstance(l, prop, key, dir);
    }

    public void render(VobScene scene, int into, int matchingParent,
		       float d, boolean visible) {
	ConnectionVobMatcher m = (ConnectionVobMatcher)scene.matcher;
	int focus = m.getFocus();

	SizeRequest r = delegate.getSizeRequest();
	int cs = scene.coords.box(into, r.width(), r.height());

	m.add(matchingParent, cs, key);
	m.link(focus, dir, cs, prop);

	super.render(scene, cs, cs, d, visible);
    }

    public boolean mouse(VobMouseEvent e, VobScene scene, int cs, 
			 float x, float y) {

	ConnectionVobMatcher m = (ConnectionVobMatcher)scene.matcher;

	cs = m.getLink(m.getFocus(), dir, key, prop);
	if(cs < 0) throw new Error("ARGH");
	return delegate.mouse(e, scene, cs, x, y);
    }

    private static final Factory FACTORY = new Factory() {
	    public Object create() {
		return new ConnectorLob();
	    }
	};
}
