/*
PlainPaperMaker.java
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
import org.nongnu.libvob.*;
import org.nongnu.libvob.gl.*;
import org.nongnu.libvob.memory.*;

/** A PaperMaker that makes a simple single-pass 
 * paper of only the given texture.
 * This PaperMaker loads the texture into n of the first texunits
 * and executes the given callgl code.
 */
public class PlainPaperMaker extends PaperMaker {

    int n;

    String setup;
    String teardown;

    Object depends;

    PaperMill papermill;

    /** Create a new PlainPaperMaker that just copies the texture value
     * to the screen.
     * This does not put in the proper filtering - it's better
     * to use org.fenfire.spanimages.gl.papermakers for these.
     */
    public PlainPaperMaker() {
	this(1, "PushAttrib TEXTURE_BIT ENABLE_BIT\nTexEnv TEXTURE_ENV TEXTURE_ENV_MODE REPLACE\n" +
		"Enable TEXTURE_2D"
		, "PopAttrib\n", null);
    }

    /** Create a new PlainPaperMaker.
     * @param n The number of texunits to put the given texture into.
     * @param setup The setup code.
     * @param teardown The teardown code
     * @param depends The objects that mustn't be reclaimed by the GC
     * 			before the papers.
     * 			For instance, OpenGL programs or display lists.
     */
    public PlainPaperMaker(int n, String setup, String teardown, 
			Object depends) {
	this.n = n;
	this.setup = setup;
	this.teardown = teardown;
	this.depends = depends;
    }

    /** Create a new PlainPaperMaker.
     * @param n The number of texunits to put the given texture into.
     * @param setup The setup code.
     * @param teardown The teardown code
     * @param depends The objects that mustn't be reclaimed by the GC
     * 			before the papers.
     * 			For instance, OpenGL programs or display lists.
     * @param papermill The papermill to use to make the optimized background paper
     */
    public PlainPaperMaker(int n, String setup, String teardown, 
			Object depends, PaperMill papermill) {
	this.n = n;
	this.setup = setup;
	this.teardown = teardown;
	this.depends = depends;
	this.papermill = papermill;
    }

    public Paper makePaper(SingleImage img, float[] texgen) {
	Paper p;
	Paper.Pass pass;
	int offs = 0;
	StringBuffer buf = new StringBuffer();
	if(papermill != null) {
	    p = (Paper) papermill.getOptimizedPaper(
			img.scrollBlock.hashCode()).clone();
	    if(p.getNPasses() != 1)
		throw new Error("Invalid paper gotten back: npasses "+
				p.getNPasses()+" "+p);
	    pass = p.getPass(0);
	    if(pass.getNTexGens() != 1)
		throw new Error("Invalid paper gotten back: ntexgens "+
				pass.getNTexGens()+" "+p);
	    offs = 1;
	    buf.append(pass.getSetupcode());
	} else {
	    p = new Paper();
	    p.setNPasses(1);
	    pass = p.getPass(0);
	}
	if(depends != null)
	    p.addDepend(depends);

	int iind = pass.getNIndirectTextureBinds();
	pass.setNIndirectTextureBinds(iind + n);


	buf.append(setup);
	
	pass.setNTexGens(n + offs);
	for(int i=0; i<n ; i++) {
	    buf.append("\nActiveTexture TEXTURE");
	    buf.append(i + offs);
	    buf.append("\nEnable TEXTURE_2D\n");

	    pass.putIndirectTextureBind(iind + i, 
				"TEXTURE"+(i+offs), 
				"TEXTURE_2D",
				img.virtualTexture.indirectTexture);

	    pass.putNormalTexGen(i + offs, texgen);
	}
	buf.append("\nActiveTexture TEXTURE0\n");

	pass.setSetupcode(buf.toString());

	pass.setTeardowncode(teardown + pass.getTeardowncode());

	return p;
    }
}



