/*
TransientXuIndexer.java
 *    
 *    Copyright (c) 2002, Tuomas J. Lukka
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
 * Written by Tuomas J. Lukka
 */

package org.fenfire.index.impl;
import org.fenfire.index.*;

/** An index of xanadu links.
 */
public class TransientXuIndexer implements XuIndexer {
    EnfiladeOverlapIndex
	    f = new EnfiladeOverlapIndex(),
	    b = new EnfiladeOverlapIndex();
    
    public Index getForwardIndex() { return f; }
    public Index getBackwardIndex() { return b; }

    public void add(XuLink link) {
	f.set(1, link, link.from);
	b.set(1, link, link.to);
    }
    
    public void remove(XuLink link) {
	f.set(-1,link, link.from);
	b.set(-1,link, link.to);
    }
}
