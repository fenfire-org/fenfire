/*
Cursor.java
 *
 *    Copyright (c) 2005 by Benja Fallenstein
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
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire;
import org.nongnu.libvob.fn.Model;
import org.nongnu.libvob.fn.FastInt;
import org.fenfire.swamp.smush.SmushListener;
import javolution.realtime.RealtimeObject;
import java.util.*;

/** A Fenfire cursor position.
 */
public final class Cursor implements SmushListener {

    public Object node;
    public Object spatialCursor;
    public int textCursor = -1;
    public Map rotations = new HashMap(); // should perhaps be LRU...

    public final Model textCursorModel = new TextCursorModel();


    public void smushed(Object old, Object into) {
	if(old.equals(node)) node = into;

	System.out.println("*** smushed => "+old+" => "+into+" ***");

	Map old_r = new HashMap(rotations);

	for(Iterator i=old_r.keySet().iterator(); i.hasNext();) {
	    Object n = i.next();
	    Rotation r = (Rotation)old_r.get(n);

	    Object n1 = old.equals(n) ? into : n;

	    Object p = r.rotationProperty, n2 = r.rotationNode;
	    if(old.equals(p))  p = into;
	    if(old.equals(n2)) n2 = into;

	    rotations.remove(n);
	    rotations.put(n1, new Rotation(p, n2, r.rotationDir));
	}
	
	if(getSpatialCursor() instanceof SmushListener)
	    ((SmushListener)getSpatialCursor()).smushed(old, into);
    }


    public Object getNode() {
	return node;
    }

    public Object getSpatialCursor() {
	return spatialCursor;
    }

    public Rotation getRotation(Object node) {
	return (Rotation)rotations.get(node);
    }

    public Rotation getRotation() {
	return getRotation(getNode());
    }

    public void setRotation(Object node, Rotation rotation) {
	rotations.put(node, rotation);
    }

    /** Set the rotation both ways.
     */
    public void setRotation(Object node1, Object prop, Object node2) {
	setRotation(node1, prop, node2, 1);
	setRotation(node2, prop, node1, -1);
    }

    public void setRotation(Object node, Object rotationProperty, 
			    Object rotationNode, int dir) {
	rotations.put(node, new Rotation(rotationProperty, rotationNode, dir));
    }

    public void set(Object node, Object spatialCursor) {
	this.node = node;
	this.spatialCursor = spatialCursor;
	this.textCursor = -1;
    }

    public void set(Object node, Object rotationProperty, 
		    Object rotationNode, int dir) {
	set(node);
	setRotation(node, rotationProperty, rotationNode, dir);
    }

    /** Set the node, and set the spatial cursor to 'null.'
     *  To set the node without changing the spatial cursor, use 'setNode().'
     */
    public void set(Object node) {
	this.node = node;
	this.spatialCursor = null;
	this.textCursor = -1;
    }

    /** Set the node, don't change the spatial cursor.
     *  To set the node and set the spatial cursor to null, use 'set().'
     */
    public void setNode(Object node) {
	this.node = node;
	this.textCursor = -1;
    }

    public void setSpatialCursor(Object spatialCursor) {
	this.spatialCursor = spatialCursor;
    }


    public static final class Rotation {
	private final Object rotationProperty, rotationNode;
	private final int rotationDir;

	public Object getRotationProperty() { return rotationProperty; }
	public Object getRotationNode() { return rotationNode; }
	public int getRotationDir() { return rotationDir; }
	
	public Rotation(Object rotationProperty, Object rotationNode, 
			int rotationDir) {
	    this.rotationProperty = rotationProperty; 
	    this.rotationNode = rotationNode; this.rotationDir = rotationDir;
	}
	
	public boolean equals(Object other) {
	    if(!(other instanceof Rotation)) return false;
	    Rotation o = (Rotation)other;

	    return
		getRotationProperty().equals(o.getRotationProperty()) &&
		getRotationNode().equals(o.getRotationNode()) &&
		getRotationDir() == o.getRotationDir();
	}

	public int hashCode() {
	    return 2349*getRotationProperty().hashCode() +
		34908*getRotationNode().hashCode() + getRotationDir();
	}
    }


    public final class TextCursorModel 
	extends RealtimeObject implements Model {

	private TextCursorModel() {
	}

	public int getInt() {
	    return textCursor;
	}
	
	public void set(int value) {
	    textCursor = value;
	}

	public Object get() {
	    return FastInt.newInstance(textCursor);
	}

	public void set(Object o) {
	    textCursor = ((FastInt)o).intValue();
	}
    }
}
