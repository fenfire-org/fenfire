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
import org.nongnu.libvob.layout.Model;
import org.nongnu.libvob.layout.ObjectModel;
import java.util.Map;

/** A Fenfire cursor position.
 */
public final class Cursor {

    public interface SpatialPosition {
    }

    public static final class SpatialCursor {
	public final Object spatialContext;
	public final Model spatialPosition;

	public SpatialCursor(Object spatialContext) {
	    this(spatialContext, new ObjectModel());
	}

	public SpatialCursor(Object spatialContext, SpatialPosition pos) {
	    this(spatialContext, new ObjectModel(pos));
	}

	public SpatialCursor(Object spatialContext, Model spatialPosition) {
	    this.spatialContext = spatialContext;
	    this.spatialPosition = spatialPosition;
	}

	public Object getSpatialPosition() {
	    return spatialPosition.get();
	}
    }

    public static final class NodeCursor {
	public final Object node;
	public final Model contentCursor;

	public NodeCursor(Object node) {
	    this(node, new ObjectModel());
	}

	public NodeCursor(Object node, Model contentCursor) {
	    this.node = node;
	    this.contentCursor = contentCursor;
	}

	public Object getContentCursor() {
	    return contentCursor.get();
	}
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

    public final Map rotations;

    public final Model spatialCursor;
    public final Model nodeCursor;

    public Cursor(Object node) {
	this(new SpatialCursor(null), new NodeCursor(node));
    }

    public Cursor(SpatialCursor spatialCursor, NodeCursor nodeCursor) {
	this(new java.util.HashMap(), new ObjectModel(spatialCursor), 
	     new ObjectModel(nodeCursor));
    }

    public Cursor(Map rotations, Model spatialCursor, Model nodeCursor) {
	this.rotations = rotations;
	this.spatialCursor = spatialCursor;
	this.nodeCursor = nodeCursor;
    }

    public SpatialCursor getSpatialCursor() {
	return (SpatialCursor)spatialCursor.get();
    }

    public NodeCursor getNodeCursor() { 
	return (NodeCursor)nodeCursor.get();
    }

    public Object getNode() {
	return getNodeCursor().node;
    }
}
