/*
UniqueShortcutController.java
 *    
 *    Copyright (c) 2005, Benja Fallenstein
 *
 *    This file is part of Libvob.
 *    
 *    Libvob is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Libvob is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Libvob; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.view;
import org.nongnu.libvob.lob.*;
import org.nongnu.libvob.fn.*;
import org.nongnu.libvob.*;
import org.fenfire.*;
import org.fenfire.potion.*;
import org.fenfire.swamp.*;
import org.fenfire.vocab.*;
import javolution.realtime.*;
import java.util.*;

public class UniqueShortcutController extends AbstractDelegateLob {

    private static final int SHORTCUT_LEN = 3;

    protected Collection properties;
    protected Graph graph;
    protected Cursor cursor;
    protected Graph prefsGraph;
    protected List potionsCommandStack;

    public static final String
	EASY = "ASDFGHJKLWERUIO",
	HARD = "QTYPZXCVBNM";

    public static String getShortcut(Object property) {
	return getShortcut(property, SHORTCUT_LEN);
    }

    public static String getShortcut(Object property, int length) {

	java.util.Random r = new Random(property.hashCode());
	String s = "";
	
	for(int i=0; i<length-1; i++) {
	    s += EASY.charAt(r.nextInt(EASY.length()));
	}

	s += HARD.charAt(r.nextInt(HARD.length()));

	return s;
    }

    private UniqueShortcutController() {}

    public static UniqueShortcutController newInstance(
        Lob content, Collection properties, Graph graph, Cursor cursor, 
	Graph prefsGraph, List potionsCommandStack) {

	UniqueShortcutController c = (UniqueShortcutController)FACTORY.object();
	c.delegate = content;
	c.properties = properties;
	c.graph = graph;
	c.cursor = cursor;
	c.prefsGraph = prefsGraph;
	c.potionsCommandStack = potionsCommandStack;
	return c;
    }

    public Lob wrap(Lob l) {
	return newInstance(l, properties, graph, cursor, prefsGraph,
			   potionsCommandStack);
    }

    public boolean key(String key) {
	String PREFIX = "Alt-";

	if(key.startsWith(PREFIX) && key.length() == PREFIX.length()+1) {

	    char c = key.charAt(PREFIX.length());

	    String s = (String)
		Components.getState(Maps.map(), "unique shortcut so far", "");

	    if(EASY.indexOf(c) >= 0) {
		if(s.length() < SHORTCUT_LEN) {
		    Components.setState(Maps.map(), "unique shortcut so far", 
					s+c);
		    return true;
		} else {
		    return super.key(key);
		}
	    } else if(HARD.indexOf(c) >= 0) {
		s += c;
		Components.setState(Maps.map(), "unique shortcut so far", "");

		if(s.length() == SHORTCUT_LEN) {
		    List list = new ArrayList(properties);

		    for(Iterator i=graph.findN_A1X_Iter(FF.bookmarks); i.hasNext();)
			list.add(i.next());

		    for(Iterator i=list.iterator(); i.hasNext();) {
			Object prop = i.next();
			if(getShortcut(prop, SHORTCUT_LEN).equals(s)) {
			    
			    Action a = new PotionAction(Potions.node(prop, prop.toString()), graph, cursor, prefsGraph, potionsCommandStack);
			    a.run();

			    return true;
			}
		    }

		    return false;
		} else {
		    return super.key(key);
		}
	    } else {
		return super.key(key);
	    }
	} else {
	    Components.setState(Maps.map(), "unique shortcut so far", "");
	    return super.key(key);
	}
    }

    public List getFocusableLobs() {
	return Lists.list(this);
    }

    public boolean move(ObjectSpace os) {
	if(super.move(os)) {
	    if(properties instanceof Realtime)
		((Realtime)properties).move(os);
	    return true;
	}
	return false;
    }

    private static final Factory FACTORY = new Factory() {
	    public Object create() {
		return new UniqueShortcutController();
	    }
	};
}
