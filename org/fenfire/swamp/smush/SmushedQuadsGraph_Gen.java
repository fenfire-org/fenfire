// AUTOGENERATED By makeswamp.py - DO NOT EDIT.

package org.fenfire.swamp.smush;
import org.nongnu.navidoc.util.Obs;
import org.fenfire.swamp.*;
import org.fenfire.swamp.impl.*;
import java.util.Iterator;

public abstract class SmushedQuadsGraph_Gen extends AbstractQuadsGraph {
    protected QuadsGraph unsmushed = new HashQuadsGraph();
    protected QuadsGraph smushed = new HashQuadsGraph();

    protected abstract Object get(Object node);

    public void startUpdate() { smushed.startUpdate(); }
    public void endUpdate() { smushed.endUpdate(); }

    public boolean contains(Object s, Object p, Object o, Object c, Obs obs) {
        return smushed.contains(get(s), get(p), get(o), c, obs);
    }

    public Object find1_X11A(Object pred, Object obj, Obs obs) {
    return smushed.find1_X11A(pred, obj, obs);
}
public Iterator findN_X11A_Iter(Object pred, Object obj, Obs obs) {
    return smushed.findN_X11A_Iter(pred, obj, obs);
}
public Object find1_1X1A(Object subj, Object obj, Obs obs) {
    return smushed.find1_1X1A(subj, obj, obs);
}
public Iterator findN_1X1A_Iter(Object subj, Object obj, Obs obs) {
    return smushed.findN_1X1A_Iter(subj, obj, obs);
}
public Object find1_11XA(Object subj, Object pred, Obs obs) {
    return smushed.find1_11XA(subj, pred, obs);
}
public Iterator findN_11XA_Iter(Object subj, Object pred, Obs obs) {
    return smushed.findN_11XA_Iter(subj, pred, obs);
}
public Object find1_X1AA(Object pred, Obs obs) {
    return smushed.find1_X1AA(pred, obs);
}
public Iterator findN_X1AA_Iter(Object pred, Obs obs) {
    return smushed.findN_X1AA_Iter(pred, obs);
}
public Object find1_1XAA(Object subj, Obs obs) {
    return smushed.find1_1XAA(subj, obs);
}
public Iterator findN_1XAA_Iter(Object subj, Obs obs) {
    return smushed.findN_1XAA_Iter(subj, obs);
}
public Object find1_1AXA(Object subj, Obs obs) {
    return smushed.find1_1AXA(subj, obs);
}
public Iterator findN_1AXA_Iter(Object subj, Obs obs) {
    return smushed.findN_1AXA_Iter(subj, obs);
}
public Object find1_XA1A(Object obj, Obs obs) {
    return smushed.find1_XA1A(obj, obs);
}
public Iterator findN_XA1A_Iter(Object obj, Obs obs) {
    return smushed.findN_XA1A_Iter(obj, obs);
}
public Object find1_AX1A(Object obj, Obs obs) {
    return smushed.find1_AX1A(obj, obs);
}
public Iterator findN_AX1A_Iter(Object obj, Obs obs) {
    return smushed.findN_AX1A_Iter(obj, obs);
}
public Object find1_A1XA(Object pred, Obs obs) {
    return smushed.find1_A1XA(pred, obs);
}
public Iterator findN_A1XA_Iter(Object pred, Obs obs) {
    return smushed.findN_A1XA_Iter(pred, obs);
}
public Object find1_XAAA(Obs obs) {
    return smushed.find1_XAAA(obs);
}
public Iterator findN_XAAA_Iter(Obs obs) {
    return smushed.findN_XAAA_Iter(obs);
}
public Object find1_AXAA(Obs obs) {
    return smushed.find1_AXAA(obs);
}
public Iterator findN_AXAA_Iter(Obs obs) {
    return smushed.findN_AXAA_Iter(obs);
}
public Object find1_AAXA(Obs obs) {
    return smushed.find1_AAXA(obs);
}
public Iterator findN_AAXA_Iter(Obs obs) {
    return smushed.findN_AAXA_Iter(obs);
}
public Object find1_X111(Object pred, Object obj, Object context, Obs obs) {
    return smushed.find1_X111(pred, obj, context, obs);
}
public Iterator findN_X111_Iter(Object pred, Object obj, Object context, Obs obs) {
    return smushed.findN_X111_Iter(pred, obj, context, obs);
}
public Object find1_1X11(Object subj, Object obj, Object context, Obs obs) {
    return smushed.find1_1X11(subj, obj, context, obs);
}
public Iterator findN_1X11_Iter(Object subj, Object obj, Object context, Obs obs) {
    return smushed.findN_1X11_Iter(subj, obj, context, obs);
}
public Object find1_11X1(Object subj, Object pred, Object context, Obs obs) {
    return smushed.find1_11X1(subj, pred, context, obs);
}
public Iterator findN_11X1_Iter(Object subj, Object pred, Object context, Obs obs) {
    return smushed.findN_11X1_Iter(subj, pred, context, obs);
}
public Object find1_X1A1(Object pred, Object context, Obs obs) {
    return smushed.find1_X1A1(pred, context, obs);
}
public Iterator findN_X1A1_Iter(Object pred, Object context, Obs obs) {
    return smushed.findN_X1A1_Iter(pred, context, obs);
}
public Object find1_1XA1(Object subj, Object context, Obs obs) {
    return smushed.find1_1XA1(subj, context, obs);
}
public Iterator findN_1XA1_Iter(Object subj, Object context, Obs obs) {
    return smushed.findN_1XA1_Iter(subj, context, obs);
}
public Object find1_1AX1(Object subj, Object context, Obs obs) {
    return smushed.find1_1AX1(subj, context, obs);
}
public Iterator findN_1AX1_Iter(Object subj, Object context, Obs obs) {
    return smushed.findN_1AX1_Iter(subj, context, obs);
}
public Object find1_XA11(Object obj, Object context, Obs obs) {
    return smushed.find1_XA11(obj, context, obs);
}
public Iterator findN_XA11_Iter(Object obj, Object context, Obs obs) {
    return smushed.findN_XA11_Iter(obj, context, obs);
}
public Object find1_AX11(Object obj, Object context, Obs obs) {
    return smushed.find1_AX11(obj, context, obs);
}
public Iterator findN_AX11_Iter(Object obj, Object context, Obs obs) {
    return smushed.findN_AX11_Iter(obj, context, obs);
}
public Object find1_A1X1(Object pred, Object context, Obs obs) {
    return smushed.find1_A1X1(pred, context, obs);
}
public Iterator findN_A1X1_Iter(Object pred, Object context, Obs obs) {
    return smushed.findN_A1X1_Iter(pred, context, obs);
}
public Object find1_XAA1(Object context, Obs obs) {
    return smushed.find1_XAA1(context, obs);
}
public Iterator findN_XAA1_Iter(Object context, Obs obs) {
    return smushed.findN_XAA1_Iter(context, obs);
}
public Object find1_AXA1(Object context, Obs obs) {
    return smushed.find1_AXA1(context, obs);
}
public Iterator findN_AXA1_Iter(Object context, Obs obs) {
    return smushed.findN_AXA1_Iter(context, obs);
}
public Object find1_AAX1(Object context, Obs obs) {
    return smushed.find1_AAX1(context, obs);
}
public Iterator findN_AAX1_Iter(Object context, Obs obs) {
    return smushed.findN_AAX1_Iter(context, obs);
}
public Object find1_111X(Object subj, Object pred, Object obj, Obs obs) {
    return smushed.find1_111X(subj, pred, obj, obs);
}
public Iterator findN_111X_Iter(Object subj, Object pred, Object obj, Obs obs) {
    return smushed.findN_111X_Iter(subj, pred, obj, obs);
}
public Object find1_AAAX(Obs obs) {
    return smushed.find1_AAAX(obs);
}
public Iterator findN_AAAX_Iter(Obs obs) {
    return smushed.findN_AAAX_Iter(obs);
}
public void rm_111A(Object subj, Object pred, Object obj) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_111X_Iter(subj, pred, obj);
        if(!i.hasNext()) return;
        Object context = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_11AA(Object subj, Object pred) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_11XA_Iter(subj, pred);
        if(!i.hasNext()) return;
        Object obj = i.next();
        i = unsmushed.findN_111X_Iter(subj, pred, obj);
        if(!i.hasNext()) return;
        Object context = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_1A1A(Object subj, Object obj) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_1X1A_Iter(subj, obj);
        if(!i.hasNext()) return;
        Object pred = i.next();
        i = unsmushed.findN_111X_Iter(subj, pred, obj);
        if(!i.hasNext()) return;
        Object context = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_1AAA(Object subj) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_1XAA_Iter(subj);
        if(!i.hasNext()) return;
        Object pred = i.next();
        i = unsmushed.findN_11XA_Iter(subj, pred);
        if(!i.hasNext()) return;
        Object obj = i.next();
        i = unsmushed.findN_111X_Iter(subj, pred, obj);
        if(!i.hasNext()) return;
        Object context = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_A11A(Object pred, Object obj) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_X11A_Iter(pred, obj);
        if(!i.hasNext()) return;
        Object subj = i.next();
        i = unsmushed.findN_111X_Iter(subj, pred, obj);
        if(!i.hasNext()) return;
        Object context = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_A1AA(Object pred) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_X1AA_Iter(pred);
        if(!i.hasNext()) return;
        Object subj = i.next();
        i = unsmushed.findN_11XA_Iter(subj, pred);
        if(!i.hasNext()) return;
        Object obj = i.next();
        i = unsmushed.findN_111X_Iter(subj, pred, obj);
        if(!i.hasNext()) return;
        Object context = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_AA1A(Object obj) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_XA1A_Iter(obj);
        if(!i.hasNext()) return;
        Object subj = i.next();
        i = unsmushed.findN_1X1A_Iter(subj, obj);
        if(!i.hasNext()) return;
        Object pred = i.next();
        i = unsmushed.findN_111X_Iter(subj, pred, obj);
        if(!i.hasNext()) return;
        Object context = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_AAAA() {
    while(true) {
        Iterator i;
        i = unsmushed.findN_XAAA_Iter();
        if(!i.hasNext()) return;
        Object subj = i.next();
        i = unsmushed.findN_1XAA_Iter(subj);
        if(!i.hasNext()) return;
        Object pred = i.next();
        i = unsmushed.findN_11XA_Iter(subj, pred);
        if(!i.hasNext()) return;
        Object obj = i.next();
        i = unsmushed.findN_111X_Iter(subj, pred, obj);
        if(!i.hasNext()) return;
        Object context = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_11A1(Object subj, Object pred, Object context) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_11X1_Iter(subj, pred, context);
        if(!i.hasNext()) return;
        Object obj = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_1A11(Object subj, Object obj, Object context) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_1X11_Iter(subj, obj, context);
        if(!i.hasNext()) return;
        Object pred = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_1AA1(Object subj, Object context) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_1XA1_Iter(subj, context);
        if(!i.hasNext()) return;
        Object pred = i.next();
        i = unsmushed.findN_11X1_Iter(subj, pred, context);
        if(!i.hasNext()) return;
        Object obj = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_A111(Object pred, Object obj, Object context) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_X111_Iter(pred, obj, context);
        if(!i.hasNext()) return;
        Object subj = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_A1A1(Object pred, Object context) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_X1A1_Iter(pred, context);
        if(!i.hasNext()) return;
        Object subj = i.next();
        i = unsmushed.findN_11X1_Iter(subj, pred, context);
        if(!i.hasNext()) return;
        Object obj = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_AA11(Object obj, Object context) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_XA11_Iter(obj, context);
        if(!i.hasNext()) return;
        Object subj = i.next();
        i = unsmushed.findN_1X11_Iter(subj, obj, context);
        if(!i.hasNext()) return;
        Object pred = i.next();

        rm_1111(subj, pred, obj, context);
    }
}
public void rm_AAA1(Object context) {
    while(true) {
        Iterator i;
        i = unsmushed.findN_XAA1_Iter(context);
        if(!i.hasNext()) return;
        Object subj = i.next();
        i = unsmushed.findN_1XA1_Iter(subj, context);
        if(!i.hasNext()) return;
        Object pred = i.next();
        i = unsmushed.findN_11X1_Iter(subj, pred, context);
        if(!i.hasNext()) return;
        Object obj = i.next();

        rm_1111(subj, pred, obj, context);
    }
}

}
