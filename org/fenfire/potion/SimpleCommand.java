/*
SimpleCommand.java
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

/** An abstract implementation of Command for commands 
 *  that don't do anything special when passed multiple parameters.
 *  The implementation of execute(List[], context) calls 
 *  the abstract method execute(Object[], context) one time for every 
 *  combination of objects chosen from the lists; e.g., with lists
 *  <pre>
 *      {[1, 2, 3], [a, b, c], [x, y]}
 *  </pre>
 *  execute(Object[], context) will be called once with every one
 *  of the following arrays:
 *  <pre>
 *      {1, a, x}, {1, a, y}, {1, b, x}, {1, b, y}, {1, c, x}, {1, c, y},
 *      {2, a, x}, {2, a, y}, {2, b, x}, {2, b, y}, {2, c, x}, {2, c, y},
 *      {3, a, x}, {3, a, y}, {3, b, x}, {3, b, y}, {3, c, x}, {3, c, y}
 *  </pre>
 */
public abstract class SimpleCommand extends AbstractHead implements Command {
    public SimpleCommand(Object[] spec) { super(spec); }

    protected abstract void execute(Object[] params, Map context);
	
    public void execute(List[] lists, Map context) {
	Object[] params = new Object[lists.length];

	recursive(lists, params, 0, context);
    }

    private void recursive(List[] lists, Object[] params, int i, 
			   Map context) {
	if(i >= lists.length) {
	    execute(params, context);
	} else {
	    for(Iterator iter=lists[i].iterator(); iter.hasNext();) {
		params[i] = iter.next();
		recursive(lists, params, i+1, context);
	    }
	}
    }
}
