/* -*-java-*-
CommandGet.java
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

public class CommandGet {
    static public boolean dbg = false;
    static private void p(String s) { System.out.println("CommandGet:: "+s); }

    public static void get(String[] args) {
	    if (args.length == 0) Storm.exitErr("No directory, URI or host given.");
	    boolean ok = false;
	    Repository r = null;
	    try { r = Repository.getRepository();
	    } catch (Error e) {
		ok = true;
	    } // we didn't fine root of any repo!
	    if (!ok) Storm.exitErr("You are already in a repo directory! "+
			     "root:"+(r == null? "(null)": r.root.toString()));

	    String src = args[0];
	    boolean isFile = true;
	    File f = null;
	    try {
		f = (new File(src)).getCanonicalFile();
		isFile = f.exists();
	    } catch (IOException e) {
		isFile = false;
	    }

	    // FILE
	    if (isFile && f != null) {
		String name = (new File(src)).getAbsoluteFile().getName();
		if ((new File(Storm.wd(), name)).isDirectory())
		    Storm.exitErr("Could not get storm repo because of there "+
			    "exists a directory '"+name+"' already.");
		File nFile = new File(Storm.wd(), name);
		nFile.mkdir();
		
		r = new Repository(f);
		r.copy(nFile);
		
	    } // HTTP 
	    else if (!isFile && src.startsWith("http://")) {
		if (src.lastIndexOf("/") == src.length() - 1)
		    src = src.substring(0, src.lastIndexOf("/", src.length()-1));
		String repoName = src.substring(src.lastIndexOf("/") +1);
		if (dbg) p("SRC: "+src+ ", repoName: "+repoName);

		File root = new File(Storm.wd(), repoName);
		if (root.exists() || !root.mkdir())
		    Storm.exitErr("There exists a repository named: "+
			    repoName+ " or disk is full.");
		CommandInit.initRepository(root);
		try {
		    String uri = src+"/_storm/root";
		    HTTPContext context = new HTTPContext();
		    
		    context.setAccept("application/storm-x-dir,"+
				      "application/storm-x-file");
		    HTTPResource res = new HTTPResource(uri, context);
		    InputStream in = res.getInputStream();
		    String rootId = (new BufferedReader(
					 new InputStreamReader(in))
			).readLine();
		    p("rootId: "+rootId);
		    
		    Repository repo = new Repository(root);
		    repo.init();
		    String bp = new BlockId(rootId).getBitprint();
		    String ct = Http.getCT(src+"/_storm/pool/types_"+bp);
		    CopyUtil.copy(Http.getData(src+"/_storm/pool/data_"+bp), 
				  new FileOutputStream(new File(root, "_storm/pool/data_"+bp)));
		    CopyUtil.copy(Http.getData(src+"/_storm/pool/types_"+bp), 
				  new FileOutputStream(new File(root, "_storm/pool/types_"+bp)));
		    
		    Dir d = new Dir(repo.pool, repo.root.getPath(), 
				    rootId, null);
		    Http.pullHttp(d, src, root);
		    d.writeFiles(root);
			
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new Error(e.getMessage());
		}

	    }

    }
}
