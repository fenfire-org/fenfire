/*
Cursor.java
 *    
 *    Copyright (c) 2003, Matti Katila
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
 * Written by Matti Katila
 */

package org.fenfire.view.lava;
import org.fenfire.*;
import org.fenfire.util.*;


/** XXX in wrong package. This is a part 
 * of a view but provides nothing to see.
 *
 * Cursor is usually a pointer in text to show 
 * current screen position. Only one cursor 
 * should be shown in the view.
 */
public class Cursor {

    private AlphContent alph;
    public Cursor(AlphContent alph) { 
	this.alph = alph;
    }

    private int cursorOffset = 0;

    /** @return current and correct offset for cursor 
     */
    public int setCursorOffset(int offset) { 
	cursorOffset = offset;
	checkOffset();
	return cursorOffset;
    }
    public int getCursorOffset() { return cursorOffset; }

    private Object accursed = null;
    public void setAccursed(Object node) {
	accursed = node;
	setCursorOffset(0);
    }
    public Object getAccursed() { return accursed; }
    public boolean isAccursed(Object node) {
	return node == accursed; 
    }
    public boolean hasAccursed() {
	return accursed != null; 
    }

    private void checkOffset() {
	if (cursorOffset < 0) cursorOffset = 0;
	cursorOffset = (int) Math.min(cursorOffset, length() );
    }
    private int length() {
	String s = alph.getText(accursed);
	return s.length();
    }
}
