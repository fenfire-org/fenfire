/*   
CreateURIAction.java
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
import java.util.*;

/** Create a new node with a random URI and then instantiate an action
 *  with that node and perform the action.
 */
public class CreateURIAction extends AbstractAction {

    public static final Object NEW_NODE =
	"http://fenfire.org/2004/10/20/CreateURIAction/newNode";

    protected Object newNodeKey;
    protected Action action;

    public CreateURIAction(Action action) {
	this(NEW_NODE, action);
    }

    public CreateURIAction(Object newNodeKey, Action action) {
	this.newNodeKey = newNodeKey;
	this.action = action;
    }

    protected Replaceable[] getParams() {
	return new Replaceable[] { action };
    }
    protected Object clone(Object[] params) {
	return new CreateURIAction((Action)params[0]);
    }

    public void run() {
	Map params = new HashMap();
	params.put(newNodeKey, new ObjectModel(Nodes.N()));

	Action a = (Action)action.instantiateTemplate(params);
	a.run();
    }
}
