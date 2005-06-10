/*
LodElevator.java
 *    
 *    Copyright (c) 2005, Matti J. Katila
 *
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
 *
 */
/*
 * Written by Matti J. Katila
 */


package org.fenfire.spanimages.fuzzybear;
import org.fenfire.spanimages.gl.*;

import org.nongnu.libvob.*;
import org.nongnu.libvob.lob.*;
import org.nongnu.libvob.lob.lobs.*;

import java.awt.image.*;
import java.awt.Color;
import java.awt.Dimension;
import java.util.*;
import java.io.*;


/** A class that has all image (also page) requests and tries to put them in order of level of detail.
 */
public class LodElevator {
   private static void p(String s) { System.out.println("LodElevator:: "+s); }


    List actives = new ArrayList();

    Map node2state;
    LobbedImagePool pool;
    WindowAnimation anim;
    public LodElevator(final Map node2state,
		       final LobbedImagePool pool,
		       final WindowAnimation anim) {
	this.node2state = node2state;
	this.pool = pool;
	this.anim = anim;

	Thread t = new Thread() {
		public void run() {

		    int activesSize = actives.size();

		    while (true) {
			try {
			    boolean changes = false;
			    int n = 0;

			    Collections.sort(actives, new Comparator() {
				    public int compare(Object o1, Object o2) {
					SinglePage sp1 = (SinglePage) o1;
					SinglePage sp2 = (SinglePage) o2;
					return ((sp1.basePrior+sp1.prior) - (sp2.basePrior+sp2.prior));
				    }
				    public boolean equals(Object o) { return false; }
				});


			    for (int i = 0; i<actives.size(); i++) {
				SinglePage sp = (SinglePage)actives.get(i);
				if ((sp.state.poolInds[sp.getPage()] == i) &&
				    (activesSize == actives.size())) continue;
				else sp.state.poolInds[sp.getPage()] = i;
				changes = true;
				
				int w = pool.getW(i);
				int h = pool.getH(i);
				File img = new File(ScrollBlockImager.tmp(),
						    w+"x"+h+"_"+
						    sp.state.tmpImgPrefix+
						    (sp.state.ct.startsWith(
						       "image/")?
						     "": (""+(sp.getPage()+1))));
				if (img.exists())
				    pool.setImage(new FileInputStream(img), i, w,h);
				else p("no such image! "+img);
			    }

			    activesSize = actives.size();

			    if (changes &&
				!anim.hasSceneReplacementPending() &&
				!anim.hasAnimModeSet())
				anim.switchVS();

			    if (!changes)
				sleep(1000);
			    else
				sleep(500);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		    }
		}
	    };
	t.start();
    }

    
    // page is in 0-based
    public void setLOD(State s, int pageB0, int prior) {
	if (s.pages[pageB0] == null) {
	    s.pages[pageB0] = new SinglePage(s, prior);
	    actives.add(s.pages[pageB0]);
	    s.poolInds[pageB0] = actives.indexOf(s.pages[pageB0]);
	}
	s.pages[pageB0].basePrior = 0;
	s.pages[pageB0].prior = prior;
    }

    public void flush() {
	for (int i = 0; i<actives.size(); i++) {
	    SinglePage sp = (SinglePage)actives.get(i);
	    sp.basePrior++;
	}
    }

    
    static public class SinglePage {
	State state;
	int prior;
	int basePrior = 0;
	SinglePage(State s, int pr) {
	    state = s; prior = pr;
	}
	public int getPage() {
	    for (int i=0; i<state.pages.length; i++)
		if (state.pages[i] == this) return i;
	    throw new Error("no page found? -- This should not happend because every page is in system.");
	}
    }
}


