/*
ImageScrollBlockImager.java
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

/** A class that converts ImageSpans to SingleImages.
 */
public class ImageScrollBlockImager extends ScrollBlockImager {
    public static boolean dbg = true;
    private static void p(String s) { 
	System.out.println("ImageScrollBlockImager: "+s); }

    public int RESOLUTION = 160;
    public int MAXTEXSIZE = 2048;



    private class Image {
	SingleImage singleImage;
	CacheControl.Listener listener;
    }

    private Map block2img = Collections.synchronizedMap(
				    new CachingMap(200));

    private Image getImageblock2imageMap(ScrollBlock sb) {
	Image image = 
	    (Image)block2img.get(sb.getID());
	if (image == null) {
	    image = new Image();
	    image.listener = 
		CacheControl.registerCache(image, 
					   "ImageScrollBlockImager");
	    block2img.put(sb.getID(), image);
	}
	return image;
    }

    public SingleImage getSingleImage(ImageSpan img) {
	ScrollBlock sb = img.getScrollBlock();
	Image image = getImageblock2imageMap(sb);
	SingleImage singleImage = image.singleImage;

	if(singleImage == null) {
	    image.listener.startMiss(sb.getID());

	    try {
		p("so, we need to make mipzip..");
		ImageFile imageFile = makeImageFile(sb);
		singleImage = new SingleImage(
			sb.getID(),
			0, 
			imageFile.filename,
			imageFile.xreso,
			imageFile.yreso
			);
	    } catch(Exception e) {
		e.printStackTrace();
		throw new Error("Couldn't create singleimage! "+e);
	    }
	    image.singleImage = singleImage;
	} else
	    image.listener.hit(sb.getID());
	return singleImage;
    }

    private class ImageFile {
	int xreso, yreso;
	String filename;
    }


    PythonInterpreter interp;

    private synchronized ImageFile makeImageFile(ScrollBlock image) 
	    throws java.io.IOException {
	String ct = image.getContentType();
	if(! (ct.startsWith("image/")))
	    throw new Error("Not a proper image scroll.");

	Dimension d = ((ImageSpan)image.getCurrent()).getSize();
	int maxw = d.width;
	int maxh = d.height;

	System.out.println("maxw "+maxw+" maxh "+maxh);

	float inchWidth = maxw / 72.0f;
	float inchHeight = maxh / 72.0f;

	int xreso = (int)(MAXTEXSIZE / inchWidth);
	int yreso = (int)(MAXTEXSIZE / inchHeight);

	if(maxw > maxh) yreso = xreso;
	else            xreso = yreso;

	String resolution = MAXTEXSIZE+"x"+MAXTEXSIZE; //""+xreso+"x"+yreso;
	String prefix = protectChars(image.getID()) + "-" + resolution;

	boolean exist = true;
	String base = prefix;

	File f = new File(tmp(), base);
	String path = f.getPath();

	String tmppath = new File(tmp(), "tmp"+base+".png").getPath();
	if(!f.exists()) exist = false;

	ImageFile res = new ImageFile();
	res.xreso = xreso;
	res.yreso = yreso;
	res.filename = path;

	if(exist) return res;
	
	
	// Need to make them.
	//
	if(interp == null) {
	    interp = new PythonInterpreter();
	    interp.exec("import alph.util.psimages\n"+
			"import vob.putil.mipzipmaker\n"+
			"cv = alph.util.images.convert2Image\n"+
			"mz = vob.putil.mipzipmaker.makeMipzip\n"
			);
	}


	// 1. Convert image to .png files
	BlockFile f2 = image.getBlockFile();
	if(! interp.get("cv").__call__(new PyObject[] {
	    new PyString(f2.getFilename()),
	    new PyString(tmppath),
	    //new PyString(new File(tmp(), "tmp"+prefix).getPath()),
	    new PyString(resolution)
	}).__nonzero__())
	    throw new Error("Conversion unsuccessful");
	f2.close();

	// 2. Convert .png to mipzip files
	interp.get("mz").__call__(new PyObject[] {
	    new PyString(tmppath),
	    new PyString(path),
	    new PyInteger(MAXTEXSIZE),
	    new PyInteger(MAXTEXSIZE)
	});
	
	(new File(tmppath)).delete();

	return res;
    }

    
}
