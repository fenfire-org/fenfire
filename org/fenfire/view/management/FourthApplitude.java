/*
FourthApplitude.java
 *    
 *    Copyright (c) 2004, Matti J. Katila
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

package org.fenfire.view.management;
import org.fenfire.swamp.*;
import org.fenfire.swamp.cloudberry.*;
import org.fenfire.util.*;
import org.fenfire.structure.*;
import org.fenfire.view.*;
import org.fenfire.vocab.*;
import org.fenfire.functional.*;
import org.fenfire.view.main.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.vobs.*;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.component.*;
import java.util.*;

/** Fourth test applitude :)
 */
public class FourthApplitude extends AbstractDelegateLob
    implements Applitude, Layoutable
{
    static public boolean dbg = false;
    private void p(String s) { System.out.println("FourthApplitude:: "+s); }


    public Replaceable[] getParams() {
	throw new Error("unimplemented"); 
    }
    protected Object clone(Object[] i) {
	throw new Error("unimplemented"); 
    }


    public static final Object Home =  Nodes.get("http://fenfire.org/rdf-v/2004/09/05/SecondApplitude#Home");
    public static final Object is =  Nodes.get("http://fenfire.org/rdf-v/2004/09/05/SecondApplitude#is");


    public void register() {
	f.environment.request("layoutable", this);
    }

    
    private Graph g;
    final FServer f;
    public FourthApplitude(final FServer fs) {
	super();
	this.f = fs;
	
	StormGraph[] sg = new StormGraph[1];
	try {
	    f.environment.request("settings", sg, this);
	} catch (NullPointerException e) {}
	if (sg[0] == null) {
	    Graph gg[] = new Graph[1];
	    f.environment.request("global graph", gg, null);
	    g = gg[0];
	} else g = sg[0];
	
	
	// set mind map
	if (g.find1_11X(Home, is) == null) {
	    p("H: "+Home+", "+is);
	    g.set1_11X(Home, is, RDFUtil.N(g, org.fenfire.modules.init.ViewModelsLoader.Fenmm));
	    p("NODE: "+g.find1_11X(Home, is));
	}
	home = g.find1_11X(Home, is);
	g.add(home, RDF.type, CANVAS2D.Canvas);
	
	
	for (int i=0; i<5; i++)
	    g.add(home, org.fenfire.vocab.STRUCTLINK.linkedTo, Nodes.N());
	p("home: "+home);
	

	Canvas2D canvas = Canvas2D.create(g);
	Object node = Nodes.N();
	canvas.placeOnCanvas(home, node, 0, 0);
	canvas.placeOnCanvas(home, Nodes.N(), -50, -50);
	canvas.placeOnCanvas(home, Nodes.N(), 0, -150);
	
	viewsFunc = new CachedPureNodeFunction(5, g, 
	     new ProperViewsFunction(f));
    }
    Object home;

    protected PureNodeFunction viewsFunc;


    public boolean key(String s) {
	if (s.startsWith("Ctrl-Prio") ||
	    s.startsWith("Ctrl-PgU")) {
	    // simple view change operation..
	    if (lastViews != null) {
		currentIndex = 
		    (currentIndex + 1) % lastViews.size();
		f.getWindowAnimation().switchVS();
	    }
	    super.chg();
	    return true;
	}
	return getDelegate().key(s);
    }


    public Lob getLob() { 
	return this; 
    }

    int currentIndex = 0;
    String current = null;
    List lastViews = null;
    protected Lob getDelegate() {
	Object newViewList = viewsFunc.f(g, home);
	if (lastViews != newViewList) {
	    lastViews = (List) newViewList;
	    for (int i=0; i<lastViews.size(); i++) {
		if (lastViews.get(i).toString() == current)
		    currentIndex = i;
	    }
	    if (currentIndex >= lastViews.size())
		currentIndex = 0;
	}
	current = lastViews.get(currentIndex).toString();
	Object o = lastViews.get(currentIndex);
	if (o instanceof MainViewFactory) {
	    lastViews.set(currentIndex, ((MainViewFactory)o).product(home));
	} 
	
	//p("child: "+ lastViews.get(currentIndex));
	return (Lob) lastViews.get(currentIndex);
    }
}
