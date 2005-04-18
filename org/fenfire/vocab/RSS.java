/*
RSS.java
 *    
 *    Copyright (c) 2004, Benja Fallenstein
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
 * Written by Tuukka Hastrup
 */

package org.fenfire.vocab;
import org.fenfire.swamp.Nodes;

public class RSS {

    static public String namespace = "http://purl.org/rss/1.0/";

    static public Object description = Nodes.get(namespace+"description");
    static public Object encoded = 
	Nodes.get(namespace+"modules/content/encoded");
}
