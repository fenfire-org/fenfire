// AUTOGENERATED By makeswamp.py - DO NOT EDIT.

package org.fenfire.swamp.impl;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import java.util.Iterator;

public class StdObservedQuadsConstGraph implements QuadsConstGraph {
    QuadsConstGraph constgraph;
    Obs obs;

    public StdObservedQuadsConstGraph(QuadsConstGraph graph, Obs obs) {
	this.constgraph = graph;
	this.obs = obs;
    }

    public QuadsConstGraph getObservedConstGraph(Obs obs) {
	throw new Error("DoubleObs");
    }

    public void close() { }

    public Obs getObserver() {
	return obs;
    }

    public QuadsConstGraph getOriginalConstGraph() {
	return constgraph;
    }

    public Object getSmushedNode(Object o) {
        return constgraph.getSmushedNode(o);
    }

    public boolean contains(Object e0, Object e1, Object e2, Object e3) {
	return constgraph.contains(e0, e1, e2, e3, obs);
    }
    public boolean contains(Object e0, Object e1, Object e2,
                            Object e3, Obs o) {
	throw new Error("DoubleObs");
    }

    public Object find1_X11A(Object pred, Object obj) {
    return constgraph.find1_X11A(pred, obj, obs);
}
public Object find1_X11A(Object pred, Object obj, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_X11A_Iter(Object pred, Object obj) {
    return constgraph.findN_X11A_Iter(pred, obj, obs);
}
public Iterator findN_X11A_Iter(Object pred, Object obj, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_1X1A(Object subj, Object obj) {
    return constgraph.find1_1X1A(subj, obj, obs);
}
public Object find1_1X1A(Object subj, Object obj, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_1X1A_Iter(Object subj, Object obj) {
    return constgraph.findN_1X1A_Iter(subj, obj, obs);
}
public Iterator findN_1X1A_Iter(Object subj, Object obj, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_11XA(Object subj, Object pred) {
    return constgraph.find1_11XA(subj, pred, obs);
}
public Object find1_11XA(Object subj, Object pred, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_11XA_Iter(Object subj, Object pred) {
    return constgraph.findN_11XA_Iter(subj, pred, obs);
}
public Iterator findN_11XA_Iter(Object subj, Object pred, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_X1AA(Object pred) {
    return constgraph.find1_X1AA(pred, obs);
}
public Object find1_X1AA(Object pred, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_X1AA_Iter(Object pred) {
    return constgraph.findN_X1AA_Iter(pred, obs);
}
public Iterator findN_X1AA_Iter(Object pred, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_1XAA(Object subj) {
    return constgraph.find1_1XAA(subj, obs);
}
public Object find1_1XAA(Object subj, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_1XAA_Iter(Object subj) {
    return constgraph.findN_1XAA_Iter(subj, obs);
}
public Iterator findN_1XAA_Iter(Object subj, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_1AXA(Object subj) {
    return constgraph.find1_1AXA(subj, obs);
}
public Object find1_1AXA(Object subj, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_1AXA_Iter(Object subj) {
    return constgraph.findN_1AXA_Iter(subj, obs);
}
public Iterator findN_1AXA_Iter(Object subj, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_XA1A(Object obj) {
    return constgraph.find1_XA1A(obj, obs);
}
public Object find1_XA1A(Object obj, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_XA1A_Iter(Object obj) {
    return constgraph.findN_XA1A_Iter(obj, obs);
}
public Iterator findN_XA1A_Iter(Object obj, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_AX1A(Object obj) {
    return constgraph.find1_AX1A(obj, obs);
}
public Object find1_AX1A(Object obj, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_AX1A_Iter(Object obj) {
    return constgraph.findN_AX1A_Iter(obj, obs);
}
public Iterator findN_AX1A_Iter(Object obj, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_A1XA(Object pred) {
    return constgraph.find1_A1XA(pred, obs);
}
public Object find1_A1XA(Object pred, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_A1XA_Iter(Object pred) {
    return constgraph.findN_A1XA_Iter(pred, obs);
}
public Iterator findN_A1XA_Iter(Object pred, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_XAAA() {
    return constgraph.find1_XAAA(obs);
}
public Object find1_XAAA(Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_XAAA_Iter() {
    return constgraph.findN_XAAA_Iter(obs);
}
public Iterator findN_XAAA_Iter(Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_AXAA() {
    return constgraph.find1_AXAA(obs);
}
public Object find1_AXAA(Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_AXAA_Iter() {
    return constgraph.findN_AXAA_Iter(obs);
}
public Iterator findN_AXAA_Iter(Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_AAXA() {
    return constgraph.find1_AAXA(obs);
}
public Object find1_AAXA(Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_AAXA_Iter() {
    return constgraph.findN_AAXA_Iter(obs);
}
public Iterator findN_AAXA_Iter(Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_X111(Object pred, Object obj, Object context) {
    return constgraph.find1_X111(pred, obj, context, obs);
}
public Object find1_X111(Object pred, Object obj, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_X111_Iter(Object pred, Object obj, Object context) {
    return constgraph.findN_X111_Iter(pred, obj, context, obs);
}
public Iterator findN_X111_Iter(Object pred, Object obj, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_1X11(Object subj, Object obj, Object context) {
    return constgraph.find1_1X11(subj, obj, context, obs);
}
public Object find1_1X11(Object subj, Object obj, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_1X11_Iter(Object subj, Object obj, Object context) {
    return constgraph.findN_1X11_Iter(subj, obj, context, obs);
}
public Iterator findN_1X11_Iter(Object subj, Object obj, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_11X1(Object subj, Object pred, Object context) {
    return constgraph.find1_11X1(subj, pred, context, obs);
}
public Object find1_11X1(Object subj, Object pred, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_11X1_Iter(Object subj, Object pred, Object context) {
    return constgraph.findN_11X1_Iter(subj, pred, context, obs);
}
public Iterator findN_11X1_Iter(Object subj, Object pred, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_X1A1(Object pred, Object context) {
    return constgraph.find1_X1A1(pred, context, obs);
}
public Object find1_X1A1(Object pred, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_X1A1_Iter(Object pred, Object context) {
    return constgraph.findN_X1A1_Iter(pred, context, obs);
}
public Iterator findN_X1A1_Iter(Object pred, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_1XA1(Object subj, Object context) {
    return constgraph.find1_1XA1(subj, context, obs);
}
public Object find1_1XA1(Object subj, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_1XA1_Iter(Object subj, Object context) {
    return constgraph.findN_1XA1_Iter(subj, context, obs);
}
public Iterator findN_1XA1_Iter(Object subj, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_1AX1(Object subj, Object context) {
    return constgraph.find1_1AX1(subj, context, obs);
}
public Object find1_1AX1(Object subj, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_1AX1_Iter(Object subj, Object context) {
    return constgraph.findN_1AX1_Iter(subj, context, obs);
}
public Iterator findN_1AX1_Iter(Object subj, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_XA11(Object obj, Object context) {
    return constgraph.find1_XA11(obj, context, obs);
}
public Object find1_XA11(Object obj, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_XA11_Iter(Object obj, Object context) {
    return constgraph.findN_XA11_Iter(obj, context, obs);
}
public Iterator findN_XA11_Iter(Object obj, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_AX11(Object obj, Object context) {
    return constgraph.find1_AX11(obj, context, obs);
}
public Object find1_AX11(Object obj, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_AX11_Iter(Object obj, Object context) {
    return constgraph.findN_AX11_Iter(obj, context, obs);
}
public Iterator findN_AX11_Iter(Object obj, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_A1X1(Object pred, Object context) {
    return constgraph.find1_A1X1(pred, context, obs);
}
public Object find1_A1X1(Object pred, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_A1X1_Iter(Object pred, Object context) {
    return constgraph.findN_A1X1_Iter(pred, context, obs);
}
public Iterator findN_A1X1_Iter(Object pred, Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_XAA1(Object context) {
    return constgraph.find1_XAA1(context, obs);
}
public Object find1_XAA1(Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_XAA1_Iter(Object context) {
    return constgraph.findN_XAA1_Iter(context, obs);
}
public Iterator findN_XAA1_Iter(Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_AXA1(Object context) {
    return constgraph.find1_AXA1(context, obs);
}
public Object find1_AXA1(Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_AXA1_Iter(Object context) {
    return constgraph.findN_AXA1_Iter(context, obs);
}
public Iterator findN_AXA1_Iter(Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_AAX1(Object context) {
    return constgraph.find1_AAX1(context, obs);
}
public Object find1_AAX1(Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_AAX1_Iter(Object context) {
    return constgraph.findN_AAX1_Iter(context, obs);
}
public Iterator findN_AAX1_Iter(Object context, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_111X(Object subj, Object pred, Object obj) {
    return constgraph.find1_111X(subj, pred, obj, obs);
}
public Object find1_111X(Object subj, Object pred, Object obj, Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_111X_Iter(Object subj, Object pred, Object obj) {
    return constgraph.findN_111X_Iter(subj, pred, obj, obs);
}
public Iterator findN_111X_Iter(Object subj, Object pred, Object obj, Obs obs) {
    throw new Error("DoubleObs");
}
public Object find1_AAAX() {
    return constgraph.find1_AAAX(obs);
}
public Object find1_AAAX(Obs obs) {
    throw new Error("DoubleObs");
}
public Iterator findN_AAAX_Iter() {
    return constgraph.findN_AAAX_Iter(obs);
}
public Iterator findN_AAAX_Iter(Obs obs) {
    throw new Error("DoubleObs");
}

}
