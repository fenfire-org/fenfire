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
import org.fenfire.fenfeed.http.*;
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

/** A class that creates lobs that show pages of PS/PDF files,
 *  if they have been loaded already, or status information if they
 *  have not been loaded yet.
 */
public class PageRequests {
    private static void p(String s) { System.out.println("PageRequests:: "+s); }

    static final String FILE = "file://";
    static final String HTTP = "http://";
    static final String CLASSPATH = "java-classpath:"; // argh, hack! --Benja

    private Graph graph;
    private WindowAnimation anim;
    private LobbedImagePool pagePool;
    private LodElevator lodElevator;

    public PageRequests(Graph graph, 
			WindowAnimation anim) {
	this.graph = graph;
	this.anim = anim;
	if (anim == null) throw new IllegalArgumentException();
	if (System.getProperty("LOTS_OF_MEMORY") != null)
	    pagePool = LobbedImagePool.pagePool(2);
	else
	    pagePool = LobbedImagePool.pagePool(1);
	lodElevator = new LodElevator(node2state, pagePool, anim);
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
	    } else throw new Error("There shouln't be another state..");
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

    
    public Lob getOnePage(Object node, int page) {
	State s = (State) node2state.get(node);
	if (s == null)
	    return Components.label("Information lost in cyberspace.");

	if (!s.imagesGenerated)
	    return s.getLob(1, 1);
	
	
	int w = s.maxw;
	int h = s.maxh;
	Lob l = Lobs.nullLob();
	try {
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


    public Lob getRegion(Object node, int start, int end,
			 float x0, float y0, float x1, float y1) {
	State s = (State) node2state.get(node);
	if (s == null)
	    return Components.label("Information lost in cyberspace.");

	if (!s.imagesGenerated)
	    return s.getLob(x1-x0, y1-y0);


	if (start+1 == end) {
	    lodElevator.setLOD(s, start, 0);

	    Lob l = Lobs.nullLob();
	    float w = s.maxw * (x1-x0);
	    float h = s.maxh * (y1-y0);
	    //System.out.println("START IS "+start+" POOLIND "+s.poolInds[start]+", FINNISH KEYMAP IS BORING");
	    l = pagePool.getLob(s.poolInds[start], x0,y0,x1-x0,y1-y0);
	    l = Lobs.request(l, w,w,w,h,h,h);
	    return l;
	} else {
	    throw new Error("not yet implemented.");	    
	}
    }

    public Lob getWholeDocument(Object node, float dx) {
	State s = (State) node2state.get(node);
	if (s == null)
	    return Components.label("Information lost in cyberspace.");

	if (!s.imagesGenerated)
	    return s.getLob(1, 1);

	// set priority == LOD
	int n = s.n;
	float x0 = 0;
	if (dx < 0) {
	    for (int i=0; i<n; i++)
		lodElevator.setLOD(s, i, i);
	} else if (dx > (n*s.maxw)) {
	    for (int i=0; i<n; i++)
		lodElevator.setLOD(s, i, n-i-1);
	} else {
	    for (int i=0; i<n; i++) {
		if (x0 <= dx && dx < (x0+s.maxw)) {
		    int lod = 0;
		    lodElevator.setLOD(s, i, lod);
		    lod = ((dx%s.maxw) > (.5f*s.maxw) ? 1: 2);
		    for (int j=i+1; j<n; j++) {
			lodElevator.setLOD(s, j, lod++);
		    }
		    lod = ((dx%s.maxw) > (.5f*s.maxw) ? 2:1);
		    for (int j=i-1; j>=0; j--) {
			lodElevator.setLOD(s, j, lod++);
		    }
		    break;
		}
		x0 += s.maxw;
	    }
	}

	Lob l = Lobs.hbox();

	for (int i=0; i<s.n; i++) {
	    int w = s.maxw;
	    int h = s.maxh;
	    Lob l2;
	    try {
		l2 = pagePool.getLob(s.poolInds[i]);
		l2 = Lobs.key(l2, s.id.toString()+i);
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
	s = s.replace('&','+');
	return new File(getTempDir(), s);
    }

    Map node2state = Collections.synchronizedMap(new HashMap());

    protected void setFile(State s) {
	// first get it from network if it's http://something..
	if (s.uri.startsWith(HTTP)) {
	    try {
		String uri = s.uri;
		HTTPContext context = new HTTPContext();
		HTTPResource res = new HTTPResource(uri, context);
		s.file = res.getFile();
		p("file: "+s.file);
	    } catch (Exception e) {

		try {

		    if (!getCacheFile(s.uri).exists()) {
			String exec = 
			    //"http_proxy=\"http://localhost:8080\" "+
			    "wget -v --proxy=on "+
			    s.uri+" -O "+getCacheFile(s.uri).getPath();
			p(exec);
			Process p = Runtime.getRuntime().
			    exec(exec);
			/*
			  BufferedReader br = new BufferedReader(
			  new InputStreamReader(p.getErrorStream()));
			  String str;
			  while ((str=br.readLine()) != null) {
			  p(str);
			  }
			*/
			if (p.waitFor() != 0) throw new Error("bad return ");
		    }
		    s.file = getCacheFile(s.uri);
		} catch (Exception e_) {
		    e_.printStackTrace();
		    
		    e.printStackTrace();
		    throw new Error(e.getMessage());
		    
		}
	    }
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
	    s.pages = new LodElevator.SinglePage[s.n];
	    s.poolInds = new int[s.n];
	    for (int i=0; i<s.n; i++) {
		s.pages[i] = null; //new LodElevator.SinglePage(s, 10);
		s.poolInds[i] = -1;
	    }

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
		    // Convert ps/pdf to .png files

		    File imgFile = new File(ScrollBlockImager.tmp(),
					    w_size+"x"+h_size+"_"+
					    s.tmpImgPrefix);  

		    String resolution = ""+xreso+"x"+yreso;
		    
		    BlockFile f = s.page.getBlockFile();

		    String cmdline = "gs -dBATCH -dNOPAUSE -sDEVICE=png256 -r"+resolution+" -sOutputFile="+imgFile.getPath()+"%d "+f.getFilename();

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
