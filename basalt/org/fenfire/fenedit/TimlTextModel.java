/*   
TimlTextModel.java
 *    
 *    Copyright (c) 2004, Benja Fallenstein.
 *
 *    This file is part of Fenfire.
 *    
 *    Gzz is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Gzz is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU General
 *    Public License along with Gzz; if not, write to the Free
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
import org.nongnu.libvob.layout.*;
import org.nongnu.libvob.util.*;
import org.nongnu.libvob.vobs.TextVob;
import org.nongnu.navidoc.util.Obs;
import org.nongnu.alph.TString;
import java.util.*;

public class TimlTextModel extends TextModel.AbstractTextModel {

    public TimlTextModel(Model stringModel, Model fontModel) {
	super(stringModel, fontModel, true);
    }

    protected Object clone(Object[] params) {
	return new TimlTextModel((Model)params[0], (Model)params[1]);
    }

    public void insert(int pos, String newText) {
	TString text = (TString)stringModel.get();
	stringModel.set(TString.plus(text.substring(0, pos),
				     TString.newRICC(newText),
				     text.substring(pos)));
    }

    public void delete(int start, int end) {
	TString text = (TString)stringModel.get();
	stringModel.set(text.substring(0, start).plus(text.substring(end)));
    }

    public int getCharCount() {
	return ((TString)stringModel.get()).length();
    }

    public char getChar(int index) {
	return ((TString)stringModel.get()).charAt(index);
    }

    public Object getKey(int index) {
	if(index == getCharCount()) return "line-end";
	return ((TString)stringModel.get()).getCharURI(index);
    }

    public int getIntKey(int index) {
	if(index == getCharCount()) return 0;
	return ((TString)stringModel.get()).getCharIndex(index);
    }
}
