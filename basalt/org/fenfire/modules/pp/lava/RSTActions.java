/*
RSTActions.java
 *    
 *    Copyright (c) 2003, Matti J. Katila
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

package org.fenfire.modules.pp.lava;
import org.fenfire.functional.NodeFunction;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.Remote;


/** The interface for rdf graph which is able to perform the
 * actions needed to make xanalogical system and editor like view.
 * RST as reStructuredTex - the poor mans way to present text.
 * <p>
 * RST name is only illusion but as the idea come from these 
 * actions come from rst I keep calling this with it.
 */
public interface RSTActions extends Remote {

    /** Create a new rst-canvas.
     */
    Object newRSTCanvas() throws RemoteException;

    /** Create a new paragraph to canvas.
     * @param canvas canvas where you like to create the paragraph.
     * @param width width of paragraph. 
     * @param x x-coordinate
     * @param y y-coordinate
     * @return created sentence
     */
    Object newParagraph(Object canvas, int width, int x, int y) 
	throws RemoteException;
    void deleteParagraph(Object paragraph) throws RemoteException;

    /** Create a new sentence to paragraph and creates also 
     * empty node into itself.
     */
    Object newSentence(Object paragraph, int num) 
	throws RemoteException;
    void deleteSentence(Object sentence) throws RemoteException;


    /** Insert node to sentence.
     * @param offset Offset of nodes. First is 0.
     */
    void insertNode(Object sentence, Object node, int offset)
	throws RemoteException;

    void deleteNode(Object sentence, Object node)
	throws RemoteException;

}
