/*
MMTextCursor.java
 *    
 *    Copyright (c) 2003, Asko Soukka
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
 * Written by Asko Soukka
 */

/**
 * Cursor class especially designed for MM purposes. Could be
 * inherited from some more general class later on.
 */

package org.fenfire.fenmm;

import org.nongnu.libvob.VobScene;
import org.nongnu.libvob.TextStyle;
import org.nongnu.libvob.vobs.ContinuousLineVob;
import org.fenfire.swamp.MultiplexerNodeFunction;

import org.nongnu.libvob.lava.placeable.TextPlaceable;

import java.awt.Color;
import java.util.HashSet;

public class MMTextCursor {
    public static final float DEFAULT_SCALE = 1f;
    public static final Color DEFAULT_COLOR = Color.black;
    static class NotYetRenderedException extends Exception {}

    protected final Color color;
    protected final TextStyle style;
    protected final float scale;
 
    private Object accursed = null;
    private TextPlaceable p = null;
    private int offset = 0;
    private int length = 0;

    public MMTextCursor(TextStyle style, Color color, float scale) {
	this.color = color;
	this.style = style;
	this.scale = scale;
    }
    public MMTextCursor(TextStyle style, Color color) {
	this(style, color, DEFAULT_SCALE);
    }
    public MMTextCursor(TextStyle style) {
	this(style, DEFAULT_COLOR, DEFAULT_SCALE);
    }

    public void setAccursed(Object node) { accursed = node; offset = 0; }

    public Object getAccursed() { return accursed; }
    public int setOffset(int offset) { this.offset = offset; return this.offset; }
    public int setOffset(float x, float y) throws NotYetRenderedException {
	if (p == null) throw new NotYetRenderedException();
	offset = p.getCursorPos(x, y);
	return offset;
    }
    public int getOffset() { return offset; }

    /** Move the cursor a character to the left. */
    public int moveLeft() {
	if (offset > 0) offset -= 1;
	return offset;
    }
    /** Move the cursor a character to the right. */
    public int moveRight() throws NotYetRenderedException {
	if (p == null) throw new NotYetRenderedException();
	if (length == 0) length = getLength();
	if (offset < length) offset += 1;
	return offset;
    }
    /** Move the cursor to the previous line. */
    public int moveUp() throws NotYetRenderedException {
	if (p == null) throw new NotYetRenderedException();
        float cursorXY[] = new float[3];
	p.getCursorXYY(offset, cursorXY);
        cursorXY[2] -= style.getHeight(scale);
        offset = p.getCursorPos(cursorXY[0], cursorXY[2]);
	return offset;
    }
    /** Move the cursor to the next line. */
    public int moveDown() throws NotYetRenderedException {
	if (p == null) throw new NotYetRenderedException();
        float cursorXY[] = new float[3];
        p.getCursorXYY(offset, cursorXY);
        cursorXY[2] += style.getHeight(scale);
        offset = p.getCursorPos(cursorXY[0], cursorXY[2]);
	return offset;
    }
    /** Move the cursor into the beginning of the text. */
    public int moveBegin() {
	offset = 0;
	return offset;
    }
    /** Move the cursor to the end of the text. */
    public int moveEnd() throws NotYetRenderedException {
	if (p == null) throw new NotYetRenderedException();
	if (length == 0) length = getLength();
	offset = length;
	return offset;
    }
    /** Move the cursor into the beginning of the line. */
    public int moveBeginLine() throws NotYetRenderedException{
	if (p == null) throw new NotYetRenderedException();
	float cursorXY[] = new float[3];
	p.getCursorXYY(offset, cursorXY);
	cursorXY[0] = 0;
        offset = p.getCursorPos(cursorXY[0], cursorXY[2]);
	return offset;
    }
    /** Move the cursor to the end of the line. */
    public int moveEndLine() throws NotYetRenderedException {
	if (p == null) throw new NotYetRenderedException();
	if (length == 0) length = getLength();
	// the end of line is reached by going to home of the line below
	// and returning a single character back if not the end of text was reached
	float cursorXY[] = new float[3];
	p.getCursorXYY(offset, cursorXY);
	cursorXY[0] = 0;
	cursorXY[2] += style.getHeight(scale);
	offset = p.getCursorPos(cursorXY[0], cursorXY[2]);
	if (offset < length) offset -= 1;
	return offset;
    }

    /** Way to use without rendering. */
    public void setTextPlaceable(TextPlaceable p) {
	this.p = p;
	this.length = 0;
    }

    public void render(VobScene vs, int into, int matchCS, TextPlaceable p) {
	render(vs, into, matchCS, -1, p);
    }
    public void render(VobScene vs, int into, int matchCS, int matchParentCS, TextPlaceable p) {
	setTextPlaceable(p);

	float cursorXY[] = new float[3];
        this.p.getCursorXYY(offset, cursorXY);

        int nodeCS = 0;
	int cursorCS = 0;
	if (matchParentCS == -1) {
	    cursorCS = vs.coords.ortho(into, -1000,
				       -p.getWidth()/2 + cursorXY[0],
				       -p.getHeight()/2 + cursorXY[2],
				       1, -style.getHeight(scale));
	    vs.matcher.add(matchCS, cursorCS, "CURSOR");
	} else {
	    nodeCS = vs.matcher.getCS(matchParentCS, accursed);
	    float nodeXY[] = {0f, 0f, 0f};
	    vs.coords.transformPoints3(nodeCS, nodeXY, nodeXY);
	 
	    cursorCS = vs.coords.ortho(into, -1000,
				      nodeXY[0] + cursorXY[0],
				      nodeXY[1] + cursorXY[2],
				      1, -style.getHeight(scale));
	    vs.matcher.add(matchCS, cursorCS, "CURSOR");
	}

	float points[] = {0,0,0,0,1,0};
	vs.put(new ContinuousLineVob(2f, points, false, 100, color), cursorCS);
    }

    private int getLength() throws NotYetRenderedException {
	if (p == null) throw new NotYetRenderedException();
	return p.getCursorPos(p.getWidth(), p.getHeight());
    }
}
