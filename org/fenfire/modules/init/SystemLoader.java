/*
SystemLoader.java
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
import org.fenfire.view.management.impl.*;

/** Chicken and egg loader for system. Chicken and egg 
 *  problem can be solved by public or private pools but since
 *  there's no many users yet private pool makes more sense. 
 */
public class SystemLoader {

    private FServer f;
    public SystemLoader(FServer f, String [] args) {
	this.f = f;

	try {  // chicken and egg problem!

	    new GlobalGraphLoader(f);

	    // how to get language/culture/country settings 
	    // from the system? -> needed to translate user 
	    // information regarding to signer block

	    new StormLoader(f);
	    new AlphLoader(f);

	    new SettingsLoader(f);
	    // once system settings has been loaded from 
            // private rdf block we know how to manage different pools
	    new PoolManager(f);
	    
	    new ViewsLoader(f);
	    new FenLoader(f);

	    // ...and what theme should be used with view
	    ApplitudeManager mgr = 
		new LobManager(f, f.getWindowAnimation());
	    f.setManager(mgr, null);
	    new BackgroundPlugin(f,mgr);
	    //new BindingsPlugin(f, mgr);

	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	} catch (Error e) {
	    e.printStackTrace();
	    System.exit(1);
	}
    }

}
