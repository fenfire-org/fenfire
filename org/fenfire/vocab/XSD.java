/*
XSD.java
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

package org.fenfire.vocab;
import org.fenfire.swamp.*;

/** Vocabulary of the datatypes in XML Schema.
 */
public class XSD {

    static private String base = "http://www.w3.org/2001/XMLSchema#";

    static public final Object 
        string  = Nodes.get(base+"string"),
        _boolean  = Nodes.get(base+"boolean"),
        decimal  = Nodes.get(base+"decimal"),
        _float = Nodes.get(base+"float"),
        _double = Nodes.get(base+"double"),
        duration = Nodes.get(base+"duration"),
        dateTime = Nodes.get(base+"dateTime"),
        time = Nodes.get(base+"time"),
        date = Nodes.get(base+"date"),
        gYearMonth = Nodes.get(base+"gYearMonth"),
        gYear = Nodes.get(base+"gYear"),
        gMonthDay = Nodes.get(base+"gMonthDay"),
        gDay = Nodes.get(base+"gDay"),
        gMonth = Nodes.get(base+"gMonth"),
        hexBinary = Nodes.get(base+"hexBinary"),
        base64Binary = Nodes.get(base+"base64Binary"),
        anyURI = Nodes.get(base+"anyURI"),
        QName = Nodes.get(base+"QName"),
        normalizedString = Nodes.get(base+"normalizedString"),
        token = Nodes.get(base+"token"),
        language = Nodes.get(base+"language"),
        NMTOKEN = Nodes.get(base+"NMTOKEN"),
        NMTOKENS = Nodes.get(base+"NMTOKENS"),
        Name = Nodes.get(base+"Name"),
        NCName = Nodes.get(base+"NCName"),
        ID = Nodes.get(base+"ID"),
        IDREF = Nodes.get(base+"IDREF"),
        IDREFS = Nodes.get(base+"IDREFS"),
        ENTITY = Nodes.get(base+"ENTITY"),
        ENTITIES = Nodes.get(base+"ENTITIES"),
        integer = Nodes.get(base+"integer"),
        nonPositiveInteger = Nodes.get(base+"nonPositiveInteger"),
        negativeInteger = Nodes.get(base+"negativeInteger"),
        _long = Nodes.get(base+"long"),
        _int = Nodes.get(base+"int"),
        _short = Nodes.get(base+"short"),
        _byte = Nodes.get(base+"byte"),
        nonNegativeInteger = Nodes.get(base+"nonNegativeInteger"),
        unsignedLong = Nodes.get(base+"unsignedLong"),
        unsignedInt = Nodes.get(base+"unsignedInt"),
        unsignedShort = Nodes.get(base+"unsignedShort"),
        unsignedByte = Nodes.get(base+"unsignedByte"),
        positiveInteger = Nodes.get(base+"positiveInteger");
}
