#
# Copyright (c) 2002, Benja Fallenstein and Tuomas J. Lukka
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
copyrighter.py -- utility to add copyright notices to source files

Invocation:

    python copyrighter.py "Project name" [dir]

This script walks a directory tree and looks for files starting with
one of the following lines:

//(c): PERSON
// (c) PERSON
#(c): PERSON

where PERSON is any string. It then inserts a Fenfirish copyright statement
and license notice, attributing the file to PERSON, quoted by /* */
in the first and # in the second case. The original tag line is replaced
by the copyright statement and license notice.

The idea is to relieve us from the task of manually inserting
the copyright statement everywhere.

Additionally, it checks all files with the suffixes
.java
.py
.test
.pl

for having the text "General Public License" in the first three lines
and dies if it is not.

Best run as 'make copyrighted'.
"""

import sys, os, os.path, time, re

year = time.localtime()[0]
project_name = sys.argv[1]

oldStart = re.compile("You may use and distribute")
oldEnd = re.compile("for more details")

copyright = """
Copyright (c) %(year)s, %(author)s
"""

license = """This file is part of %(name)s.

%(name)s is free software; you can redistribute it and/or modify it under
the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

%(name)s is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General
Public License for more details.

You should have received a copy of the GNU General
Public License along with %(name)s; if not, write to the Free
Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
MA  02111-1307  USA
"""


template = copyright + license

java_template = """\
/*
%s
%s
 */
/*
 * Written by %s
 */
"""

doCopyrightRe = re.compile(r"(//|#)\s*\([cC]\)\s*:?\s*([^\n]+)")
doSuffix = re.compile(r"\.(?:java|py|test|pl|PL|c|h|cxx|hxx|texture)$")

def renewNotice(file):
    f = open(file, "r")
    lines = f.readlines()
    f.close()
    for i in range(0, len(lines)):
	if oldStart.search(lines[i]):
	    start = i
	    break
    for i in range(0, len(lines)):
	if oldEnd.search(lines[i]):
	    end = i
	    break
    # Now, which one
    if lines[start].startswith("#"):
	repl = ["# "+s+"\n" for s in license.split('\n')]
    else:
	repl = [" *    "+s+"\n" for s in license.split('\n')]
    lines[start:end+1] = repl

    print "Inserting new copyright statement in file %s" % (file,)
    f = open(file, 'w')
    f.write("".join(lines))

def process_dir(arg, dir, names):
    (head, tail) = os.path.split(dir)
    if tail == "{arch}":
    	names[0:len(names)-1] = []
    for name in names:
	process_file(dir, name)

def process_file(dir, name):
    file = os.path.join(dir, name)
    if os.path.isdir(file): return
    firstlines = open(file, 'r').readlines(2000)
    if len(firstlines) < 2: return

    firstlines = firstlines[0:31]

    # Check if we have an old notice
    if ( max([oldStart.search(l) for l in firstlines]) != None and
         max([oldEnd.search(l) for l in firstlines]) != None ):
	    renewNotice(file)

    theLine = 0
    mat = doCopyrightRe.match(firstlines[0])
    if mat == None: 
	mat = doCopyrightRe.match(firstlines[1])
	theLine = 1

    if mat != None:
	author = mat.group(2)
	first = firstlines[0]

        params = {
            'year': year,
            'author': author,
            'name': project_name,
        }

	if mat.group(0).startswith('#'):
	    pymode = 1
	else:
	    pymode = 0;

	lines = []
	for l in open(file, 'r').readlines():
	    if l[-1] == '\n': lines.append(l[:-1])
	    else: lines.append(l)

	notice_lines = (template % params).split('\n');

	if pymode:
	    notice_lines = ['# '+s for s in notice_lines]
	else:
	    notice_lines = [' *    '+s for s in notice_lines]

	if not pymode:
	    notice = '\n'.join(notice_lines)
	    notice = java_template % (name, notice, author)
	    text = '\n'.join(lines[0:theLine]) + notice + '\n'.join(lines[(theLine+1):]) + '\n'
	else:
	    lines[theLine:(theLine+1)] = notice_lines + ['']
	    text = '\n'.join(lines) + '\n'

	print "Inserting copyright statement into file %s" % (file,)
	open(file, 'w').write(text)
    else:
	# Is this a file type that needs to be copyrighted?
	if re.search(doSuffix, name):
	    m = max([re.search("General Public License", line) 
			for line in firstlines])
	    if m == None:
		print file+":1: not copyrighted"

if len(sys.argv) > 2:
    dir = sys.argv[2]
else:
    dir = './'

os.path.walk(dir, process_dir, None)
