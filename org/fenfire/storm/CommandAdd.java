/* -*-java-*-
CommandAdd.java
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
import org.fenfire.fenfeed.http.*;

import org.nongnu.storm.*;
import org.nongnu.storm.impl.*;
import org.nongnu.storm.util.*;

import java.io.*;
import java.util.*;

public class CommandAdd {
    static public boolean dbg = false;
    static private void p(String s) { System.out.println("CommandAdd:: "+s); }

    public static void add(String[] args) {
	if (args.length == 0) Storm.exitErr("No files nor directories given.");
	
	Repository r = Repository.getRepository();
	for (int i=0; i<args.length; i++) 
	    r.add(args[i]);
	
	try {
	    /*
	      r.init();
	      Dir d = new Dir(r.pool, r.root.getPath(), 
	      (new BufferedReader(
	      new InputStreamReader(
	      new FileInputStream(
	      new File(r.root, "_storm/root")
	      )))).readLine(), null);
	      d.dump();
	    */
	} catch (Exception e) {
	    e.printStackTrace();
	    Storm.exitErr(e.getMessage());
	}
    }
}
