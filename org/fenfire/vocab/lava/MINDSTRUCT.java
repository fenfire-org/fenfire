/*
MINDSTRUCT.java
 *    
 *    Copyright (c) 2003, Matti J. Katila, Asko Soukka
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
 * Written by Matti J. Katila, Asko Soukka
 */


package org.fenfire.vocab.lava;
import org.fenfire.swamp.Nodes;

/** Simple MindMap structure vocabulary for data in MinMap diagrams. */
public class MINDSTRUCT {
    static public final String _nsId = 
	"http://fenfire.org/EXPERIMENTAL/rdf-v/2003/06/mindstruct";
    static public final Object Data;

    static { Data = Nodes.get(_nsId + "#Data"); }
}
