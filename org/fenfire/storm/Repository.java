/* -*-java-*-
Repository.java
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

public class Repository {
    static public boolean dbg = false;
    static private void p(String s) { System.out.println("Repository:: "+s); }

    static public Repository getRepository() {
	return new Repository(Storm.wd());
    }




    File root;
    Repository parent = null;
    String rootId = null;
    StormPool pool = null;
    public void init() {
	if (pool != null) return;
	try {
	    pool = new DirPool(new File(root, "_storm/pool/"), 
			       Collections.EMPTY_SET);
	    rootId = (new BufferedReader(
			  new InputStreamReader(
			      new FileInputStream(
				  new File(root, "_storm/root")
				  )))).readLine();
	
	} catch (Exception e) {
	    e.printStackTrace();
	    Storm.exitErr(e.getMessage());
	}
    }

    public Repository(File workingDir) {
	if (!workingDir.exists()) throw new Error("I have no idea "+
						  "what's going on");
	File f = workingDir.getAbsoluteFile();
	File fStorm = new File(f, "_storm");
	while ( !fStorm.exists() && !fStorm.isDirectory()) {
	    f = f.getParentFile();
	    fStorm = new File(f, "_storm");
	    if (f == null) throw new Error("No root of storm "+
					   "repository found.");
	} 
	root = null;
	try {
	    root = f.getCanonicalFile();
	} catch (Exception e) {
	    e.printStackTrace();
	    Storm.exitErr(e.getMessage());
	}
    }

    public void add(String file) {
	File f = (new File(file)).getAbsoluteFile();
	if (!f.exists()) {
	    p("Skipping file: '"+f+"'; it doesn't exist.");
	    return;
	}

	// check that file is inside of the repo
	try {
	    File canonRoot = root.getCanonicalFile();
	    File canonAdd = f.getCanonicalFile();
	    if (!canonAdd.getPath().startsWith(canonRoot.getPath())) {
		p("file '"+canonAdd+"' out of repository! Thr root is: "+
		  canonRoot);
		return; 
	    }		

	    // check awesome a == b
	    if (canonRoot.getPath().equals(canonAdd.getPath())) {
		p("file '"+canonAdd+"' is the repository?! -- Skipping...");
		return; 
	    }		


	    // do we have parent for this dir?
	    init();
	    LinkedList parents = new LinkedList();
	    String name = canonAdd.getName();
	    canonAdd = canonAdd.getParentFile();
	    while ( !canonAdd.getPath().equals(canonRoot.getPath()) ) {
		parents.addFirst(canonAdd.getName());
		canonAdd = canonAdd.getParentFile();
	    }
	    for (int i=0; i<parents.size(); i++)
		p("parents: "+parents.get(i));
		
	    Dir d = new Dir(pool, canonRoot.getPath(), rootId, null);
	    // check that we are not playing something stupid...
	    if (parents.size() > 0 && parents.getFirst().equals("_storm")) {
		p("You are trying to add to _storm directory -- "+ 
		  "I will say this only once: *do not do that!*.");
		return;
	    }
		
	    for (Iterator i=parents.iterator(); i.hasNext();) {
		String dir = (String) i.next();
		if (!d.has(dir)) 
		    Storm.exitErr("No parent dir '"+dir+
			    "' in repository.("+f+")");
		d = d.cd(dir);
	    }

	    // is the file in system already?
	    if (d.has(name)) {
		p("File '"+f+"' is in system already -- skipping.");
		return;
	    }
	    // ok we need to add it

	    File adds = new File(root, "_storm/waits/");
	    if(!adds.exists() && !adds.mkdirs())
		throw new Error("Couldn't create file for additions");
	    adds = new File(adds, "adds");

	    BufferedWriter buf = new BufferedWriter(
		new FileWriter(adds, true));
	    for (Iterator i=parents.iterator(); i.hasNext();) {
		String dir = (String) i.next();
		buf.write(dir);
		buf.write("/");
	    }
	    buf.write(name);
	    buf.write("\n");
	    buf.close();
	    
	    return;
/*	    
	    // DIRECTORY
	    if (f.isDirectory()) {
		d.addDir(name);
		p("dir "+name);
	    } 
	    // FILE
	    else if (f.isFile()) {
		d.addFile(name);
		p("file "+name);
	    } else throw new Error("undefined file!? "+f);
*/
	} catch (Exception e) {
	    e.printStackTrace();
	    Storm.exitErr(e.getMessage());
	}

    }

    public void copy(File dest) {
	try {
	    StormPool pool = new DirPool(new File(root, "_storm/pool/"), 
					 Collections.EMPTY_SET);
	    Storm.exitErr("not implemented yet.");
	    // plaah...
	} catch (Exception e) {
	    e.printStackTrace();
	    Storm.exitErr(e.getMessage());
	}
    }
}
