/*
Expression.java
 *    
 *    Copyright (c) 2002, Benja Fallenstein
 *    
 *    This file is part of Gzz.
 *    
 *    Gzz is free software; you can redistribute it and/or modify it under
 *    the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *    
 *    Gzz is distributed in the hope that it will be useful, but WITHOUT
 *    ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *    or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General
 *    Public License for more details.
 *    
 *    You should have received a copy of the GNU Lesser General
 *    Public License along with Gzz; if not, write to the Free
 *    Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 *    MA  02111-1307  USA
 *    
 *    
 */
/*
 * Written by Benja Fallenstein and others
 */
package gzz.potion;
import java.util.*;
import gzz.vob.linebreaking.HChain;

public interface Expression {

    String getString(Map context);
    void render(Map context, HChain into);

    /** Whether this expression is ready to be
     *  evaluated/executed. If there are 'blanks,'
     *  it's not ready yet.
     */
    boolean isComplete();
}
