/*
ParticleMindMapMainView.java
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

package org.fenfire.view.main.impl;
import org.fenfire.view.management.*;
import org.fenfire.view.main.*;
import org.fenfire.fenmm.*;
import org.fenfire.*;
import org.fenfire.functional.*;
import org.fenfire.fenmm.*;
import org.fenfire.fenmm.impl.*;
import org.fenfire.swamp.*;

import org.nongnu.libvob.*;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.component.*;
import org.nongnu.libvob.layout.unit.*;
import org.nongnu.libvob.mouse.*;
import org.nongnu.libvob.physics.*;
import org.nongnu.libvob.physics.impl.*;

import java.util.*;

public class ParticleMindMapMainView extends AbstractDelegateLob {
    static private void p(String s) { System.out.println("ParticleMainView2:: "+s); }

    public Replaceable[] getParams() {
	throw new Error("unimplemented"); 
    }
    protected Object clone(Object[] i) {
	throw new Error("unimplemented"); 
    }



    RingMindNet net;
    ParticleSpace particleSpace;
    ParticleSimulator simulator;
    Geometry geometry;
    Thread background;
    long timeLong, timeShort;



    static final Object LOCK = new Object();

    final Model zoom = new FloatModel(1);
    PureNodeFunction particleFunc;
    Object node;
    final FServer f;
    public ParticleMindMapMainView(final FServer f, Object node) {
	this.f = f;
	this.node = node;

	particleSpace = new ParticleSpaceImpl();
	simulator = new ParticleSimulator(particleSpace);

	geometry = new GeometryImpl(simulator,
				    particleSpace,
				    new GeometryPlugin());
	
	Fen[] fen = new Fen[1];
	f.environment.request("fen", fen, null);

	background = new Thread() {
		public void run() {
		    while (true) {
		    try {
			synchronized(LOCK) {
			    simulator.simulate(0.1f);
			    geometry.setModels();
			}
			f.getWindowAnimation().rerender();
			if (timeLong < System.currentTimeMillis()) 
			    sleep(2000);
			else if (timeShort < System.currentTimeMillis()) 
			    sleep(300);
			else sleep(100);
		    } catch (Exception e) { e.printStackTrace(); }
		    } 
		}
	    };
	timeLong = timeShort = System.currentTimeMillis();
	background.start();

	
	particleFunc = new CachedPureNodeFunction(2, fen[0].graph, 
	     new PureNodeFunction() {
		 public Object f(ConstGraph g, Object o) {
		     synchronized(LOCK) {
			 ((ParticleSpaceImpl)particleSpace).init();
			 net = new RingMindNetImpl(g);
			 net.constructNet(o);
			 geometry.construct(net);
			 geometry.generateModels();
			 
			 Lob lob = NullLob.instance;
			 try {
			     lob = new Image(new java.io.File("ff_bg.png"));
			 } catch (Exception e) { e.printStackTrace(); }
			 
			 lob = new Between(lob, drawMindMap(), NullLob.instance);
			 
			 lob = new PanZoomLob(lob, zoom);
			 lob = new DragController(lob, 3, new RelativeAdapter() {
				 public void changedRelative(float dx, float dy) {
				     zoom.setFloat(zoom.getFloat() + dy/100);
				     f.getWindowAnimation().rerender();
				 }
			     });
			 lob = new Between(new Label("This is a zoomable area (mouse button 3) in where there are particle mind map that you can control with mouse button 1."), 
					   lob, NullLob.instance);
			 return lob;
		     }
		 }
	     });
    }
    
    public Lob getDelegate() { 
	Fen[] fen = new Fen[1];
	f.environment.request("fen", fen, null);
	Lob l = (Lob) particleFunc.f(fen[0].graph, node); 
	return l;
    }

    private Lob drawMindMap() {
	Tray nodes = new Tray(false);
        Map links = new HashMap();

	Iterator i = net.iterateAllNodes();
	while (i.hasNext()) {
            final Object node = i.next();
            final Place pl = geometry.getPlace(node);
            if (pl == null)
		throw new Error(" raise 'argh! no place?!'");

	    Iterator it = net.iterator(node);
            while (it.hasNext()) {
                Object n = it.next();
                Place pl2 = geometry.getPlace(n);
                if (pl2 == null)
		    throw new Error("raise 'argh! again no place?!'");
	    }

            //# draw text etc..
	    if (node != null) {
		Lob l = new Label(node.toString().substring(0, 7));
		l = new Frame(l, Theme.lightColor, 
			      Theme.darkColor, 3, 3, 
			      true, true, false);
		l = pl.lob = new AnchorLob(l);
		l = new RelativeDragController(l, 1, new RelativeDragListener() {
			public void change(float dx, float dy) {
			    synchronized(LOCK) {
				// let the simulator to run a lot on next 9 secs.
				// and a bit slower after that and almoust 
				// stop after 1 minute.
				timeShort = System.currentTimeMillis() + 9000;
				timeLong = System.currentTimeMillis() + 60000;
				pl.x.setFloat(pl.x.getFloat() + dx/zoom.getFloat());
				pl.particle.x(pl.x.getFloat());
				pl.y.setFloat(pl.y.getFloat() + dy/zoom.getFloat());
				pl.particle.y(pl.y.getFloat());
				f.getWindowAnimation().rerender();
			    }
			}
		    });
		
		l = new AlignLob(l, 0,0, .5f, .5f);
		l = new TranslationLob(l, pl.x, pl.y);
		nodes.add(l, node);
	    } else throw new Error("You might have initialized the mindet "+
				   "with nothing. Anyway, this is user error.");
	}

	// add connections
	Tray lines = new Tray(true);
	i = net.iterateAllNodes();
	while (i.hasNext()) {
            Object node = i.next();
	    Place pl = geometry.getPlace(node);
            if (pl == null)
		throw new Error(" raise 'argh! no place?!'");

	    Iterator it = net.iterator(node);
            while (it.hasNext()) {
                Object n = it.next();
                Place pl2 = geometry.getPlace(n);
                if (pl2 == null)
		    throw new Error("raise 'argh! again no place?!'");
		lines.add(new Decoration(NullLob.instance, connection,
					 pl.lob, pl2.lob));
	    }
 	}
	return new Between(lines, nodes, NullLob.instance);
    }

    Vob connection = 
    new org.nongnu.libvob.vobs.SimpleConnection(.5f,.5f,.5f,.5f);

}
