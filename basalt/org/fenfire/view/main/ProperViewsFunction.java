/*
ProperViewsFunction.java
 *    
 *    Copyright (c) 2004, Matti J. Katila
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
 */
/*
 * Written by Matti J. Katila
 */

package org.fenfire.view.main;
import org.fenfire.view.management.*;
import org.fenfire.swamp.*;
import org.fenfire.functional.*;

import java.util.*;

public class ProperViewsFunction implements PureNodeFunction {
    static public boolean dbg = false;
    private void p(String s) { System.out.println("ProperViewsFunction:: "+s); }


    final FServer f;
    List views = new ArrayList();
    public ProperViewsFunction(FServer f) {
	this.f = f;
    } 


    /** Filter improper views away.
     */
    public Object f(ConstGraph cg, Object n) {
	Iterator[] i = new Iterator[1];
	f.environment.request("views", i, null);
	if(i[0] != null) {
	    int j = 0;
	    while(i[0].hasNext()) {
		MainViewFactory viewFactory = (MainViewFactory) i[0].next();
		if (viewFactory.isViewable(cg, n)) {
		    if (views.size() <= j)
			views.add(viewFactory);
		    else if (viewFactory != views.get(j)) {
			List l = new ArrayList();
			for (int k=0; k<j; k++)
			    l.add(views.get(k));
			l.add(viewFactory);
			views = l;
		    }
		    j++;
		}
	    }
	}
	return views;
    }

}


