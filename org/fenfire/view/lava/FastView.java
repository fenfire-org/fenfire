/*
FastView.java
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

package org.fenfire.view.lava;
import org.nongnu.libvob.VobScene;

/** An interface for a view which can change by setting coordinate system parameters.
 * If animation/interpolation is not needed and changing of 
 * scene must be fast, the libvob platform provides a technique 
 * to not regenerate VobScene but set parameters of existing coordinate systems.
 * 
 * @see VobCoorder
 */
public interface FastView {

    /* A method to push a view to set it's coordinate system parameters.
     * @param oldVobScene is the VobScene which is used in current visible 
     *        scene. The view must not try to rerender into this scene
     *        but use parametrisation of old coordinate system instead.
     * @see VobCoorder
     */ 
    void chgFast(VobScene oldVobScene, int parent);
}
