/*
PageScrollBlockImager.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka
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
 * Written by Tuomas J. Lukka
 */

package org.fenfire.spanimages.gl;
import org.nongnu.alph.*;
import org.nongnu.alph.util.*;
import org.nongnu.navidoc.util.*;
import org.nongnu.libvob.util.CacheControl;
import org.nongnu.storm.*;
import org.python.util.*;
import org.python.core.*;
import java.awt.image.*;
import java.awt.Dimension;
import java.io.*;
import java.util.*;

/** A class that converts PageImageSpans to SingleImages.
 */
public class PageScrollBlockImager extends ScrollBlockImager {
    public static boolean dbg = true;
    private static void p(String s) { 
	System.out.println("PageScrollBlockImager: "+s); }

    public int RESOLUTION = 160;
    public int MAXTEXSIZE = 2048;

    /** A cache of block file names.
     * Useful because usually pages of the same file are created
     * right next to each other.
     */
    private Map block2pagefiles = Collections.synchronizedMap(
				    new CachingMap(200));
    private CacheControl.Listener block2pagefileListener =
		CacheControl.registerCache(this,
			    "PageScrollBlockImager block2pagefile");

    /** A list (index==page) of maps (key==blockid, value==SingleImage).
     * There are far fewer pages than blocks - therefore,
     * we'll use a list of pages and for each page, a map
     * from block id to singleimage.
     * A bit strange but efficient.
     */
    private List pageblock2singleimage  = Collections.synchronizedList(new ArrayList());
    private class PageMap {
	Map map;
	CacheControl.Listener listener;
    }
    private PageMap getPageblock2singleimageMap(int i) {
	while(i >= pageblock2singleimage.size()) 
	    pageblock2singleimage.add(null);
	PageMap m = (PageMap)pageblock2singleimage.get(i);
	if(m == null) {
	    m = new PageMap();
	    m.map = Collections.synchronizedMap(new SoftValueMap());
	    m.listener = CacheControl.registerCache(m, 
			"PageScrollBlockImager map");
	    pageblock2singleimage.set(i, m);
	}
	return m;
    }

    class PageFiles {
	int xreso, yreso;
	String[] filenames;
    }

    public SingleImage getSingleImage(ImageSpan img) {
	PageImageSpan span = (PageImageSpan)img;
	int page = span.getPageIndex();

	PageMap map = getPageblock2singleimageMap(page);
	ScrollBlock sb = img.getScrollBlock();

	SingleImage singleImage = (SingleImage)map.map.get(sb.getID());

	if(singleImage == null) {
	    map.listener.startMiss(sb.getID());

	    try {
		PageFiles pageFiles = getPageFiles(sb);
		singleImage = new SingleImage(
			sb.getID(),
			page,
			pageFiles.filenames[page],
			pageFiles.xreso,
			pageFiles.yreso
			);
	    } catch(Exception e) {
		e.printStackTrace();
		throw new Error("Couldn't create singleimage! "+e);
	    }
	    map.map.put(sb.getID(), singleImage);
	} else
	    map.listener.hit(sb.getID());
	return singleImage;
    }

    private PageFiles getPageFiles(ScrollBlock pages) 
	    throws IOException {
	PageFiles f = (PageFiles) block2pagefiles.get(pages);
	if(f == null) {
	    block2pagefileListener.startMiss(pages);
	    f = makePageFiles(pages);
	    block2pagefiles.put(pages, f);
	    block2pagefileListener.endMiss(pages);
	} else
	    block2pagefileListener.hit(pages);
	return f;
    }

    PythonInterpreter interp;

    private synchronized PageFiles makePageFiles(ScrollBlock pages) 
	    throws java.io.IOException {
	String ct = pages.getContentType();
	if(! (ct.equals("application/pdf") ||
	      ct.equals("application/postscript")))
	    throw new Error("Not a proper page scroll");

	int n = ((Span1D)(pages.getCurrent())).length();

	// Find largest dimensions
	int maxw = 0, maxh = 0;
	for(int p=0; p<n; p++) {
	    Dimension d = ((PageScrollBlock)pages).getPage(p).getSize();
	    if(d.width > maxw) maxw = d.width;
	    if(d.height > maxh) maxh = d.height;
	}

	float inchWidth = maxw / 72.0f;
	float inchHeight = maxh / 72.0f;

	int xreso = (int)(MAXTEXSIZE / inchWidth);
	int yreso = (int)(MAXTEXSIZE / inchHeight);

	String resolution = ""+xreso+"x"+yreso;

	String prefix = protectChars(pages.getID()) + "-" + resolution + "-";

	String[] tmppaths = new String[n];
	String[] paths = new String[n];
	boolean exist = true;
	for(int i=0; i<n; i++) {
	    String base = prefix + (i+1);
	    File f = new File(tmp(), base);
	    paths[i] = f.getPath();
	    tmppaths[i] = new File(tmp(), "tmp"+base).getPath();
	    if(!f.exists()) exist = false;
	}

	PageFiles res = new PageFiles();
	res.xreso = xreso;
	res.yreso = yreso;
	res.filenames = paths;

	if(exist) return res;
	
	// Need to make them.
	//
	if(interp == null) {
	    interp = new PythonInterpreter();
	    interp.exec("import alph.util.psimages\n"+
			"import vob.putil.mipzipmaker\n"+
			"cv = alph.util.psimages.convertFile\n"+
			"mz = vob.putil.mipzipmaker.makeMipzip\n"
			);
	}

	// 1. Convert ps/pdf to .png files
	BlockFile f = pages.getBlockFile();
	if(! interp.get("cv").__call__(new PyObject[] {
	    new PyString(f.getFilename()),
	    new PyString(new File(tmp(), "tmp"+prefix).getPath()),
	    new PyString(resolution)
	}).__nonzero__())
	    throw new Error("Conversion unsuccessful");

	// 2. Convert .png to mipzip files
	for(int i=0; i<n; i++) {
	    interp.get("mz").__call__(new PyObject[] {
		new PyString(tmppaths[i]),
		new PyString(paths[i]),
		new PyInteger(MAXTEXSIZE),
		new PyInteger(MAXTEXSIZE)
	    });
	    (new File(tmppaths[i])).delete();
	}

	return res;
    }


}
