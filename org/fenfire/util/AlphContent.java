/*
AlphContent.java
 *    
 *    Copyright (c) 2003, Matti J. Katila and Tuomas J. Lukka
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
 * Written by Matti J. Katila and Tuomas J. Lukka
 */

package org.fenfire.util;
import org.fenfire.*;
import org.fenfire.structure.Ff;
import org.nongnu.alph.*;
import org.nongnu.alph.xml.*;

import java.util.*;
import java.io.*;

/** Help class to work with Alph
 */
public class AlphContent {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("AlphContent:: "+s); }


    private Fen fen;
    private Ff ff;
    public AlphContent( Fen fen, Ff ff  ) {
	this.fen = fen;
	this.ff = ff;
    }

    public String getText(Object node) {
	Enfilade1D enf = (Enfilade1D)ff.getContent(node);
	return enf.makeString();
    }

    public void setText(Object node, String s, boolean user) {
	ff.setContent(node, fen.enfMaker.makeEnfilade(
		(user ? fen.userSpanMaker : fen.fakeSpanMaker)
			.makeTextSpan(s)));
    }

    public void insertText(Object node, int ind, String s, boolean user) {
	Enfilade1D old = (Enfilade1D)ff.getContent(node);

	Enfilade1D enf = old.sub(0, ind);
	enf = enf.plus(
		(user ? fen.userSpanMaker : fen.fakeSpanMaker)
		.makeTextSpan(s));
	enf = enf.plus(old.sub(ind));

	ff.setContent(node, enf);
    }

    public void deleteText(Object node, int begin, int end) {
	if (dbg) p("begin: "+begin+", end: "+end);
	Enfilade1D old = (Enfilade1D)ff.getContent(node);
	Enfilade1D enf = old.sub(0, begin);
	enf = enf.plus(old.sub(end));

	ff.setContent(node, enf);
    }

}
