/*
Loom.java
 *
 *    Copyright (c) 2003 by Benja Fallenstein
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
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.util;
import java.awt.*;
import java.awt.event.*;

/** Yet another message box implementation, again raising the
 *  old question: Why isn't there one in AWT?
 *  The msgBox() method doesn't block (because I wasn't able
 *  to make it; I got muzzled by Java's threading).
 */
public class MsgBox extends Dialog {
    Label label = new Label();
    Button ok = new Button("Ok");

    public MsgBox(Frame parent) {
	super(parent);
	setLayout(new FlowLayout());
	add(label);
	add(ok);
	ok.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setVisible(false);
		}
	    });
    }

    public void msgBox(String title, String msg) {
	setTitle(title);
	label.setText(msg);
	setVisible(true);
    } 

    /** For testing.
     */
    public static void main(String[] args) {
	Frame f = new Frame();
	MsgBox b = new MsgBox(f);
	b.msgBox("First message", "First message of two");
	while(b.isVisible());
	b.msgBox("2nd message", "Second message of two");
	while(b.isVisible());
	System.exit(0);
    }
}
