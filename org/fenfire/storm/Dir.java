/* -*-java-*-
Dir.java
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

/** A dir which utilizes DirPool and thus is 
 *  application interface for it.
 */
public class Dir {
    static public boolean dbg = false;
    static private void p(String s) { System.out.println("Dir:: "+s); }

 


    StormPool pool;
    String dir;
    BlockId root;
    Dir parent;
    SortedMap name2hash = null;
    Dir(StormPool pool, String d, String rootId, Dir parent) {
	this.pool = pool;
	this.dir = d;
	this.root = new BlockId(rootId);
	this.parent = parent;
    }
    public Iterator iterator() { return name2hash().keySet().iterator(); }
    public String get(Object o) { return (String) name2hash().get(o); }

    public SortedMap name2hash() {
	if (name2hash != null) return name2hash;
	SortedMap m = new TreeMap();
	try {
	    Block b = pool.get(root);
	    BufferedReader r = 
		new BufferedReader(new InputStreamReader(b.getInputStream()));
	    String str = r.readLine();
	    while(str != null) {
		String hash = str;
		str = r.readLine();
		if (str == null) throw new Error("Your data is corrupted!");
		m.put(str, hash);
		str = r.readLine();
	    }
	} catch (IOException e) {
	    p("Root of your repository does not exist! "+root);
	    throw new Error(e.getMessage());
	}
	name2hash = m;
	return m;
    }

    public boolean has(String dir) {
	if (dbg) 
	    for (Iterator i=name2hash().entrySet().iterator(); 
		 i.hasNext();)
		if (dbg) p(getPath()+ " has: "+dir+" ? "+i.next());

	return name2hash().containsKey(dir);
    }

    private BlockId writeDir(SortedMap m) {
	try {
	    BlockOutputStream bos = pool.getBlockOutputStream(
		"application/storm-x-dir");
	    OutputStreamWriter w = new OutputStreamWriter(bos);
	    for (Iterator i=m.keySet().iterator(); i.hasNext();) {
		String name = (String) i.next();
		String hash = (String) m.get(name);
		w.write(hash);
		w.write("\n");
		w.write(name);
		w.write("\n");
	    }
	    w.flush(); 
	    w.close();
	    return bos.getBlockId();
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new Error(e.getMessage());
	}
    }

    public void writeFiles(File root) throws IOException {
	for (Iterator i=iterator(); i.hasNext();) {
	    String name = (String) i.next();
	    String hash = get(name);
	    BlockId id = new BlockId(hash);
	    String bp = id.getBitprint();
	    String ct = id.getContentType();
	    p("name: "+name+", ct: "+ct);
	    if (ct.equals("application/storm-x-file"))
		CopyUtil.copy(new FileInputStream(new File(root, "/_storm/pool/data_"+bp)), 
			      new FileOutputStream(new File(getPath(), name)));
	    if (ct.equals("application/storm-x-dir")) {
		(new File(getPath(), name)).mkdirs();
		cd(name).writeFiles(root);
	    }
	}
    }


    public void addDir(String d) {
	if (dbg) p(getPath()+": AddDir: "+d);
	try {
	    // write file
	    BlockOutputStream bos = pool.getBlockOutputStream(
		"application/storm-x-dir");
	    bos.close();

	    SortedMap m = name2hash();
	    m.put(d, bos.getBlockId().getURI());

	    BlockId id = writeDir(m);
	    // update this and other dirs too..
	    if (parent == null)
		updateRoot(dir, id.getURI());
	    else 
		parent.update(dir, id.getURI());
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new Error(e.getMessage());
	}

    }


    public void addFile(String f) {
	if (dbg) p(getPath()+": AddFile: "+f);
	try {
	    // write file
	    BlockOutputStream bos = pool.getBlockOutputStream(
		"application/storm-x-file");
	    CopyUtil.copy(new FileInputStream(new File(getPath(), f)), 
			  bos);
	    SortedMap m = name2hash();
	    m.put(f, bos.getBlockId().getURI());

	    // update this dir
	    BlockId id = writeDir(m);
		
	    // update this and other dirs too..
	    if (parent == null)
		updateRoot(dir, id.getURI());
	    else 
		parent.update(dir, id.getURI());
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new Error(e.getMessage());
	}
    }

    private void update(String dir, String hash) {
	SortedMap m = name2hash();
	if (!m.containsKey(dir)) throw new Error("NO KEY FOUND!!");
	m.put(dir, hash);
	BlockId id = writeDir(m);
	if (parent == null)
	    updateRoot(this.dir, id.getURI());
	else 
	    parent.update(this.dir, id.getURI());
    }
	
    private void updateRoot(String dir, String hash) {
	if (dbg) p(getPath()+" update: "+dir +", id: "+hash);
	try {
	    if (parent == null) {
		if (dbg) p("create a new ROOT");
		PrintWriter p = new PrintWriter(
		    new FileOutputStream(new File(this.dir, "_storm/root")));
		p.println(hash);
		p.close();
	    } else {
		SortedMap m = name2hash();
		if (!m.containsKey(dir)) throw new Error("NO KEY FOUND!!");
		m.put(dir, hash);
		BlockId id = writeDir(m);
		    
		parent.update(this.dir, id.getURI());
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new Error(e.getMessage());
	}
    }

    public File getPath() {
	if (parent == null) return new File(dir);
	return new File(parent.getPath(), dir);
    }

    public Dir cd(String dir) {
	Map m = name2hash();
	if (!m.containsKey(dir)) throw new Error("no dir found");
	return new Dir(pool, dir, (String)m.get(dir), this);
    }

    public void dump() {
	dump(0);
    }
    public void dump(int indent) {
	String ind = "";
	for (int i=0; i<indent; i++) ind += "  ";


	p(ind+"PATH: "+getPath());
	Map m = name2hash();
	for (Iterator i=m.keySet().iterator(); i.hasNext();) {
	    String name = (String) i.next();
	    String hash = (String) m.get(name);
	    if ((new BlockId(hash)).getContentType().equals(
		    "application/storm-x-dir")) {
		p(ind+"dir "+name+"/"+" "+hash.substring(15, 50));
		cd(name).dump(indent+1);
		p(ind+"<---");
	    } else
		p(ind+"file: "+name +" "+hash.substring(15, 50));
	}
		
    }

}

