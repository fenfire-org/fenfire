/*
SingleImage.java
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
import java.io.File;
import org.nongnu.libvob.gl.*;
import org.nongnu.libvob.gl.virtualtexture.*;
import org.nongnu.libvob.memory.*;

/** A single texture image of an imagespan or pageimagespan.
 * This class incorporates a single picture and tracks how the different mipmap
 * levels would be used.
 */
public class SingleImage {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("SingleImage: "+s); }

    public final VirtualTexture virtualTexture;
    public final GL.TexAccum accum;

    /** For each mipmap level of the texture, the number of pixels
     * that would be rendered less than optimally.
     * These numbers are calculated cumulatively with time, along
     * with an exponential decay term.
     */
    public final double[] missingPixels;

    public double nPixels() {
	return missingPixels[missingPixels.length - 1];
    }


    private long lastUpdate = System.currentTimeMillis();

    /** Decay coefficient.
     * Coefficient C for exp(- C*(t1-t0)) when decaying.
     */
    public static float exponentialCoeff = (float)Math.log(.05) / 5000;


    public final String scrollBlock;
    public final int page;
    public final float xresolution;
    public final float yresolution;


    /** Create.
     * @param scrollBlock The scrollblock identifier.
     * @param page The page index. For ImageSpans, always 0.
     * @param filename The file to load the image from.
     * @param xresolution The resolution (DPI) the image in the file is at.
     *  	 For example, if a PS file was compiled into an image at
     *  	 160dpi, then this number is 160. Zero or negative = unknown,
     *  	 use pixel data.
     * @param yresolution The resolution (DPI) the image in the file is at.
     *  	 For example, if a PS file was compiled into an image at
     *  	 160dpi, then this number is 160. Zero or negative = unknown,
     *  	 use pixel data.
     * @param statsCallback The callback to give the TexAccum
     */
    public SingleImage(
		    String scrollBlock, int page,
		    String filename, 
			float xresolution,
			float yresolution
			) throws java.io.IOException {
	this.scrollBlock = scrollBlock;
	this.page = page;
	this.xresolution = xresolution;
	this.yresolution = yresolution;

	this.virtualTexture = new VirtualTexture(
		    new MipzipFile(new File(filename)));

	this.accum = GL.createTexAccum(PoolManager.getInstance(), this);

	if(dbg) p("Create single image: "+scrollBlock+" "+page+" "+filename
		    +" "+xresolution+" "+yresolution);

	this.missingPixels = new double[
		this.virtualTexture.mipzipFile.getNLevels()];
    }

    public void finalize() {
	if(dbg) p("Finalize single image: "+scrollBlock+" "+page+" "
		    +" "+xresolution+" "+yresolution);


    }

    /** Read the data from the texaccum and add to 
     * missingPixels.
     */
    public synchronized void readTexAccum() {
	double nnow = 0;
	// accum: 0 = n of pixels rendered where whole texcoord is in one pixel
	// accum: 1 = n of pixels rendered where whole texcoord is in four pixels
	// ...
	// missingPixels: 0 = n of pixels too inaccurate at level 0
	//                1 = n of pixels too inaccurate at level 1
	for(int i=19; i>=0; i--) { // magic no: accum size
	    double ac = accum.get(i);
	    nnow += ac;
	    if(dbg) p("Accum: "+i+" "+ac);
	    if(missingPixels.length-1-i >= 0)
		missingPixels[missingPixels.length-1 - i] += nnow;
	}
	if(dbg) p("SingleImage: readTexAccum - total: "+nnow+" "+nPixels());
	accum.clear();
    }

    /** Update to the given time.
     */
    public synchronized void updateTime(long curTime) {
	double msecs = curTime - lastUpdate;
	double mul = Math.exp(exponentialCoeff * msecs);
	for(int i=0; i<missingPixels.length; i++)
	    missingPixels[i] *= mul;
	if(dbg) p("SingleImage: updateTime- "+lastUpdate+" "+curTime+
			" mul: "+mul+"  totalnow: "+nPixels());
	lastUpdate = curTime;
    }

}
