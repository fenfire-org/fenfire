/*
SimpleFunction.java
 *    
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
 * Written by Benja Fallenstein
 */
package org.fenfire.potion;
import java.util.*;

/** Like SimpleCommand, but for functions.
 */
public abstract class SimpleFunction extends AbstractFunction {
    public SimpleFunction(Object[] spec) { super(spec); }

    protected abstract Object evaluate(Object[] params, Map context);
	
    public List evaluate(List[] lists, Map context) {
	Object[] params = new Object[lists.length];
	List results = new ArrayList();

	recursive(lists, params, 0, results, context);

	return results;
    }

    private void recursive(List[] lists, Object[] params, int i, List results,
			   Map context) {
	if(i >= lists.length) {
	    results.add(evaluate(params, context));
	} else {
	    for(Iterator iter=lists[i].iterator(); iter.hasNext();) {
		params[i] = iter.next();
		recursive(lists, params, i+1, results, context);
	    }
	}
    }
}
