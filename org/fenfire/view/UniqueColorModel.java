/*
UniqueColorModel.java
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
import org.fenfire.Cursor;
import org.fenfire.swamp.*;
import org.fenfire.vocab.*;
import org.fenfire.lob.*;
import org.nongnu.libvob.lob.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.util.*;
import org.nongnu.navidoc.util.Obs;
import java.awt.Color;
import java.util.*;

public class UniqueColorModel {}/*extends AbstractModel.AbstractObjectModel {
    private Model node;
    private float minBrightness, maxBrightness;

    public UniqueColorModel(Model node, float minBrightness, 
			    float maxBrightness) {
	this.node = node;
	this.minBrightness = minBrightness;
	this.maxBrightness = maxBrightness;
	node.addObs(this);
    }
	
    protected Replaceable[] getParams() {
	return new Replaceable[] { node };
    }
    protected Object clone(Object[] params) {
	return new UniqueColorModel((Model)params[0], minBrightness,
				    maxBrightness);
    }

    Color color;

    public void chg() {
	color = null;
	super.chg();
    }

    public Object get() {
	if(color == null) {
	    float min = minBrightness, range = maxBrightness-min;

	    java.util.Random r = new Random(node.get().hashCode());
	    float 
		R = min + r.nextFloat() * range,
		G = min + r.nextFloat() * range,
		B = min + r.nextFloat() * range;
	    color = new Color(R,G,B);

	    //color = Color.getHSBColor(r.nextFloat(), r.nextFloat(),
	    //			      min + r.nextFloat() * range);
	}
	return color;
    }
}
*/
