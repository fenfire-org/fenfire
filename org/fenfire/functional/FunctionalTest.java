/*
FunctionalTest.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka
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
 * Written by Tuomas J. Lukka
 */

package org.fenfire.functional;
import org.fenfire.util.*;
import org.fenfire.swamp.*;
import java.util.*;

/** Helper classes for testing the Functional API.
 * All subclasses have the impure static member "counter"
 * which counts the number of invocations. While testing 
 * whether something is cached, some impurity is vital :(
 * The member is static because the instances are
 * created inside the Functional API.
 */
public class FunctionalTest {
    public static boolean dbg = true;
    private static void p(String s) { System.out.println(s); }

    /** A function that appends its constructor parameter to
     * its parameter.
     */
    static public class G0 implements PureFunction {
	static public int counter;
	String param;

	public G0(String param) {
	    this.param = param;
	}

	public Object f(Object o) {
	    counter++;
	    if(dbg) p("G0.f - "+this.param+": "+o);
	    return ((String)o) + this.param;
	}
    }

    /** A function that has both superlazy and error placeholders,
     * and is otherwise like G0 except that it throws an error
     * if the input is "E".
     * The placeholder is the string "FOO" and the error placeholder is the
     * string "ERROR".
     */
    static public class G0PlaceHolderError extends G0 {
	static public Functional.Hints functionalHints = 
	    (new Functional.HintsMaker())
		.setHint(Functional.HINT_PLACEHOLDER, "FOO")
		.setHint(Functional.HINT_ERRORPLACEHOLDER, "ERROR")
		.make();

	public G0PlaceHolderError(String param) {
	    super(param);
	}

	public Object f(Object o) {
	    Object r = super.f(o); // Increment counter
	    if("E".equals(o))
		throw new Error("The error");
	    return r;
	}
    }

    /** A function that calls another function with its parameter
     * and appends its constructor parameter to the result.
     */
    static public class G1 implements PureFunction {
	static public int counter = 0;
	String param;
	Function f;

	public G1(String param, Function f) {
	    this.param = param;
	    this.f = f;
	}

	public Object f(Object o) {
	    counter++;
	    Object other = this.f.f(o);
	    if(dbg) p("G1.f - "+this.param+" - "+other+": "+o);
	    return ((String)(other)) + this.param;
	}
    }

    /** A node function that appends its constructor parameter to
     * its parameter.
     */
    static public class G0_Node implements PureNodeFunction {
	static public int counter = 0;
	String param;

	public G0_Node(String param) {
	    this.param = param;
	}

	public Object f(ConstGraph g, Object o) {
	    counter++;
	    return ((String)o) + this.param;
	}
    }

    /** A node function that calls another function with its parameter
     * and appends its constructor parameter to the result.
     */
    static public class G1_Node implements PureNodeFunction {
	static public int counter = 0;
	String param;
	NodeFunction f;

	public G1_Node(String param, NodeFunction f) {
	    this.param = param;
	    this.f = f;
	}

	public Object f(ConstGraph g, Object o) {
	    counter++;
	    return ((String)(this.f.f(g, o))) + this.param;
	}
    }

    /** A node function returning set of third components
     * in triples of two given nodes.
     */
    static public class TripleSet_Node implements PureNodeFunction {
	Object o2;
	public TripleSet_Node(Object o2) {
	    this.o2 = o2;
	}
	public Object f(ConstGraph g, Object o) {
	    Set res = new HashSet();
	    for(Iterator iter = g.findN_11X_Iter(o, o2); iter.hasNext();)
		res.add(iter.next());
	    return res;
	}
    }

    /** An identity node function.
     */
    static public class Identity_Node implements PureNodeFunction {
	NodeFunction f;
	public Identity_Node(NodeFunction f) {
	    this.f = f;
	}
	public Object f(ConstGraph g, Object o) {
	    return f.f(g, o);
	}
    }

 
    /** A class used to test mapping of integer
     * and double parameters into float.
     */
    static public class ConstructorParam_float implements Function {
	public ConstructorParam_float(float f) { }
	public Object f(Object o) {  return o; }
    }

    /** A class used to test mapping of float
     * parameters into double.
     */
    static public class ConstructorParam_double implements Function {
	public ConstructorParam_double(double d) { }
	public Object f(Object o) {  return o; }
    }

    /** A class used to test mapping of boolean
     * parameters.
     */
    static public class ConstructorParam_boolean implements Function {
	public ConstructorParam_boolean(boolean d) { }
	public Object f(Object o) {  return o; }
    }

    /** A class used to test mapping of int
     * parameters.
     */
    static public class ConstructorParam_int implements Function {
	public ConstructorParam_int(int d) { }
	public Object f(Object o) {  return o; }
    }
}

