/*
GeometryImpl.java
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
import org.nongnu.libvob.*;
import org.nongnu.libvob.physics.*;
import org.nongnu.libvob.physics.impl.*;
import java.util.*;

/** Mind Map geometry!
 */
public class GeometryImpl implements Geometry {
    static private void p(String s) { System.out.println("GeometryImpl:: "+s); }
    static public boolean dbg = false;

    public float startRotation = (float) Math.PI / 2.0f;

    protected Map places = new HashMap();
    public Place getPlace(Object node) { return (Place) places.get(node); }
    public Particle getParticle(Object node) { 
	return (Particle) getPlace(node).particle; }
    private float filletWidth = 50;


    protected ParticleSimulator simulator;
    protected Geometry.Plugin plugin;
    protected ParticleSpace particles;
    public GeometryImpl(ParticleSimulator s, ParticleSpace p, 
			Geometry.Plugin plugin) { 
	this.simulator = s; 
	this.plugin = plugin;
	this.particles = p;
    }

    public void construct(RingMindNet net) {
	//places.clear();

	for (int depth=0; depth<net.maxDepth(); depth++) {
	    List ring = net.getRing(depth);
	    float n = ring.size();
	    float rotation = ((float)(2*Math.PI))/n;
	    float angle = startRotation;

	    for (Iterator i=ring.iterator(); i.hasNext();) {
		Object node = i.next();
		if (!places.containsKey(node))
		    places.put(node, place(angle, depth));
		angle += rotation;
	    }
	}

	plugin.run(this, particles, net);

	/*
	// stick the center
	final Object center = net.getRing(0).get(0);
	particles.createStagnantParticle(center, getParticle(center));
	
	int n = net.getLeafCount();
	float rotation = ((float)(2*Math.PI))/n;
	float angle = startRotation;
	p("ring size: "+n+"start: "+angle);
	for (Iterator i=net.getLeafs().iterator(); i.hasNext();) {
	    Object leaf = i.next();
	    if (dbg) p("leaf "+leaf);
	    MindNet.Node mnode = net.getNode(leaf);
	    float x = (float)(100*(Math.cos(angle) * (mnode.getDepth()+3.6)));
	    float y = (float)(100*(Math.sin(angle) * (mnode.getDepth()+3.6)));

	    particles.createStagnantParticle("_"+leaf, x,y,0,1,1);
	    particles.createLiveParticle(leaf, getParticle(leaf));
	    particles.connectParticlesWithSpringMassModel("_"+leaf, leaf, 8);

	    Object node = leaf;
	    Object parent = mnode.getParent();
	    while (true) {
		if (parent != center)
		    particles.createLiveParticle(parent, getParticle(parent));

		particles.connectParticlesWithSpringMassModel(node, parent, 8);
		if (dbg) p("   parent "+parent);
		if (parent == center) break;

		node = parent;

		mnode = net.getNode(node);
		parent = mnode.getParent();

	    }

	    angle += rotation;
	}



	for (int depth=1; depth<net.maxDepth(); depth++) {
	    List ring = net.getRing(depth);
	    if (dbg) p("ring: "+depth);
	    if (ring.size() > 1)
		particles.connectParticlesWithSpringMassModel(ring.get(0), ring.get(ring.size()-1), 1);
	    for (int i=0; i<ring.size()-1; i++) {
		particles.connectParticlesWithSpringMassModel(ring.get(i), ring.get(i+1), 1);
		if (dbg) p("  connect: ");
	    }
	}
	*/
	/*
	for (int i=0; i<net.getLeafs().size()-1; i++) {
	    particles.connectParticlesWithSpringMassModel(net.getLeafs().get(i), net.getLeafs().get(i+1));	    
	}
	particles.connectParticlesWithSpringMassModel(net.getLeafs().get(0), net.getLeafs().get(net.getLeafs().size()-1));	    
	*/

	//for (int i=0; i<32; i++) simulator.simulate(particles);
	
    }
    public static double getScale(int depth) { return 1-(Math.log(depth+1)/Math.E); }

    Place place(float rotation, int depth) {
	if (depth==0) return new Place(new Particle(0,0),0);
	float x = (float)(100*(Math.cos(rotation) * depth)); ///(float)Math.log(Math.E+(5*depth)));
	float y = (float)(100*(Math.sin(rotation) * depth)); ///(float)Math.log(Math.E+(5*depth)));
	if (dbg) p("xy"+x+", "+y);
	return new Place(new Particle(x,y), depth);
    }


    public void generateModels() {
	for (Iterator i=places.entrySet().iterator(); i.hasNext();) {
	    Map.Entry entry = (Map.Entry) i.next();
	    Place p = (Place) entry.getValue();
	    p.x.setFloat(p.particle.x());
	    p.y.setFloat(p.particle.y());
	}
    }
    public void setModels() {
	for (Iterator i=places.entrySet().iterator(); i.hasNext();) {
	    Map.Entry entry = (Map.Entry) i.next();
	    Place p = (Place) entry.getValue();
	    p.x.setFloat(p.particle.x());
	    p.y.setFloat(p.particle.y());
	}
    }



    public void generateCS(VobScene vs, int into) { generateCS(vs,into, null); }
    public void generateCS(VobScene vs, int into, RingMindNet net) {
	if (net != null) construct(net);
	for (Iterator i=places.entrySet().iterator(); i.hasNext();) {
	    Map.Entry entry = (Map.Entry) i.next();
	    Object node = entry.getKey();
	    Place p = (Place) entry.getValue();

	    double s = filletWidth * getScale(p.depth);
	    p.cs = vs.orthoBoxCS(into, node.toString()+"_FILLET", p.depth,
				 (float)(p.particle.x() - s/2f), (float)(p.particle.y() - s/2f),
				 1, 1, (float)s, (float)s); 
	}
    }

    public void setCSparams(VobScene vs, int into) {
	for (Iterator i=places.entrySet().iterator(); i.hasNext();) {
	    Map.Entry entry = (Map.Entry) i.next();
	    Object node = entry.getKey();
	    Place p = (Place) entry.getValue();

	    double s = filletWidth * getScale(p.depth);
	    vs.coords.setOrthoBoxParams(p.cs, p.depth,
					(float)(p.particle.x() - s/2f), 
					(float)(p.particle.y() - s/2f),
					1, 1, (float)s, (float)s); 
	    if (dbg) p("node: "+node+", x:"+p.particle.x()+", y: "+p.particle.y());
	}
    }
    

    
    public int getCS(Object node) {
	Place place = (Place) places.get(node);
	if (place == null) throw new Error("No CS!");
	
	return place.cs;
    }

}
