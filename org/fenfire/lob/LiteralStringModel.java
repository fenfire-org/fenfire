/*   
LiteralStringModel.java
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
package org.fenfire.lob;
import org.nongnu.navidoc.util.Obs;
import org.nongnu.libvob.layout.*;
import org.fenfire.swamp.*;

public class LiteralStringModel extends AbstractModel.AbstractObjectModel {

    protected Model literal;

    public LiteralStringModel(Model literal) {
	this.literal = literal;
	literal.addObs(this);
    }

    protected Replaceable[] getParams() {
	return new Replaceable[] { literal };
    }
    protected Object clone(Object[] params) {
	return new LiteralStringModel((Model)params[0]);
    }

    public Object get() {
	Literal l = (Literal)literal.get();
	if(l == null) return "";
	return l.getString();
    }

    public void set(Object value) {
	String s = (String)value;
	if(s != null)
	    literal.set(new PlainLiteral(s)); // XXX datatype/langtag support
	else
	    literal.set(null);
    }
}
