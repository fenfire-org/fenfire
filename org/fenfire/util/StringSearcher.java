/*   
StringSearcher.java
 *    
 *    Copyright (c) 2001, Tuomas Lukka
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
 * Written by Tuomas Lukka
 */
package org.fenfire.util;

/** An interface for storing a set of strings with keys
 * and searchig from them.
 * The search behaviour, i.e. whether we search for initial substrings,
 * case-free substrings or occurrences is not specified by this interface;
 * rather, different objects exist for different algorithms
 * (the Strategy pattern in GOF).
 */
public interface StringSearcher {

    void addString(String s, Object value);

    /** Search for the given string.
     * The returned collection <b>may not be modified</b>.
     */
    java.util.Collection search(String s);
}
