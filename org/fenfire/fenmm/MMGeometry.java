/*
MMGeometry.java
 *    
 *    Copyright (c) 2003, Matti Katila, Asko Soukka
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
 * Written by Matti Katila, Asko Soukka
 */

package org.fenfire.fenmm;

import org.fenfire.swamp.ConstGraph;
import org.fenfire.vocab.STRUCTLINK;
import org.nongnu.libvob.VobScene;
import org.fenfire.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

public class MMGeometry {
    private final static int INITIAL_CAPACITY = 10;

    public static double getScaleLength(int depth) {
	if (depth <= 0) return 1.0;
	return (Math.log(depth)*(-1)+Math.E)*(1/(2*Math.E));
    }
    public static double getTextScale(int depth) { return 1-(Math.log(depth+1)/Math.E)/2; }
    public static double getScale(int depth) { return 1-(Math.log(depth+1)/Math.E); }

    private ConstGraph g;
    private int maxDepth;
    private float filletLength;
    private float filletWidth;
    private float initRotation;

    public MMGeometry(ConstGraph g,
		      float filletLength, float filletWidth,
		      float initRotation, int maxDepth) {
	this.g = g;
	init(filletLength, filletWidth, initRotation, maxDepth);
    }

    public void init(float filletLength, float filletWidth,
		     float initRotation, int maxDepth) {
	this.maxDepth = maxDepth;
	this.filletLength = filletLength;
	this.filletWidth = filletWidth;
	this.initRotation = initRotation;
    }

    private void getXY(double x0, double y0, double angle,
		       int depth, double[] xyOut) {
        if (depth == 0 || xyOut.length < 2) return;
	
	double r = filletLength * getScaleLength(depth-1);
	xyOut[0] = x0 + Math.sin(angle) * r;
        xyOut[1] = y0 + Math.cos(angle) * r;
    }

    private MMPlace getPlace(VobScene vs, int into, double x0, double y0,
			     Object node, double angle, int depth) {
	double[] xy = {0f, 0f};
        getXY(x0, y0, angle, depth, xy);
	double x = xy[0];
	double y = xy[1];

        double s = filletWidth * getScale(depth);
	if (depth == maxDepth) s = 0;

	int cs = vs.orthoBoxCS(into, node.toString()+"_FILLET", depth,
			       (float)(x - s/2f), (float)(y - s/2f),
			       1, 1, (float)s, (float)s); 
        return new MMPlace(cs, x, y);
    }

    /**
     * Get all links available with node.
     */
    private List getLinkedNodes(ConstGraph g, Object node) {
	Iterator a = g.findN_11X_Iter(node, STRUCTLINK.linkedTo);
	Iterator b = g.findN_X11_Iter(STRUCTLINK.linkedTo, node);
	ArrayList nodes = new ArrayList(INITIAL_CAPACITY);
        while (a.hasNext()) nodes.add(a.next());
        while (b.hasNext()) nodes.add(b.next());
        return nodes;
    }

    private Object prevCenter = null;
    private double prevRotationAngle = 0f;
    private double prevStartAngle = 0f;
    private Object curCenter = null;
    private double curRotationAngle = 0f;
    private double curStartAngle = 0f;

    public MindNet buildMindNet(VobScene vs, int cs, List path) {
 	MindNet net = new MindNet();
	if (path.size() == 0) return net;
	
	Object node = path.get(path.size()-1);

        MMPlace pl = getPlace(vs, cs, 0,0, node, 0,0);
        net.put(node, pl, 0);

        List links = getLinkedNodes(g, node);
        if (links.size() < 1) net.link(node, null);
	else {
	    curStartAngle = getStartAngle(path);
	    curRotationAngle = (2f*Math.PI) / links.size();
            for (int i=1; i<maxDepth+1; i++)
                buildDepth(vs, cs, net, node, null, links, 0, 0,
			   curStartAngle + initRotation,
			   curRotationAngle, 1, i);
	}
	curCenter = node;
	return net;
    }

    private double getStartAngle(List path) {
	Iterator i = path.iterator();
	while (i.hasNext()) {
	    Object node = i.next();
	    if (curCenter != node) {
		prevCenter = curCenter;
		prevRotationAngle = curRotationAngle;
		prevStartAngle = curStartAngle;
	    }
                         
	    int curIndex = 0;
	    int prevIndex = 0;

	    List links = getLinkedNodes(g, node);
	    if (prevCenter != null) {
		List prevLinks = getLinkedNodes(g, prevCenter);
		prevIndex = prevLinks.indexOf(node);
		curIndex = links.indexOf(prevCenter);
	    }
	    
	    curRotationAngle = (2f*Math.PI) / links.size();

	    // Calculates the "correct" starting angle
	    if (prevIndex >= 0 && curIndex >= 0)
		curStartAngle = prevStartAngle + prevRotationAngle*prevIndex -Math.PI
		    + curRotationAngle*(links.size() - curIndex);
	    else curRotationAngle = 0;
	    curCenter = node;
	} return curStartAngle;
   }

   /**
    * Recursively go through all cs.
    */
   private void buildDepth(VobScene vs, int into, MindNet net,
			   Object centerNode, Object oldCenter, List links,
			   double x, double y, double startAngle, double rotationAngle,
			   int beginDepth, int endDepth) {

       // Create CS (within place) and put it into mindNet
       for (int i=0; i<links.size(); i++) {
	   Object link = links.get(i);
	   if (net.hasBeenLinked(centerNode, link)) continue;
	   net.link(centerNode, link);
	   
	   if (net.getPlace(link) == null) {
	       MMPlace pl = getPlace(vs, into, x, y, link,
				     startAngle+rotationAngle*i,
				     beginDepth);
	       net.put(link, pl, beginDepth);
	   }
       }

       // Another recursion step
       if (beginDepth >= endDepth) return;
       for (int i=0; i<links.size(); i++) {
	   Object link = links.get(i);
	   if (link == oldCenter) continue;
	   
	   MMPlace pl = net.getPlace(link);
	   List nextLinks = getLinkedNodes(g, link);
	   double nextAngle = (2f*Math.PI)/nextLinks.size();
           int index = nextLinks.indexOf(centerNode);

	   double nextStartAngle;
	   if (index >= 0) nextStartAngle = startAngle+rotationAngle*i -Math.PI
				  + nextAngle*(nextLinks.size()-index);
	   else nextStartAngle = 0;
            
	   buildDepth(vs, into, net, link, centerNode, nextLinks, pl.x, pl.y,
		      nextStartAngle, nextAngle, beginDepth + 1, endDepth);
       }
   }
}
