/*
Geometry.java
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

import org.nongnu.libvob.VobScene;
import org.nongnu.libvob.physics.*;

/** Mind Map geometry!
 */
public interface Geometry {

    static interface Plugin {
	void run(Geometry g, ParticleSpace particles, RingMindNet net);
    }
    
    void construct(RingMindNet net);

    int getCS(Object node);

    void generateModels();
    void setModels();

    void generateCS(VobScene vs, int into);
    void setCSparams(VobScene vs, int into);

    Place getPlace(Object node);
    Particle getParticle(Object node);
    /*
    float getRotation();
    
    void setRotation(float rat);
    */
}
