// (c): Matti J. Katila

package org.fenfire.structure;
import org.fenfire.swamp.*;
import org.fenfire.vocab.*;
import java.util.Iterator;

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


    static public Object createNewIfNeeded(Graph g, Object root, Object newApplicant) {

	if (Tree.isRoot(g, newApplicant)) {
	    Object firstInList = g.find1_11X(newApplicant, TREE.root);
	    if (firstInList == RDF.nil) {
		g.rm_111(newApplicant, TREE.root, RDF.nil);
		Object n = Nodes.N();
		g.add(root, TREE.root, n);
		Object n_item = Nodes.N();
		g.add(n, RDF.first, n_item);
		g.add(n, RDF.rest, RDF.nil);
		newApplicant = n_item;
	    }
	    else if (List.hasListItem(g, firstInList))
		newApplicant = g.find1_11X(firstInList, RDF.first);
	    else throw new Error(" not yet impl. ");
	}
	return newApplicant;
    }


    static public boolean isTreeLeaf(Graph g, Object n) {
	if (List.isListItem(g, n) && !Tree.isLeaf(g, n))
	    return true;
	return false;
    }

    static public Object addNewItemBefore(Graph g, Object n) {
	throw new Error("not yet. impl.");
    }

    static private Object getFirst(Graph g, Object root) {
	if (!Tree.isRoot(g,root)) throw new Error("Not a root!");

	Object i = g.find1_11X(root, TREE.root);
	if (i == RDF.nil) return null; //throw new Error("Tree list is empty!");
	while (true) {
	    Object was = i;
	    if (Tree.hasSubTree(g, i))
		i = g.find1_11X(i, TREE.subTree);
	    if (List.hasListItem(g, i))
		i = g.find1_11X(i, RDF.first);
	    if (i == was) break;
	}

	if (!List.isListItem(g, i))
	    throw new Error("no list item found!");

	return i;
    } 

    static public Iterator iterator(final Graph g, final Object root) {
	return new Iterator(){
		Object current = root;
		public boolean hasNext() {
		    if (current == null) return false;
		    
		    if (Tree.isRoot(g, current)) {
			if (g.find1_11X(current, TREE.root) == RDF.nil)
			    return false;
			return true;
		    }
		    if (!List.isListItem(g, current ))
			throw new Error("Works only for list items!");

		    Object cur = current;
		    while (true) {
			if (List.hasNextListItem(g, cur)) return true;
		    
			if (List.isListItem(g, cur)) {
			    Object l = List.getBeginOfList(g, cur);
			    if (Tree.isSubTree(g, l))
				cur = g.find1_X11(TREE.subTree, l);
			    else return false;
			}
		    }
		}

		public Object next() {
		    if (current == null) 
			throw new Error("argh, null current!");

		    if (Tree.isRoot(g, current)) {
			current = TreeList.getFirst(g,root);
			return current;
		    }

		    Object cur = current;
		    while (true) {
			if (List.hasNextListItem(g, cur)) {
			    current = g.find1_X11(RDF.first, cur);
			    current = g.find1_11X(cur, RDF.rest);
			    current = g.find1_11X(cur, RDF.first);
			    
			    // then go to leaf!
			    while (true) {
				Object was = current;
				if (Tree.isSubTree(g, current))
				    current = g.find1_11X(current, 
							  TREE.subTree);
				else break;
				if (List.isListItem(g, current))
				    current = g.find1_11X(current, RDF.first);
				else break;
				if (was == current) break;
			    }
			    
			    return current;
			}
		    
			if (List.isListItem(g, cur)) {
			    Object l = List.getBeginOfList(g, cur);
			    if (Tree.isSubTree(g, l))
				cur = g.find1_X11(TREE.subTree, l);
			    else throw new Error("argh!");
			}
		    }
		}
		public void remove() {
		    throw new Error("Can't remove! Illegal operation!");
		}
	    };
    }


    static public class List {

	public static boolean hasListItem(Graph g, Object n) {
	    return g.find1_11X(n, RDF.first) != null;
	}
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

	public static boolean hasNextListItem(Graph g, Object n) {
	    if (!isListItem(g, n)) throw new Error("Not a list item!");

	    Object l = g.find1_X11(RDF.first, n);
	    l = g.find1_11X(l, RDF.rest);
	    if (l == null) throw new Error("Nil is lost!");
	    else if (l == RDF.nil) return false;
	    return g.find1_11X(l, RDF.first) != null;
	}	

    }


    static public class Tree {

	public static boolean hasSubTree(Graph g, Object n) {
	    return g.find1_11X(n, TREE.subTree) != null;
	}
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

	public static boolean isLeaf(Graph g, Object n) {
	    return g.find1_11X(n, TREE.subTree) != null;
	}
    }


}
