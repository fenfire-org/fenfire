/*
StormLoader.java
 *    
 *    Copyright (c) 2004, Matti J. Katila
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

package org.fenfire.modules.init;
import org.fenfire.view.management.*;
import org.nongnu.storm.*;
import org.nongnu.storm.impl.*;
import org.nongnu.storm.references.*;

import java.io.*;
import java.util.*;

import java.awt.*;

/** A loader/initalizer for user to use Storm.
 *
 *  XXX Creates a block signer if not exist.
 */
public class StormLoader implements FServer.RequestHandler {
    private void p(String s) { System.out.println("StormLoader:: "+s); }

    static final private String PRIV_KEY_FILENAME = "privateOwner.key";
    static final public String SIGNER = "storm-ptr-signer";

    // questions that are asked if no creation information exists.
    private String[] questions = new String[]{
	"Whole name", "E-mail", "Address",
	"Birthday", "Place of birth", 
	"Phone", "Additional information"
    };

    private IndexedPool pool;
    private PointerSigner signer;
    private FServer f;
    public StormLoader(FServer f) throws Exception {
	this.f = f;

	if (f.environment.createRequest("storm", this))
	    throw new Error("Storm loader already inited!");
	    
	String dir = System.getProperty("storm.pooldir");
	if (dir==null) 
	    throw new Error("dir pool property 'storm.pooldir' not defined!");
	File d = new File(new File(dir), "private");
	d.mkdirs();
	if (! d.isDirectory()) 
	    throw new Error("directory creation failed!");
	

	// XXX what pools are here? how acces is granted to them?

	pool = new BerkeleyDBPool(d, Collections.singleton(PointerIndex.type));

	if(!hasSignature(pool, d)) makeSignature(pool, d);
	//((BerkeleyDBPool) pool).flush();
	// check whether there are any 
	    
	signer = new PointerSigner(pool,
	   new FileInputStream(new File(d, PRIV_KEY_FILENAME)));

	if (f.environment.createRequest(SIGNER, this))
	    throw new Error("Storm signer already inited!");
    }

    public void handleRequest(Object req, Applitude app) {
    }
    public void handleRequest(Object req, Object[] o, Applitude app) {
	if (req.equals("storm"))
	    if (o instanceof StormPool[]) o[0] = pool;
	
	if (req.equals(SIGNER)) {
	    p("signer get: "+signer);
	    if (o instanceof PointerSigner[]) o[0] = signer;
	}
    }
    

    private boolean hasSignature(StormPool p, File dir) throws Exception {
	// String pointer = "application/prs.fallenstein.pointersignature";
	for (Iterator i = p.getIds().iterator(); i.hasNext();) {
	    BlockId id = (BlockId) i.next();
	    //if (id.getContentType() == pointer) return true;

	    Block b = pool.get(id);
	    p("id: "+id);
	    p("ct: "+id.getContentType());
	    if (id.getContentType().equals("image/png")) continue;
	    BufferedReader buf = new BufferedReader(
		new InputStreamReader(b.getInputStream(), "US-ASCII"));
	    String s = buf.readLine();
	    while (s != null) {
		p("-> "+s);
		s = buf.readLine();
	    }
	}
	//return false;
	return (new File(dir, PRIV_KEY_FILENAME)).exists();

    }

    private void  makeSignature(final IndexedPool pool, final File dir) 
	throws Exception {
	String doc = 
"Because your pool don't specify a block containing identifying information "+
"about yourself, you will be asked a few questions; your answers will be "+
"used to create the block. Questions that will be asked include name, "+
"address, e-mail, phone number, and place of birth. All answers are "+
"voluntary, and you don't need to share the created block publicly. \n"+
"\n"+
"    The reason for having this block is that we may later operate a "+
"registry allowing you to register a new cryptographic key, for example "+
"if your old one is stolen; without such a registry, you would not any "+
"more be able to update any of your old documents maintained in Storm. \n"+
"\n"+
"    The block will be used to prove to the registry that you are really "+
"the owner of this identity, so you will have to share this information "+
"with it. If you specify less information, you might find it harder to "+
"prove to the registry that you are really the legitimate owner of this "+
"identity; it's your choice. Do not specify incorrect information: that "+
"would make it impossible for you to register with the registry. Rather, "+
"just leave any field blank that you do not want to fill out.";

	Frame fr = new Frame("ARGH");
	Dialog dia = new Dialog(fr, "Creating a signature block - phase 1/2", true) {
		public boolean action(Event evt, Object arg) {
		    if("Ok".equals(arg))  {
			dispose();
			return true;
		    } return false;
		}
	    };
	
	
	p("foo");
	while (true) {
	    try {
		p("times...");
		f.environment.request("screen", null);
		break;
	    } catch (NullPointerException e) { }
	}
	p("plaah");
	Dimension dim = this.f.getScreen().getCurrentVS().size; 
	p("dia: "+dia+", dim: "+dim);
	dia.setBounds((int)dim.getWidth()/4, (int)dim.getHeight()/4,
		     (int)dim.getWidth()/2, (int)dim.getHeight()/2);
	
	BorderLayout gr = new BorderLayout();
	dia.setLayout(gr);
	dia.setResizable(false);
	TextArea text = new TextArea(doc, 80,80, 
	     TextArea.SCROLLBARS_VERTICAL_ONLY);
	text.setEditable(false);
	Button b = new Button("Ok");
	dia.add(text, BorderLayout.CENTER);
	dia.add(b, BorderLayout.SOUTH);
	dia.show();

	
	final Panel p = new Panel();
	p.setLayout(new GridLayout(questions.length, 2));
	final TextField[] fields = new TextField[questions.length * 2];
	final Button b2 = new Button("Create the signature block");
	dia = new Dialog(fr, "Creating a signature block - phase 2/2", true) {
		public boolean action(Event evt, Object arg) {
		    if("Create the signature block".equals(arg))  { try {
			remove(b2);
			remove(p);
			add(new TextArea("Creating signature block..."), BorderLayout.CENTER);
			show();
			String str = "";
			for(int i=0; i<fields.length;)
			    str += fields[i++].getText() + 
				fields[i++].getText() + "\n";
			BlockOutputStream bos = 
			    pool.getBlockOutputStream("text/plain");
			bos.write(str.getBytes());
			bos.close();
			
			PointerSigner signer = 
			    PointerSigner.createOwner(
				pool, bos.getBlockId());
			signer.writeKeys(
			    new java.io.FileOutputStream(
				new java.io.File(dir, PRIV_KEY_FILENAME)));

			dispose();
			return true;
		    } catch (Exception e) {
			e.printStackTrace();
			System.exit(2);
		    }}
		    return false;
		}
	    };
	
	dia.setBounds((int)dim.getWidth()/4, (int)(dim.getHeight()/4)-40,
		     (int)dim.getWidth()/2, (int)dim.getHeight()/2);
	
	dia.setLayout(new BorderLayout());
	dia.setResizable(false);
	for(int i=0; i<questions.length; i++) {
	    
	    fields[i*2] = new TextField(questions[i]+":");
	    fields[i*2+1] = new TextField("", 20);
	    p.add(fields[i*2]);
	    p.add(fields[i*2+1]);
	}
	
	dia.add(p, BorderLayout.CENTER);
	dia.add(b2, BorderLayout.SOUTH);
	dia.show();
	p("done");
	dia.hide();
	
    }

    

}
