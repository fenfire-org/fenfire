/*
PotionAction.java
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

public class PotionAction extends AbstractAction {

    protected Graph graph;
    protected Model cursor;
    protected Model currentCommand;

    protected CommandExpression command;
    protected boolean useCurrentNode;
    protected FunctionExpression function;

    public PotionAction(Command command, boolean useCurrentNode,
			Function function, 
			Graph graph, Model cursor, Model currentCommand) {
	this(command == null ? null : new CommandExpression(command), 
	     useCurrentNode,
	     function == null ? null : new FunctionExpression(function),
	     graph, cursor, currentCommand);
    }

    public PotionAction(CommandExpression command, boolean useCurrentNode,
			FunctionExpression function,
			Graph graph, Model cursor, Model currentCommand) {
	this.command = command;
	this.function = function;

	this.useCurrentNode = useCurrentNode;

	this.graph = graph;
	this.cursor = cursor;
	this.currentCommand = currentCommand;
    } 

    public void run() {
	CommandExpression c = (CommandExpression)currentCommand.get();

	if(c == null) {
	    if(command == null) return;

	    c = command;
	    if(useCurrentNode) {
		org.fenfire.Cursor cur = (org.fenfire.Cursor)cursor.get();
		Object n = cur.getNode();
		Function fn = Potions.node(n, Nodes.toString(n));
		FunctionExpression expr = new FunctionExpression(fn);
		c = (CommandExpression)c.setNextParam(expr);
	    }
	}

	if(!c.isComplete() && function != null) {
	    c = (CommandExpression)c.setNextParam(function);
	}

	if(c.isComplete()) {
	    Map context = new HashMap();
	    context.put("graph", graph);
	    context.put("cursor", cursor);

	    c.execute(context);
	    currentCommand.set(null);

	    System.out.println("performed: "+c);
	} else {
	    currentCommand.set(c);
	    System.out.println("set current command to: "+c);
	}

	org.nongnu.libvob.AbstractUpdateManager.chg(); // XXX
    }
}
