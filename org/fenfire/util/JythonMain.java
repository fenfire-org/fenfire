/*
JythonMain.java
 *    
 *    Copyright (c) 2003, Tuukka Hastrup
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
 *    Public License along with Gzz; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *    
 */
/*
 * Written by Tuukka Hastrup
 */
package org.fenfire.util;

import org.python.util.PythonInterpreter; 
import org.python.core.*; 

/** A wrapper class for starting jython module from Java. This class can be 
 *  used as the main class in a jar package.
 */
public class JythonMain {
    public static void main(String[] args) throws PyException {
	if(args.length == 0) {
	    System.err.println("Please supply a python module" +
			       " as an argument.");
	    System.exit(5);
	}
	importModule(args[0], args, 1);
        // interp.exec("Gzz.run()");
    }

    public static void importModule(String module, String[] args, int startarg)
	throws PyException {
	String[] pyargs = new String[args.length-startarg+1];
	System.arraycopy(args, startarg, pyargs, 1, args.length-startarg);
	pyargs[0] = module+".py"; // sys.argv[0] is the command name
	// Python search path will be the same as Java classpath
	System.setProperty("python.path", 
			   System.getProperty("java.class.path"));
	PythonInterpreter.initialize(System.getProperties(), null, pyargs);
	PythonInterpreter interp = new PythonInterpreter();
	// XXX __name__ is not __main__
	interp.exec("import "+module);	
    }
}
