/*
RST.java
 *    
 *    Copyright (c) 2003, Matti J. Katila
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
 * Written by Matti J. Katila
 */

package org.fenfire.vocab.lava;
import org.fenfire.swamp.*;


/** Vocabulary for RST text like canvas */
public class RST {
    protected static void pa(String s) { System.out.println("RST: "+s); }

    // Propertys
    static final public Object beginParagraph;
    static final public Object nextSentence;
    static final public Object nextNode;
    static final public Object firstSentence;
    static final public Object width;


    // Resources
    static final public Object Canvas;
    static final public Object Paragraph;
    static final public Object Sentence;
    static final public Object Node;

    static final public Object NewLine;

    // Gen as generated
    static final public Object GenSpace;
    static final public Object GenNewLine;

    static {
	String rst = "http://fenfire.org/vocabulary/rst.html#";

	// Propertys
	beginParagraph = Nodes.get(rst+"beginParagraph");
	nextNode = Nodes.get(rst+"nextNode");
	nextSentence = Nodes.get(rst+"nextSentence");
	firstSentence = Nodes.get(rst+"firstSentence");
	width = Nodes.get(rst+"width");

	// Resources
	Canvas = Nodes.get(rst+"Canvas");
	Paragraph = Nodes.get(rst+"Paragraph");
	Sentence = Nodes.get(rst+"Sentence");
	Node = Nodes.get(rst+"Node");
	NewLine = Nodes.get(rst+"NewLine");
	GenSpace = Nodes.get(rst+"GenSpace");
	GenNewLine = Nodes.get(rst+"GenNewLine");
    }
}
