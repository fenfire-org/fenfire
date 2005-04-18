/*
ImportUtil.java
 *    
 *    Copyright (c) 2005, Matti J. Katila
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

package org.fenfire.util;
import org.fenfire.swamp.*;
import java.io.*;


/** Imports images, documents etc. to system and helps 
 *  converting these into lobs.
 */
public class ImportUtil {

    static String FILE = "file://";

    public static boolean isInteresting(Literal lit) {
	if (lit.getString().startsWith(FILE)) return true;
	//if (lit.getString().startswith("") return true;
	return false;
    }


    // assumes that the file is interesting!
    static File getTempFileBy(Literal lit) {
	if (lit.getString().startsWith(FILE))
	    return new File(lit.getString().substring(FILE.length()));
	return null;
    }


    /** Returns content type in MIME format.
     * <em> This method depends on native call to "file --brief --mime" 
     * program</em>.
     */
    public static String getContentType(Literal lit) {
	if (!isInteresting(lit))
	    throw new Error("The literal is not interesting "+
			    "at all to get the content type! "+lit);
	File f = getTempFileBy(lit);
	return getContentType(f);
    }
    public static String getContentType(File f) {
	if (!f.exists()) throw new Error("File does not exists! '"+f+"'");
	String path = f.getPath();
	try {
	    Process p = Runtime.getRuntime().exec("file --brief --mime "+path);
	    BufferedReader br = new BufferedReader(
		new InputStreamReader(p.getInputStream()));
	    return br.readLine();
	}catch (Exception e) {
	    e.printStackTrace();
	    throw new Error(e.getMessage());
	}
    }

    public static final String [] knownMIMEs = {
	"image/png",
	"application/pdf",
	"application/ps"
    };


    public static void ffImport(Literal lit) {
	

    } 


}
