/*
ObsDoubler.java
 *    
 *    Copyright (c) 2003, Tuomas J. Lukka
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
 * Written by Tuomas J. Lukka
 */

package org.fenfire.util;
import org.nongnu.navidoc.util.Obs;

/** An easy way to chain Observers into a set.
 */
public class ObsDoubler implements Obs {
    Obs o1, o2;
    public ObsDoubler(Obs o1, Obs o2) {
	this.o1 = o1;
	this.o2 = o2;
    }

    public void chg() {
	if(o1 != null) o1.chg();
	if(o2 != null) o2.chg();
    }

}
