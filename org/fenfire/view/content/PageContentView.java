/*
PageContentView.java
 *    
 *    Copyright (c) 2003-2005, Benja Fallenstein
 *                  2005, Matti J. Katila
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
 * Written by Benja Fallenstein and Matti J. Katila
 */
package org.fenfire.view.content;
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

public class PageContentView implements ContentViewSettings.ContentView {
    private static void p(String s) { System.out.println("PageContentView:: "+s); }


    private Graph graph;
    private Object[] types;
    private WindowAnimation anim;
    private LobbedPagePool pagePool;
    public PageContentView(Graph graph, Object[] types, WindowAnimation anim) {
	this.graph = graph;
	this.types = types;
	this.anim = anim;
	pagePool = LobbedPagePool.getInstance();
    }

    public Set getTypes() {
	return Collections.singleton(new ViewSettings.AbstractType() {
		public boolean containsNode(Object n) {
		    if (n instanceof Literal) return false;
		    
		    for (Iterator i=graph.findN_11X_Iter(n, RDF.type); 
			 i.hasNext();) {
			Object node = i.next();
			for (int j = 0; j<types.length; j++) {
			    if (node == types[j]) return true;
			}
		    }
		    return false;
		}
	    });
    }

    static final String FILE = "file://";
    static final String HTTP = "http://";

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

    Map node2state = new HashMap();
    class State {
	int state = 0;
	String uri;
	File file = null;
	String ct = null;
	BlockId id = null;
	PageImageScroll page = null;
	String tmpImgPrefix = null;
	boolean imagesGenerated = false;
	public State(Object node) { this.uri = (String) node; }
    }

    public Lob getLob(Object node) {
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

	lob = getRealLob(s);

	return lob;
    }


    protected Lob setFile(State s) {
	// first get it from network if it's http://something..
	if (s.uri.startsWith(HTTP)) {
	    throw new Error("Not yet implemented");
	}

	// then check the file and content type
	if (s.uri.startsWith(FILE)) {
	    s.file = new File((String)s.uri.substring(FILE.length()));
	}
	return Lobs.nullLob();
    }
    
    protected Lob setContentType(State s) {
	s.ct = ImportUtil.getContentType(s.file);
	boolean ok = false;
	if (s.ct.equals("application/pdf") ||
	    s.ct.equals("application/postscript")) { ok = true; }
	if (!ok) {
	    Lob lob = Lobs.hbox(Lobs.text(Components.font(Color.black), "Wrong content-type: '"+s.ct+"'"));
	    lob = Lobs.frame3d(lob, null, Color.red, 1, 5, false, true);
	    lob = Lobs.align(lob, .5f, .5f);
	    s.ct = null;
	    return lob;
	}
	return Lobs.nullLob();
    }
	
    protected Lob setStormId(State s) {
	// make a storm block id to create the caching
	try {
	    s.id = InputStream2BlockId.slurp(s.ct, 
					     new FileInputStream(s.file));
	} catch (Exception e) {
	    e.printStackTrace();
	    Lob lob = Lobs.hbox(Lobs.text(Components.font(Color.black), "Something wrong: '"+e.getMessage()+"'"));
	    lob = Lobs.frame3d(lob, null, Color.red, 1, 5, false, true);
	    lob = Lobs.align(lob, .5f, .5f);
	    s.id = null;
	    return lob;
	}
	return Lobs.nullLob();
    }

    protected Lob setPageImageScroll(State s) {
	// create page image scroll which keeps information of DSC etc.
	try {
	    StormPool pool = new TransientPool(new HashSet());
	    BlockOutputStream bos = pool.getBlockOutputStream(s.ct);
	    CopyUtil.copy(new FileInputStream(s.file), bos);
	    s.page = new PageImageScroll(new StormAlph(
					   pool), s.id, s.ct);
	    //p("p: "+page);
	} catch (Exception e) {
	    e.printStackTrace();
	    Lob lob = Lobs.hbox(Lobs.text(Components.font(Color.black), "Something wrong while creating page image scroll: '"+e.getMessage()+"'"));
	    lob = Lobs.frame3d(lob, null, Color.red, 1, 5, false, true);
	    lob = Lobs.align(lob, .5f, .5f);
	    s.id = null;
	    return lob;
	}
	return Lobs.nullLob();
    }


    public Lob generateImages(State s) {
	int n = ((Span1D)(s.page.getCurrent())).length();
	
	// Find largest dimensions
	int maxw = 0, maxh = 0;
	for(int p=0; p<n; p++) {
	    Dimension d = ((PageScrollBlock)s.page).getPage(p).getSize();
	    if(d.width > maxw) maxw = d.width;
	    if(d.height > maxh) maxh = d.height;
	}
	float inchWidth = maxw / 72.0f;
	float inchHeight = maxh / 72.0f;
	
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
	} catch (Exception e) {
	    s.imagesGenerated = true;
	    e.printStackTrace();
	}
	return Lobs.nullLob();
    }
    
    boolean u = true;

    protected Lob getRealLob(State s) {
	List list = new ArrayList();
	int n = ((Span1D)(s.page.getCurrent())).length();
	for (int i=0; i<n; i++) {
	    int w = pagePool.getW(i);
	    int h = pagePool.getH(i);
	    File img = new File(ScrollBlockImager.tmp(),
				w+"x"+h+"_"+s.tmpImgPrefix+(i+1));

	    try {
		if (u)
		    pagePool.setImage(new FileInputStream(img), i, w,h);
		list.add(pagePool.getLob(i));
	    } catch(Exception e) {
		e.printStackTrace();
	    }
	}
	u = false;
	return Lobs.hbox(list);
    }	

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
