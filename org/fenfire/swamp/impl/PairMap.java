/*
PairMap.java
 *    
 *    Copyright (c) 2003-2004, Tuomas J. Lukka and Benja Fallenstein
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
 * Written by Benja Fallenstein
 */

package org.fenfire.swamp.impl;
import org.nongnu.libvob.util.AbstractHashtable;
import java.util.*;

/** A mapping from pairs of objects to sets of objects, for use as an 
 *  index in HashGraph.
 *  <p>
 *  XXX rename... it is actually a mapping from triples of object to
 *  sets of objects, for use in HashQuadsGraph, but the methods for
 *  treating it as a mapping from pairs to sets still exist.
 */
public class PairMap extends AbstractHashtable {
    private void p(String s) { System.out.println(this+":: "+s); }

    public static class NotUniqueException extends Exception {
	public NotUniqueException() { super(); }
    }


    protected Object[] key1, key2, key3, value;


    public PairMap() {
	this(128);
    }
    public PairMap(int size) {
	super(size);
	key1 = new Object[size];
	key2 = new Object[size];
	key3 = new Object[size];
	value = new Object[size];
    }


    protected final int hashCode(Object k1, Object k2, Object k3) {
	int h1 = (k1!=null) ? k1.hashCode() : 1290438;
	int h2 = (k2!=null) ? k2.hashCode() : 1290438;
	int h3 = (k3!=null) ? k3.hashCode() : 1290438;
	return h1 + (h2 * 34273) + (h3 * 1023);
    }
    protected final int hashCode(int entry) {
	return hashCode(key1[entry], key2[entry], key3[entry]);
    }

    protected void expandArrays(int size) { 
	Object[] n1 = new Object[size];
	Object[] n2 = new Object[size];
	Object[] n3 = new Object[size];
	Object[] nv = new Object[size];

	System.arraycopy(key1, 0, n1, 0, key1.length);
	System.arraycopy(key2, 0, n2, 0, key1.length);
	System.arraycopy(key3, 0, n2, 0, key1.length);
	System.arraycopy(value, 0, nv, 0, key1.length);

	key1=n1; key2=n2; key3=n3; value=nv;
    }


    /** Get the single value for these keys.
     *  If there is no value, returns null. If there is more than one
     *  such value, throws PairMap.NotUniqueException().
     */
    public Object get(Object k1, Object k2) throws NotUniqueException {
	return get(k1, k2, null);
    }
    public Object get(Object k1, Object k2, Object k3) throws NotUniqueException {
	Object o = null;

	for(int i=first(hashCode(k1, k2, k3)); i>=0; i=next(i)) {
	    if(equals(k1, key1[i]) && equals(k2, key2[i]) && 
	       equals(k3, key3[i])) {
		if(o == null)
		    o = value[i];
		else
		    throw new NotUniqueException();
	    }
	}

	return o;
    }

    public Iterator getIter(final Object k1, final Object k2) {
	return getIter(k1, k2, null);
    }
    public Iterator getIter(final Object k1, final Object k2, final Object k3) {
	return new Iterator() {
		int index = first(PairMap.this.hashCode(k1, k2, k3));
		public boolean hasNext() { skip(); return index >= 0; }
		public Object next() {
		    skip();
		    if(index == -1) throw new NoSuchElementException();
		    Object o = value[index];
		    index = PairMap.this.next(index);
		    return o;
		}
		private void skip() {
		    while(true) {
			if(index == -1) break;
			if(PairMap.equals(k1, key1[index]) && 
			   PairMap.equals(k2, key2[index]) && 
			   PairMap.equals(k3, key3[index]))
			    break;
			index = PairMap.this.next(index);
		    }
		}
		public void remove() { 
		    throw new UnsupportedOperationException();
		}
	    };
    }

    public boolean contains(Object k1, Object k2, Object v) {
	return contains(k1, k2, null, v);
    }
    public boolean contains(Object k1, Object k2, Object k3, Object v) {
	for(int i=first(hashCode(k1, k2, k3)); i>=0; i=next(i))
	    if(equals(k1, key1[i]) && equals(k2, key2[i]) &&
	       equals(k3, key3[i]) && equals(v, value[i]))
		return true;

	return false;
    }

    public void add(Object k1, Object k2, Object v) {
	add(k1, k2, null, v);
    }
    public void add(Object k1, Object k2, Object k3, Object v) {
	if(contains(k1, k2, k3, v)) return;

	int e = newEntry();
	key1[e] = k1; key2[e] = k2; key3[e] = k3; value[e] = v;
	put(e);
    }

    public void rm(Object k1, Object k2, Object v) {
	rm(k1, k2, null, v);
    }
    public void rm(Object k1, Object k2, Object k3, Object v) {
	for(int i=first(hashCode(k1, k2, k3)); i>=0; i=next(i)) {
	    if(equals(k1, key1[i]) && equals(k2, key2[i]) &&
	       equals(k3, key3[i]) && equals(v, value[i])) {
		removeEntry(i);
		return;
	    }
	}
    }


    private static boolean equals(Object o1, Object o2) {
	if(o1 == null) 
	    return o2 == null;

	return o1.equals(o2);
    }
}
