/* -*-java-*-
CommandRec.java
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

import jline.*;

import java.io.*;
import java.util.*;

public class CommandRec {
    static public boolean dbg = false;
    static private void p(String s) { System.out.println("CommandRec:: "+s); }

    static String getLine() throws IOException {
        BufferedReader in
            = new BufferedReader(new InputStreamReader(System.in));
        return in.readLine();
    }


    public static void rec(String[] args) {
	try {
	    Repository r = Repository.getRepository();
	    r.init();
	    Dir d = new Dir(r.pool, r.root.getPath(), r.rootId, null);
	    
	    File pDir = new File(r.root, "_storm/recs/tmp_pool/");
	    if(!pDir.exists() && !pDir.mkdirs())
		throw new Error("Couldn't create directories.");
	    
	    StormPool tmpPool = null;
	    try { tmpPool = new DirPool(pDir, Collections.EMPTY_SET); 
	    } catch (Exception e) {
		e.printStackTrace();
		throw new Error(e.getMessage());
	    }
	    
	    recAdds(r,d, tmpPool);
	    //recmoves
	    //recRemoves
	    
	    recChanges(r, d, tmpPool);

	    commitMsg(r, d, tmpPool);

	} finally {
	    Repository r = Repository.getRepository();
	    r.init();
	    File d = new File(r.root, "_storm/recs/");
	    //clean(d);
	    d.delete();
	}
    }	

    static private void clean(File d) {
	String [] list;
	if ((list = d.list()) != null) {
	    for (int i=0; i<list.length; i++) {
		clean(new File(d, list[i]));
		(new File(d, list[i])).delete();
	    }
	}

    }

    static private void commitMsg(Repository r, Dir d, StormPool tmpPool) {
	try {
	    // print patch ...

	    //tmpPool.

	    System.out.println("Give a commit message: ");
	    String msg = getLine();
	    
	    BlockOutputStream bos = tmpPool.getBlockOutputStream(
		"application/storm-x-patch-commit-message");
	    BufferedWriter patch = new BufferedWriter(
		new OutputStreamWriter(bos));
	    
	    patch.write("Commit msg:\n");
	    patch.write(msg);
	    patch.newLine();
	    patch.close();
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new Error(e.getMessage());
	}
    }

    static private void recChanges(Repository r, Dir d, StormPool tmpPool) {
	try {

	    for(Iterator i=d.iterator(); i.hasNext();) {
		String name = (String) i.next();
		String hash = d.get(name);
		BlockId id = new BlockId(hash);
		String bp = id.getBitprint();
		String ct = id.getContentType();
		
		String bits = InputStream2BlockId.bitprint(
		    new FileInputStream(new File(d.getPath(), name)),
		    InputStream2BlockId.BLOCKSIZE, false);
		p("bits: "+bits);
	    }

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new Error(e.getMessage());
	}
    }

    static private void recAdds(Repository r, Dir d, StormPool tmpPool) {
	// go trough additionals
	try {
	    File adds = new File(r.root, "_storm/waits/adds");
	    File recs = new File(r.root, "_storm/recs/");
	    if(adds.exists()) {
		if(!recs.exists() && !recs.mkdirs())
		    throw new Error("Couldn't create file for additions");
		recs = new File(recs, "adds");
		
		BufferedWriter wr = new BufferedWriter(
		    new FileWriter(recs, true));
		BufferedReader re = new BufferedReader(
		    new FileReader(adds));
		
		Terminal term = Terminal.getTerminal();
		String str;
		while ( (str=re.readLine()) != null) {
		    while (true) {
			System.out.println("Do you want to add '"+str+
					   "' to repository? (y/n/v/h)");
			int ch = term.readCharacter(System.in);
			switch (ch) {
			case 'y': {
			    p("YES!");

			    File f = new File(r.root, str);
			    BlockOutputStream bos = null;
			    if (f.isDirectory())
				bos = tmpPool.getBlockOutputStream(
				    "application/storm-x-patch-add-dir");
			    else
				bos = tmpPool.getBlockOutputStream(
				    "application/storm-x-patch-add-file");
			    BufferedWriter patch = new BufferedWriter(
				new OutputStreamWriter(bos));

			    if (f.isDirectory())
				patch.write("Add dir:\n");
			    else
				patch.write("Add file:\n");
			    patch.write(str);
			    patch.newLine();
			    patch.close();

			    wr.write(str);
			    wr.newLine();

			    if (f.isFile())
				while (true) {
				    System.out.println(
					"Do you want to add the "+
					"content of a file '"+str+
					"' to repository? "+
					"(y/n/v/h)");
				    ch = term.readCharacter(System.in);
				    switch (ch) {
				    case 'y': {
					p("YES!");
					bos = tmpPool.getBlockOutputStream(
					    "application/storm-x-patch-content");
					CopyUtil.copy(new FileInputStream(f),
						      bos);
					break;
				    } 
				    case 'n': {
					p("fine..");
					break;
				    }
				    case 'h': {
					System.out.println(
					    "Your options:\n"+
					    "  y: yes    add the content "+
					    "of a file to the repository.\n"+
					    "  n: no     do not add file "+
					    "to the repository.\n"+
					    "  v: view   view content of "+
					    "the file.\n"+
					    "  h: help   print this help "+
					    "message.\n");
					continue;
				    }
				    case 'v': {
					BufferedReader show = 
					    new BufferedReader(
						new FileReader(f));
					String s;
					while ( (s=show.readLine()) != null)
					    System.out.println("+"+s);
					continue;
				    }
				    default: 
					System.out.println(
					    "No key binded: try 'h'.");
					continue;
				    }
				    break;
				}
			    break;
			} 
			case 'n': {
			    p("fine..");
			    break;
			}
			case 'h': {
			    System.out.println(
				"Your options:\n"+
				"  y: yes    add file to repository.\n"+
				"  n: no     do not add file to repo.\n"+
				"  v: view   view content of file.\n"+
				"  h: help   print this help message.\n");
			    continue;
			}
			case 'v': {
			    File f = new File(r.root, str);
			    if (f.isDirectory())
				System.out.println(str+"/ is a directory.");
			    else {
				BufferedReader show = new BufferedReader(
				    new FileReader(f));
				String s;
				while ( (s=show.readLine()) != null)
				    System.out.println("+"+s);
			    }
			    continue;
			}
			default: 
			    System.out.println(
				"No key binded: try 'h'.");
			    continue;
			}
			break;
		    }
		}
		wr.close();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new Error(e.getMessage());
	}
    }
}
