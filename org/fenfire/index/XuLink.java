/*
XuLink.java
 *    
 *    Copyright (c) 2003, : Tuomas J. Lukka
 *    
 *    This file is part of Gzz.
 *    
 *    Gzz is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Gzz is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Gzz; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *    
 */
/*
 * Written by : Tuomas J. Lukka
 */

package org.fenfire.index;
import org.nongnu.alph.*;

/** An immutable class representing a single Xanadu model link.
 */
public final class XuLink {
    // XXX Id?
    public final Enfilade1D from;
    public final Enfilade1D to;
    public XuLink(Enfilade1D from, Enfilade1D to) {
	this.from = from;
	this.to = to;
    }
}
