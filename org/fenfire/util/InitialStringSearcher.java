/*
InitialStringSearcher.java
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
import java.util.*;

/** Searches for strings having the parameter string
 * as the initial substring.
 * Initial, not terribly efficient implementation.
 * Assumes that we will search for a few of the first characters.
 * <p>
 * Note that searching for a single character will return nothing.
 * Searching for more than 6 characters will return the same as searching
 * for the first 6 characters in that string.
 */
public class InitialStringSearcher implements StringSearcher {

    /*
    class Node {
	char ch;
	Node nextSibling;
	Node firstChild;
	String whole;
	Object value;
    }

    Node root = new Node();

    void makeOrGet(Node n, int depth, char ch) {
	if(n.firstChild != null) {
	}
	Node prev = n.firstChild;
	n.firstChild = new Node();
	n.firstChild.ch = ch;
	n.firstChild.nextSibling = prev;
	return n.firstChild;
    }

    public void addString(String s, Object value) {
	Node cur = root;
	for(int i=0; i<s.length(); i++) {
	    cur = makeOrGet(cur, i, s.charAt(i));
	    if(cur.firstChild == null)
		break;
	}
	cur.whole = s;
	cur.value = value;
    }
    */

    Map[] sets = new Map[7];

    {
	for(int i=2; i<sets.length; i++)
	    sets[i] = new HashMap();
    }

    public void addString(String s, Object value) {
	for(int i=0; i<sets.length; i++) {
	    if(sets[i] == null) continue;
            if(s.length() < i) continue;
	    String key = s.substring(0, i).toLowerCase();
	    List list = (List) sets[i].get(key);
	    if(list == null) {
		sets[i].put(key, list = new ArrayList());
	    }
	    list.add(value);
	}
    }

    public java.util.Collection search(String s) {
	if(s.length() >= sets.length) s = s.substring(0, s.length()-1);
	while(sets[s.length()] == null) {
            if(s.length() == 0) return null;
            s = s.substring(0, s.length()-1);
        }
	return (Collection)sets[s.length()].get(s.toLowerCase());
    }


}
