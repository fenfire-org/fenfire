/*   
URIComparator.java
 *    
 *    Copyright (c) 2004, Benja Fallenstein.
 *
 *    This file is part of Fenfire.
 *    
 *    Fenfire is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Fenfire is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Fenfire; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.lob;
import org.nongnu.navidoc.util.Obs;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.component.*;
import org.fenfire.swamp.*;
import java.util.Comparator;

public class PropertyComparator 
    extends AbstractReplaceable.AbstractObservable implements Comparator {

    protected Model graphModel;
    protected Object property;

    public PropertyComparator(Model graphModel, Object property) {
	this.graphModel = graphModel;
	this.property = property;
    }

    protected Replaceable[] getParams() { 
	return new Replaceable[] { graphModel }; 
    }
    protected Object clone(Object[] params) { 
	return new PropertyComparator((Model)params[0], property);
    }

    public void chg() {
	obses.trigger();
    }

    public int compare(Object o1, Object o2) {
	Graph g = (Graph)graphModel.get();
	Literal l1 = (Literal)g.find1_11X(o1, property, this);
	Literal l2 = (Literal)g.find1_11X(o2, property, this);
	    
	int res;
	    
	if(l1 == null)
	    res = (l2==null) ? 0 : -1;
	else if(l2 == null)
	    res = +1;
	else 
	    res = l1.getString().compareTo(l2.getString());
	    
	if(res == 0) res = o1.toString().compareTo(o2.toString());
	return res;
    }
}
