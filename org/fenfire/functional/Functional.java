/*
Functional.java
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
import java.lang.reflect.*;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import org.python.core.*;

/** An object that manages a DAG of Function instances, enabling 
 * transparent and super-lazy caching.
 * This interface allows different implementations, from ones that simply
 * wrap and call the actual function instances to ones that allow
 * transparent run-time data-sensitive caching and super-lazy caching.
 */
public abstract class Functional {
    public static boolean dbg = false;
    static private void p(String s) { System.out.println(s); }

    /** The id of the hint for which background thread group
     * a function should use, if it's not run directly. 
     * This is useful for using Libvob OpenGL, since 
     * OpenGL objects should only be handled in one thread.
     * The value for the OpenGL thread is "OPENGL".
     */
    public final static String HINT_BGGROUP=
	"urn:urn-5:Avc8CAG5IlAxiPld5A-eHFpe33lA".intern();

    /** The id of the hint about function speed.
     * If non-null, assumes the function is slow.
     */
    public final static String HINT_SLOW=
	"urn:urn-5:6tZ8QgByvj22e8zbzPb4FMb0ATUJ".intern();

    /** The id of the hint for the placeholder return object.
     * The object this hint is set to
     * is returned for super-lazily cached
     * functions when the value is not in the cache.
     */
    public final static String HINT_PLACEHOLDER=
	"urn:urn-5:VXY-8ssXNGZFa488N3+dpuMK95GE".intern();

    /** The id of the hint for the error placeholder return object.
     * If an error has occurred evaluating the function,
     * the value of this hint is returned.
     */
    public final static String HINT_ERRORPLACEHOLDER=
	"urn:urn-5:eqali8fJteYllPEPaU+0jSooXUOP".intern();

    /** Hints about a Function class.
     * Hints tell the Functional API about a Function: is it slow to evaluate,
     * does it need to be evaluated in the OpenGL thread &c.
     * <p>
     * Hints objects are created using HintsMaker.
     * An empty interface in order to be unmodifiable.
     * Each class that implements ``Function`` or ``NodeFunction``
     * that is given to this API shall have a static member ``functionalHints``
     * of this type.
     */
    public interface Hints {
    }

    protected static class DefaultHints implements Hints {
	public DefaultHints(HintsMaker maker) {
	    hints = Collections.unmodifiableMap(
			(Map)(maker.hints.clone()));
	}
	public final Map hints;
    }

    /** An interface for creating Hints objects.
     * This interface is structured so it can be used as follows:
     * <pre>
     * 		public class FooFunction implements Function {
     * 		    static public Functional.Hints functionalHints = 
     * 			(new Functional.HintsMaker())
     * 				.setHint(Functional.HINT_PLACEHOLDER, "X")
     * 				.setHint(Functional.HINT_BGGROUP, "OPENGL")
     * 				.make();
     * 					
     * 		}
     * </pre>
     */
    public static class HintsMaker {
	HashMap hints = new HashMap();

	/** Set the value of a hint.
	 * @return this object
	 */
	public HintsMaker setHint(String id, Object value) {
	    hints.put(id, value);
	    return this;
	}

	/** Create the Hints object.
	 */
	public Hints make() {
	    return new DefaultHints(this);
	}
    }

    /** Create a new node in the DAG.
     * @param id An identifier for the node. Used for determining caching &c.
     *           Should be stable between invocations.
     * @param functionClass The class of which the Function (or NodeFunction)
     *		object should 
     *	     	be created.
     * @param parameters The parameters for the constructor of the class.
     *		These may contain Node objects, which will be converted
     *		to functions or nodefunctions as appropriate.
     */
    public abstract FunctionInstance createFunctionInstance(
	Object id,
	Class functionClass,
	Object[] parameters
	);

    /** Create a new node in the DAG.
     * @param id An identifier for the node. Used for determining caching &c.
     *           Should be stable between invocations.
     * @param functionClass The class of which the Function (or NodeFunction)
     *		object should 
     *	     	be created.
     * @param parameters The parameters for the constructor of the class.
     *		These may contain Node objects, which will be converted
     *		to functions or nodefunctions as appropriate.
     */
    public abstract FunctionInstance createFunctionInstance_Jython(
	Object id,
	org.python.core.PyClass functionClass,
	org.python.core.PyObject[] parameters
	);

    /*
     * XXX I couldn't make the following work. 
    public FunctionInstance createFunctionInstance_Any(
	    Object id,
	    Object functionClass,
	    Object[] parameters) {
	p("Any: Fcl = "+functionClass);
	if(functionClass instanceof PyObject) {
	    p("Any: ispy");
	    PyObject[] params = new PyObject[parameters.length];
	    for(int i=0; i<params.length; i++)
		params[i] = Py.java2py(parameters[i]);
	    return createFunctionInstance_Jython(id, 
			(PyClass)functionClass, params);
	} else if(functionClass instanceof Class) {
	    p("Any: isj");
	    return createFunctionInstance(id, 
			(Class)functionClass, parameters);
	}
	throw new Error("CreateFunctionINstance...");
    }
    */

    /** Helper function: Select a suitable constructor.
     * Useful for createFunctionInstance.
     * Goes through the constructors of functionClass and 
     * selects one to call given the parameter objects.
     * Instead of the Node objects in parameters, 
     * gives the Function interface.
     * XXX PROBLEM: NODEFUNCTIONS!!
     * <p>
     * Chooses the first matching one, which may be too general sometimes.
     * <p>
     * Throws an error if no suitable constructor found.
     */
    protected Constructor selectConstructor(Class functionClass, Object[] parameters) {
	Constructor[] constructors = functionClass.getConstructors();
CONSTRUCTORS: for(int i=0; i<constructors.length; i++) {
	    Class[] ptypes = constructors[i].getParameterTypes();
	    if(ptypes.length != parameters.length)
		continue CONSTRUCTORS;
	    PARAMETERS: for(int j=0; j<ptypes.length; j++) {
		if(parameters[j] == null) {
		    if(dbg) p("parameters[j] == null");
		    continue PARAMETERS;
		}
		Class givenParamType = parameters[j].getClass();
		if(dbg) p("Compare parameters: "+givenParamType+" "+ptypes[j]);

		// If it's a node, allow functions and nodefunctions.
		if(FunctionInstance.class.isAssignableFrom(givenParamType)) {
		    if(dbg) p("Had FunctionInstance: "+givenParamType+"; want: "+ptypes[j]+" -- assignable from Function: "+ptypes[j].isAssignableFrom(Function.class));
		    if(ptypes[j].isAssignableFrom(PureFunction.class) ||
		       ptypes[j].isAssignableFrom(PureNodeFunction.class)) {
			if (dbg) p("MATCH: "+givenParamType+", "+ptypes[j]);
			continue PARAMETERS;
		    }
		}

		// Allow float parameter for double and integer.
		if(ptypes[j].isAssignableFrom(java.lang.Float.TYPE)) {
		    if (parameters[j] instanceof java.lang.Double) {
			parameters[j] = new java.lang.Float(((java.lang.Double)parameters[j]).floatValue());
			if (dbg) p("MATCH: "+givenParamType+", "+ptypes[j]);
			continue PARAMETERS;
		    } else if (parameters[j] instanceof java.lang.Integer) {
			parameters[j] = new java.lang.Float(((java.lang.Integer)parameters[j]).floatValue());
			if (dbg) p("MATCH: "+givenParamType+", "+ptypes[j]);
			continue PARAMETERS;
		    }
		}

		// Allow double parameter for float.
		if(ptypes[j].isAssignableFrom(java.lang.Double.TYPE)) {
		    if (parameters[j] instanceof java.lang.Float) {
			parameters[j] = new java.lang.Double(((java.lang.Float)parameters[j]).doubleValue());
			if (dbg) p("MATCH: "+givenParamType+", "+ptypes[j]);
			continue PARAMETERS;
		    }
		}

		if(ptypes[j].isAssignableFrom(java.lang.Boolean.TYPE)) {
		    if(parameters[j] instanceof java.lang.Boolean)
			continue PARAMETERS;
		}

		if(ptypes[j].isAssignableFrom(java.lang.Integer.TYPE)) {
		    if(parameters[j] instanceof java.lang.Integer)
			continue PARAMETERS;
		}

		if(! ptypes[j].isAssignableFrom(givenParamType)) {
		    if (dbg) p("DISMATCH: "+givenParamType+", "+ptypes[j]);
		    continue CONSTRUCTORS;
		}
		if (dbg) p("MATCH: "+givenParamType+", "+ptypes[j]);
	    }
	    return constructors[i];
	}

	// Since we didn't find a constructor
	// and will die, we may as well print 
	// out some useful debug info for the caller

	for(int i=0; i<parameters.length; i++) {
	    p("Parameter "+i+" "+parameters[i]+" "+parameters[i].getClass());
	}
	for(int i=0; i<constructors.length; i++) {
	    p("Constructor "+i);
	    Class[] ptypes = constructors[i].getParameterTypes();
	    for(int j=0; j<ptypes.length; j++)
		p(" "+ptypes[j]);
	}

	Class c = functionClass;
	while(c != null) {
	    p("SUP: "+c);
	    c = c.getSuperclass();
	}
	Class[] in = functionClass.getInterfaces();
	for(int i=0; i<in.length; i++)
	    p("IN: "+in[i]);

	p("Class of class");
	c = functionClass.getClass();
	while(c != null) {
	    p("SUP: "+c);
	    c = c.getSuperclass();
	}
	in = functionClass.getClass().getInterfaces();
	for(int i=0; i<in.length; i++)
	    p("IN: "+in[i]);


	throw new Error("No constructor found: "+functionClass);
    }
}


