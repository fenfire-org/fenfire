/*
RDFList.java
 *    
 *    Copyright (c) 2003, Matti J. Katila
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
 * Written by Matti J. Katila
 */

package org.fenfire.util.lava;
import org.fenfire.*;
import org.fenfire.swamp.*;
import org.fenfire.vocab.RDF;
import org.fenfire.vocab.lava.LIST;

/** Simple list implementation.
 * 
 *  (Given object will be the List) --next--> first item --next --> second item ...
 */
public class RDFList {

    private final Object list;
    private Fen fen;

    /** Give an object and it will be a list.
     */
    public RDFList(Fen fen, Object theListObj) {
	this.fen = fen;
	this.list = theListObj;
	fen.graph.add(list, RDF.type, LIST.Instance);
    }

    public Object get(int index) {
	if (index < 0 || index >= length() ) 
	    throw new Error("Index: "+index+" out of list's length"); 
	
	Object obj = list;
	for (int i=-1; i<index; i++) {
	    obj = fen.graph.find1_11X(obj, LIST.next);
	}
	return obj;
    }

    /** Push back a new object.
     */
    public void add(Object obj) {
	if (obj == null) throw new Error("Null given!");
	if (obj == list) throw new Error("List instance given!");

	Object last = goEnd();
	fen.graph.set1_11X(last, LIST.next, obj);
    }


    /** Length of a list.
     */
    public int length() {
	Object curr = list;
	int len = -1;
	for (; curr != null; len++)
	    curr = fen.graph.find1_11X(curr, LIST.next);
	return len;
    }

    public void remove(int ind) {
	throw new Error("Not implemented.");
    }
	
    private Object goEnd() {
	Object end = list;
	Object next = list;
	while (next != null) {
	    end = next;
	    next = fen.graph.find1_11X(end, LIST.next);
	}
	return end;
    }
}
