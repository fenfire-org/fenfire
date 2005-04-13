/*
CanvasNodeReprView.java
 *    
 *    Copyright (c) 2003-2005, Benja Fallenstein
 *                  2005, Matti J. Katila
 *
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
 *
 */
/*
 * Written by Benja Fallenstein and Matti J. Katila
 */
package org.fenfire.view.repr;
import org.fenfire.view.*;
import org.fenfire.Cursor;
import org.fenfire.lob.*;
import org.fenfire.swamp.*;
import org.fenfire.vocab.*;
import org.fenfire.util.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.fn.*;
import org.nongnu.libvob.lob.*;
import javolution.realtime.*;
import java.awt.Color;
import java.util.*;

public class CanvasNodeReprView extends ReprView.AbstractListView {

    private Graph graph;

    public CanvasNodeReprView(Graph graph) {
	this.graph = graph;
    }

    public ViewSettings.Type TYPE = new ViewSettings.AbstractType() {
	    public boolean containsNode(Object node) {
		return graph.findN_11X_Iter(node, CANVAS2D.x).hasNext() &&
		    graph.findN_11X_Iter(node, CANVAS2D.y).hasNext() &&
		    graph.findN_11X_Iter(node, RDFS.label).hasNext();
	    }
	};

    public Set getTypes() {
	return Collections.singleton(TYPE);
    }

    public List getLobList(Object node) {
	Literal label = (Literal)graph.findN_11X_Iter(node, RDFS.label).next();
	Literal x = (Literal)graph.findN_11X_Iter(node, CANVAS2D.x).next();
	Literal y = (Literal)graph.findN_11X_Iter(node, CANVAS2D.y).next();

	LobFont font = Components.font();

	List text = Lists.list();
	text.add(font.text(label.getString()));
	text.add(font.text(" ("));
	text.add(font.text(x.getString()));
	text.add(font.text(", "));
	text.add(font.text(y.getString()));
	text.add(font.text(")"));

	return Lists.concatElements(text);
    }
}
