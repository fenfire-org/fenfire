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
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.*;
import java.util.*;

public class BuoyConnectorLob extends AbstractMonoLob {

    protected Model cs;
    protected Object key;

    public BuoyConnectorLob(Lob content, Object key, Model cs) {
	super(content);
	this.cs = cs;
	this.key = key;
    }

    protected Replaceable[] getParams() {
	return new Replaceable[] { content, cs };
    }

    protected Object clone(Object[] params) {
	return new BuoyConnectorLob((Lob)params[0], key, (Model)params[1]);
    }

    public void render(VobScene scene, int into, int matchingParent,
		       float w, float h, float d,
		       boolean visible) {
	int cs = scene.coords.box(into, w, h);
	ConnectionVobMatcher m = (ConnectionVobMatcher)scene.matcher;
	m.add(matchingParent, cs, key);
	m.link(this.cs.getInt(), 1, cs, "structure point");
	m.getLink(this.cs.getInt(), 1, key, "structure point");
	super.render(scene, cs, cs, w, h, d, visible);
    }
}
