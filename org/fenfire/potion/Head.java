/*
Head.java
 *    
 *    Copyright (c) 2002, OSK Group
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
 * Written by OSK Group
 */
package gzz.potion;
import java.util.*;
import gzz.vob.linebreaking.HChain;

public interface Head {
    String getString(Expression[] params, Map context);
    void render(Expression[] params, Map context, HChain into);
    Type[] getParams();
}
