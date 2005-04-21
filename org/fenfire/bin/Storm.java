// (c): Matti J. Katila

package org.fenfire.bin;

import org.fenfire.fenfeed.http.*;

import org.nongnu.storm.*;
import org.nongnu.storm.util.*;
import org.nongnu.storm.impl.*;

import gnu.getopt.*;

import java.io.*;
import java.util.*;

/** Storm implementation which doesn't use 
 *  "too hard to manage" pointers.
 */
public class Storm {
    static public boolean dbg = false;
    static private void p(String s) { System.out.println("Storm:: "+s); }


    static String VERSION = "0.0.0w";




    
    static public void main(String[] args_) {
	if (args_.length == 0) printHelpAndExit();

	for (int i=0; i<args_.length; i++) p("arg("+i+") "+args_[i]);

	int c;
	String arg;
	gnu.getopt.LongOpt[] longopts = new LongOpt[2];
	StringBuffer sb = new StringBuffer();
	longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
	longopts[1] = new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'v'); 
	Getopt g = new Getopt("storm", args_, "hv", longopts);
	//g.setOpterr(true);
	while ((c = g.getopt()) != -1)
	    switch (c)
	    {
	    case 'h':
		System.out.println("HELP");
		printHelpAndExit();
	    case 'v':
		exitErr("storm version: "+VERSION);
	    default:
		exitErr("getopt() returned " + c);
	    }
	
	for (int i = g.getOptind(); i < args_.length ; i++)
	    System.out.println("Non option argv element: " + args_[i] + "\n");
	

	// we are not really interested in arguments before the command
	String cmd = args_[0];
	String args[] = new String[args_.length - 1];
	if (args_.length > 1)
	    System.arraycopy(args_,1, args,0, args.length);
	


	// INIT
	if (cmd.equals("init") || cmd.equals("initialize")) {
	    p("Command: initialize");
	    initRepository(wd());
	    System.exit(0);
	}
	// ADD
	else if (cmd.equals("add")) {
	    p("Command: Add");
	    if (args.length == 0) exitErr("No files nor directories given.");

	    Repo r = getRepo();
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
		exitErr(e.getMessage());
	    }

	    System.exit(0);
	}
	// GET
	else if (cmd.equals("get")) {
	    p("Command: Get");
	    if (args.length == 0) exitErr("No directory, URI or host given.");
	    boolean ok = false;
	    Repo r = null;
	    try { r = getRepo();
	    } catch (Error e) {
		ok = true;
	    } // we didn't fine root of any repo!
	    if (!ok) exitErr("You are already in a repo directory! "+
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
		if ((new File(wd(), name)).isDirectory())
		    exitErr("Could not get storm repo because of there "+
			    "exists a directory '"+name+"' already.");
		File nFile = new File(wd(), name);
		nFile.mkdir();
		
		r = new Repo(f);
		r.copy(nFile);
		
	    } // HTTP 
	    else if (!isFile && src.startsWith("http://")) {
		if (src.lastIndexOf("/") == src.length() - 1)
		    src = src.substring(0, src.lastIndexOf("/", src.length()-1));
		String repoName = src.substring(src.lastIndexOf("/") +1);
		if (dbg) p("SRC: "+src+ ", repoName: "+repoName);

		File root = new File(wd(), repoName);
		if (root.exists() || !root.mkdir())
		    exitErr("There exists a repository named: "+
			    repoName+ " or disk is full.");
		initRepository(root);
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
		    
		    Repo repo = new Repo(root);
		    repo.init();
		    String bp = new BlockId(rootId).getBitprint();
		    String ct = getCT(src+"/_storm/pool/types_"+bp);
		    CopyUtil.copy(getData(src+"/_storm/pool/data_"+bp), 
				  new FileOutputStream(new File(root, "_storm/pool/data_"+bp)));
		    CopyUtil.copy(getData(src+"/_storm/pool/types_"+bp), 
				  new FileOutputStream(new File(root, "_storm/pool/types_"+bp)));
		    
		    Dir d = new Dir(repo.pool, repo.root.getPath(), 
				    rootId, null);
		    pullHttp(d, src, root);
			
		} catch (Exception e) {
		    e.printStackTrace();
		    throw new Error(e.getMessage());
		}

		
		exitErr("not implemented yet.");
	    }
	    System.exit(0);
	}
	System.out.println("Unknown command: "+cmd+"\n");
	printHelpAndExit();
    }

    static private void pullHttp(Dir d, String src, File root) throws IOException {
	if (dbg) p("PullHTTP: "+d+", src: "+src+", root: "+root);
	for (Iterator i=d.iterator(); i.hasNext();) {
	    String name = (String) i.next();
	    String hash = d.get(name);

	    String bp = (new BlockId(hash)).getBitprint();
	    String ct = getCT(src+"/_storm/pool/types_"+bp);
	    CopyUtil.copy(getData(src+"/_storm/pool/data_"+bp), 
			  new FileOutputStream(new File(root, "_storm/pool/data_"+bp)));
	    CopyUtil.copy(getData(src+"/_storm/pool/types_"+bp), 
			  new FileOutputStream(new File(root, "_storm/pool/types_"+bp)));
	    if (ct.equals("application/storm-x-dir"))
		pullHttp(d.cd(name), src, root);
	}
    }

    static private String getCT(String uri) throws IOException {
	if (dbg) p("get content type: "+uri);
	HTTPContext context = new HTTPContext();
	context.setAccept("application/storm-x-dir,"+
			  "application/storm-x-file,"+
			  "*/*");
	HTTPResource res = new HTTPResource(uri, context);
	InputStream in = res.getInputStream();
	String ct = (new BufferedReader(
			 new InputStreamReader(in))
	    ).readLine();
	return ct;
    }
    static private InputStream getData(String uri) throws IOException {
	if (dbg) p("get data: "+uri);
	HTTPContext context = new HTTPContext();
	context.setAccept("application/storm-x-dir,"+
			  "application/storm-x-file,"+
			  "*/*");
	HTTPResource res = new HTTPResource(uri, context);
	return res.getInputStream();
    }


    static private void initRepository(File f) {
	File storm = new File(f, "_storm/");
	File root = new File(storm, "root");
	File poolDir = new File(storm, "pool");
	if (!storm.mkdir() || !poolDir.mkdir())
	    exitErr("Storm already initialized in this "+
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
	    exitErr(e.getMessage());
	}
    }


    static class Repo {
	File root;
	Repo parent = null;
	
	StormPool pool = null;
	public void init() {
	    if (pool != null) return;
	    try {
		pool = new DirPool(new File(root, "_storm/pool/"), 
				   Collections.EMPTY_SET);
	    } catch (Exception e) {
		e.printStackTrace();
		exitErr(e.getMessage());
	    }
	}

	public Repo(File workingDir) {
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
		exitErr(e.getMessage());
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
		
		Dir d = new Dir(pool, canonRoot.getPath(), 
				(new BufferedReader(
				    new InputStreamReader(
					new FileInputStream(
					    new File(root, "_storm/root")
					    )))).readLine(), null);
		// check that we are not playing something stupid...
		if (parents.size() > 0 && parents.getFirst().equals("_storm")) {
		    p("You are trying to add to _storm directory -- "+ 
		      "I will say this only once: *do not do that!*.");
		    return;
		}
		
		for (Iterator i=parents.iterator(); i.hasNext();) {
		    String dir = (String) i.next();
		    if (!d.has(dir)) 
			exitErr("No parent dir '"+dir+
				"' in repository.("+f+")");
		    d = d.cd(dir);
		}

		// is the file in system already?
		if (d.has(name)) {
		    p("File '"+f+"' is in system already -- skipping.");
		    return;
		}
		// ok we need to add it

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

	    } catch (Exception e) {
		e.printStackTrace();
		exitErr(e.getMessage());
	    }

	}

	public void copy(File dest) {
	    try {
		StormPool pool = new DirPool(new File(root, "_storm/pool/"), 
					     Collections.EMPTY_SET);
		exitErr("not implemented yet.");
		// plaah...
	    } catch (Exception e) {
		e.printStackTrace();
		exitErr(e.getMessage());
	    }
	}
    }


    /** A dir which utilizes DirPool and thus is 
     *  application interface for it.
     */
    static class Dir {
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

	private File getPath() {
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


    static File wd() {return new File("."); }
    static Repo getRepo() {
	return new Repo(wd());
    }

    

    static String [] help = {
	"Storm version "+VERSION+ " - Storm Repository Control System",
	"",
	"Usage: storm COMMAND ...",
	"",
	"Commands:",
	"  initialize",
	"  add",
	"  get",
    };

    static void printHelpAndExit() {
	for (int i=0; i<help.length; i++)
	    System.out.println(help[i]);
	System.exit(1);
    }

    static void exitErr(String s) {
	System.out.println(s);
	System.exit(1);
    }
    
}
