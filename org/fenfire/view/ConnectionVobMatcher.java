/*
ConnectionVobMatcher.java
 *
 *    Copyright (c) 2003-2005, Benja Fallenstein
 *
 *    This file is part of Libvob.
 *    
 *    Libvob is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Libvob is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Libvob; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.view;
import org.nongnu.libvob.*;
import org.nongnu.libvob.layout.IndexedVobMatcher;
import java.awt.*;
import java.util.*;

/** A VobMatcher that knows about Fentwine-like connections.
 */
public class ConnectionVobMatcher extends IndexedVobMatcher {
private static final String rcsid = "$Id: ConnectionVobMatcher.java,v 1.1 2003/04/29 18:07:15 benja Exp $";  
    public static boolean dbg = false;
    private static void p(String s) { System.out.println(s); }

    protected static final Object 
	LINK_CS_KEY_NEG = new Object(),
	LINK_CS_KEY_POS = new Object();

    /** The coordinate systems */
    protected boolean[] usedInTree = new boolean[16];


    /** The links between the coordinate systems */
    protected int size = 0;
    protected int[] cs1 = new int[16];
    protected Object[] linkkey = new Object[16];
    protected int[] cs2 = new int[16];
    protected int[] linkcs = new int[16];


    protected int focus = -1, nextFocus = -1;

    protected Object nextLinkKey = null;
    protected int nextLinkDir;


    public void clear() {
	super.clear();
	size = 0;
	focus = nextFocus = -1;
	nextLinkKey = null;
	java.util.Arrays.fill(usedInTree, false);
    }


    // the public interface

    public void setFocus(int focus) {
	this.focus = focus;
	this.nextFocus = focus; // default
    }

    public int getFocus() {
	return focus;
    }

    /** Indicate that two coordinate systems are linked
     *  in a way indicated by <code>linkkey</code>.
     *  The connections must form an acyclic graph such that
     *  if you take any connected coordinate system as the root,
     *  the connections form a tree. If they don't, 
     *  you'll get an infinite loop!
     *  <p>
     *  The coordinate systems must have been added using add(),
     *  or they may not be interpolated correctly.
     *  @param linkkey A key for the connection type,
     *             e.g. an RDF property.
     */
    public void link(int parent, int dir, int child,
		     Object linkKey) {
	link(parent, dir, child, linkKey, -1);
    }

    /** Indicate that two coordinate systems are linked
     *  in a way indicated by <code>linkkey</code>.
     *  The connections must form an acyclic graph such that
     *  if you take any connected coordinate system as the root,
     *  the connections form a tree. If they don't, 
     *  you'll get an infinite loop!
     *  <p>
     *  The coordinate systems must have been added using add(),
     *  or they may not be interpolated correctly.
     *  @param linkkey A key for the connection type,
     *             e.g. an RDF property.
     *  @param linkCS A coordinate system placed <em>on the link</em>,
     *                e.g. for showing the RDF property of a connection.
     *                This is interpolated if the connection is interpolated.
     */
    public void link(int parent, int dir, int child,
		     Object linkKey, int linkCS) {
	if(dbg) p("link: "+cs1+" "+linkkey+" "+cs2);
	ensureSize(size+1);

	ensureMaxCS(parent);
	ensureMaxCS(child);
	ensureMaxCS(linkCS);

	if(parent < 0)
	    throw new IllegalArgumentException("parent = "+parent+" < 0");
	
	this.linkkey[size] = linkKey;
	this.linkcs[size] = linkCS;

	usedInTree[parent] = true;
	usedInTree[child] = true;
	if(linkCS >= 0)
	    usedInTree[linkCS] = true;

	if(dir > 0) {
	    this.cs1[size] = parent;
	    this.cs2[size] = child;
	} else {
	    this.cs1[size] = child;
	    this.cs2[size] = parent;
	}

	size++;
    }
    
    public void setNextFocus(int nextFocus) {
	System.out.println("set next focus "+nextFocus);
	this.nextFocus = nextFocus;
    }

    public void setNextFocus(int nextFocus, 
			     Object nextLinkKey, int nextLinkDir) {
	System.out.println("set next focus (b) "+nextFocus);
	this.nextFocus = nextFocus;
	this.nextLinkKey = nextLinkKey;
	this.nextLinkDir = nextLinkDir;
    }

    public int getLink(int from, int dir, Object childKey,
		       Object linkKey) {
	if(dir > 0)
	    return cs2[getLinkIndex(from, dir, childKey, linkKey)];
	else
	    return cs1[getLinkIndex(from, dir, childKey, linkKey)];
    }

    public int getLinkIndex(int from, int dir, Object childKey,
			    Object linkKey) {
	if(dir > 0) {
	    for(int i=0; i<size; i++)
		if(cs1[i] == from && linkkey[i].equals(linkKey)
		   && getKey(cs2[i]).equals(childKey))
		    return i;
	} else {
	    for(int i=0; i<size; i++)
		if(cs2[i] == from && linkkey[i].equals(linkKey)
		   && getKey(cs1[i]).equals(childKey))
		    return i;
	}
	
	throw new NoSuchElementException();
    }

    /** Iterate through the links in a given direction with a given link key.
     *  For example:
     *  <pre>
     *  int cs = -1;
     *  while((cs = matcher.getNextLinkedCS(from, dir, linkKey, cs)) >= 0) {
     *      Object key = matcher.getKey(cs);
     *      ...
     *  }
     *  </pre>
     */
    public int getNextLinkedCS(int from, int dir, Object linkKey, 
			       int lastLinkedCS) {
	int i=0;

	if(dir > 0) {
	    if(lastLinkedCS >= 0)
		for(; i<size; i++)
		    if(cs2[i] == lastLinkedCS) { i++; break; }

	    for(; i<size; i++)
		if(cs1[i] == from && linkkey[i].equals(linkKey)) 
		    return cs2[i];
	} else {
	    if(lastLinkedCS >= 0)
		for(; i<size; i++)
		    if(cs1[i] == lastLinkedCS) { i++; break; }

	    for(; i<size; i++)
		if(cs2[i] == from && linkkey[i].equals(linkKey)) 
		    return cs1[i];
	}

	return -1;
    }

    // the link traversing -- XXX not fast

    protected void traverse(int parent, int cs, 
			    ConnectionVobMatcher o, int ocs,
			    int[] list) {
	if(dbg) p("traverse: "+parent+" "+cs+" "+o+" "+ocs);

	for(int i=0; i<size; i++) {
	    if(cs == cs1[i]) {
		if(cs2[i] == parent) continue;
		int j;
		try {
		    j = o.getLinkIndex(ocs, 1, getKey(cs2[i]), linkkey[i]);
		} catch(NoSuchElementException _) {
		    continue;
		}
		list[cs2[i]] = o.cs2[j];
		if(linkcs[i] >= 0 && o.linkcs[j] >= 0)
		    list[linkcs[i]] = o.linkcs[j];
		traverse(cs1[i], cs2[i], o, o.cs2[j], list);
		if(dbg) p("Rematch(+): "+cs1[i]+" "+linkkey[i]+" "+cs2[i]);
	    } else if(cs == cs2[i]) {
		if(cs1[i] == parent) continue;
		int j;
		try {
		    j = o.getLinkIndex(ocs, -1, getKey(cs1[i]), linkkey[i]);
		} catch(NoSuchElementException _) {
		    continue;
		}
		list[cs1[i]] = o.cs1[j];
		if(linkcs[i] >= 0 && o.linkcs[j] >= 0)
		    list[linkcs[i]] = o.linkcs[j];
		traverse(cs2[i], cs1[i], o, o.cs1[j], list);
		if(dbg) p("Rematch(-): "+cs1[i]+" "+linkkey[i]+" "+cs2[i]);
	    }
	}
    }


    // implementation of VobMatcher

    public int[] interpList(VobMatcher other, boolean towardsOther) {
	long start = System.currentTimeMillis();

	ConnectionVobMatcher o = (ConnectionVobMatcher)other;

	boolean hasFocus = false;

	//p(towardsOther+" "+nextFocus+" "+o.nextFocus+" "+this+" "+o);

	int root=0, oroot=0;
	if(towardsOther && nextFocus >= 0 && o.focus >= 0) {
	    root = nextFocus;
	    oroot = o.focus;

	    if(!getKey(root).equals(o.getKey(oroot))) {
		oroot = o.getCS(o.getParent(o.focus), getKey(focus));
	    }

	    hasFocus = true;
	} else if(!towardsOther && o.nextFocus >= 0 && focus >= 0) {
	    root = focus;
	    oroot = o.nextFocus;

	    if(!getKey(root).equals(o.getKey(oroot))) {
		root = getCS(getParent(focus), o.getKey(o.focus));
	    }

	    hasFocus = true;
	}

	//p("oRoot: "+oroot+" "+o.getKey(oroot));
	//p("Root: "+root+" "+getKey(root));

	//p("hasFocus = "+hasFocus);

	int[] list = new int[maxCS+1];
	list[0] = 0; // interpolate 0 cs to 0 cs by default

	if(hasFocus) {
	    for(int i=1; i<maxCS+1; i++)
		list[i] = (usedInTree[i] ? DONT_INTERP : SHOW_IN_INTERP);

	    list[root] = oroot;
	    traverse(-1, root, o, oroot, list);
	} else {
	    for(int i=1; i<maxCS+1; i++)
		list[i] = SHOW_IN_INTERP;
	}

	for(int i=0; i<ncs; i++) {
	    int cs = csList[i];
	    if(hasFocus && usedInTree[cs]) continue;

	    if(list[csParent[cs]] == SHOW_IN_INTERP) {
		//System.out.println("parent showininterp");
		list[cs] = SHOW_IN_INTERP;
		continue;
	    }

	    // XXX assumes that parent is set already
	    int oparent = list[csParent[cs]];
	    if(oparent < 0) 
		list[cs] = oparent;
	    else
		list[cs] = getOtherCS(o, oparent, cs);
	}

	long end = System.currentTimeMillis();
	if(dbg || end-start > MAX_MILLIS)
	    p("Time for interpList generation: "+(end-start)+" millis");
	/*
	      "maxCS="+maxCS+", size="+size+", ncs="+ncs+
	      ", getCSCalls="+o.getCSCalls+", uIT="+usedInTrees+", "+
	      "parentNI="+parentNotInterp);
	*/

	//p("root interpolated to: "+list[root]);

	return list;
    }

    protected final void ensureSize(int want) {
	if(want > linkkey.length)
	    expandSize(2*want);
    }
    
    protected void expandSize(int n) {
	final int len = linkkey.length;

	int[] n1 = new int[n];
	Object[] nk = new Object[n];
	int[] n2 = new int[n];
	int[] nl = new int[n];
	
	System.arraycopy(cs1, 0, n1, 0, len);
	System.arraycopy(linkkey, 0, nk, 0, len);
	System.arraycopy(cs2, 0, n2, 0, len);
	System.arraycopy(linkcs, 0, nl, 0, len); 
	
	cs1 = n1; linkkey = nk; cs2 = n2; linkcs = nl;
    }

    protected void expandMaxCS(int n) {
	super.expandMaxCS(n);

	boolean[] nused = new boolean[n];
	System.arraycopy(usedInTree, 0, nused, 0, usedInTree.length);
	usedInTree = nused;
    }
}
