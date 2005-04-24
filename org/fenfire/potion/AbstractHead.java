/*
AbstractHead.java
 *    
 *    Copyright (c) 2002-2005, Benja Fallenstein
 *    
 *    This file is part of Fenfire.
 *    
 *    Fenfire is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Fenfire is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU Lesser General
 *    Public License along with Fenfire; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *    
 */
/*
 * Written by Benja Fallenstein
 */
package org.fenfire.potion;
import org.nongnu.libvob.lob.LobFont;
import org.nongnu.libvob.lob.Lists;
import java.util.*;

public abstract class AbstractHead implements Head {
    protected Object[] spec;
    
    protected AbstractHead(Object[] spec) {
	this.spec = spec;
    }

    public Head instantiatePattern(Map context) {
	return this;
    }
    
    public String getString(Expression[] params, Map context) {
	String s = "";
	int p = 0;
	
	for(int i=0; i<spec.length; i++) {
	    if(spec[i] instanceof String)
		s += (String)spec[i];
	    else if(spec[i] instanceof Type) {
		if(params[p] != null) 
		    s += params[p].getString(context);
		else
		    s += ((Type)spec[i]).getQuestionString();

		p++;
	    } else
		throw new IllegalArgumentException("Neither String nor Type: "+spec[i]);
	}

	return s;
    }

    public List getLobs(Expression[] params, Map context, LobFont font) {
	List parts = Lists.list();
	int p = 0;
	
	for(int i=0; i<spec.length; i++) {
	    if(spec[i] instanceof String)
		parts.add(font.text((String)spec[i]));
	    else if(spec[i] instanceof Type) {
		if(params[p] != null) 
		    parts.add(params[p].getLobs(context, font));
		else
		    parts.add(((Type)spec[i]).getQuestionLobs(font));

		p++;
	    } else
		throw new IllegalArgumentException("Neither String nor Type: "+spec[i]);
	}

	return Lists.concatElements(parts);
    }

    public Type[] getParams() {
	List types = new ArrayList();
	for(int i=0; i<spec.length; i++)
	    if(spec[i] instanceof Type)
		types.add(spec[i]);

	Type[] result = new Type[types.size()];
	for(int i=0; i<result.length; i++)
	    result[i] = (Type)types.get(i);

	return result;
    }    
}
