/*
ApplitudeManager.java
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

package org.fenfire.view.management;
import org.fenfire.swamp.Nodes;
import org.nongnu.libvob.*;
import org.nongnu.libvob.layout.*;

public interface ApplitudeManager extends FServer.RequestHandler {

    /*
    public static final Object 
	TITLE = Nodes.get("http://fenfire.org/rdf-v/2004/10/20/Manager#Title"),
	CONTENT = Nodes.get("http://fenfire.org/rdf-v/2004/10/20/Manager#Content"),
	X = Nodes.get("http://fenfire.org/rdf-v/2004/10/20/Manager#X"),
	Y = Nodes.get("http://fenfire.org/rdf-v/2004/10/20/Manager#Y"),
	W = Nodes.get("http://fenfire.org/rdf-v/2004/10/20/Manager#W"),
	H = Nodes.get("http://fenfire.org/rdf-v/2004/10/20/Manager#H"),
	ZOOM = Nodes.get("http://fenfire.org/rdf-v/2004/10/20/Manager#Zoom"),
	VIRTUAL_DESK = Nodes.get("http://fenfire.org/rdf-v/2004/10/20/Manager#VirtualDesk");
    */

    public static interface Plugin {

    }
    void addPlugin(ApplitudeManager.Plugin plugin);

    //void key(String s);
    //void mouse(VobMouseEvent ev);
    //void scene(VobScene vs);

    Lob getLob();

}
