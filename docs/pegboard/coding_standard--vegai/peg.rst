=============================================================
PEG coding_standard--vegai: Coding Standard
=============================================================

:Author:   Vesa Kaihlavirta
:Last-Modified: $Date: 2003/03/31 09:37:41 $
:Revision: $Revision: 1.1 $
:Status:   Incomplete
:Scope:	   Minor
:Type:     Policy

Our coding standard (/gzz/CODING) requires revising, mainly because of the
large and growing amount of python code.

Issues
======

- our \*.java have the license attached to every file. Is that necessary?

    RESOLVED: Yes, according to FSF.

- should we have automatic tests for enforcing parts of the standard?

    At least for the most trivial ones (rcsid, tab check, imports, class names).
    Failing tests will clearly tell what the developer did wrong.

- should the rcs id variable be "rcsid" or "rcsId" ?

    RESOLVED: rcsId, adhering to rule 9.

- should all code and data really be in classes? If so, why?

    RESOLVED: No. Rule 6 modified to catch the idea better.

- should Tabs be allowed or not?

    RESOLVED: a conversion tool at "make committable" will
    convert tabs into 8 spaces

- how should the import statements be grouped?

    - Benja: "I'd prefer first gzz, then java, for consistency with Java files and 
      because this makes those imports rest in alphabetical order. (Hmm, come 
      to think of it, I would like to have that rule included: In each group, 
      imports should be in alphabetical order.)"

    - Tuomas: "I disagree: more importantly it should be "foreign stuff" "our stuff"."

    I feel that the order of grouping is less important than the actual
    grouping. How about defining the imports as a set of sets?

- does every file need to have its module name in the headers?

    [ponder about lava/ and basalt/, otherwise "RESOLVED: No."]

- What case rules should be observed for variables and functions and
  modules?

    RESOLVED: modules should be lowercase and variables and functions
    mixedCase. This is the convention that is used in Java and we want 
    to keep our Java and python code as closely readable as possibly.

- should "from bar import *" be allowed when importing from current package?

    It depends. This is a potential namespace clutterer, and makes code less
    clear, especially for first-time readers, eg.::

        [./foo.py:]
        def a(): print "foo.a"

        [./blaa.py:]
        def a(): print "blaa.a"
	def b(): print "blaa.b"
        
        [./program.py:]
        from . import *    # can't import from . really, this line is pseudocode

        a()                # the reader of program.py can not tell where a() 
        b()                # and b() are coming from.

    On the other hand, more verbose code is not always very elegant, eg::

        [gzz/views/buoy/program.py:]
        import gzz.views.buoy
     
        gzz.views.buoy.a()
        gzz.views.buoy.b()        

    [add something to conclude this]

            

Changes
=======

These apply to all \*.py (and possibly \*.test).

1. Header comments should include full module name of the file (eg.
   gzz/modules/pp/demotest.py would have gzz.modules.pp.demotest).

2. Header comments should include authors.

3. After header comments, rcsId:
   rcsId = "$Id: peg.rst,v 1.1 2003/03/31 09:37:41 humppake Exp $"

4. After rcsid, the imports (unless there's a good reason to delay
   importing).

   - Prefer "import foo" to "from foo import bar".
   - Prefer "from foo import bar" to "from foo import \*".
   - No more than one import package in one line, except when importing gzz
     and java::

        import os, sys   # Preferably no.

        import os        # Yes.
        import sys

        import gzz, java # Yes.


5. Imports should be grouped in the following order:

   - standard python imports
   - 3rd party python imports
   - java imports
   - gzz imports

   * imports should be in alphabetical order in the groups
   
6. Code should be structured so that it can be imported and re-used, for example by
   putting state in classes instead of the module namespace. Executable code
   in module namespace is discouraged -- except for "if __name__=='__main__':"

7. Class names are CapitalizedWords.

8. Class methods and attributes are mixedCase.

9. Functions and variables are mixedCase.

10. Tab-size is 8, indentation at 4 spaces.

11. Run make committable before committing code.

    - converts tabs to 8 spaces
    - runs tests
    
Notes
=====

- "A Foolish Consistency is the Hobgoblin of Little Minds" - Ralph Waldo Emerson


References
==========

http://www.python.org/doc/essays/styleguide.html
http://www.python.org/peps/pep-0008.html
http://www.wikipedia.org/wiki/CamelCase

