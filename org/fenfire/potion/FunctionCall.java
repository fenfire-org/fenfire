/*
FunctionCall.java
 *    
 *    Copyright (c) 2002, Anton Feldmann
 *    Copyright (c) 2005, Benja Fallenstein
 *    
 *    This file is part of Fenfire.
 *    
 *    Fenfire is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Fenfire is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU Lesser General
 *    Public License along with Fenfire; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *    
 */
/*
 * Written by Anton Feldmann
 */
package org.fenfire.potion;
import java.util.*;

public class FunctionCall extends Call implements FunctionExpression {

    Function function;

    public FunctionCall(Function function, FunctionExpression[] params) {
	super(function, params);
	this.function = function;
    }

    public List evaluate(Map context) {
	return function.evaluate(evaluateParams(context), context);
    }

    public Call newCall(FunctionExpression[] params) {
	return new FunctionCall(function, params);
    }
}
