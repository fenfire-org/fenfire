/*   
TextNodeView.java
 *    
 *    Copyright (c) 2003, Benja Fallenstein, Asko Soukka
 *
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
 *
 */
/*
 * Written by Benja Fallenstein, Asko Soukka
 */
package org.fenfire.view;
import org.fenfire.swamp.*;
import org.fenfire.functional.*;
import org.nongnu.alph.*;
import org.nongnu.libvob.*;
import org.nongnu.libvob.linebreaking.*;
import org.nongnu.libvob.vobs.*;

import java.awt.Color;
import java.lang.Math;

/** 
 * A node function returning a vob that shows the given node as text.
 * The maximum size of a single line is determined by <pre>width</pre>.
 */
public class TextNodeView implements PureNodeFunction {

    public static boolean dbg = false;
    private static void pa(String s) { System.out.println("TextNodeView::"+s); }

    public final static float DEFAULT_SCALE = 1f;
    public final static float DEFAULT_WIDTH = 300f;
    public final static Color DEFAULT_TEXT_COLOR = Color.black;

    private final SimpleLinebreaker breaker = new SimpleLinebreaker();
    private final NodeFunction nodeContent;
    private final TextStyle style;
    private final Color textColor;
    private final float scale;
    private final float width;

    public TextNodeView(NodeFunction nodeContent,
			TextStyle style,
			Color textColor,
			float width,
			float scale) {
	this.nodeContent = nodeContent;
        this.style = style;
	this.textColor = textColor;
	this.width = width;
	this.scale = scale;
    }
    public TextNodeView(NodeFunction nodeContent, TextStyle style,
			Color textColor, float width) {
	this(nodeContent, style, textColor, width, DEFAULT_SCALE);
    }
    public TextNodeView(NodeFunction nodeContent, TextStyle style,
			Color textColor) {
	this(nodeContent, style, textColor, DEFAULT_WIDTH, DEFAULT_SCALE);
    }
    public TextNodeView(NodeFunction nodeContent, TextStyle style,
			float width) {
	this(nodeContent, style, DEFAULT_TEXT_COLOR, width, DEFAULT_SCALE);
    }
    public TextNodeView(NodeFunction nodeContent, TextStyle style) {
	this(nodeContent, style, DEFAULT_TEXT_COLOR, DEFAULT_WIDTH, DEFAULT_SCALE);
    }

    public Object f(ConstGraph g, Object node) {
	final Enfilade1D enf = (Enfilade1D)nodeContent.f(g, node);
	final String s = enf.makeString();
	if (s.length() == 0) 
	    return new org.nongnu.libvob.lava.placeable.TextPlaceable() {
		    public void place(VobScene vs, int into) {}
		    public float getWidth() { return 10; }
		    public float getHeight() { return 10; }
		    public void getCursorXYY(int position, float[] xyOut) { getXYY(s, position, xyOut); }
		    public int getCursorPos(float x, float y) { return getPos(s, x, y); }
		};
	final HChain ch = getChain(s);
	final HBroken br = breaker.breakLines(ch, width, scale);
	final float height = br.getHeight();

	final float width;
	if(br.getLineCount() > 1) { // Let's get the longest line
	    float maxWidth = br.getLineWidth(0);
	    for (int i=1; i<br.getLineCount(); i++)
		if (br.getLineWidth(i) > maxWidth)
		    maxWidth = br.getLineWidth(i);
	    width = maxWidth;
	} else width = br.getLineWidth(0);

	//// Code of the old single line TextNodeView
	//final TextVob vob = new TextVob(style, s, false);
	//final float width = style.getWidth(s, scale);
	//final float height = style.getHeight(scale);

	if(dbg) {
	    pa(" "+s+"' "+width+" "+height+" "+scale+" "+br);
	}

	return new org.nongnu.libvob.lava.placeable.TextPlaceable() {
		public void place(VobScene vs, int into) {
		    br.put(vs, into);
		}
		public float getWidth() { return width; }
		public float getHeight() { return height; }
		public void getCursorXYY(int position, float[] xyOut) { getXYY(s, position, xyOut); }
		public int getCursorPos(float x, float y) { return getPos(s, x, y); }
	    };
    }
    
    /**
     * Get the position of the first character placed the most
     * closest to the given coordinates.
     */
    public int getPos(ConstGraph g, Object node, float x, float y) {
	Enfilade1D enf = (Enfilade1D)nodeContent.f(g, node);
	return getPos(enf.makeString(), x, y);
    }
    public int getPos(String s, float x, float y) {
	if (s.length() == 0) return 0;

	HChain ch = getChain(s);
	HBroken br = breaker.breakLines(ch, width, scale);

	int pos = 0;
	int line = 0;
	if (y < 0) return 0;
	while (line < br.getLineCount()){
	    if (y > br.getLineOffset(line)) line++;
	    else break;
	}

	float xoffs = 0;
	for (int i=0; i<ch.length(); i++) {
	    int newLine = br.getLine(pos, null);
	    if (newLine < line) pos += ch.getBox(i).getLength();
	    else if (newLine == line) {
		if (xoffs + ch.getBox(i).getWidth(scale) < x) {
		    pos += ch.getBox(i).getLength();
		    xoffs += ch.getBox(i).getWidth(scale);
		} else {
		    for (int j=0; j<ch.getBox(i).getLength(); j++) {
			if (xoffs + ch.getBox(i).getX(j, scale) < x) pos++;
			else {
			    xoffs += ch.getBox(i).getX(j, scale);
			    break;
			}
		    }
		    break;
		}
	    } else break;
	}
	while (br.getLine(pos, null) > line) pos--;

	// more tuning
	if (xoffs > 0) {
	    float xy[] = new float[1];
	    br.getLine(pos-1, xy);
	    if (dbg) pa("x: "+x+" xoffs: "+xoffs+" xy: "+xy[0]);
	    if (Math.abs(x - xy[0]) < Math.abs(x - xoffs)) pos--;
	}
	
	return pos;
    }

    /** 
     * Get the coordinates before the given character position.
     * Two Y coordinates are returned, above and below the line.
     */
    public void getXYY(ConstGraph g, Object node, int pos, float[] xy) {
	Enfilade1D enf = (Enfilade1D)nodeContent.f(g, node);
	getXYY(enf.makeString(), pos, xy);
    }
    public void getXYY(String s, int pos, float[] xy) {
	if (s.length() == 0) {
	    if (xy != null && xy.length >= 2) {
		xy[0] = 0;
		xy[1] = style.getHeight(scale) + style.getAscent(scale);
	    }
	    return;
	}

	HChain ch = getChain(s);
	HBroken br = breaker.breakLines(ch, width, scale);

	float xoffs[] = new float[1];
	int line = br.getLine(pos, xoffs);
	
	if (xy != null && xy.length >= 2) {
	    // XXX the x offs doesn't seem to be exact,
	    //     when there is a lot of spaces. An old bug.
	    xy[0] = xoffs[0];
	    xy[1] = br.getLineOffset(line-1);
	    xy[2] = br.getLineOffset(line);
	}
    }

    protected HChain getChain(String s) {
	HChain ch = new LinebreakableChain();

	int pos = 0;
	int last;
	while(pos < s.length()) {
	    last = pos;
	    int sp = s.indexOf(' ', pos);
	    int br = s.indexOf('\n', pos);

	    if(sp >= 0 && br >= 0)
	        pos = sp<br ? sp+1 : br;
            else if(sp >= 0) pos = sp+1;
	    else if(br >= 0) pos = br;
	    else
	        pos = s.length();

            addVobs(s, ch, last, pos);
	    if(pos == br) {
		ch.addBox(new HBox.Null(1));
	        ch.addBreak();
	        pos++;
	    }
	}

	return ch;
    }

    protected void addVobs(String s, HChain ch, int start, int end) {
        Object key = new Integer(start+1472);
	s = s.substring(start, end);

	if(dbg) pa("addVobs: "+start+" "+end+" '"+s+"'");

        TextVob vob = new TextVob(style, s, false, key, textColor);
        ch.addBox(vob);
    }
}
