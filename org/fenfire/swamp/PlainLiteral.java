/*
PlainLiteral.java
 *    
 *    Copyright (c) 2004, Benja Fallenstein
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

package org.fenfire.swamp;

/** A plain RDF literal: string plus language tag.
 */
public final class PlainLiteral extends Literal {

    private final String lang;

    public PlainLiteral(String string) {
	this(string, null);
    }

    public PlainLiteral(String string, String lang) {
	super(string);
	this.lang = lang;
    }

    public String getLang() {
	return lang;
    }

    public boolean equals(Object o) {
	if(!(o instanceof PlainLiteral)) return false;
	PlainLiteral l = (PlainLiteral)o;
	if(!string.equals(l.string)) return false;
	if(lang == null) return l.lang == null;
	return lang.equals(l.lang);
    }

    public int hashCode() {
	if(lang == null) return string.hashCode();
	return string.hashCode() + 324891*lang.hashCode();
    }

    public String toString() {
	if(lang == null)
	    return '"' + string + '"';
	else
	    return '"' + string + '"' + '@' + lang;
    }
}
