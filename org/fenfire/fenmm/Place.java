/*
Place.java
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

package org.fenfire.fenmm;

import org.nongnu.libvob.physics.*;
import org.nongnu.libvob.layout.*;

/** Place of a note in a Mind Map geometry.
 */
public class Place {
    public Particle particle;
    public int cs = -1;
    public int depth = 0;
    public AnchorLob lob;
    public FloatModel x = new FloatModel(0);
    public FloatModel y = new FloatModel(0);
    public Place(Particle p, int depth) {
	this.particle = p;
	this.depth = depth;
    }
}
