/*   
Editor.java
 *    
 *    Copyright (c) 2004, Benja Fallenstein.
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
 * Written by Benja Fallenstein
 */
package org.fenfire.fenedit;
import org.nongnu.libvob.*;
import org.nongnu.libvob.impl.Main;
import org.nongnu.libvob.impl.LobMain;
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.layout.Action;
import org.nongnu.libvob.layout.unit.*;
import org.nongnu.libvob.layout.component.*;
import org.nongnu.libvob.vobs.*;
import org.nongnu.libvob.util.*;
import org.nongnu.navidoc.util.Obs;
import org.nongnu.alph.*;
import java.awt.Color;

public class Editor extends LobLob {

    protected Model tstringModel;
    protected TextModel textModel;
    protected Model textCursorModel;

    protected WindowManager.SimpleWindowManager windowManager;

    protected java.awt.Frame awtFrame = new java.awt.Frame();
    protected java.awt.FileDialog fileDialog =
	new java.awt.FileDialog(awtFrame);

    static private org.nongnu.alph.xml.TIMLReader timlReader = 
    	new org.nongnu.alph.xml.TIMLReader();

    /**
    public void setScreen(Screen screen) {
        super.setScreen(screen);
	if(screen.window instanceof org.nongnu.libvob.impl.awt.FrameScreen) {
	    awtFrame = ((org.nongnu.libvob.impl.awt.FrameScreen)screen.window).getFrame();
	} else {
	    awtFrame = new java.awt.Frame();
	}
	fileDialog = new java.awt.FileDialog(awtFrame);
    }
    **/

    protected Action open = new AbstractAction() { public void run() {
	    boolean wasVisible = awtFrame.isVisible();
	    awtFrame.setVisible(true);
	    fileDialog.requestFocus();
	    fileDialog.show();
	    awtFrame.setVisible(wasVisible);
	    if(fileDialog.getFile() == null) return;
	    String fileName = fileDialog.getDirectory() + fileDialog.getFile();
	    openFile(fileName);
    }};
    
    protected void openFile(String fileName) {
	TString text;
	try {
	    text = timlReader.read(new java.io.FileInputStream(fileName));
	} catch(Exception e) {
	    e.printStackTrace();
	    return;
	}
	textCursorModel.setInt(0);
	tstringModel.set(text);
    }

    protected Action save = new AbstractAction() { public void run() {
	    boolean wasVisible = awtFrame.isVisible();
	    awtFrame.setVisible(true);
	    fileDialog.show();
	    awtFrame.setVisible(wasVisible);
	    if(fileDialog.getFile() == null) return;
	    String fileName = fileDialog.getDirectory() + fileDialog.getFile();
	    TString text = (TString)tstringModel.get();
	    try {
		java.io.OutputStream out = 
		    new java.io.FileOutputStream(fileName);
		out.write(text.toXML().getBytes("UTF-8"));
		out.close();
	    } catch(Exception e) {
		e.printStackTrace();
		return;
	    }
	    return;
    }};

    protected Action quit = new AbstractAction() { public void run() {
	    System.exit(0);
    }};

    protected class SetColor extends AbstractAction {
	protected Color color;
	protected SetColor(Color color) { this.color = color; }
	public void run() { Theme.darkColor.set(color); }
    }

    protected Action red = new SetColor(new Color(.7f, .5f, .5f));
    protected Action green = new SetColor(new Color(.6f, .7f, .5f));
    protected Action blue = new SetColor(new Color(.5f, .6f, .7f));

    public boolean key(String s) {
	if(s.equals("Ctrl-O")) {
	    lobOpen.run();
	} else if(s.equals("Ctrl-S")) {
	    save.run();
	} else if(s.equals("Ctrl-Q")) {
	    quit.run();
	} else if(s.equals("Ctrl-M")) {
	    showMessage.run();
	} else if(s.equals("Ctrl-R")) {
	    red.run();
	} else if(s.equals("Ctrl-G")) {
	    green.run();
	} else if(s.equals("Ctrl-B")) {
	    blue.run();
	} else {
	    return delegate.key(s);
	}
	
	AbstractUpdateManager.chg();
	return true;
    }

    public Editor() {
	    LobFont font = new LobFont("Serif", 0, 16, java.awt.Color.black);
	    Model fontModel = new ObjectModel(font);
	    
	    tstringModel = new ObjectModel(TString.newFake(""));
	    textModel = new TimlTextModel(tstringModel, fontModel);

	    Model searchTStringModel = new ObjectModel(TString.newFake(""));
	    TextModel searchTextModel = new TimlTextModel(searchTStringModel,
							  fontModel);

	    float inf = Float.POSITIVE_INFINITY;
	    
	    Lob search = new TextField(searchTextModel);
	    TextArea area = new TextArea(textModel);

	    textCursorModel = area.getTextCursorModel();

	    Box vbox = new Box(Y);
	    vbox.glue(1, 1, 1);
	    if(false) {
		vbox.add(search);
		vbox.glue(2, 2, 2);
	    }
	    vbox.addRequest(area, 40, 40, inf);
	    vbox.glue(1, 1, 1);

	    Box hbox = new Box(X);
	    hbox.glue(20, 20, inf);
	    hbox.addRequest(vbox, 100, 500, 500);
	    //hbox.glue(20, 20, inf);
	    AnchorLob anchor1 = new AnchorLob(new Glue(Lob.X, 20, 20, inf));
	    hbox.add(anchor1);

	    Menu menubar = new Menu(X);
	    Menu filemenu = new Menu(Y);
	    Menu editmenu = new Menu(Y);

	    menubar.add("File", filemenu);
	    menubar.add("View", editmenu);
	    menubar.close();

	    Action doNothing = new AbstractAction() { public void run() {
		System.out.println("not implemented");
	    } };

	    filemenu.add("Open...", open);
	    filemenu.add("Open (Lob-based dialog)...", lobOpen);
	    filemenu.add("Save...", save);
	    filemenu.add("Show message box", showMessage);
	    filemenu.add("Quit", quit);

	    editmenu.add("Red", red);
	    editmenu.add("Green", green);
	    editmenu.add("Blue", blue);
	    editmenu.close();

	    AnchorLob anchor2 = new AnchorLob(menubar);

	    vbox = new Box(Y);
	    vbox.add(anchor2);
	    vbox.add(hbox);

	    Vob connection = 
		new org.nongnu.libvob.vobs.SimpleConnection(.5f,.5f,.5f,.5f);

	    Lob logo;
	    
	    try {
		logo = new Image(new java.io.File("ff_logo.png"));
	    } catch(java.io.IOException e) {
		logo = new NullLob();
		System.err.println("Error while loading Fenfire logo: "+e);
		System.err.println("I'll continue without displaying "+
				   "the logo");
	    }

	    Lob l  = new Between(new AlignLob(logo, 1, 1, 1, 1),
				 vbox, new NullLob());

	    // DEMO: show a connection between two lobs
	    //l = new Decoration(l, connection, anchor1, anchor2);

	    l = new FocusLob(l);
	    windowManager = new WindowManager.SimpleWindowManager(l);

	    setDelegate(windowManager);
    }


    protected Action showMessage = new AbstractAction() { public void run() {
	new MsgBox("Test message", "Message", windowManager);
    }};


    protected Action lobOpen = new AbstractAction() { public void run() {
	new FileDialog(new FileDialog.Listener() {
		public void fileChosen(java.io.File file) {
		    openFile(file.getAbsolutePath());
		}
	    }, windowManager);
    }};


    public static class EditMain extends LobMain {
	public EditMain(Color bgColor) { super(bgColor); }

	protected Lob createLob() {
	    return new Editor();
	}
    }

    public static void main(String[] argv) {
	Main m = new EditMain(new Color(1, 1, .8f));
	m.start();
    }
}
