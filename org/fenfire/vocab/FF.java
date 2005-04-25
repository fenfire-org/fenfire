/*
FF.java
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

package org.fenfire.vocab;
import org.fenfire.swamp.Nodes;

/** RDF Vocabulary of central concepts of Fenfire.
 */
public class FF {
    static public final String _nsId = 
	"http://fenfire.org/rdf-v/2003/05/ff#";

    /** A property signifying fluid media "content" of a node.
     * Used as  (node, FF.content, literal) where the literal is
     * an XML literal containing an enfilade
     * parseable by alph.
     * This is analogous to spreadsheet or zzStructure cell contents.
     */
    static public final Object content = Nodes.get(_nsId + "content");


    /** Alph vocabulary for images
     */
    static public final Object AlphImg = Nodes.get("http://fenfire.org/rdf-v/2005/03/alph-image");


    static public final String pspdf = 
	"http://fenfire.org/rdf-v/2005/04/ps-pdf-region#";

    static public final Object PsPdfDocument 
	= Nodes.get(pspdf+"PsPdfDocument");

    static public final Object PsPdfRegion = Nodes.get(pspdf+"PsPdfRegion");

    static public final Object regionOf    = Nodes.get(pspdf+"regionOf");

    /** Pages are one-based, i.e., the first page is page 1
     *  endPage/endX/endY are inclusive, i.e., startPage = 1 and endPage = 2
     *  means a region consisting of two pages (not one).
     */
    static public final Object startPage   = Nodes.get(pspdf+"startPage");
    static public final Object endPage     = Nodes.get(pspdf+"endPage");
    static public final Object startX      = Nodes.get(pspdf+"startX");
    static public final Object endX        = Nodes.get(pspdf+"endX");
    static public final Object startY      = Nodes.get(pspdf+"startY");
    static public final Object endY        = Nodes.get(pspdf+"endY");

    static public final Object startNode = Nodes.get(_nsId+"startNode");
}

