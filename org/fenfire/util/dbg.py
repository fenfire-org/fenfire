# 
# Copyright (c) 2003, Tuomas J. Lukka
# 
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
# 


# Jython:
# Provide variables for and read commandline debug options.

# python imports
import re

# java imports 
from java.lang import System

# fenfire imports
import org.fenfire.util.Dbg
from org.nongnu.libvob.gl import GL, GLRen

debugger = org.fenfire.util.Dbg()

short = "d:G:D:"
long = ["--dbg=", "--gldbg="]

all = ["-d", "-G", "-D"] + long


def option(o,a):
    if o in ("-d", "--dbg"):
        debugger.debugClass(a, 1)
    elif o in ("-G", "--gldbg"):
        GL.loadLib()
        print "Setting GL debug ",a
        GL.setDebugVar(a, 1)
    elif o in ("-D",):
        m = re.match('^(.*)=(.*)$', a)
        assert m
	prop = System.getProperties()
        prop.setProperty(m.group(1), m.group(2))
	System.setProperties(prop)

