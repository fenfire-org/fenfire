// AUTOGENERATED By makeswamp2.py - DO NOT EDIT.

package org.fenfire.swamp;
import java.util.Iterator;
import org.nongnu.navidoc.util.Obs;

/** A non-modifiable RDF graph. The iterators may implement the method
 *  remove, but it should not be used, as it may cause unspecified behavior.
 */
public interface QuadsConstGraph {
    /** Get a QuadsConstGraph whose queries will return the same
     * value as the queries for this graph, but will 
     * set up the Obs for those queries.
     * When the result of any of those queries changes,
     * Obs is called immediately.
     */
    QuadsConstGraph getObservedConstGraph(org.nongnu.navidoc.util.Obs o);

    /** This observed graph will not be used any more, and
     * if desired, may be recycled.
     * This operation is allowed to be a no-op, and
     * if the graph this method is called on is not one that
     * has been returned by getObservedConstGraph, is
     * defined to be so..
     */
    void close();

    /** If this graph is observed (returned from getObservedConstGraph),
     * get the observer.
     */
    Obs getObserver();
    /** If this graph is observed (returned from getObservedConstGraph),
     * get the original.
     */
    QuadsConstGraph getOriginalConstGraph();

    boolean contains(Object e0, Object e1, Object e2, Object context);
    boolean contains(Object e0, Object e1, Object e2, Object context, Obs o);

    Object find1_111X(Object subj, Object pred, Object obj);
Iterator findN_111X_Iter(Object subj, Object pred, Object obj);
Object find1_111X(Object subj, Object pred, Object obj, Obs obs);
Iterator findN_111X_Iter(Object subj, Object pred, Object obj, Obs obs);
Object find1_11X1(Object subj, Object pred, Object context);
Iterator findN_11X1_Iter(Object subj, Object pred, Object context);
Object find1_11X1(Object subj, Object pred, Object context, Obs obs);
Iterator findN_11X1_Iter(Object subj, Object pred, Object context, Obs obs);
Object find1_1X11(Object subj, Object obj, Object context);
Iterator findN_1X11_Iter(Object subj, Object obj, Object context);
Object find1_1X11(Object subj, Object obj, Object context, Obs obs);
Iterator findN_1X11_Iter(Object subj, Object obj, Object context, Obs obs);
Object find1_X111(Object pred, Object obj, Object context);
Iterator findN_X111_Iter(Object pred, Object obj, Object context);
Object find1_X111(Object pred, Object obj, Object context, Obs obs);
Iterator findN_X111_Iter(Object pred, Object obj, Object context, Obs obs);
Object find1_11XA(Object subj, Object pred);
Iterator findN_11XA_Iter(Object subj, Object pred);
Object find1_11XA(Object subj, Object pred, Obs obs);
Iterator findN_11XA_Iter(Object subj, Object pred, Obs obs);
Object find1_1X1A(Object subj, Object obj);
Iterator findN_1X1A_Iter(Object subj, Object obj);
Object find1_1X1A(Object subj, Object obj, Obs obs);
Iterator findN_1X1A_Iter(Object subj, Object obj, Obs obs);
Object find1_X11A(Object pred, Object obj);
Iterator findN_X11A_Iter(Object pred, Object obj);
Object find1_X11A(Object pred, Object obj, Obs obs);
Iterator findN_X11A_Iter(Object pred, Object obj, Obs obs);
Object find1_11AX(Object subj, Object pred);
Iterator findN_11AX_Iter(Object subj, Object pred);
Object find1_11AX(Object subj, Object pred, Obs obs);
Iterator findN_11AX_Iter(Object subj, Object pred, Obs obs);
Object find1_1XAA(Object subj);
Iterator findN_1XAA_Iter(Object subj);
Object find1_1XAA(Object subj, Obs obs);
Iterator findN_1XAA_Iter(Object subj, Obs obs);
Object find1_1XA1(Object subj, Object context);
Iterator findN_1XA1_Iter(Object subj, Object context);
Object find1_1XA1(Object subj, Object context, Obs obs);
Iterator findN_1XA1_Iter(Object subj, Object context, Obs obs);
Object find1_XAAA();
Iterator findN_XAAA_Iter();
Object find1_XAAA(Obs obs);
Iterator findN_XAAA_Iter(Obs obs);
Object find1_XAA1(Object context);
Iterator findN_XAA1_Iter(Object context);
Object find1_XAA1(Object context, Obs obs);
Iterator findN_XAA1_Iter(Object context, Obs obs);
Object find1_X1AA(Object pred);
Iterator findN_X1AA_Iter(Object pred);
Object find1_X1AA(Object pred, Obs obs);
Iterator findN_X1AA_Iter(Object pred, Obs obs);
Object find1_X1A1(Object pred, Object context);
Iterator findN_X1A1_Iter(Object pred, Object context);
Object find1_X1A1(Object pred, Object context, Obs obs);
Iterator findN_X1A1_Iter(Object pred, Object context, Obs obs);

}
