/*
View2DList.java
 *    
 *    Copyright (c) 2003, Matti J. Katila
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

package org.fenfire.view;
import org.nongnu.libvob.VobScene;


/** Array of parallel View2D intances enclapsed 
 *  to one View2D. The first item in array is 
 *  the most meaningful, i.e., it determines the size 
 *  and it is first asked to return a child view2d.
 *  The simple idea of parallelism is to have only
 *  one instance of no meaningful View2D instances
 *  by sharing them for different views.
 * <p>
 * Example - three views: 
 *<ol>
 *  <li> A view which draws a logo of Fenfire. 
 *      This view is very special since it must 
 *      be drawn at first of every other views.</li>
 *  <li> A view which draws red box </li>
 *  <li> A view which draws a blue circle </li>
 *</ol>
 *  We want to construct two views of given three which are:
 *<ul>
 *  <li> A view with blue circle and a logo. </li>
 *  <li> A view with red box and a logo. </li>
 *</ul>
 * We have two options:
 *<pre>

 circleAndLogo = new FenfireLogo(new BlueCircle())
 boxAndLogo = new FenfireLogo(new RedBox()) 

 *</pre>
 *  or only one instance of each and View2DList as a glue:
 *<pre>

 logo = new FenfireLogo()
 circle = new BlueCircle())
 box = new RedBox()

 circleAndLogo = new View2DList(new View2D[]{logo, circle})
 boxAndLogo = new View2DList(new View2D[]{logo, box})

 *</pre>
 *  Now we are able to make changes within logo without
 *  creating a lot of objects because we have a reference to it,
 *  i.e., <code>logo</code>.
 */
public class View2DList extends View2D  {

    private View2D[] views;
    public View2DList(View2D [] views) {
	this.views = views;
    }

    public void render(VobScene vs, 
		       Object plane,
		       int matchingParent,
		       int box2screen, int box2plane
		       ) {
	for (int i=0; i<views.length; i++) {
	    views[i].render(vs, plane, matchingParent,
			    box2screen, box2plane);
	}
    }

    /** Return the size of first item in view2d array.
     */
    public void getSize(Object plane, float[] wh) {
	float [] tmp = new float[2];
	for (int i=0; i<views.length; i++) {
	    views[i].getSize(plane, tmp);
	    wh[0] = java.lang.Math.max(-1, tmp[0]);
	    wh[1] = java.lang.Math.max(-1, tmp[1]);
	    return;
	}
    }

    // implement
    public View2D getContentView2D() { 
	for (int i=0; i<views.length; i++) {
	    View2D v = views[i].getContentView2D();
	   if (v != null) return v;
	}
	return null; 
    }
    
    
    /** Return the first found selected object.
     *  The searching is started at the first item 
     *  in view2d array and iterated at the end of it.
     */
    public Object getSelectedObject(Object plane, float x, float y, 
				    float w, float h) 
    {
	for (int i=0; i<views.length; i++) {
	    Object o = views[i].getSelectedObject(plane, x,y,w,h);
	    if (o != null) return o;
	}
	return null; 
    }

    // implements
    public void chgFast(VobScene vs, Object plane,
			int matchingParent,
			int box2screen, int box2plane) { 
	for (int i=0; i<views.length; i++) {
	    View2D v = views[i];
	    v.chgFast(vs, plane, matchingParent, box2screen, box2plane); 
	}
    }
    
}
