/*
MMPlace.java
 *    
 *    Copyright (c) 2003, Asko Soukka
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
 * Written by Asko Soukka
 */

/** MMPlace structure stores a coordinate system and its related MM
 * spesific information.
 */
package org.fenfire.fenmm;

public class MMPlace {

    public final int cs;
    public final double x;
    public final double y;

    public MMPlace(int cs, double x, double y) {
	this.cs = cs;
	this.x = x;
	this.y = y;
    }

    public int getCS() { return cs; }
    public double getX() { return x; }
    public double getY() { return y; }
}
