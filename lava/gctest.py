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


# Test the Java VM gc ordering

from java.lang import System, ref, Thread

class C:
    def __init__(self, n):
	self.n = n
    def __del__(self):
	print "FINALIZING: ",self.n

foo = []

for j in range(0, 100):
    cs = [C(10*j + i) for i in range(0,5)]
    foo.append(ref.SoftReference(cs))
    cs = None
    System.gc()
    Thread.sleep(1000)
    for k in range(0, min(4, len(foo))):
	b = foo[k].get()
	print b
    print "GCD"
