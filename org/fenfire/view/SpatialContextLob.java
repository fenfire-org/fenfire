/*
SpatialContextLob.java
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
import javolution.realtime.*;
import java.util.*;

public class SpatialContextLob extends AbstractDelegateLob {
    
    private static LocalContext.Variable lcs = new LocalContext.Variable(null);

    public static int getSpatialContextCS() {
	FastInt fi = (FastInt)lcs.getValue();
	if(fi == null)
	    throw new IllegalStateException("no spatial context");
	return fi.intValue();
    }

    private SpatialContextLob() {}

    public static SpatialContextLob newInstance(Lob content) {
	SpatialContextLob l = (SpatialContextLob)FACTORY.object();
	l.delegate = content;
	return l;
    }

    public Lob wrap(Lob lob) {
	return newInstance(lob);
    }

    public void render(VobScene scene, int into, int matchingParent,
		       float d, boolean visible) {
	scene.matcher.add(matchingParent, into, "spatial context");

	LocalContext.enter();
	try {
	    lcs.setValue(FastInt.newInstance(into));
	    super.render(scene, into, matchingParent, d, visible);
	} finally {
	    LocalContext.exit();
	}
    }

    private static final Factory FACTORY = new Factory() {
	    public Object create() {
		return new SpatialContextLob();
	    }
	};
}
