/*
NodedView2D.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka
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
 * Written by Tuomas J. Lukka
 */

package org.fenfire.view;
import org.nongnu.libvob.*;

/** An interface which a View2D may implement.
 */
public interface NodedView2D {
    /** Return the node coordinate system that is at the given 
     *  coordinates, or  -1 if none.
     */
    int getNodeCS(VobScene oldvs, 
		     int x, int y,
		     Object plane, int matchingParent, 
		     int box2screen, int box2plane,
		     float[] xyout);

    /** Return the given node's coordinate system or throw an error.
     */
    int getNodeCS(VobScene oldvs, 
		  Object node,
		  Object plane, int matchingParent, 
		  int box2screen, int box2plane);

    /** Return the node matched with coordinate system or null.
     */
    Object getNode(VobScene oldvs, int nodeCS);

}

