/*
SmushedQuadsGraph.java
 *    
 *    Copyright (c) 2004-2005, Benja Fallenstein
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
package org.fenfire.swamp.smush;
import org.fenfire.swamp.*;
import org.fenfire.swamp.impl.AllQuadsGraph;
import org.nongnu.navidoc.util.Obs;
import java.util.*;
import java.security.*;

public final class SmushedQuadsGraph extends SmushedQuadsGraph_Gen {
    public static boolean dbg = false;
    private static void p(String s) { System.out.println("SmushedQuadsGraph:: "+s); }

    private Object 
	MBOX = Nodes.get("http://xmlns.com/foaf/0.1/mbox"),
	SHA1SUM = Nodes.get("http://xmlns.com/foaf/0.1/mbox_sha1sum");


    private Set ifps = new HashSet(Arrays.asList(Smusher.IFPs));
    private Comparator cmp = new Smusher.NodeComparator();


    private Map groupsBySubject = new HashMap();
    private Map groupsByPair = new HashMap();


    private Set smushListeners = new HashSet();


    private class Pair extends org.fenfire.util.Pair {
	Pair(Object p, Object o) { super(p, o); }

	Object p() { return first; }
	Object o() { return second; }
    }

    private class Group {
	Group(Object n) { canonicalNode = n; }

	Object canonicalNode;

	Set subjects = new HashSet();
	Set pairs    = new HashSet();
    }

    
    // Map ( predicate -> Map ( object -> pair ) )
    private Map pairs = new HashMap(); 

    private Pair getPair(Object p, Object o) {
	Map m = (Map)pairs.get(p);
	if(m == null) {
	    m = new HashMap(); pairs.put(p, m);
	}
	Pair pair = (Pair)m.get(o);
	if(pair == null) {
	    pair = new Pair(p, o); m.put(o, pair);
	}
	return pair;
    }


    public void addSmushListener(SmushListener l) {
	smushListeners.add(l);
    }
    public void removeSmushListener(SmushListener l) {
	smushListeners.remove(l);
    }


    public Object getSmushedNode(Object o) {
	return get(o);
    }

    /** Get the canonical node of the resource represented by a given node.
     */
    public Object get(Object node) {
	Group g = (Group)groupsBySubject.get(node);
	
	if(g == null) 
	    return node;
	else {
	    //if(dbg) p("GET "+node+" ====> "+g.canonicalNode);
	    return g.canonicalNode;
	}
    }


    public void add(Object subj, Object pred, Object obj, Object context) {
	if(subj == null || pred == null || obj == null || context == null)
	    throw new NullPointerException(subj+" "+pred+" "+obj+" "+context);

	//if(dbg) p("A ---- "+subj+" "+pred+" "+obj+" "+context+" ------ "+get(subj)+" "+get(pred)+" "+get(obj));

	unsmushed.add(subj, pred, obj, context);
	smushed.add(get(subj), get(pred), get(obj), context);

	if(pred.equals(MBOX) && Nodes.isNode(obj)) {
	    pred = SHA1SUM;
	    obj = new PlainLiteral(get_sha1_hex(Nodes.toString(obj)));
	}

	if(!ifps.contains(pred)) return;

	if(dbg) p("Smushing:\n    "+subj+" \n    "+pred+" \n    "+obj+
			   "\n  CURR\n    "+get(subj)+"\n    "+get(pred)+
			   "\n    "+get(obj)+"\n  IN\n    "+
			   context);

	Pair po = getPair(pred, obj);

	Group pairGroup = (Group)groupsByPair.get(po);

	if(pairGroup == null) {
	    Group g = (Group)groupsBySubject.get(subj);
	    if(g == null) {
		g = new Group(subj);
		groupsBySubject.put(subj, g);
		g.subjects.add(subj);
		if(dbg) p("--- new group for subject ---");
	    }

	    if(dbg) p("--- put pair in subject group ---");
	    groupsByPair.put(po, g);
	    g.pairs.add(po);

	} else {
	    Group g1 = (Group)groupsBySubject.get(subj);
	    if(g1 == null) {
		g1 = new Group(subj);
		groupsBySubject.put(subj, g1);
		g1.subjects.add(subj);
	    }
	    if(g1 == pairGroup) return;

	    Group g2 = pairGroup;

	    Object c1 = g1.canonicalNode, c2 = g2.canonicalNode;
	    if(cmp.compare(c1, c2) > 0) {
		Group tmp=g2; g2=g1; g1=tmp;
	    }
	    
	    // merge g2 into g1
	    if(dbg) p("MERGE "+g2.canonicalNode+" INTO "+g1.canonicalNode);
	    
	    for(Iterator i=g2.subjects.iterator(); i.hasNext();) {
		Object o = i.next();
		if(dbg) p("move over subject "+o);
		g1.subjects.add(o);
		groupsBySubject.put(o, g1);
	    }
	    for(Iterator i=g2.pairs.iterator(); i.hasNext();) {
		Object o = i.next();
		g1.pairs.add(o);
		groupsByPair.put(o, g1);
	    }

	    remove(g2.canonicalNode);
	    putAll(g2.subjects);
	    
	    if(dbg) p("All =============== CLEAN-o! ================= for "+g2.canonicalNode);

	    for(Iterator i = smushListeners.iterator(); i.hasNext();) {
		((SmushListener)i.next()).smushed(g2.canonicalNode, 
						  g1.canonicalNode);
	    }
	}
    }

    public void rm_1111(Object subj, Object pred, Object obj, Object context) {
	unsmushed.rm_1111(subj, pred, obj, context);
	smushed.rm_1111(get(subj), get(pred), get(obj), context);

	if(dbg) p("rm ? "+pred);

	if(pred.equals(MBOX) && Nodes.isNode(obj)) {
	    pred = SHA1SUM;
	    obj = new PlainLiteral(get_sha1_hex(Nodes.toString(obj)));
	}

	if(!ifps.contains(pred)) return;

	if(dbg) p("rm...");

	if(unsmushed.findN_111X_Iter(subj, pred, obj).hasNext()) {
	    // the same triple still exists in a different context,
	    // so the smushing isn't affected
	    if(dbg) p("still exists");
	    return;
	}

	Group g = (Group)groupsBySubject.get(subj);
	Pair po = getPair(pred, obj);

	if(!unsmushed.findN_X11A_Iter(pred, obj).hasNext()) {
	    // the PO was only connected to this one subject
	    if(dbg) p("just remove po from group");
	    g.pairs.remove(po);
	    return;
	}

	Set visited = new HashSet(), needToVisit = new HashSet();
	needToVisit.add(subj);

	while(!needToVisit.isEmpty()) {
	    Object x = needToVisit.iterator().next();
	    needToVisit.remove(x);
	    visited.add(x);

	    if(x instanceof Pair) {
		Pair p = (Pair)x;

		for(Iterator i=unsmushed.findN_X11A_Iter(p.p(), p.o());
		    i.hasNext();) {

		    Object s = i.next();
		    if(s.equals(subj)) {
			// we found a link between the PO and the subject:
			// the group wasn't split, and still contains
			// both subject and PO, so we need not do anything
			
			if(dbg) p("another link exists");
			return;
		    }

		    if(!visited.contains(s))
			needToVisit.add(s);
		}
		
	    } else {
		for(Iterator i=ifps.iterator(); i.hasNext();) {
		    Object xp = i.next();

		    for(Iterator j=unsmushed.findN_11XA_Iter(x, xp); 
			j.hasNext();) {

			Object xo = j.next();

			Pair p = getPair(xp, xo);
			
			if(!visited.contains(p))
			    needToVisit.add(p);
		    }
		}
	    }
	}

	Group g1 = new Group(null), g2 = new Group(null);

	if(dbg) p("SPLIT");
	
	for(Iterator i=visited.iterator(); i.hasNext();) {
	    Object x = i.next();
	    if(x instanceof Pair) {
		g1.pairs.add(x);
		groupsByPair.put(x, g1);
	    } else {
		if(g1.canonicalNode == null || 
		   cmp.compare(x, g1.canonicalNode) < 0) {
		    g1.canonicalNode = x;
		}
		g1.subjects.add(x);
		groupsBySubject.put(x, g1);
		if(dbg) p("g1: "+x);
	    }
	}

	Set complement = new HashSet(g.subjects);
	complement.addAll(g.pairs);
	complement.removeAll(visited);

	for(Iterator i=complement.iterator(); i.hasNext();) {
	    Object x = i.next();
	    if(x instanceof Pair) {
		g2.pairs.add(x);
		groupsByPair.put(x, g2);
	    } else {
		if(g2.canonicalNode == null || 
		   cmp.compare(x, g1.canonicalNode) < 0) {
		    g2.canonicalNode = x;
		}
		g2.subjects.add(x);
		groupsBySubject.put(x, g2);
		if(dbg) p("g2: "+x);
	    }
	}

	if(g1.canonicalNode==null || g2.canonicalNode==null)
	    throw new AssertionError(g1.canonicalNode+" "+g2.canonicalNode);

	if(dbg) p("now replacing");

	remove(g.canonicalNode);
	putAll(g1.subjects);
	putAll(g2.subjects);
    }


    private Object get(Object node, Object oldCanonical, Object newCanonical) {
	return node.equals(oldCanonical) ? newCanonical : get(node);
    }
    
    
    private void putAll(Set nodes) {
	for(Iterator i=nodes.iterator(); i.hasNext();)
	    put(i.next());
    }


    /** Remove all triples involving a particular (smushed) node
     *  from the smushed graph.
     */
    private void remove(Object node) {
	smushed.rm_1AAA(node);
	smushed.rm_A1AA(node);
	smushed.rm_AA1A(node);

	if(dbg) checkSanity();
    }


    /** Put all triples involving a particular (non-smushed) node
     *  from the unsmushed to the smushed graph.
     */
    private void put(Object node) {
	if(dbg) p("PUT");

	for(Iterator i0=unsmushed.findN_AAAX_Iter(); i0.hasNext();) {
	    Object c = i0.next();

	    if(dbg) p("a "+c);

	    Object subj = node;
	    for(Iterator i1=unsmushed.findN_1XA1_Iter(subj,c); i1.hasNext();) {
		Object pred = i1.next();

		if(dbg) p("b "+subj+" "+pred);

		for(Iterator i2=unsmushed.findN_11X1_Iter(subj, pred, c);
		    i2.hasNext();) {

		    Object obj = i2.next();

		    if(dbg) p("- put "+get(subj)+" "+get(pred)+" "+get(obj));
		    smushed.add(get(subj), get(pred), get(obj), c);
		}
	    }

	    Object pred = node;
	    for(Iterator i1=unsmushed.findN_X1A1_Iter(pred, c); i1.hasNext();) {
		subj = i1.next();

		for(Iterator i2=unsmushed.findN_11X1_Iter(subj, pred, c);
		    i2.hasNext();) {

		    Object obj = i2.next();
		    
		    if(dbg) p("- put "+get(subj)+" "+get(pred)+" "+get(obj));
		    smushed.add(get(subj), get(pred), get(obj), c);
		}
	    }

	    Object obj = node;
	    for(Iterator i1=unsmushed.findN_AX11_Iter(obj, c); i1.hasNext();) {
		pred = i1.next();

		for(Iterator i2=unsmushed.findN_X111_Iter(pred, obj, c);
		    i2.hasNext();) {

		    subj = i2.next();
		    
		    if(dbg) p("- put "+get(subj)+" "+get(pred)+" "+get(obj));
		    smushed.add(get(subj), get(pred), get(obj), c);
		}
	    }
	}
    }

    public void checkSanity() {
	for(Iterator i=groupsBySubject.values().iterator(); i.hasNext();) {
	    checkSanity((Group)i.next());
	}
    }

    public void checkSanity(Group g) {
	for(Iterator i=g.subjects.iterator(); i.hasNext();) {
	    Object o = i.next();
	    if(!o.equals(g.canonicalNode)) checkSanity(o);
	}
    }

    /** check that a node doesn't appear in the smushed graph
     */
    public void checkSanity(Object node) {
	if(smushed.findN_1AXA_Iter(node).hasNext())
	    throw new Error(node+" "+smushed.findN_1AXA_Iter(node).next());
	if(smushed.findN_XA1A_Iter(node).hasNext())
	    throw new Error(node+" "+smushed.findN_XA1A_Iter(node).next());

	if(findN_1AXA_Iter(node).hasNext())
	    throw new Error(node+" "+findN_1AXA_Iter(node).next());
	if(findN_XA1A_Iter(node).hasNext())
	    throw new Error(node+" "+findN_XA1A_Iter(node).next());
    }


    public static String get_sha1_hex(String s) {
	MessageDigest digest;

	try {
	    digest = MessageDigest.getInstance("SHA1");
	} catch(NoSuchAlgorithmException e) {
	    throw new Error(e);
	}

	digest.update(s.getBytes());
	byte[] b = digest.digest();

	String result = "";
	for(int i=0; i<b.length; i++) {
	    int val = b[i]<0 ? b[i]+256 : b[i];

	    if(val < 16) result += "0";
	    result += Integer.toHexString(val);
	}

	return result;
    }
}
