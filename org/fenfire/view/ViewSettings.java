/*
ViewSettings.java
 *    
 *    Copyright (c) 2004-2005, Benja Fallenstein and Matti Katila
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
 * Written by Benja Fallenstein and Matti Katila
 */
package org.fenfire.view;
import org.fenfire.Cursor;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.util.*;
import org.nongnu.navidoc.util.Obs;
import java.util.*;

public class ViewSettings {

    public interface Type {
	boolean contains(Cursor cursor);
    }

    public interface SpatialView {
	Set getTypes();
	Lob getLob(Cursor cursor);
	boolean showBig(); // whether the mainview should be shown big
    }

    public static Type ALL = new Type() {
	    public boolean contains(Cursor cursor) {
		return true;
	    }
	};

    protected Set views;

    protected List types; // the current *order* of types
    protected Map viewByType;
    protected Map viewListByType;

    public ViewSettings(Set views) {
	this.views = views;

	this.types = new ArrayList();
	this.viewByType = new HashMap();
	this.viewListByType = new HashMap();

	for(Iterator i=views.iterator(); i.hasNext();) {
	    SpatialView v = (SpatialView)i.next();
	    Set typeSet = (Set)v.getTypes();

	    for(Iterator j=typeSet.iterator(); j.hasNext();) {
		Type t = (Type)j.next();
		
		List l = (List)viewListByType.get(t);
		if(l == null) {
		    l = new ArrayList();
		    viewListByType.put(t, l);
		}

		l.add(v);

		if(!types.contains(t)) 
		    types.add(t);

		if(!viewByType.containsKey(t))
		    viewByType.put(t, v);
	    }
	}
    }

    protected Replaceable[] getParams() {
	return new Replaceable[] { };
    }
    protected Object clone(Object[] params) {
	return new ViewSettings(views);
    }

    protected SpatialView getView(Cursor cursor) {
	// use numeric iteration instead of Iterator
	// because this is called in an inner loop
	for(int i=0; i<types.size(); i++) {
	    Type type = (Type)types.get(i);
	    if(type.contains(cursor))
		return (SpatialView)viewByType.get(type);
	}

	return null;
    }

    private Lob errorLob = new org.nongnu.libvob.layout.component.Label("No matching nodeview found!");

    public Lob getLob(Cursor cursor) {
	SpatialView v = getView(cursor);
	if(v != null)
	    return v.getLob(cursor);
	else
	    return errorLob;
    }

    public boolean showBig(Cursor cursor) {
	SpatialView v = getView(cursor);
	if(v != null)
	    return v.showBig();
	else
	    return false;
    }

    public void changeView(Cursor position, int steps) {
	List list = new ArrayList();
	System.out.println("views: "+list);

	for(Iterator i=views.iterator(); i.hasNext();) {
	    SpatialView v = (SpatialView)i.next();

	    for(Iterator j=v.getTypes().iterator(); j.hasNext();) {
		Type t = (Type)j.next();
		if(t.contains(position)) {
		    list.add(v);
		    break;
		}
	    }
	}

	int index = list.indexOf(getView(position));
	index = (index + steps) % list.size();
	if(index < 0) index += list.size();
	System.out.println("move to index "+index);
	changeView(position, (SpatialView)list.get(index));
    }

    public void changeView(Cursor position, SpatialView v) {
	// find first matching type

	Type t = null;
	for(Iterator i=types.iterator(); i.hasNext();) {
	    t = (Type)i.next();
	    if(v.getTypes().contains(t) && t.contains(position))
		break;
	}

	if(t == null)
	    // no matching type
	    return;

	types.remove(t);
	types.add(0, t);
	viewByType.put(t, v);
    }
}
