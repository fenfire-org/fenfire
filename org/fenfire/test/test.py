#
# Copyright (c) 2002-2005, Benja Fallenstein and Tuomas J. Lukka
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

"""
Test.py -- load *.test files and execute tests in them.

The directory containing .test files to be run is passed as a parameter.
(Can also be more than one.)

Here, I liked the trivial test suites in lava/ far too much to let them go
(the nice thing was that you could just make a plain text file and
start writing tests into it). So, here's a tool that looks for *.test
files in the test/ subtree and turns them into unittest classes.
Let's try to incrementally make it as comfortable as possible.

One thing I'd really like would be *.spec files that are Ly files which
can be tangled into unittests...
"""
import sys
#sys.path.insert(0, ".")
import os.path, imp
import java
import getopt
import fnmatch
import re
import traceback
import org.fenfire.util.dbg
import vob

def getName(module, fn):
    test = fn.__name__
    rawname = '%s.%s' % (module.__name__, test)

    if not fn.__doc__:
        name = rawname
    else:
        lines = fn.__doc__.split('\n')
        if lines[0] == '':
            name = '%s.%s (%s)' % (module.__name__, test,
                                   lines[1].strip())
        else:
            name = '%s.%s (%s)' % (module.__name__, test,
                                   lines[0].strip())
    return name, rawname


def test(module, mapOfAttribs={}, gfx=1):
    tests = [test for test in dir(module) if test.startswith('test')]
    exceptions = []

    for test in tests:
        fn = getattr(module, test)
        for key in mapOfAttribs.keys():
            name, ref = key, mapOfAttribs[key]
            setattr(module, name, ref)
        name, rawname = getName(module, fn)
        print name + "... ",

        if not shouldRun(fn):
            "Not run."
            continue

        try:
            if gfx == 1:
                org.fenfire.test.gfx._didRender = 0
            if hasattr(module, 'setUp'): module.setUp()
            fn()
            if hasattr(module, 'tearDown'): module.tearDown()
        except:
            print "failed."
            exceptions.append([name, {
                'exception': sys.exc_info(),
                'test': test,
                'testname': name,
            }])
            if gfx == 1 and org.fenfire.test.gfx._didRender:
		print "SAVING IMAGE\n"
		file = "testfail_"+rawname+".png"
		print "Saving result of graphical test to ", file
		s = org.fenfire.test.gfx.win.getSize()
		vob.putil.saveimage.save(file,
		    org.fenfire.test.gfx.win.
			readPixels(0, 0, s.width, s.height),
		    s.width, s.height
		    )
            
        else:
            print "ok."

    return exceptions
        

def shouldRun(obj):
    if getattr(obj,"__doc__", None) == None: return 1
    doc = obj.__doc__
    m = re.search("\n\s*fail:\s*(.*?)\s*\n", doc)
    fail = None
    if m != None:
        fail = m.group(1)
        print "TEST FAILS IN ", obj, fail
        if fail not in ["AWT", "GL", "*"]:
            raise str(("Invalid docstring fail message ",obj.__doc__))
    if not (fail in runfail):
        print "NOT RUNNING DUE TO WRONG F: ",obj, fail, runfail
        return 0

    return 1


def load(file):
    name = os.path.splitext(file)[0]
    while name.startswith('./'): name = name[2:]
    name = '.'.join(name.split('/'))
    name = '.'.join(name.split('\\'))

    module = imp.new_module(name)
    execfile(file, module.__dict__)
    return module

    

def tests(files, match='*.test'):
    """
    Return a list of all *.test files in a given directory and its
    subdirectories.
    """

    def addTests(list, dirname, names):
        list, match = list
        names = [n for n in names if fnmatch.fnmatch(n, match)]
        names = [os.path.join(dirname, name) for name in names]
        list.extend(names)

    tests = []
    for f in files:
        if os.path.isdir(f):
            os.path.walk(f, addTests, (tests,match))
        else:
            tests.append(f)
    return tests


def main(args):
    global runfail
    runfail = [None]
    
    opts, args = getopt.getopt(args, 
            org.fenfire.util.dbg.short + "f:", 
            org.fenfire.util.dbg.long + ["--allowfail="])
    for o,a in opts:
        print "Opt: ",o,a
        if o in org.fenfire.util.dbg.all:
            org.fenfire.util.dbg.option(o,a)
        elif o in ("-f", "--allowfail"):
	    print "Run failing: ",a
            if a == "*":
                runfail = [None, "GL", "AWT", "*"]
            else:
                runfail = [None, a]
                
    startGraphicsAPI(args)



# Have to do before importing test.tools.gfx
def startGraphicsAPI(dirs):
    class Starter(java.lang.Runnable):
        def __init__(self, dirs): self.dirs = dirs
        def run(self):
            exceptions = []

            for file in tests(self.dirs):
		try:
		    loaded = load(file)
		    exceptions.extend(test(loaded))

		    print
		except org.fenfire.test.gfx.GLNeeded, e:
		    print "Skipping, needs GL."

            if exceptions:
                print "Java stack traces:"
                for name, exc in exceptions:
                    if hasattr(exc['exception'][1], 'printStackTrace'):
                        print 75 * '-'
                        print name
                        exc['exception'][1].printStackTrace()
            
                print 75 * '-'
                print

                print "Python stack traces:"
                for name, exc in exceptions:
                    print 75 * '-'
                    print name
                    traceback.print_exception(*exc['exception'])
            
                print 75 * '-'
                print

                print "%s test failures." % len(exceptions)

            java.lang.System.exit(exceptions != [])

    org.nongnu.libvob.GraphicsAPI.getInstance().startUpdateManager(Starter(dirs))


if __name__ == '__main__':
    main(sys.argv[1:])
