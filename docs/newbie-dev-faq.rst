====================================================================================
``newbie-dev-faq.rst``: Usefull documentation to share between (newbie) developers
====================================================================================

:Date:		2002-11-24
:Revision:	$Revision: 1.4 $
:Last-Modified:	$Date: 2003/08/29 08:02:58 $
   

This file is for newbie developers.
Proposed to be in free form.
Add your headaches here - fill in answers ;)

.. contents::

Fa(q/cts)
=========

Doesn't work
------------

**Q**: Gurus! Help me! Xyz doesn't work correctly. What's wrong?

**A**: Did you update all projects and recompile them?   ...<hups>


Screen is empty?
----------------

**Q**: I don't see anything. Just plain black with GL.

**A**: Probably you have some problems with your X drivers etc.
Update your /usr/X11R6/include/GL/\*.

**A2**: unset LANG


Compiling
---------

**Q**: How to compile?

**A**: With luck. 


Debug
-----

**Q**: How to set debug options?

**A**: Java: make run DBG="-d gzz.client.foo" 

:: 

   C++/GL: make rungl DBG="-G JNI.general -G JNI.foo"

   Remember: if you see something like dbg_paperquad in the source it was 
             probably initialized with 
   DBGVAR(dbg_paperquad, "Renderable.paperquad");
   So, use DBG="-G Renderable.paperquad"

To find these, use grep: that's the most important tool of a programmer.
Tjl has the aliases: 

::

	alias gj='find . -name "*.java" | xargs grep '
	alias gc='find . -name "*.[ch]*" | xargs grep '
	alias gp='find . -name "*.[p]*" | xargs grep '

for this. E.g.

 ::

	gc dbg_paperquad

will find you the above line.

It's more efficient to use boolean primitive to check debug strings.
So, you are *NOT* allowed to construct debug messages like: ::

    class Foo {
        static public boolean dbg = false;
        private void p(String s) { if (dbg) pa(s); }
        private void pa(String s) { System.out.println("Foo::"+s);


        public void example(int i) {
            p("Just a test " + i)
        }
    }

If you use this, debug string is always created using immutable string concatenation
and that isn't very efficient in java.
Well, instead of previous example, write a bit longer but more efficient: ::

    class Foo {
        static public boolean dbg = false;
        private void pa(String s) { System.out.println("Foo::"+s);


        public void example(int i) {
            if (dbg) pa("Just a test " + i)
        }
    }


Creating well documentated source
---------------------------------

1. Documenting is part of good source

    - In Fenfire we use java-doc and docxx

2. See the documentation already exists.

    - The most important part of documentation is the first line.

3. Write the documentation

    - Remember the importance of first line in class and method documentation.
    - Write the truth, don't lie.
    - If method implements an interface it's javadocced by the interface.
      So, don't make any worse documentation in implementation, leave it
      alone or mark it with comment // javadoc in interface etc.

4. Test

    - Ask your friend to look the compiled document and 
      ask what the class do. If the answer is mess, 
      your documentation is even worse mess.

Slang
=====

``'daissaus' or 'daissata' [Finnish]`` = Something is splitted/chopped in very small pieces.
Probably it is a texture.

   - Engl. "dice", p.o. suom. "pilkkoa"/"pieniae"

   - when having a large rectangle, which is to be rendered
     in a distorted view, it first needs to be diced i.e.
     made into a large number of small rectangles, so that
     it will appear curved when the vertices of the small
     rectangles are transformed.


Terms
=====

Bilinear and Trilinear

  - Close temrs to mipmapping. Trilinear reads 8 texels from two mipmap levels.
    and calculates a weighted average 
    Bilinear is same with 4 texels from one mipmap level.
    Of course trilinear looks much better - it doesn't jump around.

