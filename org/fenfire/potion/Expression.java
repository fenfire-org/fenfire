/*
Expression.java
 *    
 *    Copyright (c) 2002, Sarah Stehlig
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
 * Written by Sarah Stehlig
 */
package org.fenfire.potion;
import java.util.*;

public abstract class Expression {

    public final Head head;
    public final FunctionExpression[] params;
  
    public Expression(Head head, FunctionExpression[] params) {
	this.head = head;
	this.params = params;
    }
  
    public String getString(Map context) {
	return head.getString(params, context);
    }

    public boolean isComplete() {
	for(int i=0; i<params.length; i++) {
	    if(params[i] == null) return false;
	    if(!params[i].isComplete()) return false;
	}

	return true;
    }

    public Type getNextParam() {
	for(int i=0; i<params.length; i++) {
	    if(params[i] == null) return head.getParams()[i];
	    if(params[i] instanceof Expression) {
		Type t = ((Expression)params[i]).getNextParam();
		if(t != null) return t;
	    }
	}
	return null;
    }

    public Expression setNextParam(FunctionExpression expr) {
	FunctionExpression[] n = new FunctionExpression[params.length];
	int i=0;
	for(; i<params.length; i++) {
	    if(params[i] == null) {
		n[i] = expr;
		break;
	    }
	    if(params[i] instanceof FunctionExpression) {
		FunctionExpression fc = (FunctionExpression)params[i];
		if(!fc.isComplete()) {
		    n[i] = (FunctionExpression)fc.setNextParam(expr);
		    break;
		}
	    }
	    n[i] = params[i];
	}

	if(i >= params.length)
	    throw new IllegalStateException("Expression is already complete");

	for(i++; i<params.length; i++)
	    n[i] = params[i];

	return newExpression(head, n);
    }

    public Expression instantiatePattern(Map context) {
	FunctionExpression[] n = new FunctionExpression[params.length];
	for(int i=0; i<params.length; i++) {
	    if(params[i] != null)
		n[i] = (FunctionExpression)params[i].instantiatePattern(context);
	}

	return newExpression(head.instantiatePattern(context), n);
    }

    abstract protected Expression newExpression(Head head,
						FunctionExpression[] params);

    protected List[] evaluateParams(Map context) {
	List[] result = new List[params.length];
	for(int i=0; i<params.length; i++)
	    result[i] = params[i].evaluate(context);
	return result;
    }

    public String toString() {
	StringBuffer buf = new StringBuffer(head.toString());
	buf.append("[");
	for(int i=0; i<params.length-1; i++) {
	    buf.append(params[i]);
	    buf.append(", ");
	}

	if(params.length > 0)
	    buf.append(params[params.length-1]);

	buf.append("]");
	return buf.toString();
    }
}
