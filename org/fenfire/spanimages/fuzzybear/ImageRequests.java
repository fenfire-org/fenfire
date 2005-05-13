/*
ImageRequests.java
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

public class ImageRequests {
    private static void p(String s) { System.out.println("ImageRequests:: "+s); }

    static final String FILE = "file://";
    static final String HTTP = "http://";
    static final String CLASSPATH = "java-classpath:"; // argh, hack! --Benja

    private Graph graph;
    private WindowAnimation anim;
    private LobbedImagePool imgPool;
    private LodElevator lodElevator;

    public ImageRequests(Graph graph, 
			WindowAnimation anim) {
	this.graph = graph;
	this.anim = anim;
	if (anim == null) throw new IllegalArgumentException();
	imgPool = LobbedImagePool.imagePool();
	lodElevator = new LodElevator(node2state, imgPool, anim);
    }

    public void flush() { 
	lodElevator.flush(); 
    }

    public void request(final String node) {
	if (node2state.containsKey(node)) return;

	p("request: "+node);
	synchronized (node2state) {
	    State s = (State) node2state.get(node);
	    if (s == null) {
		s = new State(node, anim);
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
		    } catch (Exception e) { e.printStackTrace(); 
		    } catch (Error e) { e.printStackTrace(); 
		    }
		}
	    };
	t.start();
    }

    

    public Lob getRegion(Object node, 
			 float x0, float y0, float x1, float y1) {
	State s = (State) node2state.get(node);
	if (s == null)
	    return Components.label("Information lost in cyberspace.");

	if (!s.imagesGenerated)
	    return s.getLob(x1-x0, y1-y0);


	Lob l;
	float w = s.maxw * (x1-x0);
	float h = s.maxh * (y1-y0);
	//System.out.println("START IS "+start+" POOLIND "+s.poolInds[start]+", FINNISH KEYMAP IS BORING");
	l = imgPool.getLob(s.poolInds[0], x0,y0,x1-x0,y1-y0);
	l = Lobs.request(l, w,w,w,h,h,h);
	return l;
    }

    public Lob getWholeImage(Object node, float dx) {
	State s = (State) node2state.get(node);
	if (s == null)
	    return Components.label("Information lost in cyberspace.");

	if (!s.imagesGenerated)
	    return s.getLob(1, 1);

	// set priority == LOD
	lodElevator.setLOD(s, 0, (int) dx);
	return getRegion(node, 0,0,1,1);
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

    protected void setFile(State s) {
	// first get it from network if it's http://something..
	if (s.uri.startsWith(HTTP)) {
	    throw new Error("Not yet implemented");
	}

	if(s.uri.startsWith(CLASSPATH)) {
	    String relative = s.uri.substring(CLASSPATH.length());
	    
	    try {
		InputStream in = 
		    getClass().getClassLoader().getResourceAsStream(relative);

		if(in == null)
		    throw new Error("resource not found: "+relative);

		s.file = File.createTempFile("fenfire", null);
		s.file.deleteOnExit();
		OutputStream out = new FileOutputStream(s.file);
		
		org.nongnu.storm.util.CopyUtil.copy(in, out);
	    } catch(IOException e) {
		throw new Error(e); // argh...
	    }
	}

	// then check the file and content type
	if (s.uri.startsWith(FILE)) {
	    s.file = new File((String)s.uri.substring(FILE.length()));
	}
    }
    
    protected void setContentType(State s) {
	s.ct = ImportUtil.getContentType(s.file);
	boolean ok = false;
	if (s.ct.startsWith("image/")) { ok = true; }
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
	    s.page = new SimpleImageScroll(new StormAlph(
					     pool), s.id, s.ct);

	    s.n = 1;
	    s.pages = new LodElevator.SinglePage[s.n];
	    s.poolInds = new int[s.n];
	    s.pages[0] = null; //new LodElevator.SingleImage(s, 10);
	    s.poolInds[0] = -1;

	    // Find largest dimensions
	    Dimension d = 
		((ImageSpan) 
		 ((SimpleImageScroll)s.page).getCurrent()).getSize();
	    s.maxw = d.width;
	    s.maxh = d.height;
	} catch (Exception e) {
	    e.printStackTrace();
	    s.setMessage("ERROR: Something wrong while creating page image scroll: '"+e.getMessage()+"'");
	    throw new Error(e);
	}
    }


    public void generateImages(State s) {
	float inchWidth = s.maxw / 72.0f;
	float inchHeight = s.maxh / 72.0f;
	
	try {
	    for (int i=0; i<imgPool.getSizes(); i++) {
		int w_size = imgPool.getSizeW(i);
		int h_size = imgPool.getSizeH(i);

		s.tmpImgPrefix = ScrollBlockImager.protectChars(
		    s.page.getID() + "-");
		
		File imgFile = new File(ScrollBlockImager.tmp(),
					w_size+"x"+h_size+"_"+
					s.tmpImgPrefix);
		if (!imgFile.exists()) {
		    // Convert image to png file

		    BlockFile f = s.page.getBlockFile();

		    String cmdline = "convert -size "+w_size+"x"+h_size+
			" " + f.getFilename()+ 
			" " + imgFile.getPath();

		    Process p = Runtime.getRuntime().exec(cmdline);
		    int res = p.waitFor();

		    if(res != 0) {
			throw new Error("Error running Ghostscript: "+res);
		    }
		}
	    }
	    s.imagesGenerated = true;

	    // clear to mimimize memory usage
	    s.page = null;

	} catch (Exception e) {
	    s.imagesGenerated = false;
	    e.printStackTrace();
	}
    }

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
