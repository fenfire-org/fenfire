/*
PotionTextModel.java
 *    
 *    Copyright (c) 2005, Benja Fallenstein
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
import org.fenfire.swamp.*;
import org.nongnu.libvob.layout.*;
import java.util.*;

public class PotionTextModel extends AbstractModel.AbstractObjectModel {

    protected Model expression;

    public PotionTextModel(Model expression) { 
	this.expression = expression;
	expression.addObs(this);
    }

    protected Replaceable[] getParams() {
	return new Replaceable[] { expression };
    }
    protected Object clone(Object[] params) {
	return new PotionTextModel((Model)params[0]);
    }

    public Object get() {
	Expression e = (Expression)expression.get();
	if(e == null) return "";
	return e.getString(new HashMap());
    }
}
