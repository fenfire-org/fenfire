/* -*-java-*-
CommandInit.java
 *
 *    Copyright (c) 2005 by Matti J. Katila
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
 */
/*
 * Written by Matti J. Katila
 */

package org.fenfire.storm;
import org.fenfire.bin.*;
import org.nongnu.storm.*;
import org.nongnu.storm.impl.*;
import org.nongnu.storm.util.*;
import java.io.*;
import java.util.*;

public class CommandInit {


    static public void initRepository(File f) {
	File storm = new File(f, "_storm/");
	File root = new File(storm, "root");
	File poolDir = new File(storm, "pool");
	if (!storm.mkdir() || !poolDir.mkdir())
	    Storm.exitErr("Storm already initialized in this "+
		    "directory or disk full.");
	try {
	    StormPool pool = new DirPool(poolDir, Collections.EMPTY_SET);
	    BlockOutputStream bos = pool.getBlockOutputStream(
		"application/storm-x-dir");
	    bos.close();
	    PrintWriter p = new PrintWriter(new FileOutputStream(root));
	    p.println(bos.getBlockId().getURI());
		p.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    Storm.exitErr(e.getMessage());
	}
    }


}
