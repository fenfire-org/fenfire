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


public class LodElevator {
   private static void p(String s) { System.out.println("LodElevator:: "+s); }


    List actives = new ArrayList();

    Map node2state;
    LobbedPagePool pool;
    WindowAnimation anim;
    public LodElevator(final Map node2state,
		       final LobbedPagePool pool,
		       final WindowAnimation anim) {
	this.node2state = node2state;
	this.pool = pool;
	this.anim = anim;

	Thread t = new Thread() {
		public void run() {
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
				if (sp.state.poolInds[sp.getPage()] == i) continue;
				else sp.state.poolInds[sp.getPage()] = i;
				changes = true;
				
				int w = pool.getW(i);
				int h = pool.getH(i);
				File img = new File(ScrollBlockImager.tmp(),
						    w+"x"+h+"_"+sp.state.tmpImgPrefix+
						    (sp.getPage()+1));
				if (img.exists())
				    pool.setImage(new FileInputStream(img), i, w,h);
				else p("no such image! "+img);
			    }

			    if (!anim.hasSceneReplacementPending() &&
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

    
    // page is in base 0
    public void setLOD(PageRequests.State s, int pageB0, int prior) {
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
	PageRequests.State state;
	int prior;
	int basePrior = 0;
	SinglePage(PageRequests.State s, int pr) {
	    state = s; prior = pr;
	}
	public int getPage() {
	    for (int i=0; i<state.pages.length; i++)
		if (state.pages[i] == this) return i;
	    throw new Error("no page found? -- This should not happend because every page is in system.");
	}
    }
    /*
	
    void setLOD(State s, int page, int prior) {
	//p("SET LOD: page: "+page+". prior: "+prior);
	boolean needCreate = true;
	for (int i=0; i<actives.size(); i++) {
	    SPP spp = (SPP)actives.get(i);
	    if (spp.state.uri.equals(s.uri) && spp.page == page)
		needCreate = false;
	}
	if (needCreate) actives.add(new SPP(s, page, prior));
	//p("needCreate: "+needCreate);

	    
	int current = 0;
	for (int i=0; i<actives.size(); i++) {
	    SPP spp = (SPP)actives.get(i);
	    if (spp.state.uri.equals(s.uri) && spp.page == page) {
		current = i; 
		spp.prior = prior;
		break;
	    }
	}
	    
	int swap = current;
	for (int i=swap-1; i>=0; i--) {
	    SPP spp = (SPP)actives.get(i);
	    if ((spp.prior > ((SPP)actives.get(current)).prior) &&
		((pool.getW(i) > pool.getW(current)) ||
		 (pool.getH(i) > pool.getW(current)))) {
		swap = i;
		break;
	    }
	}

	//p("current: "+current+", swap: "+swap);
		
	if (needCreate) {
	    int w = pool.getW(current);
	    int h = pool.getH(current);
	    File img = new File(ScrollBlockImager.tmp(),
				w+"x"+h+"_"+s.tmpImgPrefix+(page+1));
		
	    try {
		pool.setImage(new FileInputStream(img), current, w,h);
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	    return;
	}
	    
	if (swap != current) {
	    //p("SWAP current: "+current+" with swap: "+swap);
	    SPP swap_ = (SPP) actives.get(swap);
	    SPP current_ = (SPP) actives.get(current);

	    try {
		int w = pool.getW(swap);
		int h = pool.getH(swap);
		File img = new File(ScrollBlockImager.tmp(),
				    w+"x"+h+"_"+current_.state.tmpImgPrefix+
				    (current_.page+1));
		pool.setImage(new FileInputStream(img), swap, w,h);

		w = pool.getW(current);
		h = pool.getH(current);
		img = new File(ScrollBlockImager.tmp(),
			       w+"x"+h+"_"+swap_.state.tmpImgPrefix+
			       (swap_.page+1));
		pool.setImage(new FileInputStream(img), current, w,h);
		    
		actives.set(current, swap_);
		actives.set(swap, current_);

	    } catch(Exception e) {
		e.printStackTrace();
	    }
		
	}
		
    }
	
    int getIndex(State s, int page) {
	for (int i=0; i<actives.size(); i++) {
	    SPP spp = (SPP)actives.get(i);
	    if (spp.state.equals(s) && (spp.page == page))
		return i;
	}
	return -1;
    }

    */
}


