/*   
Triple.java
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

public class Triple extends AbstractReplaceable.AbstractObservable {

    protected Model subject, predicate, object;

    public Triple(Model subject, Model predicate, Model object) {
	this.subject = subject;
	this.predicate = predicate;
	this.object = object;

	subject.addObs(this); predicate.addObs(this); object.addObs(this);
    }

    public Triple(Object subject, Object predicate, Object object) {
	this((subject instanceof Model)   ? (Model)subject 
	                                  : new ObjectModel(subject),
	     (predicate instanceof Model) ? (Model)predicate
	                                  : new ObjectModel(predicate),
	     (object instanceof Model)    ? (Model)object 
                                          : new ObjectModel(object));
    } 

    protected Replaceable[] getParams() {
	return new Replaceable[] { subject, predicate, object };
    }
    protected Object clone(Object[] params) {
	return new Triple((Model)params[0], (Model)params[1],
			  (Model)params[2]);
    }

    public Model getSubjectModel() { return subject; }
    public Model getPredicateModel() { return predicate; }
    public Model getObjectModel() { return object; }

    public Object getSubject() { return subject.get(); }
    public Object getPredicate() { return predicate.get(); }
    public Object getObject() { return object.get(); }
}
