// (c): Matti J. Katila

package org.fenfire.bin;
import org.fenfire.storm.*;
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
	    CommandInit.initRepository(wd());
	    System.exit(0);
	}
	// ADD
	else if (cmd.equals("add")) {
	    p("Command: Add");
	    CommandAdd.add(args);
	    System.exit(0);
	}
	// GET
 	else if (cmd.equals("get")) {
	    p("Command: Get");

	    CommandGet.get(args);
	    System.exit(0);
	}
	// RECORD
 	else if (cmd.equals("rec") || cmd.equals("record")) {
	    p("Command: Reocrd");

	    CommandRec.rec(args);
	    System.exit(0);
	}
	System.out.println("Unknown command: "+cmd+"\n");
	printHelpAndExit();
    }





    static public File wd() {return new File("."); }
    

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

    static public void printHelpAndExit() {
	for (int i=0; i<help.length; i++)
	    System.out.println(help[i]);
	System.exit(1);
    }

    static public void exitErr(String s) {
	System.out.println(s);
	System.exit(1);
    }
    
}
