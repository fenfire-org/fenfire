// AUTOGENERATED By makeswamp.py - DO NOT EDIT.

    package org.fenfire.swamp.impl;
    import org.nongnu.navidoc.util.Obs;
    import org.fenfire.swamp.*;
    import java.util.*;

    public class HashGraph extends AbstractGraph {
	private StdObserver observer = new StdObserver();

        public void startUpdate() { observer.startUpdate(); }
        public void endUpdate() { observer.endUpdate(); }

        PairMap map_X11 = new PairMap();
public Object find1_X11(Object pred, Object obj, Obs obs) {
    if(obs != null)
        observer.addObs(observer.WILDCARD, pred, obj, obs);

    try {
        return map_X11.get(pred, obj);
    } catch(PairMap.NotUniqueException _) {
        throw new NotUniqueError(null, pred, obj);
    }
}
public Iterator findN_X11_Iter(Object pred, Object obj, Obs obs){
    if(obs != null)
        observer.addObs(observer.WILDCARD, pred, obj, obs);

    return map_X11.getIter(pred, obj);
}
PairMap map_1X1 = new PairMap();
public Object find1_1X1(Object subj, Object obj, Obs obs) {
    if(obs != null)
        observer.addObs(subj, observer.WILDCARD, obj, obs);

    try {
        return map_1X1.get(subj, obj);
    } catch(PairMap.NotUniqueException _) {
        throw new NotUniqueError(subj, null, obj);
    }
}
public Iterator findN_1X1_Iter(Object subj, Object obj, Obs obs){
    if(obs != null)
        observer.addObs(subj, observer.WILDCARD, obj, obs);

    return map_1X1.getIter(subj, obj);
}
PairMap map_11X = new PairMap();
public Object find1_11X(Object subj, Object pred, Obs obs) {
    if(obs != null)
        observer.addObs(subj, pred, observer.WILDCARD, obs);

    try {
        return map_11X.get(subj, pred);
    } catch(PairMap.NotUniqueException _) {
        throw new NotUniqueError(subj, pred, null);
    }
}
public Iterator findN_11X_Iter(Object subj, Object pred, Obs obs){
    if(obs != null)
        observer.addObs(subj, pred, observer.WILDCARD, obs);

    return map_11X.getIter(subj, pred);
}
PairMap map_X1A = new PairMap();
public Object find1_X1A(Object pred, Obs obs) {
    if(obs != null)
        observer.addObs(observer.WILDCARD, pred, observer.WILDCARD, obs);

    try {
        return map_X1A.get(pred, null);
    } catch(PairMap.NotUniqueException _) {
        throw new NotUniqueError(null, pred, null);
    }
}
public Iterator findN_X1A_Iter(Object pred, Obs obs){
    if(obs != null)
        observer.addObs(observer.WILDCARD, pred, observer.WILDCARD, obs);

    return map_X1A.getIter(pred, null);
}
PairMap map_1XA = new PairMap();
public Object find1_1XA(Object subj, Obs obs) {
    if(obs != null)
        observer.addObs(subj, observer.WILDCARD, observer.WILDCARD, obs);

    try {
        return map_1XA.get(subj, null);
    } catch(PairMap.NotUniqueException _) {
        throw new NotUniqueError(subj, null, null);
    }
}
public Iterator findN_1XA_Iter(Object subj, Obs obs){
    if(obs != null)
        observer.addObs(subj, observer.WILDCARD, observer.WILDCARD, obs);

    return map_1XA.getIter(subj, null);
}
PairMap map_1AX = new PairMap();
public Object find1_1AX(Object subj, Obs obs) {
    if(obs != null)
        observer.addObs(subj, observer.WILDCARD, observer.WILDCARD, obs);

    try {
        return map_1AX.get(subj, null);
    } catch(PairMap.NotUniqueException _) {
        throw new NotUniqueError(subj, null, null);
    }
}
public Iterator findN_1AX_Iter(Object subj, Obs obs){
    if(obs != null)
        observer.addObs(subj, observer.WILDCARD, observer.WILDCARD, obs);

    return map_1AX.getIter(subj, null);
}
PairMap map_XA1 = new PairMap();
public Object find1_XA1(Object obj, Obs obs) {
    if(obs != null)
        observer.addObs(observer.WILDCARD, observer.WILDCARD, obj, obs);

    try {
        return map_XA1.get(obj, null);
    } catch(PairMap.NotUniqueException _) {
        throw new NotUniqueError(null, null, obj);
    }
}
public Iterator findN_XA1_Iter(Object obj, Obs obs){
    if(obs != null)
        observer.addObs(observer.WILDCARD, observer.WILDCARD, obj, obs);

    return map_XA1.getIter(obj, null);
}
PairMap map_AX1 = new PairMap();
public Object find1_AX1(Object obj, Obs obs) {
    if(obs != null)
        observer.addObs(observer.WILDCARD, observer.WILDCARD, obj, obs);

    try {
        return map_AX1.get(obj, null);
    } catch(PairMap.NotUniqueException _) {
        throw new NotUniqueError(null, null, obj);
    }
}
public Iterator findN_AX1_Iter(Object obj, Obs obs){
    if(obs != null)
        observer.addObs(observer.WILDCARD, observer.WILDCARD, obj, obs);

    return map_AX1.getIter(obj, null);
}
PairMap map_A1X = new PairMap();
public Object find1_A1X(Object pred, Obs obs) {
    if(obs != null)
        observer.addObs(observer.WILDCARD, pred, observer.WILDCARD, obs);

    try {
        return map_A1X.get(pred, null);
    } catch(PairMap.NotUniqueException _) {
        throw new NotUniqueError(null, pred, null);
    }
}
public Iterator findN_A1X_Iter(Object pred, Obs obs){
    if(obs != null)
        observer.addObs(observer.WILDCARD, pred, observer.WILDCARD, obs);

    return map_A1X.getIter(pred, null);
}
PairMap map_XAA = new PairMap();
public Object find1_XAA(Obs obs) {
    if(obs != null)
        observer.addObs(observer.WILDCARD, observer.WILDCARD, observer.WILDCARD, obs);

    try {
        return map_XAA.get(null, null);
    } catch(PairMap.NotUniqueException _) {
        throw new NotUniqueError(null, null, null);
    }
}
public Iterator findN_XAA_Iter(Obs obs){
    if(obs != null)
        observer.addObs(observer.WILDCARD, observer.WILDCARD, observer.WILDCARD, obs);

    return map_XAA.getIter(null, null);
}
PairMap map_AXA = new PairMap();
public Object find1_AXA(Obs obs) {
    if(obs != null)
        observer.addObs(observer.WILDCARD, observer.WILDCARD, observer.WILDCARD, obs);

    try {
        return map_AXA.get(null, null);
    } catch(PairMap.NotUniqueException _) {
        throw new NotUniqueError(null, null, null);
    }
}
public Iterator findN_AXA_Iter(Obs obs){
    if(obs != null)
        observer.addObs(observer.WILDCARD, observer.WILDCARD, observer.WILDCARD, obs);

    return map_AXA.getIter(null, null);
}
PairMap map_AAX = new PairMap();
public Object find1_AAX(Obs obs) {
    if(obs != null)
        observer.addObs(observer.WILDCARD, observer.WILDCARD, observer.WILDCARD, obs);

    try {
        return map_AAX.get(null, null);
    } catch(PairMap.NotUniqueException _) {
        throw new NotUniqueError(null, null, null);
    }
}
public Iterator findN_AAX_Iter(Obs obs){
    if(obs != null)
        observer.addObs(observer.WILDCARD, observer.WILDCARD, observer.WILDCARD, obs);

    return map_AAX.getIter(null, null);
}
public void rm_111(Object subj, Object pred, Object obj) {
    checkNode(subj); checkNode(pred); checkNodeOrLiteral(obj);
    Iterator iter;

    map_X11.rm(pred, obj, subj);

    map_1X1.rm(subj, obj, pred);

    map_11X.rm(subj, pred, obj);

    iter = findN_11X_Iter(subj, pred);
    if(!iter.hasNext() ||
       (iter.next().equals(obj) && !iter.hasNext())) {
        map_X1A.rm(pred, null, subj);
    }

    iter = findN_11X_Iter(subj, pred);
    if(!iter.hasNext() ||
       (iter.next().equals(obj) && !iter.hasNext())) {
        map_1XA.rm(subj, null, pred);
    }

    iter = findN_1X1_Iter(subj, obj);
    if(!iter.hasNext() ||
       (iter.next().equals(pred) && !iter.hasNext())) {
        map_1AX.rm(subj, null, obj);
    }

    iter = findN_1X1_Iter(subj, obj);
    if(!iter.hasNext() ||
       (iter.next().equals(pred) && !iter.hasNext())) {
        map_XA1.rm(obj, null, subj);
    }

    iter = findN_X11_Iter(pred, obj);
    if(!iter.hasNext() ||
       (iter.next().equals(subj) && !iter.hasNext())) {
        map_AX1.rm(obj, null, pred);
    }

    iter = findN_X11_Iter(pred, obj);
    if(!iter.hasNext() ||
       (iter.next().equals(subj) && !iter.hasNext())) {
        map_A1X.rm(pred, null, obj);
    }

    iter = findN_1XA_Iter(subj);
    if(!iter.hasNext() ||
       (iter.next().equals(pred) && !iter.hasNext())) {
    iter = findN_11X_Iter(subj, pred);
    if(!iter.hasNext() ||
       (iter.next().equals(obj) && !iter.hasNext())) {
        map_XAA.rm(null, null, subj);
    }
    }

    iter = findN_X1A_Iter(pred);
    if(!iter.hasNext() ||
       (iter.next().equals(subj) && !iter.hasNext())) {
    iter = findN_11X_Iter(subj, pred);
    if(!iter.hasNext() ||
       (iter.next().equals(obj) && !iter.hasNext())) {
        map_AXA.rm(null, null, pred);
    }
    }

    iter = findN_XA1_Iter(obj);
    if(!iter.hasNext() ||
       (iter.next().equals(subj) && !iter.hasNext())) {
    iter = findN_1X1_Iter(subj, obj);
    if(!iter.hasNext() ||
       (iter.next().equals(pred) && !iter.hasNext())) {
        map_AAX.rm(null, null, obj);
    }
    }
    
    observer.triggerObs(-1, subj, pred, obj);
}
public void add(Object subj, Object pred, Object obj) {
    checkNode(subj); checkNode(pred); checkNodeOrLiteral(obj);
    map_X11.add(pred, obj, subj);
    map_1X1.add(subj, obj, pred);
    map_11X.add(subj, pred, obj);
    map_X1A.add(pred, null, subj);
    map_1XA.add(subj, null, pred);
    map_1AX.add(subj, null, obj);
    map_XA1.add(obj, null, subj);
    map_AX1.add(obj, null, pred);
    map_A1X.add(pred, null, obj);
    map_XAA.add(null, null, subj);
    map_AXA.add(null, null, pred);
    map_AAX.add(null, null, obj);
    
    observer.triggerObs(1, subj, pred, obj);
}


	public boolean contains(Object subj, Object pred, Object obj, Obs o) {
	    if(o != null) observer.addObs(subj, pred, obj, o);
            return map_11X.contains(subj, pred, obj);
	}
    }
