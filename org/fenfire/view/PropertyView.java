/*
PropertyView.java
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
package org.fenfire.view;
import org.fenfire.Cursor;
import org.fenfire.lob.*;
import org.fenfire.swamp.*;
import org.fenfire.util.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.fn.*;
import org.nongnu.libvob.lob.*;
import java.awt.Color;
import java.util.*;


/** A view that represent properies in structure view.
 *  Internationalization goes in this class if anywhere.
 */
public class PropertyView {
    private static void p(String s) { System.out.println("PropertyView:: "+s); }

    private Map propCache = new org.nongnu.navidoc.util.WeakValueMap();

    private Graph graph;
    private Cursor cursor;
    private NamespaceMap nmap;
    private Set textProperties;
    private Object defaultProperty;

    private float minPropBrightness, maxPropBrightness;

    private NodeTexter texter;

    public PropertyView(Graph graph, Cursor cursor, NamespaceMap nmap, 
			     Set textProperties, Object defaultProperty,
			     float minPropBrightness,
			     float maxPropBrightness) {
	this.graph = graph;
	this.cursor = cursor;
	this.nmap = nmap;
	this.textProperties = textProperties;
	this.defaultProperty = defaultProperty;
	this.minPropBrightness = minPropBrightness;
	this.maxPropBrightness = maxPropBrightness;

	this.texter = new NodeTexter(graph, nmap, textProperties,
				     defaultProperty);
    }

    public Lob getPropertyLob(Object node) {
	if(propCache.get(node) != null) return (Lob)propCache.get(node);
	Lob l = makeLob(node);
	propCache.put(node, l);
	return l;
    }

    private Lob makeLob(Object node) {
	Color color = UniqueColors.getColor(node, minPropBrightness,
					    maxPropBrightness);
	return Lobs.hbox(Lobs.text(Components.font(color),
				   texter.getText(node)));
	/*
	Model nodeModel = new ObjectModel(node);

	Lob l;

	Model color = 
	    new UniqueColorModel(new ObjectModel(node),
				 minPropBrightness, maxPropBrightness);
	
	TextStyle ts = ((LobFont)Theme.getFont().get()).getTextStyle();
	LobFont font = new LobFont(ts, (Color)color.get());
	
	TextModel text = 
	    new TextModel.StringTextModel(str, new ObjectModel(font));
	
	l = new Label(text);
	return l;
	*/
    }

}
