/*
EnfiladeOverlapIndex.java
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
import java.util.*;
import org.fenfire.index.*;
import org.nongnu.alph.impl.Enfilade1DImpl;

/** A simple index which returns all entries whose enfilade
 * overlaps with the given one.
 * VERY INEFFICIENT.
 */
public class EnfiladeOverlapIndex implements Index {

    private HashMap map;
    public Map getContents() { 
	return Collections.unmodifiableMap(map);
    }

    public EnfiladeOverlapIndex() {
	clear();
    }
    public void clear() {
	map = new HashMap();  
    }

    public void set(int dir, Object entryName, Object value) {
	if(value == null || dir < 0) {
	    map.remove(entryName);
	    return;
	}
	if(!(value instanceof Enfilade1DImpl)) return;
	map.put(entryName, value);
    }

    public Collection getMatches(Object o) {
	Set s = map.entrySet();
	List ret = new ArrayList();
	Enfilade1DImpl searchenf = (Enfilade1DImpl)o;
	for(Iterator i = s.iterator(); i.hasNext(); ) {
	    Map.Entry ent = (Map.Entry)i.next();
	    Enfilade1DImpl enf = (Enfilade1DImpl)ent.getValue();
	    if(enf == null) continue;
	    if(enf.intersects(searchenf))
		ret.add(ent.getKey());
	}
	return ret;
    }

}
