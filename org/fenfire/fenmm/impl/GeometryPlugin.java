/*
GeometryPlugin.java
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

package org.fenfire.fenmm.impl;
import org.fenfire.fenmm.*;
import org.nongnu.libvob.physics.*;
import java.util.*;

public class GeometryPlugin implements Geometry.Plugin {

    public void run(Geometry geometry, ParticleSpace particles, 
		    RingMindNet net) {

	// stick the center
	Object center = null;
	try {
	    center = net.getRing(0).get(0);
	} catch (IndexOutOfBoundsException e) { return; }

	Particle parent = particles.createStagnantParticle(
	    center, geometry.getParticle(center));

        for (int i=1; i<net.maxDepth(); i++) {
            for (int j=0; j<net.getRing(i).size(); j++){ 
                Object node = net.getRing(i).get(j);

                Particle particle = particles.createLiveParticle(
		    node, geometry.getParticle(node));
                particles.setRepulsionForce(particle, 3000);
	    }
	}
	
        recursivelyConnect(net, 1, 0, net.getRing(1).size(), 
			   particles, parent);
    }

    public void recursivelyConnect(RingMindNet net, int depth, int start, int end, 
				   ParticleSpace particles, Particle parentParticle) {
    
	/*
        for (int i=start; i<end; i++) {
            Object node = net.getRing(depth).get(i);
            Particle p = particles.getParticle(node);
            particles.connectParticlesWithSpringMassModel(parentParticle, p, 100, 4-depth);
	    RingMindNet.Node n = net.getNode(node);
            recursivelyConnect(net, depth+1, n.getFirst(), n.getLast(),
			       particles, p);
	}
	*/
	for (Iterator i = net.iterateAllNodes(); i.hasNext();) {
	    Object node = i.next();
	    for (Iterator j = net.iterator(node); j.hasNext();) {
		Particle p = particles.getParticle(node);
		Particle po = particles.getParticle(j.next());
		particles.connectParticlesWithSpringMassModel(p, po, 100, 1);
	    }
	}
    }
}



    
