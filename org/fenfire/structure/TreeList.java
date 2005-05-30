// (c): Matti J. Katila

package org.fenfire.structure;
import org.fenfire.swamp.*;
import org.fenfire.vocab.*;


public class TreeList {
    static private void p(String s) { System.out.println("TreeList:: "+s); }

    /** Tries to find the root node of the structure. 
     *  Throws an error in case of fail.
     */
    static public Object getRoot(Graph g, Object nodeApplicant) {
	//p("getRoot: "+nodeApplicant);

	Object was = nodeApplicant;
	while (true) {
	    if (List.isListItem(g, nodeApplicant))
		nodeApplicant = List.getBeginOfList(g,nodeApplicant);
	    if (Tree.isSubTree(g, nodeApplicant))
		nodeApplicant = Tree.getParentTree(g,nodeApplicant);

	    if (was == nodeApplicant) break;
	    was = nodeApplicant;
	}

	if (Tree.hasRoot(g, nodeApplicant))
	    return Tree.getRoot(g, nodeApplicant);
	
	if (Tree.isRoot(g, nodeApplicant)) {
	    //p("is root! "+nodeApplicant);
	    return nodeApplicant;
	}
	throw new Error("No root found!");
    }

    static public class List {

	public static boolean isListItem(Graph g, Object n) {
	    return g.find1_X11(RDF.first, n) != null;
	}

	/** Give a list item and this routine returns a begin of list,
	 * i.e., not the first item but first node which points to
	 * first item.
	 */
	public static Object getBeginOfList(Graph g, Object n) {
	    if (!isListItem(g,n)) throw new Error("Not a list item! "+n);

	    n = g.find1_X11(RDF.first, n);
	    while (g.find1_X11(RDF.rest, n) != null)
		n = g.find1_X11(RDF.rest, n);
	    return n;
	}

    }

    static public class Tree {

	public static boolean isSubTree(Graph g, Object n) {
	    return g.find1_X11(TREE.subTree, n) != null;
	}
	public static Object getParentTree(Graph g, Object n) {
	    if (!isSubTree(g,n)) throw new Error("Not a sub tree! "+n);

	    return g.find1_X11(TREE.subTree, n);
	}

	public static boolean hasRoot(Graph g, Object n) {
	    return g.find1_X11(TREE.root, n) != null;
	}
	public static Object getRoot(Graph g, Object n) {
	    if (!hasRoot(g,n)) throw new Error("Node hasn't root! "+n);
	    return g.find1_X11(TREE.root, n);
	}


	public static boolean isRoot(Graph g, Object n) {
	    n = g.find1_11X(n, TREE.root);
	    if (n == null) return false;
	    if (g.find1_11X(n, RDF.first) != null || n == RDF.nil)
		return true;
	    return false;
	}


    }


}
