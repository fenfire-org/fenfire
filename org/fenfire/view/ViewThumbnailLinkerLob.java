/*
ViewThumbnailLinkerLob.java
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
import javolution.util.FastList;
import java.util.*;

public class ViewThumbnailLinkerLob extends AbstractDelegateLob {
    
    private static LocalContext.Variable 
	linkerLob = new LocalContext.Variable(null);

    public static void connectToFocus(int cs) {
	ViewThumbnailLinkerLob l = 
	    (ViewThumbnailLinkerLob)linkerLob.getValue();

	if(l != null) {
	    FastInt i = FastInt.newInstance(cs);
	    i.preserve();
	    l.contexts.add(i);
	}
    }

    protected FastList contexts = new FastList();

    private ViewThumbnailLinkerLob() {}

    public static ViewThumbnailLinkerLob newInstance(Lob content) {
	ViewThumbnailLinkerLob l = (ViewThumbnailLinkerLob)FACTORY.object();
	l.delegate = content;
	return l;
    }

    public Lob wrap(Lob lob) {
	return newInstance(lob);
    }

    public void render(VobScene scene, int into, int matchingParent,
		       float d, boolean visible) {
	ConnectionVobMatcher matcher = (ConnectionVobMatcher)scene.matcher;

	LocalContext.enter();
	try {
	    linkerLob.setValue(this);

	    contexts.clear();

	    // this must set the focus and may add contexts:
	    super.render(scene, into, matchingParent, d, visible);

	    int focus = matcher.getFocus();
	    
	    if(focus < 0)
		throw new Error("focus not set by ViewThumbnailLinkerLob's child");

	    for(Iterator iter = contexts.fastIterator(); iter.hasNext();) {
		FastInt i = (FastInt)iter.next();

		int context = i.intValue();
		matcher.link(context, 1, focus, "structure point");

		i.unpreserve();
	    }
	} finally {
	    LocalContext.exit();
	}
    }

    private static final Factory FACTORY = new Factory() {
	    public Object create() {
		return new ViewThumbnailLinkerLob();
	    }
	};
}
