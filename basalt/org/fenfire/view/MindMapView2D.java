/*
MindMapView2D.java
 *    
 *    Copyright (c) 2004, Matti J. Katila and Asko Soukka
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
 * Written by Matti J. Katila and Asko Soukka
 */

package org.fenfire.view;
import org.fenfire.view.management.*;
import org.fenfire.swamp.*;
import org.fenfire.vocab.*;
import org.fenfire.fenmm.*;
import org.fenfire.fenmm.impl.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.mouse.*;
import org.nongnu.libvob.physics.*;
import org.nongnu.libvob.physics.impl.*;

import java.util.*;

/** View representing of mind map.
 */
public class MindMapView2D extends View2D {
    private static void p(String s) { System.out.println("MMView2D:: "+s); }

    
    //ConstGraph graph = null;
    public void setGraph(ConstGraph g) {
	net = new RingMindNetImpl(g);
	geometry.construct(net);
        ((ParticleSpaceImpl)particleSpace).init();
	
    }

    public void flush() {
	actions.clear();
	particleSpace = new ParticleSpaceImpl();
	simulator = new ParticleSimulator(particleSpace);
	geometry = new GeometryImpl(simulator,
				    particleSpace,
				    new GeometryPlugin());
	geometry.construct(net);
    }

    static final Object LOCK = new Object();


    RingMindNet net;
    ParticleSpace particleSpace;
    ParticleSimulator simulator;
    Geometry geometry;
    Thread background;
    Zoomable zoo;
    WindowAnimation anim;
    final FServer f;
    public MindMapView2D(FServer f, Zoomable z, final WindowAnimation win) {
	this.f = f;
	this.zoo = z;
	this.anim = win;

	particleSpace = new ParticleSpaceImpl();
	simulator = new ParticleSimulator(particleSpace);

	geometry = new GeometryImpl(simulator,
				    particleSpace,
				    new GeometryPlugin());
	

	background = new Thread() {
		public void run() {
		    while (true) {
		    try {
			synchronized(LOCK) {
			    simulator.simulate(0.1f);
			    geometry.setCSparams(lastScene, -1);
			}
			win.rerender();
			sleep(120);
		    } catch (Exception e) { e.printStackTrace(); }
		    } 
		}
	    };
	background.start();
    }


/*
    def __init__(self, fen, context):
        self.fen = fen
        self.context = context

        self.multiplexer = self.context.multiplexer

        self.depth = 7
        self.filletWidth = 20
        self.filletLength = 150
        self.initRotation = 0
        
        self.geometry = MMGeometry(self.fen.graph, self.filletLength, self.filletWidth,
                                   self.initRotation, self.depth)

        # fillet set up
        self.angle = 1
        self.thick = 1
        self.drawEdge = 0
        self.drawInside = 1
        self.drawInsideColor = java.awt.Color.blue
        self.depthColor = 1
        self.lines = 0
        self.ellipses = 1
        self.stretched = 1
        self.curvature = 0
        self.sectors = 1
        self.fillets = 1
        self.dice = 10
        self.fillet3d = 1
        self.blend3d = 0
        self.linewidth = 1
        self.perspective = 0
        self.texture = 0
        self.dicelen = 100
        self.tblsize = 20
        self.mode = 0

        self.oldCenter = None

        self.t = None
        self.particles = None
        self.simul = None
*/

    int matchingParent = -1;

    public void render(VobScene vs, Object node,
		       int matchingParent, int box2screen, 
		       int box2plane) {

        this.matchingParent = matchingParent;

	int paper2box = vs.invertCS(box2plane, "minMap_INv");
	int paper2screen = vs.concatCS(matchingParent, "mindMap_CONCAT",
				       paper2box);

        //# XXX
        //self.zoomPanCS = paper2screen
	p("render!");
	synchronized(LOCK) {
	    lastScene = vs;
	    ((ParticleSpaceImpl)particleSpace).init();
	    net.constructNet(node);
	    geometry.construct(net);
        
	    geometry.generateCS(vs, paper2screen);

	    drawMindMap(vs);
	}
    }

    static class L implements MouseClickListener, Action.RequestHandler {
	VobScene lastScene;
	Object o;
	int cs;
	L(VobScene last, int c, Object o) {
	    lastScene = last;
	    this.o = o;
	    cs = c;
	}
	public void clicked(int x, int y) {
	    p("node!: "+lastScene.getKeyAt(cs, x,y, null));
	}
	public Object handleRequest(Object req) {
	    p("req: "+req);
	    if (req.equals("link destination")) {
		p("catch please!!!");
		return o;
	    }
	    return null;
	}
    }


    Map actions = new HashMap();
    VobScene lastScene = null;
    private void drawMindMap(VobScene vs) {
        Map links = new HashMap();


	Iterator i = net.iterateAllNodes();
	while (i.hasNext()) {
            Object node = i.next();
            Place pl = geometry.getPlace(node);
            if (pl == null)
		throw new Error(" raise 'argh! no place?!'");
            List c = new ArrayList();
	    c.add(""+pl.cs);

	    Iterator it = net.iterator(node);
            while (it.hasNext()) {
                Object n = it.next();
                Place pl2 = geometry.getPlace(n);
                if (pl2 == null)
		    throw new Error("raise 'argh! again no place?!'");
                c.add(""+pl2.cs);
	    }
	    /*    
            if dbg: p('Fillet coordinates:', c)
            def pc(conns, cs):
                if self.context.main.getPlane() == node:
                    vs.put(vob.putil.misc.getDListNocoords("Color 1 0 0"))
                vs.put(conns, cs+c)

            //# draw fillets
	    //vob.fillet.light3d.drawFillets(self, vs, pc)
	    */

            //# draw text etc..
            vs.put(new org.nongnu.libvob.vobs.RectBgVob(java.awt.Color.white), pl.cs);
            int cs = vs.coords.translate(pl.cs, 0,0, -100);
            vs.matcher.add(matchingParent, pl.cs, node);
	    vs.activate(pl.cs); // XXX

	    if (!actions.containsKey(node)) {
		L link = new L(lastScene, matchingParent, node);
		Action a = new Action(link);
		a.setListener(1,0,"click", link);
		addActions(a, node, zoo, (ParticleSpaceImpl)particleSpace, 
			   geometry.getPlace(node).particle, anim);

		Linkables.addActions(a, f, node, STRUCTLINK.linkedTo, null);
		actions.put(node, a);
	    }
	    vs.actions.put(pl.cs, (Action)actions.get(node));
	    p("put actions: "+vs+", cs: "+pl.cs);

	    int depth = geometry.getPlace(node).depth;



            it = net.iterator(node);
            while (it.hasNext()) {
                Object n = it.next();
                Place pl2 = geometry.getPlace(n);
                vs.put(new org.nongnu.libvob.vobs.SimpleConnection(
                       .5f,.5f,.5f,.5f), pl.cs, pl2.cs);

            }



	    /*
            if (depth < this.depth):
                self.putNodeContent(vs, node, cs)
            else:
                p = self.multiplexer.f(self.fen.graph, node) 
                cs = vs.orthoBoxCS(cs, node, 0, 0, 0, 0, 0, 0, 0)
                p.place(vs, cs)
	    */
	}
    }

/*
    pulic def putNodeContent(self, vs, node, into):
        # scaling after depth
        depth = self.geometry.getPlace(node).depth
        scale = ff.fenmm.MMGeometry.getScale(depth)
        textScale = ff.fenmm.MMGeometry.getTextScale(depth)

        p = self.multiplexer.f(self.fen.graph, node) 

        x = (self.filletWidth*scale)/2. - (p.getWidth()*textScale)/2.
        y = (self.filletWidth*scale)/2. - (p.getHeight()*textScale)/2.

        cs = vs.orthoBoxCS(into, node,0, x,y, textScale, textScale, p.getWidth(), p.getHeight())
        #p.place(vs, cs)

        if self.net.getLeafs().contains(node):
            
            vs.put(org.nongnu.libvob.vobs.RectBgVob(java.awt.Color.magenta), cs)
        p.place(vs, cs)


*/

    public Object getSelectedObject(Object plane, float x, float y, float w, float h) {
	return null;
    }



    public void chgFast(VobScene vs, 
			Object plane,
			int matchingParent,
			int box2screen, int box2plane) { 
        geometry.setCSparams(vs, 1);
    }







    static public void addActions(Action a, 
				  final Object ob, 
				  final Zoomable z,
				  final ParticleSpaceImpl s, 
				  final Particle p, 
				  final WindowAnimation anim) {
	a.setListener(1, 0, "move mind objects", move(ob, z, s, p, anim));
    }


    static MousePressListener move(final Object o, 
				   final Zoomable z,
				   final ParticleSpaceImpl s,
				   final Particle p, 
				   final WindowAnimation anim) {
	return new MousePressListener() {
		public MouseDragListener pressed(int x, int y) {
		    return new RelativeAdapter(){
			    boolean stagnant = false;
			    public void startDrag(int x, int y) {
				super.startDrag(x,y);
				stagnant = s.isStagnant(p);
				synchronized(LOCK) {
				s.createStagnantParticle(o,p);
				}
				p("start "+o);
			    }
			    public void endDrag(int x, int y) {
				super.endDrag(x,y);
				if (!stagnant)
				synchronized(LOCK) {
				    s.setLive(p);
				}
				p("stop");
			    }
			    public void changedRelative(float x, float y) {
				synchronized(LOCK) {
				p.x(p.x() + x/z.getZoom());
				p.y(p.y() + y/z.getZoom());
				}
				// no need..  thread updates.. 
				// anim.rerender();
			    }
			};
		}
	    };
    }    





}
