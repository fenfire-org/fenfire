=======================
The Fenfire test system
=======================

:Copyright: Copyright (c) 2003 by Benja Fallenstein
:License:   
    This work is licensed under the Creative Commons 
    ShareAlike License. To view a copy of this license, 
    visit http://creativecommons.org/licenses/sa/1.0/ 
    or send a letter to Creative Commons, 559 Nathan 
    Abbott Way, Stanford, California 94305, USA. 

Fenfire classes are generally supposed to have
unit tests. If you're not familiar with unit tests,
you may want to read the article, `Test Infected:
Programmers Love Writing Tests`__, by the developers
of the well-known JUnit unit test framework.

__ http://junit.sourceforge.net/doc/testinfected/testing.htm

To write unit tests for Fenfire, you need to know
`Jython`_, the Java-based implementation of the
`Python`_ language. Writing tests in Jython is
*much* faster than writing them in Java.

.. _Jython: http://www.jython.org/
.. _Python: http://www.python.org/

The test suite is run when you compile by
typing ``ant``. You can also run it
independently by typing ``ant test``.

We have hacked together our own unit test system, 
the code for which you can find in the
``org/fenfire/test/`` directory in CVS.
This system allows you to add new tests
by simply creating a file inside the
``org/`` tree whose suffix is ``.test``.
The convention is that a class ``XXX.java``
is tested by an ``XXX.test`` file
in the same directory.

Test files are Python modules. After a test file
has been loaded, the test system looks for
methods in it whose name starts with ``test``.
(A method whose name is just ``test`` does count.)
These are the test cases.

Each test case is run in three steps:

1. Execute ``module.setUp()``, if it exists.
2. Execute ``module.testXXX()``.
3. Execute ``module.tearDown()``, if it exists.

The ``setUp()`` and ``tearDown()`` methods are
used to execute code that needs to be run
before or after different tests in the file.
For example, ::

    def setUp():
	global x, y, z
	x = java.util.HashMap()
	y = java.util.TreeMap()
	z = java.io.FileInputStream("bla.txt")

    def testSomething():
	y.put(z, z)
	x.put(z, y)

    def testSomethingElse():
	x.put(z, z.read())

    def tearDown():
	z.close()

If you include a docstring with a test, the first
line is shown when the test is run, to give
a more readable explanation of what the test
is about.
