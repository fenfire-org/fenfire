/*
PaperMaker.java
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
import org.nongnu.libvob.memory.*;
import org.fenfire.functional.PureFunction;
import org.nongnu.storm.util.Pair;

/** A function that, given a singleimage and a texgen, 
 * return a org.nongnu.libvob.gl.Paper.
 * Because this is a dyadic function, the makePaper() call
 * is also provided separately. The calls
 *
 * 	c = paperMaker.makePaper(a, b);
 *
 * and
 *      c = (Paper) paperMaker.f(new Pair(a,b));
 *
 * are equivalent.
 * <p>
 * The different implementations of this class can choose
 * to use different ways to filter the texture, different
 * backgrounds (using libpaper) etc.
 */
public abstract class  PaperMaker implements PureFunction {

    /** Make a Paper object which contains the texture
     * in the given SingleImage, with the given texgen.
     */
    public abstract Paper makePaper(SingleImage img, float[] texgen);

    public Object f(Object o) {
	Pair p = (Pair)o;
	return makePaper((SingleImage)p.first, (float[])p.second);
    }
}
