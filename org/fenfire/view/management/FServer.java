/*
FServer.java
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
import org.fenfire.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.impl.*;
import org.nongnu.libvob.impl.applet.*;

import java.util.*;

/** XXX
 */
public class FServer extends AbstractDelegateLob {
    static public boolean dbg = false;
    private void p(String s) { System.out.println("FServer:: "+s); }

    private ApplitudeManager manager = null;
    // security rules in jdk?
    public void setManager(ApplitudeManager newManager, ApplitudeManager oldManager) {
	if (manager==null) manager = newManager;
	else if (oldManager == manager) manager = newManager;
    }

    public Replaceable[] getParams() { return null; }
    protected Lob getDelegate() { 
	if (manager != null) {
	    return manager.getLob();
	}
	else if (applitudes.size() > 0)
	    return new NullLob();
	else return new NullLob();
    }
    protected Object clone(Object[] o) { 
	throw new Error("unsupported operation"); 
    }


    private Set applitudes = Collections.synchronizedSet(new HashSet());
    public Iterator getApplitudeIterator(ApplitudeManager mgr) {
	if (manager==mgr) return applitudes.iterator(); 
	throw new Error("Only applitude manager is allowed to iterate applitudes.");
    }



    public void createApplitude(Applitude newApplitude) {
	applitudes.add(newApplitude);
	// if (manager!=null) manager.notify(app);
	newApplitude.register();
    }



    public static interface RequestHandler {
	void handleRequest(Object request, Applitude app);
	void handleRequest(Object request, Object[] returnObs, Applitude app);
    }
    public class FEnv {
	private Map reqs = new HashMap();

	public void request(Object request, Applitude app) {
	    if (reqs.get(request) != null)
		((RequestHandler)reqs.get(request)).handleRequest(request, app);
	    else throw new NullPointerException();
	}
	public void request(Object request, Object[] returnObs, 
			    Applitude app) {
	    if (reqs.get(request) != null)
		((RequestHandler)reqs.get(request)).handleRequest(
		    request, returnObs, app);
	    else throw new NullPointerException();
	}

	/** @return true if already inited.
	 */
	public boolean createRequest(Object request, RequestHandler creator) {
	    if (reqs.get(request) == null) {
		reqs.put(request, creator);
		return false;
	    } return true;
	}
    }

    public FEnv environment = new FEnv();

    








    // ---- startup and co --------------------------------------

    private WindowAnimation anim;
    private GraphicsAPI.Window w;
    public WindowAnimation getWindowAnimation() { return anim; }
    public WindowAnimationImpl getScreen() { return (WindowAnimationImpl)anim; }
    private FServer(WindowAnimation anim, GraphicsAPI.Window w) {
	this.anim = anim;
	this.w = w;

	// static sets
	if (GraphicsAPI.getInstance() instanceof org.nongnu.libvob.impl.awt.AWTAPI) {
	    if (GraphicsAPI.getInstance() instanceof org.nongnu.libvob.impl.applet.APPLETAPI) {
		; //org.fenfire.spanimages.fuzzybear.FuzzySpanImageFactory.setComponent(applet);
	    } else {
		org.fenfire.spanimages.fuzzybear.FuzzySpanImageFactory.setComponent(
		    ((org.nongnu.libvob.impl.awt.FrameScreen)w).getFrame());
	    }
	} else if (GraphicsAPI.getInstance() instanceof org.nongnu.libvob.impl.gl.GLAPI) {
	    org.fenfire.spanimages.gl.PoolManager.getInstance(
	    	).setBackgroundProcessUpdate((WindowAnimationImpl)anim);
	}


	environment.createRequest("screen", new RequestHandler() {
		public void handleRequest(Object r, Applitude a) {}
		public void handleRequest(Object r, Object[] o, Applitude a) {}
	    });

	
	//if (applet == null)
	new org.fenfire.modules.init.SystemLoader(this, null);
	//else
	//new org.fenfire.modules.init.AppletLoader(this, null);
    }




    public static void main(String[] argv) {
	Main m = new LobMain(new java.awt.Color(1, 1, .8f)) {
		protected Lob createLob() {
		    return new FServer(windowAnim, window);
		}
	    };
	m.start();
    }



    /* applet stuff can now wait :)
    static java.applet.Applet applet = null;
    public static FServer startApplet(java.applet.Applet app) {
	applet = app;
	FServer f = new FServer();
	GraphicsAPI.getInstance().startUpdateManager(f);
	return f;
    }
    */

}

