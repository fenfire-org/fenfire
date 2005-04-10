/*
ImageContentView.java
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
package org.fenfire.view.content;
import org.fenfire.vocab.*;
import org.fenfire.view.*;
import org.fenfire.Cursor;
import org.fenfire.lob.*;
import org.fenfire.swamp.*;
import org.fenfire.util.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.lob.*;
import java.awt.Color;
import java.util.*;

public class ImageContentView implements ContentViewSettings.ContentView {
    private static void p(String s) { System.out.println("ImageContentView:: "+s); }


    private Graph graph;
    private Object[] types         ;
    public ImageContentView(Graph graph, Object[] types) {
	this.graph = graph;
	this.types = types;
    }

    public Set getTypes() {
	return Collections.singleton(new ViewSettings.AbstractType() {
		public boolean containsNode(Object n) {
		    if (n instanceof Literal) return false;
		    
		    for (Iterator i=graph.findN_11X_Iter(n, RDF.type); 
			 i.hasNext();) {
			Object node = i.next();
			for (int j = 0; j<types.length; j++) {
			    if (node == types[j]) return true;
			}
		    }
		    return false;
		}
	    });
    }
    
    public Lob getLob(Object node) {
	//if(cache.get(node) != null) return (Lob)cache.get(node);
	Lob l = makeLob(node);
	//cache.put(node, l);
	return l;
    }


    /** 
     */
    private Lob makeLob(Object node) {
	throw new Error();
	/*
	Lob l;

	// XXX why this doesn't work if print is removed?

	//p("make image lob");
	//l = new Label("this is picture");
	try {
	    l = new Image(new java.io.File("../libvob/testdata/libvob.png"));
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new Error("");
	}
	return l;
	*/
    }

}
