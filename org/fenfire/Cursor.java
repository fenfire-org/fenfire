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
import org.nongnu.libvob.layout.*;
import java.util.*;

/** A Fenfire cursor position.
 */
public final class Cursor {

    public final Model node = new ObjectModel();
    public final Model spatialCursor = new ObjectModel();
    public final Model textCursor = new IntModel(-1);
    public final Map rotations = new HashMap(); // should perhaps be LRU...

    public Object getNode() {
	return node.get();
    }

    public Object getSpatialCursor() {
	return spatialCursor.get();
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

    public void setRotation(Object node, Object rotationProperty, 
			    Object rotationNode, int dir) {
	rotations.put(node, new Rotation(rotationProperty, rotationNode, dir));
    }

    public void set(Object node, Object spatialCursor) {
	this.node.set(node);
	this.spatialCursor.set(spatialCursor);
	this.textCursor.setInt(-1);
    }

    public void set(Object node, Object rotationProperty, 
		    Object rotationNode, int dir) {
	setNode(node);
	setRotation(node, rotationProperty, rotationNode, dir);
    }

    /** Set the node, and set the spatial cursor to 'null.'
     *  To set the node without changing the spatial cursor, use 'setNode().'
     */
    public void set(Object node) {
	this.node.set(node);
	this.spatialCursor.set(null);
	this.textCursor.setInt(-1);
    }

    /** Set the node, don't change the spatial cursor.
     *  To set the node and set the spatial cursor to null, use 'set().'
     */
    public void setNode(Object node) {
	this.node.set(node);
	this.textCursor.setInt(-1);
    }

    public void setSpatialCursor(Object spatialCursor) {
	this.spatialCursor.set(spatialCursor);
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
}
