/*
RSTUtil.java
 *    
 *    Copyright (c) 2003, Matti J. Katila
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

package org.fenfire.modules.pp.lava;
import org.fenfire.functional.NodeFunction;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.Remote;


/** The interface for rdf graph which is able to perform the
 * actions needed to make xanalogical system and editor like view.
 * RST as reStructuredTex - the poor mans way to present text.
 * <p>
 * RST name is only illusion but as the idea come from these 
 * actions come from rst I keep calling this with it.
 */
public interface RSTUtil extends Remote {

    /** Generate the nodes' coordinates.
     */
    void generateBasicSpatialCoords(Object rstCanvas, 
				    NodeFunction nodef) 
	throws RemoteException;



    // gets
    Object getSentence(Object node)
	throws RemoteException;
    Object getParagraph(Object sentence)
	throws RemoteException;
    Object getCanvas(Object paragraph)
	throws RemoteException;
}
