/*
Settings.java
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

package org.fenfire.modules.init;
import org.fenfire.view.management.*;
import org.fenfire.util.*;
import org.fenfire.swamp.*;
import org.fenfire.swamp.impl.*;
import org.fenfire.swamp.cloudberry.*;

public class Settings {
    static StormGraph [] g = new StormGraph[1];


    static public int getInt(FServer f, Object a, Object b, 
			     Object c, int def) {
	f.environment.request("settings", g, null);

	if (g[0] == null) return def;
	
	// a bit too much simplified perhaps...
	if (!RDFUtil.hasOne11X(g[0], a,b))
	    g[0].set1_11X(a,b, Nodes.N());
	
	Object n = g[0].find1_11X(a,b);
	
	if(g[0].findN_11X_Iter(n,c).hasNext())
	    return RDFUtil.getInt(g[0], n,c);
	RDFUtil.setInt(g[0], n,c, def);
	return def;
    }
}
