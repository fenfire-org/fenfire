/*
View2D.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka and Benja Fallenstein
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
 * Written by Tuomas J. Lukka and Benja Fallenstein
 */

package org.fenfire.view;
import org.nongnu.libvob.VobScene;

/** A view defined as a region of a 2D plane.
 *  Examples: A PP canvas; an image; a PDF article;
 *  an HTML file rendered into a 2D plane.
 */
public abstract class View2D {

    /** An anchor: a given node / location on a given plane.
     */
    static public class Anchor {
	public Anchor(Object plane, float x, 
				    float y, 
				    float w, 
				    float h, Object node) {
	    this.plane = plane;
	    this.x = x;
	    this.y = y;
	    this.w = w;
	    this.h = h;
	    this.node = node;
	}
	public final Object plane;
	public final float x, y, w, h;
	public final Object node;

	public String toString() {
	    return "["+super.toString()+": "+
		plane+": ("
		+x+","+
		+y+","+
		+w+","+
		+h+") - "+node+"]";
	}
    }

    /** Render this view.
     *  The view is a rectangular area of the whole 2D plane.
     *  (For PP canvases, which are infinitely large, we
     *  could never render the whole plane.) This rectangular
     *  area, called the <em>box</em>, is placed on some
     *  point of the screen.
     *  <p>
     *  We represent this using two transformations:
     *  <code>box2screen</code>, which simply determines where
     *  the box is placed, and <code>box2plane</code>, which
     *  determines the box to place. The latter is a box
     *  coordinate system (transformation); this determines 
     *  the width and height of the area. By using
     *  translations and scalings, we can represent
     *  the panning and zooming of the box. (We could also
     *  rotate by using rotation transformations, and so on.)
     *  <p>
     *  You can think of the following three coordinate systems:
     *  <ul>
     *  <li><code>box</code>: The coordinate system
     *      into which the rectangular area is placed.</li>
     *  <li><code>paper</code>: The coordinate system
     *      of the virtual plane.</li>
     *  <li><code>screen</code>: The screen coordinate system.</li>
     *  </ul>
     *  Then, <code>box2paper</code> and <code>box2screen</code>
     *  are transformations between these coordinate systems.
     *
     * @param vs The vobscene to render into
     * @param plane The defining object of this view (the focus, plane id or somesuch).
     * @param matchingParent If buoys or other links
     * 			are to work, the matching parent
     *			of those coordsys must be this.
     *			This coordsys must not be used in 
     *			other ways.
     * @param box2screen The transformation from the frame
     * 			(which is the box of this transformation)
     * 			to the screen coordinates.
     * @param box2plane The transformation from the frame
     * 			(again, the box of this transformation)
     * 			to the plane/paper/view2d coordinates.
     */
    public abstract void render(VobScene vs, 
		Object plane,
		int matchingParent,
		    int box2screen, int box2plane
			);

    /** Get the size of this plane.
     * If the plane is infinite, wh[0] &lt; 1 shall 
     * <a href="http://www.m-w.com/cgi-bin/dictionary?book=Dictionary&va=obtain">obtain</a>.
     * Default implementation: infinite.
     */
    public void getSize(Object plane, float[] wh) {
	wh[0] = -1;
	wh[1] = -1;
    }

    /** Return the View2D which can handle the content.
     */
    public View2D getContentView2D() { return null; }

    /** A method to push a View2D to set it's coordinate system parameters.
     *  While dragging with mouse, for example, should this method be used
     *  instead of creating new scenes.
     *<p>
     *  Programming note: using one scene wherever it's possible is faster
     *  than creating a new one.
     */
    public void chgFast(VobScene currentVisibleVobScene, 
			Object plane,
			int matchingParent,
			int box2screen, int box2plane) 
    { ; }

    /** Get an object describing (as a selection) what
     * is inside a rectangular region.
     * This may be null, if it doesn't make sense
     * to describe it yet, such as Canvas views -
     * for a Page Scroll view, it's a PageImageSpan
     * of the page that most overlaps the rectangle..
     */
    public abstract Object getSelectedObject(Object plane, float x, float y, float w, float h);


}
