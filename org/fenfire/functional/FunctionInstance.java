/*
FunctionInstance.java
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



/** A node in the functional calculation DAG.
 * This class represents (one, or several equivalent) instances
 * of a class implementing Function or NodeFunction.
 * This class wraps the computation so that the implementation
 * of Functional is able to use more information to determine
 * how and when to evaluate what functions.
 */
public interface FunctionInstance {
    /** Get a function entry point for this node.
     */
    Function getCallableFunction();
}


