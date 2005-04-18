/*
PageRequests.java
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
import org.python.util.*;
import org.python.core.*;


import javolution.realtime.*;
import javolution.util.*;

import java.awt.image.*;
import java.awt.Color;
import java.awt.Dimension;
import java.util.*;
import java.io.*;

public class PageRequests {
    private static void p(String s) { System.out.println("PageRequests:: "+s); }

    static final String FILE = "file://";
    static final String HTTP = "http://";

    private Graph graph;
    private WindowAnimation anim;
    private LobbedPagePool pagePool;
    private LodElevator lodElevator;

    public PageRequests(Graph graph, 
			WindowAnimation anim) {
	this.graph = graph;
	this.anim = anim;
	if (anim == null) throw new IllegalArgumentException();
	pagePool = LobbedPagePool.getInstance();
	lodElevator = new LodElevator(node2state, pagePool, anim);
    }

    

    public void request(final String node) {
	if (node2state.containsKey(node)) return;

	p("request: "+node);
	synchronized (node2state) {
	    State s = (State) node2state.get(node);
	    if (s == null) {
		s = new State(node);
		node2state.put(node, s);
	    }
	}	
	Thread t = new Thread() {
		public void run() {
		    try {
		    State s = (State) node2state.get(node);
		    s.setMessage("Check file");
		    s.setProgress(1f/100);
			
		    setFile(s);
		    sleep(300);
		    
		    s.setMessage("Check content type");
		    s.setProgress(5f/100);

		    setContentType(s);
		    sleep(300);

		    s.setMessage("Check Storm ID");
		    s.setProgress(15f/100);
		    
		    setStormId(s);
		    sleep(300);

		    s.setMessage("Creating page image scroll");
		    s.setProgress(20f/100);

		    setPageImageScroll(s);
		    sleep(300);

		    s.setMessage("Generating page images");
		    s.setProgress(30f/100);

		    generateImages(s);
		    s.setMessage("All done - you should see an image now");
		    s.setProgress(100f/100);
		    sleep(300);
		    } catch (Exception e) { e.printStackTrace(); 
		    } catch (Error e) { e.printStackTrace(); 
		    }
		}
	    };
	t.start();
    }

    
    public Lob getOnePage(Object node, int page) {
	State s = (State) node2state.get(node);
	if (s == null)
	    return Components.label("Information lost in cyberspace.");

	if (!s.imagesGenerated)
	    return s.getLob();
	
	
	int w = s.maxw;
	int h = s.maxh;
	Lob l;
	try {
	    s.priors[page-1] = 0;
	    l = pagePool.getLob(s.poolInds[page-1], 0,0,w,h);
	} catch (Exception e) {
	    l = Components.label(""+e.getMessage());
	    l = Components.frame(l);
	    l = Lobs.align(l, .5f, .5f);
	}
	l = Lobs.between(Lobs.filledRect(Color.white),
			 l,
			 Lobs.nullLob());
	return Lobs.request(l, w,w,w,h,h,h);
    }

    public Lob getWholeDocument(Object node) {
	State s = (State) node2state.get(node);
	if (s == null)
	    return Components.label("Information lost in cyberspace.");

	if (!s.imagesGenerated)
	    return s.getLob();


	Lob l = Lobs.hbox();
	for (int i=0; i<s.n; i++) {
	    int w = s.maxw;
	    int h = s.maxh;
	    Lob l2;
	    try {
		l2 = pagePool.getLob(s.poolInds[i]);
	    } catch (Exception e) {
		l2 = Components.label(""+e.getMessage());
	    }
	    l2 = Lobs.between(Lobs.filledRect(Color.white),
			 l2,
			 Lobs.nullLob());
	    l2 = Lobs.request(l2, w,w,w,h,h,h);
	    l.add(l2);
	}
	    
	return l; 

    }




    File getTempDir() {
	String dir = System.getProperty("page.cache", "./tmpimg");
	File cacheDir = new File(dir);
	if(!cacheDir.exists() && !cacheDir.mkdirs()) {
	    //return null;
	    throw new Error("Disc full? Can't create temp directory for images.");
	}
	return cacheDir;
    }

    File getCacheFile(String in) {
	String s = in.replace('/','-');
	s = s.replace(':','_');
	return new File(getTempDir(), s);
    }

    Map node2state = Collections.synchronizedMap(new HashMap());
    class State implements ProgressListener {
	int state = -1;
	String uri;
	File file = null;
	String ct = null;
	BlockId id = null;
	PageImageScroll page = null;
	int maxw = -1, maxh = -1, n = -1;
	String tmpImgPrefix = null;
	boolean imagesGenerated = false;
	int[] poolInds = null;
	int[] priors = null;

	public State(String node) { this.uri = node; }


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

	public Lob getLob() {
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
		    l = Components.frame(l);
		    l = Lobs.align(l, .5f, .5f);
		    l = Lobs.between(Lobs.filledRect(Color.white),
				     l,
				     Lobs.nullLob());
		    l = Lobs.request(l, 
				     maxw, maxw, maxw,
				     maxh, maxh, maxh);
		}
		return Components.frame(l);
	    }
	    return Lobs.nullLob();
	}
    }

    /*
    public Lob getLob(Object node) {
	return getLob(node, 0);
    }

    public Lob getLob(Object node, float change) {
	Lob lob = Lobs.nullLob();
	State s = (State) node2state.get(node);
	if (s == null) {
	    s = new State(node);
	    node2state.put(node, s);
	}
	
	if (s.file == null)
	    lob = setFile(s);
	if (s.ct == null)
	    lob = setContentType(s);
	if (s.id == null)
	    lob = setStormId(s);
	if (s.page == null)
	    lob = setPageImageScroll(s);
	if (s.imagesGenerated == false)
	    lob = generateImages(s);

	lob = getRealLob(s, change);

	return lob;
    }
    */

    protected void setFile(State s) {
	// first get it from network if it's http://something..
	if (s.uri.startsWith(HTTP)) {
	    throw new Error("Not yet implemented");
	}

	// then check the file and content type
	if (s.uri.startsWith(FILE)) {
	    s.file = new File((String)s.uri.substring(FILE.length()));
	}
    }
    
    protected void setContentType(State s) {
	s.ct = ImportUtil.getContentType(s.file);
	boolean ok = false;
	if (s.ct.equals("application/pdf") ||
	    s.ct.equals("application/postscript")) { ok = true; }
	if (!ok) {
	    s.setMessage("ERROR: Wrong content-type: '"+s.ct+"'");
	    throw new Error("error");
	}
    }
	
    protected void setStormId(State s) {
	// make a storm block id to create the caching
	try {
	    s.id = InputStream2BlockId.slurp(s.ct, 
					     new FileInputStream(s.file));
	} catch (Exception e) {
	    e.printStackTrace();
	    s.setMessage("ERROR: Something wrong: '"+e.getMessage()+"'");
	    throw new Error(e);
	}
    }

    protected void setPageImageScroll(State s) {
	// create page image scroll which keeps information of DSC etc.
	try {
	    StormPool pool = new TransientPool(new HashSet());
	    BlockOutputStream bos = pool.getBlockOutputStream(s.ct);
	    CopyUtil.copy(new FileInputStream(s.file), bos);
	    s.page = new PageImageScroll(new StormAlph(
					     pool), s.id, s.ct);

	    s.n = ((Span1D)(s.page.getCurrent())).length();
	    s.poolInds = new int[s.n];
	    s.priors = new int[s.n];
	    for (int i=0; i<s.n; i++) s.poolInds[i] = -1;


	    // Find largest dimensions
	    int maxw = 0, maxh = 0;
	    for(int p=0; p<s.n; p++) {
		Dimension d = ((PageScrollBlock)s.page).getPage(p).getSize();
		if(d.width > maxw) maxw = d.width;
		if(d.height > maxh) maxh = d.height;
	    }
	    s.maxw = maxw;
	    s.maxh = maxh;
	} catch (Exception e) {
	    e.printStackTrace();
	    s.setMessage("ERROR: Something wrong while creating page image scroll: '"+e.getMessage()+"'");
	    throw new Error(e);
	}
    }


    public void generateImages(State s) {
	int n = s.n;
	
	// 72 dpi? http://www.scantips.com/basics1a.html
	// this is passed to gs
	float inchWidth = s.maxw / 72.0f;
	float inchHeight = s.maxh / 72.0f;
	
	try {
	    s.imagesGenerated = true;
	    for (int i=0; i<pagePool.getSizes(); i++) {
		int w_size = pagePool.getSizeW(i);
		int h_size = pagePool.getSizeH(i);
		
		int xreso = (int)(w_size / inchWidth);
		int yreso = (int)(h_size / inchHeight);
		
		s.tmpImgPrefix = ScrollBlockImager.protectChars(
		    s.page.getID() + "-");
		
		boolean ok = true;
		for(int p=0; p<n; p++) {
		    File imgFile = new File(ScrollBlockImager.tmp(),
					    w_size+"x"+h_size+"_"+
					    s.tmpImgPrefix+(p+1));
		    if (!imgFile.exists()) ok = false;
		}
		if (!ok) {
		    if(interp == null) {
			interp = new PythonInterpreter();
			interp.exec("import alph.util.psimages\n"+
				    "import vob.putil.mipzipmaker\n"+
				    "cv = alph.util.psimages.convertFile\n"+
				    "mz = vob.putil.mipzipmaker.makeMipzip\n"
			    );
		    }
		    File imgFile = new File(ScrollBlockImager.tmp(),
					    w_size+"x"+h_size+"_"+
					    s.tmpImgPrefix);  
		    String resolution = ""+xreso+"x"+yreso;
		    
		    // 1. Convert ps/pdf to .png files
		    BlockFile f = s.page.getBlockFile();
		    if(! interp.get("cv").__call__(new PyObject[] {
			new PyString(f.getFilename()),
			new PyString(imgFile.getPath()),
			new PyString(resolution)
		    }).__nonzero__())
			throw new Exception("Conversion unsuccessful");
		}
	    }

	    // clear to mimimize memory usage
	    s.page = null;

	} catch (Exception e) {
	    s.imagesGenerated = false;
	    e.printStackTrace();
	}
    }

    /*
    protected Lob getRealLob(State s, float dx) {
	long time = System.currentTimeMillis();
	long tooMuch = time + 20;

	List list = new ArrayList();
	int n = s.n;
	
	
	// set priority == LOD
	float x0 = 0;
	if (dx < 0) {
	    for (int i=0; i<n; i++)
		if (System.currentTimeMillis() < tooMuch)
		    lods.setLOD(s, i, i);
	} else if (dx > (n*s.maxw)) {
	    for (int i=0; i<n; i++)
		if (System.currentTimeMillis() < tooMuch)
		    lods.setLOD(s, i, n-i-1);
	} else {
	    for (int i=0; i<n; i++) {
		if (x0 <= dx && dx < (x0+s.maxw)) {
		    int lod = 0;
		    for (int j=i; j<n; j++) {
			if (System.currentTimeMillis() < tooMuch)
			    lods.setLOD(s, j, lod++);
		    }
		    lod = 1;
		    for (int j=i-1; j>=0; j--) {
			if (System.currentTimeMillis() < tooMuch)
			    lods.setLOD(s, j, lod++);
		    }
		    break;
		}
		x0 += s.maxw;
	    }
	}

	Lob l = Lobs.hbox();
	for (int i=0; i<n; i++) {
	    int w = s.maxw;
	    int h = s.maxh;
	    Lob l2;
	    try {
		l2 = pagePool.getLob(lods.getIndex(s, i));
	    } catch (Exception e) {
		l2 = Components.label(e.getMessage());
	    }
	    l2 = Lobs.request(l2, w,w,w,h,h,h);
	    l.add(l2);
	}
	    
	return l; 
    }	
    */


    PythonInterpreter interp;


	/*
	String uri = (String) node;
	ProgressLob l = Progress.pageLob(anim, backgroundProcess);
	if (uri.startswith("file://")) {
	    l.initPhases(4);
	    l.push();

	    // -checking cache and file 
	    // -sizes and structure...
	    // -convert to png
	    // -load image
	    // -use more progress lobs to create 
            //  each for different page.
	    */







}
