/*
ConstantFunction.java
 *    
 *    Copyright (c) 2004, Tuomas J. Lukka
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
import org.fenfire.swamp.ConstGraph;

/** A function and nodefunction that returns the same value
 * regardless of the parameter given.
 */
public class ConstantFunction implements PureFunction, PureNodeFunction {
    private final Object value;

    /** Create a new ConstantFunction.
     * @param value The value to always return.
     */
    public ConstantFunction(Object value) {
	this.value = value;
    }
    public Object f(Object param) {
	return value;
    }
    public Object f(ConstGraph g, Object param) {
	return value;
    }
}
