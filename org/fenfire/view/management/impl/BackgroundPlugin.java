/*
BackgroundPlugin.java
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
import org.fenfire.spanimages.*;

import org.nongnu.libvob.*;
import org.nongnu.libvob.vobs.*;

import org.nongnu.alph.*;
import org.nongnu.alph.impl.*;

import java.util.*;
import java.io.*;

public class BackgroundPlugin 
    implements ApplitudeManager.Plugin, Renderable {
    static public boolean dbg = false;
    
    SpanImageVob vob, vobLogo;
    ScrollBlock sc, scLogo;

    public BackgroundPlugin(FServer f, ApplitudeManager mgr) throws Exception {
	mgr.addPlugin(this);

	StormAlph [] a = new StormAlph[1];
	f.environment.request("alph", a, null);
	sc = a[0].addFile(new File("ff_bg.png"), "image/png");
	scLogo = a[0].addFile(new File("ff_logo.png"), "image/png");

	
	//System.out.println("next is functional..."+sc);
	vob = (SpanImageVob) SpanImageFactory.getDefaultInstance().f(sc.getCurrent());
	vobLogo = (SpanImageVob) SpanImageFactory.getDefaultInstance().f(scLogo.getCurrent());
	//System.out.println("vob: "+vob+", sc: "+sc);
	
	f.getScreen().switchVS();
    }

    public void render(VobScene vs, int csi) {
	//if (true) return;

	//vob = (SpanImageVob) SpanImageFactory.getDefaultInstance().f(sc.getCurrent());
	//System.out.println("vob: "+vob+", cscurr: "+sc.getCurrent());
	
	float w = vob.getWidth();
	float h = vob.getHeight();
	//System.out.println("h: "+h+", h: "+h);
	//if (w<0 || h < 0) return;

	boolean fullScreen = false;
	if (fullScreen) {
	    int cs = vs.orthoCS(0, "background", 0, 0,0,
				(float) vs.size.getWidth()/w,
				(float) vs.size.getHeight()/h);
	    if (dbg) {
		System.out.println("h: "+((float) vs.size.getWidth()/w)+
				   ", h: "+((float) vs.size.getHeight()/h));
	    }
	    vs.put(vob, cs);
	} else {
	    for (int i=0; i<vs.size.getWidth(); i+=w) {
		for (int j=0; j<vs.size.getHeight(); j+=h) {
		    //System.out.println("i: "+i+", j: "+j+", w: "+w+", h: "+h);
		    int cs = vs.orthoCS(0, "background"+i+":"+j, 10000, i,j, 1,1);
		    vs.put(vob, cs);
		}
	    }
	}
	int cs = vs.orthoCS(0, "backgroundLogo", 10000, 
			    (float) vs.size.getWidth() - vobLogo.getWidth(),
			    (float) vs.size.getHeight() - vobLogo.getHeight(),
			    1,1);
	vs.put(vobLogo, cs);
    }
    
}
