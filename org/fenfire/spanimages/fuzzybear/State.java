/*
State.java
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
import org.fenfire.vocab.*;
import org.fenfire.view.*;
import org.fenfire.spanimages.gl.*;
import org.fenfire.Cursor;
import org.fenfire.lob.*;
import org.fenfire.swamp.*;
import org.fenfire.util.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.lob.*;
import org.nongnu.libvob.lob.lobs.*;

import org.nongnu.storm.util.InputStream2BlockId;
import org.nongnu.storm.util.CopyUtil;
import org.nongnu.storm.impl.*;
import org.nongnu.storm.*;

import org.nongnu.navidoc.util.ProgressListener;

import org.nongnu.alph.impl.*;

import org.nongnu.alph.*;
import org.nongnu.alph.util.*;


import javolution.realtime.*;
import javolution.util.*;

import java.awt.image.*;
import java.awt.Color;
import java.awt.Dimension;
import java.util.*;
import java.io.*;



/** State of progres of generating temporary images for
 *  ps pdf document or image.
 */
public class State implements ProgressListener {
    int state = -1;
    String uri;
    File file = null;
    String ct = null; // content type
    BlockId id = null;
    AbstractScrollBlock page = null; // or image
    int maxw = -1, maxh = -1, n = -1;
    String tmpImgPrefix = null;
    boolean imagesGenerated = false;
    int [] poolInds = null;
    LodElevator.SinglePage[] pages = null; // or in case of image - one image

    WindowAnimation anim;
    public State(String node, WindowAnimation anim) { 
	this.uri = node; 
	this.anim = anim;
    }


    float progress = 0f;
    String msg = "Uninitialized";

    public void setProgress(float progress) {
	this.progress = progress;
	if (!anim.hasSceneReplacementPending())
	    anim.switchVS();
    }
    public void setMessage(String whatIsGoingOn) {
	this.msg = whatIsGoingOn;
	if (!anim.hasSceneReplacementPending())
	    anim.switchVS();
    }

    public Lob getLob(float wfract, float hfract) {
	if (!imagesGenerated) {
	    Lob vbox = Lobs.vbox();
	    List lobs = Lists.list();
	    lobs = Lists.concat(lobs, Lists.list(Lobs.hglue()));

	    String prog = (progress*100)+"";
	    int comma = (progress*100 < 10? 1: 
			 (progress*100 < 100? 2: 3));
	    List text = Components.font().text(prog.substring(0,comma)+"% ");
	    lobs = Lists.concat(lobs, text);

	    Lob l;
	    l = Lobs.filledRect(Color.cyan);
	    int w = (int) (100*progress);
	    int h = 10;
	    l = Lobs.request(l, w,w,w,h,h,h);
	    lobs = Lists.concat(lobs, Lists.list(l));
	    l = Lobs.filledRect(Color.white);
	    w = 100-w;
	    l = Lobs.request(l, w,w,w,h,h,h);
	    lobs = Lists.concat(lobs, Lists.list(l));
	    lobs = Lists.concat(lobs, Lists.list(Lobs.hglue()));
	    vbox.add(Lobs.hbox(lobs));
	    vbox.add(Lobs.glue(Axis.Y, 10));
	    vbox.add(Components.label(msg));
	    l = vbox;
	    //l = Lobs.scale(l, 2f, 2f);
	    if (maxw > 0 && maxh > 0) {
		float lw = maxw*wfract, lh = maxh*hfract;

		l = Components.frame(l);
		l = Lobs.align(l, .5f, .5f);
		l = Lobs.between(Lobs.filledRect(Color.white),
				 l,
				 Lobs.nullLob());
		l = Lobs.request(l, lw, lw, lw, lh, lh, lh);
	    }
	    return Components.frame(l);
	}
	return Lobs.nullLob();
    }
}
