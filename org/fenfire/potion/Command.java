/*
Command.java
 *    
 *    Copyright (c) 2003-2005, Benja Fallenstein
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

public interface Command extends Head {

    void execute(List[] params, Map context);

    CommandExpression call();
    CommandExpression call(FunctionExpression p1);
    CommandExpression call(FunctionExpression p1, FunctionExpression p2);
    CommandExpression call(FunctionExpression p1, FunctionExpression p2, 
			   FunctionExpression p3);
}
