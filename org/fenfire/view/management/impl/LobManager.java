/*
LobManager.java
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

package org.fenfire.view.management.impl;
import org.fenfire.view.management.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.unit.*;
import org.nongnu.libvob.layout.component.*;
import org.nongnu.libvob.vobs.*;
import org.nongnu.libvob.mouse.*;

import org.nongnu.storm.*;
import org.nongnu.storm.impl.*;

import java.util.*;

/** The default implementation of applitude view manager. 
 */
public class LobManager extends OverlappingWindowManager implements ApplitudeManager, FServer.RequestHandler {
    static public boolean dbg = true;
    private void p(String s) { System.out.println("DefaultAppMgr:: "+s); }
    

    class Example implements Layoutable, Applitude {
	FServer f;
	Example(FServer f) { this.f = f; }
	public void register() {
	    f.environment.request("layoutable", this);
	}
	public Lob getLob() { return new Label("Example"); }
    }

    class Example2 implements Layoutable, Applitude {
	FServer f;
	Lob editor = new org.fenfire.fenedit.Editor();
	Example2(FServer f) { this.f = f; }
	public void register() {
	    f.environment.request("layoutable", this);
	}
	public Lob getLob() { return editor; }
    }



    private FServer f;
    public LobManager(FServer f) {
	this(f, f.getWindowAnimation());
    }
    public LobManager(FServer f, WindowAnimation anim) {
	this(f, anim, new ListModel.Simple());
    }
    public LobManager(FServer f, WindowAnimation anim, ListModel windows) {
	super(anim, windows);
	this.f = f;

	f.environment.createRequest("layoutable", this);

	f.environment.createRequest("rerender", this);
	f.environment.createRequest("set link end", this);
	f.environment.createRequest("set link start", this);


	f.createApplitude(new Example2(f));
	f.createApplitude(new Example(f));
	f.createApplitude(new FourthApplitude(f));
    }
    public void addPlugin(ApplitudeManager.Plugin plug) { }

    int startCS=0, startX=0, startY=0,
	endCS=0,   endX=0,   endY=0;
    Vob linkVob = new SimpleConnection(0,0,0,0);

    public void handleRequest(Object req, Applitude app) {
	if (req.equals("rerender")) {
	    f.getScreen().getVobSceneForEvents().coords.setTranslateParams(startCS, startX, startY);
	    f.getScreen().getVobSceneForEvents().coords.setTranslateParams(endCS, endX, endY);
	}
	else if (req.equals("layoutable")) {
	    p("layoutable!"+ app);
	    if (app instanceof Layoutable) {
		add(((Layoutable)app).getLob(), "Title n+1");
	    }
	} 
    }
    public void handleRequest(Object req, Object[] o, Applitude app) {
	if (req.equals("set link start")) {
	    startX = ((Integer)o[0]).intValue();
	    startY = ((Integer)o[1]).intValue();
	} else if (req.equals("set link end")) {
	    endX = ((Integer)o[0]).intValue();
	    endY = ((Integer)o[1]).intValue();
	}
    }
}
