/* -*-java-*-
Http.java
 *
 *    Copyright (c) 2005 by Matti J. Katila
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
 * Written by Matti J. Katila
 */

package org.fenfire.storm;
import org.fenfire.bin.*;
import org.fenfire.fenfeed.http.*;
import org.nongnu.storm.*;
import org.nongnu.storm.impl.*;
import org.nongnu.storm.util.*;

import java.io.*;
import java.util.*;

public class Http {
    static public boolean dbg = false;
    static private void p(String s) { System.out.println("Http:: "+s); }


    static public void pullHttp(Dir d, String src, File root) throws IOException {
	if (dbg) p("PullHTTP: "+d+", src: "+src+", root: "+root);
	for (Iterator i=d.iterator(); i.hasNext();) {
	    String name = (String) i.next();
	    String hash = d.get(name);

	    String bp = (new BlockId(hash)).getBitprint();
	    String ct = getCT(src+"/_storm/pool/types_"+bp);
	    CopyUtil.copy(getData(src+"/_storm/pool/data_"+bp), 
			  new FileOutputStream(new File(root, "_storm/pool/data_"+bp)));
	    CopyUtil.copy(getData(src+"/_storm/pool/types_"+bp), 
			  new FileOutputStream(new File(root, "_storm/pool/types_"+bp)));
	    if (ct.equals("application/storm-x-dir"))
		pullHttp(d.cd(name), src, root);
	}
    }

    static public String getCT(String uri) throws IOException {
	if (dbg) p("get content type: "+uri);
	HTTPContext context = new HTTPContext();
	context.setAccept("application/storm-x-dir,"+
			  "application/storm-x-file,"+
			  "*/*");
	HTTPResource res = new HTTPResource(uri, context);
	InputStream in = res.getInputStream();
	String ct = (new BufferedReader(
			 new InputStreamReader(in))
	    ).readLine();
	return ct;
    }
    static public InputStream getData(String uri) throws IOException {
	if (dbg) p("get data: "+uri);
	HTTPContext context = new HTTPContext();
	context.setAccept("application/storm-x-dir,"+
			  "application/storm-x-file,"+
			  "*/*");
	HTTPResource res = new HTTPResource(uri, context);
	return res.getInputStream();
    }


}
