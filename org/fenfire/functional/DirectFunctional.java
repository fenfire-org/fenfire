/*
DirectFunctional.java
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
import java.lang.reflect.*;
import org.python.core.*;

/** An implementation of Functional that
 * just uses direct calls.
 */
public class DirectFunctional extends Functional {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println(s); }

    protected final ConstGraph constGraph;

    public DirectFunctional(ConstGraph constGraph) {
	this.constGraph = constGraph;
    }

    protected class DirectFunctionInstance implements FunctionInstance {
	public final Function f;
	public DirectFunctionInstance(Function f) {
	    this.f = f;
	}
	public Function getCallableFunction() {
	    return f;
	}
    }

    protected class DirectNodeFunctionInstance implements FunctionInstance {
	public final NodeFunction func;
	private final Function wrapped;
	public DirectNodeFunctionInstance(final NodeFunction func) {
	    this.func = func;
	    if(func instanceof PureNodeFunction)
		wrapped = new PureFunction() {
			public Object f(Object param) {
			    return func.f(constGraph, param);
			}
		    };
	    else
		wrapped = new Function() {
			public Object f(Object param) {
			    return func.f(constGraph, param);
			}
		    };
	}
	public Function getCallableFunction() {
	    return wrapped;
	}
    }

    /** Map a parameter (that may be a FunctionInstance) to a real
     * object for the parameter list of the Function class.
     */
    protected Object mapParameterToFunction(Object o) {
	if(o instanceof DirectFunctionInstance)
	    return ((DirectFunctionInstance)o).f;
	else if(o instanceof DirectNodeFunctionInstance)
	    return ((DirectNodeFunctionInstance)o).func;
	if((o instanceof Function) ||
	   (o instanceof NodeFunction)) 
		throw new Error(
		    "Can't use functions as parameters to Functional. "+o);
	return o;
    }

    /** Map a parameter (that may be a wrapped FunctionInstance) to a real
     * object for the parameter list of the Function class.
     */
    protected PyObject mapParameterToFunction_Jython(PyObject o) {
	if(o instanceof PyJavaInstance) {
	    Object dfni = o.__tojava__(DirectFunctionInstance.class);
	    if(dfni != Py.NoConversion) 
		return Py.java2py(((DirectFunctionInstance)dfni).f);

	    Object dnfni = o.__tojava__(DirectNodeFunctionInstance.class);
	    if(dnfni != Py.NoConversion) 
		return Py.java2py(((DirectNodeFunctionInstance)dnfni).func);

	    if((o.__tojava__(Function.class) != Py.NoConversion) ||
	       (o.__tojava__(NodeFunction.class) != Py.NoConversion))
		throw new Error(
		    "Can't use functions as parameters to Functional. "+o);

	}
	return o;
    }

    protected void instError(Exception e, Constructor constructor, Object[] params) {
	e.printStackTrace();
	Class[] types = constructor.getParameterTypes();
	for(int i=0; i<types.length; i++) {
	    System.err.println("Need: "+types[i]+"\tGot:"+params[i]);
	}
	throw new Error("Couldn't inst. "+e);
    }

    public FunctionInstance createFunctionInstance(
	Object id,
	Class functionClass,
	Object[] parameters0
	) {
	Constructor constructor = selectConstructor(functionClass, parameters0);

	Object[] parameters = new Object[parameters0.length];
	System.arraycopy(parameters0, 0, parameters, 0, parameters0.length);
	// Replace Nodes with their Functions
	for(int i=0; i<parameters.length; i++) {
	    parameters[i] = mapParameterToFunction(parameters[i]);
	}

	if(Function.class.isAssignableFrom(functionClass)) {
	    Function f;
	    try {
		f = (Function)constructor.newInstance(parameters);
	    } catch(Exception e) {
		instError(e, constructor, parameters);
		return null;
	    }

	    return new DirectFunctionInstance(f);
	} else {
	    NodeFunction f;
	    try {
		f = (NodeFunction)constructor.newInstance(parameters);
	    } catch(Exception e) {
		instError(e, constructor, parameters);
		return null;
	    }

	    return new DirectNodeFunctionInstance(f);
	}
    }

    public FunctionInstance createFunctionInstance_Jython(
	Object id,
	org.python.core.PyClass functionClass,
	org.python.core.PyObject[] parameters0
	) {
	PyObject[] parameters = new PyObject[parameters0.length];
	System.arraycopy(parameters0, 0, parameters, 0, parameters0.length);

	for(int i=0; i<parameters.length; i++)
	    parameters[i] = mapParameterToFunction_Jython(parameters[i]);
	
	PyObject instance = functionClass.__call__(parameters,
				new String[] {});
	
	if(dbg) p("Instance: "+instance+" "+
		    instance.__tojava__(Function.class)+" "+
		    instance.__tojava__(NodeFunction.class));

	Object f = instance.__tojava__(Function.class);
	if(f != Py.NoConversion)
	    return new DirectFunctionInstance((Function)f);
	Object nf = instance.__tojava__(NodeFunction.class);
	if(nf != Py.NoConversion)
	    return new DirectNodeFunctionInstance((NodeFunction)nf);



	/*
	org.python.core.PyTuple bases = functionClass.__bases__;
	for(int i=0; i<bases.list.length; i++) {
	    Class c = (Class)bases.list[i].__tojava__(java.lang.Class.class);
	    p("BASE: "+bases.list[i]+" "+bases.list[i].getClass()+" "+
			    c+" "+c.getClass());
	    
	    if(Function.class.isAssignableFrom(c)) {
	    } else if(NodeFunction.class.isAssignableFrom(c)) {
	    }
	}
	*/
	throw new Error("No suitable base class! "+instance+" "+
			instance.getClass());
    }
    
}
