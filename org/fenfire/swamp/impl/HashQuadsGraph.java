// AUTOGENERATED By makeswamp.py - DO NOT EDIT.

    package org.fenfire.swamp.impl;
    import org.nongnu.navidoc.util.Obs;
    import org.fenfire.swamp.*;

    import java.util.HashMap;
    import java.util.Set;
    import java.util.HashSet;
    import java.util.ArrayList;
    import java.util.Iterator;

    /** An RDF Graph implemented by HashMaps.
     * Relatively inefficient but a basic implementation
     * that can be used as a reference.
     */
    public class HashQuadsGraph extends AbstractQuadsGraph {

	private HashMap ind_0123 = new HashMap();
	private HashMap ind_0213 = new HashMap();
	private HashMap ind_1203 = new HashMap();
	private HashMap ind_1023 = new HashMap();
	private HashMap ind_0132 = new HashMap();
	private HashMap ind_0231 = new HashMap();
	private HashMap ind_1230 = new HashMap();
	private HashMap ind_0312 = new HashMap();
	private HashMap ind_3210 = new HashMap();
	private HashMap ind_3012 = new HashMap();
	private HashMap ind_1302 = new HashMap();
	

	private HashSet emptySet = new HashSet();
	private StdObserver observer = new StdObserver();

	private final void rm_ind(HashMap ind, Object o1, Object o2,
                                  Object o3, Object o4) {
	    HashMap m2 = (HashMap)ind.get(o1);
	    if(m2 == null) return;
	    HashMap m3 = (HashMap)m2.get(o2);
	    if(m3 == null) return;
	    HashSet s = (HashSet)m3.get(o3);
	    if(s == null) return;
	    s.remove(o4);
	}

	private final void add_ind(HashMap ind, Object o1, Object o2,
                                   Object o3, Object o4) {
	    HashMap m2 = (HashMap)ind.get(o1);
	    if(m2 == null) {
		m2 = new HashMap();
		ind.put(o1, m2);
	    }
	    HashMap m3 = (HashMap)m2.get(o2);
	    if(m3 == null) {
		m3 = new HashMap();
		m2.put(o2, m3);
	    }
	    HashSet s = (HashSet)m3.get(o3);
	    if(s == null) {
		s = new HashSet();
		m3.put(o3, s);
	    }
	    s.add(o4);
	}



	public boolean contains(Object e0, Object e1, Object e2, Object e3, Obs o) {
	    if(o != null) observer.addObs(e0, e1, e2, o);

	    HashMap hash1 = (HashMap) ind_0123.get(e0);
	    if(hash1 == null) return false; 
	
	    HashMap hash2 = (HashMap) hash1.get(e1);
	    if(hash2 == null) return false; 

	    HashSet hash3 = (HashSet) hash2.get(e2);
	    if(hash1 == null) return false; 

	    HashSet resSet = hash3;
	    return resSet.contains(e3);
	    
	}

	//////////////////
	// Finds

	public Object find1_111X(Object e0,Object e1,Object e2,Obs obs){
if(obs != null) observer.addObs(e0, e1, e2, obs);

		    HashMap hash1 = (HashMap) ind_0123.get(e0);
		    if(hash1 == null) return null; 
		
		    HashMap hash2 = (HashMap) hash1.get(e1);
		    if(hash2 == null) return null; 
		
		    HashSet hash3 = (HashSet) hash2.get(e2);
		    if(hash3 == null) return null; 
		
			HashSet resSet = hash3;
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(e0,e1,e2,null);
	    }

public Object find1_11X1(Object e0,Object e1,Object e3,Obs obs){
if(obs != null) observer.addObs(e0, e1, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_0132.get(e0);
		    if(hash1 == null) return null; 
		
		    HashMap hash2 = (HashMap) hash1.get(e1);
		    if(hash2 == null) return null; 
		
		    HashSet hash3 = (HashSet) hash2.get(e3);
		    if(hash3 == null) return null; 
		
			HashSet resSet = hash3;
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(e0,e1,null,e3);
	    }

public Object find1_1X11(Object e0,Object e2,Object e3,Obs obs){
if(obs != null) observer.addObs(e0, observer.WILDCARD, e2, obs);

		    HashMap hash1 = (HashMap) ind_0231.get(e0);
		    if(hash1 == null) return null; 
		
		    HashMap hash2 = (HashMap) hash1.get(e2);
		    if(hash2 == null) return null; 
		
		    HashSet hash3 = (HashSet) hash2.get(e3);
		    if(hash3 == null) return null; 
		
			HashSet resSet = hash3;
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(e0,null,e2,e3);
	    }

public Object find1_X111(Object e1,Object e2,Object e3,Obs obs){
if(obs != null) observer.addObs(observer.WILDCARD, e1, e2, obs);

		    HashMap hash1 = (HashMap) ind_1230.get(e1);
		    if(hash1 == null) return null; 
		
		    HashMap hash2 = (HashMap) hash1.get(e2);
		    if(hash2 == null) return null; 
		
		    HashSet hash3 = (HashSet) hash2.get(e3);
		    if(hash3 == null) return null; 
		
			HashSet resSet = hash3;
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(null,e1,e2,e3);
	    }

public Object find1_11XA(Object e0,Object e1,Obs obs){
if(obs != null) observer.addObs(e0, e1, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_0123.get(e0);
		    if(hash1 == null) return null; 
		
		    HashMap hash2 = (HashMap) hash1.get(e1);
		    if(hash2 == null) return null; 
		
			Set resSet = hash2.keySet();
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(e0,e1,null,null);
	    }

public Object find1_1X1A(Object e0,Object e2,Obs obs){
if(obs != null) observer.addObs(e0, observer.WILDCARD, e2, obs);

		    HashMap hash1 = (HashMap) ind_0213.get(e0);
		    if(hash1 == null) return null; 
		
		    HashMap hash2 = (HashMap) hash1.get(e2);
		    if(hash2 == null) return null; 
		
			Set resSet = hash2.keySet();
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(e0,null,e2,null);
	    }

public Object find1_X11A(Object e1,Object e2,Obs obs){
if(obs != null) observer.addObs(observer.WILDCARD, e1, e2, obs);

		    HashMap hash1 = (HashMap) ind_1203.get(e1);
		    if(hash1 == null) return null; 
		
		    HashMap hash2 = (HashMap) hash1.get(e2);
		    if(hash2 == null) return null; 
		
			Set resSet = hash2.keySet();
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(null,e1,e2,null);
	    }

public Object find1_11AX(Object e0,Object e1,Obs obs){
if(obs != null) observer.addObs(e0, e1, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_0132.get(e0);
		    if(hash1 == null) return null; 
		
		    HashMap hash2 = (HashMap) hash1.get(e1);
		    if(hash2 == null) return null; 
		
			Set resSet = hash2.keySet();
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(e0,e1,null,null);
	    }

public Object find1_1XAA(Object e0,Obs obs){
if(obs != null) observer.addObs(e0, observer.WILDCARD, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_0123.get(e0);
		    if(hash1 == null) return null; 
		
			Set resSet = hash1.keySet();
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(e0,null,null,null);
	    }

public Object find1_1XA1(Object e0,Object e3,Obs obs){
if(obs != null) observer.addObs(e0, observer.WILDCARD, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_0312.get(e0);
		    if(hash1 == null) return null; 
		
		    HashMap hash2 = (HashMap) hash1.get(e3);
		    if(hash2 == null) return null; 
		
			Set resSet = hash2.keySet();
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(e0,null,null,e3);
	    }

public Object find1_XAAA(Obs obs){
if(obs != null) observer.addObs(observer.WILDCARD, observer.WILDCARD, observer.WILDCARD, obs);

			Set resSet = ind_0123.keySet();
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(null,null,null,null);
	    }

public Object find1_XAA1(Object e3,Obs obs){
if(obs != null) observer.addObs(observer.WILDCARD, observer.WILDCARD, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_3012.get(e3);
		    if(hash1 == null) return null; 
		
			Set resSet = hash1.keySet();
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(null,null,null,e3);
	    }

public Object find1_X1AA(Object e1,Obs obs){
if(obs != null) observer.addObs(observer.WILDCARD, e1, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_1023.get(e1);
		    if(hash1 == null) return null; 
		
			Set resSet = hash1.keySet();
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(null,e1,null,null);
	    }

public Object find1_X1A1(Object e1,Object e3,Obs obs){
if(obs != null) observer.addObs(observer.WILDCARD, e1, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_1302.get(e1);
		    if(hash1 == null) return null; 
		
		    HashMap hash2 = (HashMap) hash1.get(e3);
		    if(hash2 == null) return null; 
		
			Set resSet = hash2.keySet();
		    
		if(resSet.size() == 0) return null;
		if(resSet.size() == 1) return resSet.iterator().next();
		throw new NotUniqueError(null,e1,null,e3);
	    }

public Iterator findN_111X_Iter(Object e0,Object e1,Object e2,Obs obs){
if(obs != null) observer.addObs(e0, e1, e2, obs);

		    HashMap hash1 = (HashMap) ind_0123.get(e0);
		    if(hash1 == null) return emptySet.iterator(); 
		
		    HashMap hash2 = (HashMap) hash1.get(e1);
		    if(hash2 == null) return emptySet.iterator(); 
		
		    HashSet hash3 = (HashSet) hash2.get(e2);
		    if(hash3 == null) return emptySet.iterator(); 
		
			HashSet resSet = hash3;
		    return resSet.iterator();
}

public Iterator findN_11X1_Iter(Object e0,Object e1,Object e3,Obs obs){
if(obs != null) observer.addObs(e0, e1, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_0132.get(e0);
		    if(hash1 == null) return emptySet.iterator(); 
		
		    HashMap hash2 = (HashMap) hash1.get(e1);
		    if(hash2 == null) return emptySet.iterator(); 
		
		    HashSet hash3 = (HashSet) hash2.get(e3);
		    if(hash3 == null) return emptySet.iterator(); 
		
			HashSet resSet = hash3;
		    return resSet.iterator();
}

public Iterator findN_1X11_Iter(Object e0,Object e2,Object e3,Obs obs){
if(obs != null) observer.addObs(e0, observer.WILDCARD, e2, obs);

		    HashMap hash1 = (HashMap) ind_0231.get(e0);
		    if(hash1 == null) return emptySet.iterator(); 
		
		    HashMap hash2 = (HashMap) hash1.get(e2);
		    if(hash2 == null) return emptySet.iterator(); 
		
		    HashSet hash3 = (HashSet) hash2.get(e3);
		    if(hash3 == null) return emptySet.iterator(); 
		
			HashSet resSet = hash3;
		    return resSet.iterator();
}

public Iterator findN_X111_Iter(Object e1,Object e2,Object e3,Obs obs){
if(obs != null) observer.addObs(observer.WILDCARD, e1, e2, obs);

		    HashMap hash1 = (HashMap) ind_1230.get(e1);
		    if(hash1 == null) return emptySet.iterator(); 
		
		    HashMap hash2 = (HashMap) hash1.get(e2);
		    if(hash2 == null) return emptySet.iterator(); 
		
		    HashSet hash3 = (HashSet) hash2.get(e3);
		    if(hash3 == null) return emptySet.iterator(); 
		
			HashSet resSet = hash3;
		    return resSet.iterator();
}

public Iterator findN_11XA_Iter(Object e0,Object e1,Obs obs){
if(obs != null) observer.addObs(e0, e1, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_0123.get(e0);
		    if(hash1 == null) return emptySet.iterator(); 
		
		    HashMap hash2 = (HashMap) hash1.get(e1);
		    if(hash2 == null) return emptySet.iterator(); 
		
			Set resSet = hash2.keySet();
		    return resSet.iterator();
}

public Iterator findN_1X1A_Iter(Object e0,Object e2,Obs obs){
if(obs != null) observer.addObs(e0, observer.WILDCARD, e2, obs);

		    HashMap hash1 = (HashMap) ind_0213.get(e0);
		    if(hash1 == null) return emptySet.iterator(); 
		
		    HashMap hash2 = (HashMap) hash1.get(e2);
		    if(hash2 == null) return emptySet.iterator(); 
		
			Set resSet = hash2.keySet();
		    return resSet.iterator();
}

public Iterator findN_X11A_Iter(Object e1,Object e2,Obs obs){
if(obs != null) observer.addObs(observer.WILDCARD, e1, e2, obs);

		    HashMap hash1 = (HashMap) ind_1203.get(e1);
		    if(hash1 == null) return emptySet.iterator(); 
		
		    HashMap hash2 = (HashMap) hash1.get(e2);
		    if(hash2 == null) return emptySet.iterator(); 
		
			Set resSet = hash2.keySet();
		    return resSet.iterator();
}

public Iterator findN_11AX_Iter(Object e0,Object e1,Obs obs){
if(obs != null) observer.addObs(e0, e1, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_0132.get(e0);
		    if(hash1 == null) return emptySet.iterator(); 
		
		    HashMap hash2 = (HashMap) hash1.get(e1);
		    if(hash2 == null) return emptySet.iterator(); 
		
			Set resSet = hash2.keySet();
		    return resSet.iterator();
}

public Iterator findN_1XAA_Iter(Object e0,Obs obs){
if(obs != null) observer.addObs(e0, observer.WILDCARD, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_0123.get(e0);
		    if(hash1 == null) return emptySet.iterator(); 
		
			Set resSet = hash1.keySet();
		    return resSet.iterator();
}

public Iterator findN_1XA1_Iter(Object e0,Object e3,Obs obs){
if(obs != null) observer.addObs(e0, observer.WILDCARD, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_0312.get(e0);
		    if(hash1 == null) return emptySet.iterator(); 
		
		    HashMap hash2 = (HashMap) hash1.get(e3);
		    if(hash2 == null) return emptySet.iterator(); 
		
			Set resSet = hash2.keySet();
		    return resSet.iterator();
}

public Iterator findN_XAAA_Iter(Obs obs){
if(obs != null) observer.addObs(observer.WILDCARD, observer.WILDCARD, observer.WILDCARD, obs);

			Set resSet = ind_0123.keySet();
		    return resSet.iterator();
}

public Iterator findN_XAA1_Iter(Object e3,Obs obs){
if(obs != null) observer.addObs(observer.WILDCARD, observer.WILDCARD, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_3012.get(e3);
		    if(hash1 == null) return emptySet.iterator(); 
		
			Set resSet = hash1.keySet();
		    return resSet.iterator();
}

public Iterator findN_X1AA_Iter(Object e1,Obs obs){
if(obs != null) observer.addObs(observer.WILDCARD, e1, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_1023.get(e1);
		    if(hash1 == null) return emptySet.iterator(); 
		
			Set resSet = hash1.keySet();
		    return resSet.iterator();
}

public Iterator findN_X1A1_Iter(Object e1,Object e3,Obs obs){
if(obs != null) observer.addObs(observer.WILDCARD, e1, observer.WILDCARD, obs);

		    HashMap hash1 = (HashMap) ind_1302.get(e1);
		    if(hash1 == null) return emptySet.iterator(); 
		
		    HashMap hash2 = (HashMap) hash1.get(e3);
		    if(hash2 == null) return emptySet.iterator(); 
		
			Set resSet = hash2.keySet();
		    return resSet.iterator();
}




	// --------------------------------
	// Methods which modify the structure by calling the single-triple
	// methods below.
	//


	// --------------------------------
	// Methods which actually modify the structure
	//

	public void rm_1111(Object e0, Object e1, Object e2, Object e3) {
            checkNode(e0); checkNode(e1); checkNodeOrLiteral(e2);
	    
	    rm_ind(ind_0123, e0, e1, e2, e3);
	
	    rm_ind(ind_0213, e0, e2, e1, e3);
	
	    rm_ind(ind_1203, e1, e2, e0, e3);
	
	    rm_ind(ind_1023, e1, e0, e2, e3);
	
	    rm_ind(ind_0132, e0, e1, e3, e2);
	
	    rm_ind(ind_0231, e0, e2, e3, e1);
	
	    rm_ind(ind_1230, e1, e2, e3, e0);
	
	    rm_ind(ind_0312, e0, e3, e1, e2);
	
	    rm_ind(ind_3210, e3, e2, e1, e0);
	
	    rm_ind(ind_3012, e3, e0, e1, e2);
	
	    rm_ind(ind_1302, e1, e3, e0, e2);
	

	    observer.triggerObs(-1, e0, e1, e2);
	}

	public void add(Object e0, Object e1, Object e2, Object e3) {
            checkNode(e0); checkNode(e1); checkNodeOrLiteral(e2);
	    
	    add_ind(ind_0123, e0, e1, e2, e3);
	
	    add_ind(ind_0213, e0, e2, e1, e3);
	
	    add_ind(ind_1203, e1, e2, e0, e3);
	
	    add_ind(ind_1023, e1, e0, e2, e3);
	
	    add_ind(ind_0132, e0, e1, e3, e2);
	
	    add_ind(ind_0231, e0, e2, e3, e1);
	
	    add_ind(ind_1230, e1, e2, e3, e0);
	
	    add_ind(ind_0312, e0, e3, e1, e2);
	
	    add_ind(ind_3210, e3, e2, e1, e0);
	
	    add_ind(ind_3012, e3, e0, e1, e2);
	
	    add_ind(ind_1302, e1, e3, e0, e2);
	

	    observer.triggerObs(1, e0, e1, e2);

            if (!contains(e0, e1,e2,e3))
                throw new Error("did not add"); 
	}

        protected void checkNode(Object node) {
            if(!Nodes.isNode(node))
                throw new IllegalArgumentException("Not a node: "+node);
        }

        protected void checkNodeOrLiteral(Object node) {
            if(!Nodes.isNode(node) && !(node instanceof Literal))
                throw new IllegalArgumentException("Not a node or literal: "+node);
        }

    }




    