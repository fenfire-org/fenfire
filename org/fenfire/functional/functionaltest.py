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


from __future__ import nested_scopes

from org import fenfire as ff
from org.fenfire.functional import FunctionalTest 
import java

def _simpleWaitEval(x):
    x()

# Test classes exactly corresponding to FunctionalTest.java
# Use the same counters!
# (no placeholders)
class G0(ff.functional.PureFunction):
    def __init__(self, str):
	self.str = str
    def f(self, o):
	FunctionalTest.G0.counter += 1
	return o + self.str

class G1(ff.functional.PureFunction):
    def __init__(self, str, func):
	assert isinstance(func, ff.functional.Function), func
	self.str = str
	self.func = func
    def f(self, o):
	FunctionalTest.G1.counter += 1
	fres = self.func.f(o) 
	if fres == None: fres = "null"
	return fres + self.str

class G0_Node(ff.functional.PureNodeFunction):
    def __init__(self, str):
	self.str = str
    def f(self, constGraph, o):
	FunctionalTest.G0_Node.counter += 1
	return o + self.str

class G1_Node(ff.functional.PureNodeFunction):
    def __init__(self, str, func):
	assert isinstance(func, ff.functional.NodeFunction), func
	self.str = str
	self.func = func
    def f(self, constGraph, o):
	FunctionalTest.G1_Node.counter += 1
	fres = self.func.f(constGraph, o) 
	if fres == None: fres = "null"
	return fres + self.str

class TripleSet_Node(ff.functional.PureNodeFunction):
    def __init__(self, o2):
	self.o2 = o2
    def f(self, constGraph, o):
	res = java.util.HashSet()
	iter = constGraph.findN_11X_Iter(o, self.o2)
	while iter.hasNext():
	    res.add(iter.next())
	return res

class Identity_Node(ff.functional.PureNodeFunction):
    def __init__(self, func):
	assert isinstance(func, ff.functional.NodeFunction), func
	self.func = func
    def f(self, constGraph, o):
	return self.func.f(constGraph, o)

def setUpFunctionalTest(theFunctional, waitEval = _simpleWaitEval):
    global functional
    global waitEvalFunc
    functional = theFunctional
    waitEvalFunc = waitEval

def createFunc1(functional):
    node0 = functional.createFunctionInstance(
	"A",
	FunctionalTest.G0,
	["XXX"])
    node1 = functional.createFunctionInstance(
	"B",
	FunctionalTest.G1,
	["YYY", node0])

    node0_j = functional.createFunctionInstance_Jython(
	"A",
	G0,
	["XXX"])
    node1_j = functional.createFunctionInstance_Jython(
	"B",
	G1,
	["YYY", node0_j])

    return (node1, node1_j)

def createFunc1_error_ph(functional):
    node0 = functional.createFunctionInstance(
	"A",
	FunctionalTest.G0PlaceHolderError,
	["XXX"])
    node1 = functional.createFunctionInstance(
	"B",
	FunctionalTest.G1,
	["YYY", node0])
    return (node1, )

def createFunc1_Node(functional):
    node0 = functional.createFunctionInstance(
	"C",
	FunctionalTest.G0_Node,
	["XXX"])
    node1 = functional.createFunctionInstance(
	"D",
	FunctionalTest.G1_Node,
	["YYY", node0])

    node0_j = functional.createFunctionInstance_Jython(
	"C",
	G0_Node,
	["XXX"])
    node1_j = functional.createFunctionInstance_Jython(
	"D",
	G1_Node,
	["YYY", node0_j])
    return (node1, node1_j)

# XXX Need to reorg for superlazy
def test_Functional_simple():
    for node1 in createFunc1(functional):
	f = node1.getCallableFunction()
	print "Callable: ",f, f.f

	def t():
	    assert f.f("AAA") == "AAAXXXYYY", f.f("AAA")
	waitEvalFunc(t)

def test_Functional_simpleNode():
    for node1 in createFunc1_Node(functional):
	f = node1.getCallableFunction()
	print "SimpleNode: callable:",node1, f, f.f

	def t():
	    assert f.f("AAA") == "AAAXXXYYY", f.f("AAA")
	waitEvalFunc(t)



# map java.lang.integer and java.lang.double into float
def test_Functional_param_integer2float():
    """
    A method used to test mapping of integer
    parameters into float.
    """
    foobar = functional.createFunctionInstance(
        "integer2float",
        FunctionalTest.ConstructorParam_float,
        [1])

def test_Functional_param_double2float():
    """
    A method used to test mapping of double
    parameters into float.
    """
    foobar = functional.createFunctionInstance(
        "double2float",
        FunctionalTest.ConstructorParam_float,
        [1.])

# map java.lang.Float into double
def test_Functional_param_float2double():
    """
    A method used to test mapping of float
    parameters into double.
    """
    foobar = functional.createFunctionInstance(
        "float2double",
        FunctionalTest.ConstructorParam_double,
        [java.lang.Float(1.)])



def test_Functional_param_boolean():
    """Test that boolean params work right.
    """
    foobar = functional.createFunctionInstance(
        "Boolean2boolean",
        FunctionalTest.ConstructorParam_boolean,
        [java.lang.Boolean(1)])



def test_Functional_param_int():
    """Test that int params work right.
    """
    foobar = functional.createFunctionInstance(
        "int2int",
        FunctionalTest.ConstructorParam_int,
        [java.lang.Integer(1)])


