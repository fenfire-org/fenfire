/*
PaperMillFunction.java
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

package org.fenfire.util;
import org.nongnu.libvob.gl.PaperMill;
import org.nongnu.libvob.gl.SpecialPapers;
import org.fenfire.functional.Function;
import org.fenfire.functional.PureFunction;
import org.fenfire.functional.Functional;

/** Adapt a PaperMill into the Function API.
 * Input: any object. Output an org.nongnu.libvob.gl.Paper object
 * based on the hashcode of the object.
 */
public class PaperMillFunction implements PureFunction {
    /** Whether we should request optimized papers from the papermill.
     */
    private final boolean useOptimized;
    /** The offset to add to the hashcode.
     */
    private final int offset;

    /** The papermill to use.
     */
    private final PaperMill paperMill;

    static public Functional.Hints functionalHints = 
	(new Functional.HintsMaker())
	    .setHint(Functional.HINT_BGGROUP, "OPENGL")
	    .setHint(Functional.HINT_PLACEHOLDER, null)
	    .make();

    /** Create a new PaperMillFunction.
     * @param paperMill The papermill to use
     * @param useOptimized Whether to return optimized or non-optimized
     * 			papers from the papermill
     */
    public PaperMillFunction(PaperMill paperMill, boolean useOptimized) {
	this(paperMill, useOptimized, 0);
    }

    /** Create a new PaperMillFunction.
     * @param paperMill The papermill to use
     * @param useOptimized Whether to return optimized or non-optimized
     * 			papers from the papermill
     * @param offset The offset to add to the hashCode before calling
     * 			getPaper or getOptimizedPaper.
     */
    public PaperMillFunction(PaperMill paperMill, boolean useOptimized, 
			    int offset) {
	this.paperMill = paperMill;
	this.useOptimized = useOptimized;
	this.offset = offset;
    }

    public Object f(Object input) {
	if(input == null) return null;
	if(useOptimized) {
	    return paperMill.getPaper(input.hashCode());
	} else {
	    return paperMill.getOptimizedPaper(input.hashCode());
	}
    }

}
