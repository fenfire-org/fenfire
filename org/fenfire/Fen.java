/*
Fen.java
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

package org.fenfire;

/** The central abstraction: the space that a fenfire client operates on.
 */
public class Fen {
    /** The RDF model of the graph structure part of this fen.
     */
    public org.fenfire.swamp.Graph graph;

    /** The RDF model of the graph structure part of this fen.
     */
    public org.fenfire.swamp.ConstGraph constgraph;

    /** The transclusion index.
     */
    public org.fenfire.index.Index enfiladeOverlap;

    /** The span maker for user-typed spans.
     */
    public org.nongnu.alph.SpanMaker userSpanMaker =
	new org.nongnu.alph.impl.URN5SpanMaker();

    /** The span maker for computer-generated spans.
     */
    public org.nongnu.alph.SpanMaker fakeSpanMaker = 
	new org.nongnu.alph.impl.URN5SpanMaker();
    
    /** The enfilade maker.
     */
    public org.nongnu.alph.Enfilade1D.Maker enfMaker =
	new org.nongnu.alph.impl.Enfilade1DImpl.Enfilade1DImplMaker();
    
    /** The alph used.
     * XXX Not formally approved but needed for fenpdf10
     */
    public org.nongnu.alph.Alph alph;

    /** The set of xanadu links in use with this Fen.
     */
//    public XuIndexer xuLinks;

}


