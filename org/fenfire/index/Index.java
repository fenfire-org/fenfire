/*
Index.java
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

package org.fenfire.index;
import java.util.Collection;

/** A searchable index.
 * This is an interface which is used to represent indices,
 * for example
 * <ul>
 * <li> Cell by substring of content
 * <li> Cell by overlap of an enfilade
 * <li> Cell by whole content
 * </ul>
 */
public interface Index {
    /** Get a set of entry "names" that match the given object.
     * The semantics of this call depend on the exact implementation.
     * What is guaranteed is that if no change has been made to 
     * the Index object, the same object will get an equal
     * return value from this call.
     */
    Collection getMatches(Object o);
}
