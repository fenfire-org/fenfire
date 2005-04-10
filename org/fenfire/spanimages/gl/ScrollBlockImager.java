/*
ScrollBlockImager.java
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
import org.nongnu.alph.*;

/** Base class for the classes that create scrollblock images.
 * The getSingleImage method should only be called with
 * entire image spans, i.e. objects obtained
 * from imageSpan.getSuperImageSpan(), to allow proper caching.
 * <p>
 * This class contains some necessary trappings for handling the temporary
 * images: the temporary directory and a method to munge strings
 * to fit the filesystem.
 */
public abstract class ScrollBlockImager {
    /** The directory to store the cached images in (in mipzip format).
     */
    static private File __tmp = new File("./tmpimg");

    /** Get the directory to store the cached images in (in mipzip format).
     */
    static public File tmp() {
	if(!__tmp.exists())
	    __tmp.mkdir();
	return __tmp;
    }

    /** Hide the slash and colon characters from the filesystem.
     */
    static public String protectChars(String s) {
	StringBuffer res = new StringBuffer();
	for(int i=0; i<s.length(); i++) {
	    char c = s.charAt(i);
	    switch(c) {
	    case '/': res.append("__"); break;
	    case ':': res.append("_-_"); break;
	    default: res.append(c);
	    }
	}
	return res.toString();
    }

    public abstract SingleImage getSingleImage(ImageSpan img) ;

}
