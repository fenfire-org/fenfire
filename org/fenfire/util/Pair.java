/*
Pair.java
 *    
 *    Copyright (c) 2002, Tuomas J. Lukka
 *    
 *    This file is part of Gzz.
 *    
 *    Gzz is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Gzz is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Gzz; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *    
 */
/*
 * Written by Tuomas J. Lukka
 */

package org.fenfire.util;

/** A Pair, useful as a hash key.
 */
public class Pair {
    public final Object first;
    public final Object second;
    public Pair(Object first, Object second) {
	this.first = first;
	this.second = second;
    }

    public int hashCode() {
	return 
	    (first == null ? 23472861 : first.hashCode()*317501) ^ 
	    (second == null ? 97124109 : second.hashCode()*1941);
    }

    public boolean equals(Object o) {
	if(!(o instanceof Pair)) return false;
	Pair p = (Pair)o;
	if((first == null && p.first != null) || 
	   (first != null && !first.equals(p.first)) ||
	   (second == null && p.second != null)|| 
	   (second != null && !second.equals(p.second))) 
	    return false;
	return true;
    }

    public String toString() {
	return "["+super.toString()+": "+
		first + ", " +
		second + "]";
    }
}
