# 
# Copyright (c) 2003, Tuomas J. Lukka
# This file is part of Fenfire.
# 
# Fenfire is free software; you can redistribute it and/or modify it under
# the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
# 
# Fenfire is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
# Public License for more details.
# 
# You should have received a copy of the GNU General
# Public License along with Fenfire; if not, write to the Free
# Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
# MA  02111-1307  USA
# 


import org
import java
from org import fenfire as ff


tim = java.lang.System.currentTimeMillis

def bench(nrounds, nnodes = 100):
    ro = ff.swamp.bench.RDFOps(nnodes + 50)
    ro.graph = ff.swamp.impl.HashGraph()

    ro.nrounds = nrounds * nnodes
    ro.circleSize = nnodes

    ro.traverseCircle_prepare()

    t0 = tim()
    ro.traverseCircle_find1()
    t1 = tim()

    return ((t1-t0) / 1000.0, "X")

args = {
"nnodes": (10,100,1000),
}
