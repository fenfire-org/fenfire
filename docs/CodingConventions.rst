=============================================
Coding rules for Fenfire and related projects
=============================================

:Author: Tuomas Lukka and Antti-Juhani Kaijanaho

This document attempts to give a brief guide on how to write
Java and Jython code for Fenfire and related projects.

Rules
-----

Rules are obligatory for code included in fenfire

- each Java file should contain the boilerplate copyright and
  license notice (see e.g. org/fenfire/Fen.java).

- all of the classes should be in a package (under ``org.fenfire``).

- The main packages may only depend on the JDK APIs supported by Kaffe.
  Swing is not allowed
  The java.util Collections API
  (found in JDK 1.2, kaffe and as a separate download for 1.1)
  IS allowed. Java reflection and reference APIs are allowed.

  ``org.fenfire.modules`` can depend on anything, as long as the dependency 
  is documented.
  Nothing in the main packages may depend on anything in the modules packages.
  This ensures that we retain portability and easy installation. All the user
  has to do is to give up on a module that has an odd dependency, not the whole
  system.


- For debugging log messages, use code like ::

     public static boolean dbg = false;
     protected static void p(String s) { System.out.println("Class:: "+s); }
     ...
     if(dbg) p("Debug message");

  to print out messages. The System.out.println() is too verbose to insert
  into code. This code can be added to the beginning of any class.
  Debugging can then be turned on like ::

     make run_fenpdfdemo DBG="-d org.fenfire.foobar.Foo"

- Avoid both under- and overdocumenting.

- Use anonymous classes freely

- Try to keep the external APIs small

- Indenting: tabstop==8, shiftwidth == 4, cuddled braces, i.e. ::
	
	public void method() {
	    if(...) {
		...
	    } else {
		...
	    }
	}

  however, you may use smaller shiftwidth if the function really really requires
  it (although you should try to split it up).

- for any more complicated functionality, start a PEG.

- make your code such that it can be compiled in jikes +P (pedantic),
  JDK1.1.8 and JDK1.2.2 without warnings or errors.

Conventions
-----------

Conventions are less absolute than rules - they are allowed to be broken
but usually it's good form to follow them.

Imports
"""""""

- Import first the most generic packages (i.e. java.util, ...), then 
  the more specific ones (i.e. neighbouring package &c)

- avoid importing * unless there is a reason (note: a reason, not a *good*
  reason)

- In jython, don't import too much. It's preferable to do ::

    import vob
    from org import fenfire as ff

  and then use, e.g. ``ff.swamp.Nodes``, `ff.Fen`` &c. 
  Jython is a weakly typed and soft language so it's much better to give
  the reader a little more context.

Variable names
""""""""""""""

- Avoid single-letter variable names, except for obvious cases such as
  loop indices or coordinates.

- If there's exactly one object of a given class that's relevant in
  a certain method, name the variable decapitalized. This is *especially*
  important in Jython, where code can get really hard to read.

  It's sometimes hard to resist the temptation to abbreviate the name
  but remember: **ALL** the time you win that way someone else (or you) will
  lose later multiple times when trying to read the code.
  See also the Tips section below for why abbreviations are not needed.

Documenting
"""""""""""

- At the very least, document data! Document data members of classes,
  especially in Jython.

Tips (for speeding up work)
---------------------------

- I really recommend using a coloring editor since there are
  many points where significant sections have been commented out. This
  is bad practice but inevitable in a system that doesn't easily allow
  us to attach the sections which are not useful now but might be later
  to the text in a xanadu-like manner.

- We use pretty long identifiers - make sure your editor's dynamic
  abbreviations are turned on. For example, I type ::

	Mod^P

  and start getting the options ::

	ModularSpace, ...

  for the word. NEVER type the identifiers in whole.

- Use exuberant ctags. First, "make tags", then in vim, I
  just do ::

	:ta Aff<TAB>

  which gives me the options AffineVobCoorder and AffineXYCoords.
  Then, pressing enter takes me to the definition.
  NEVER type the directory names for the classes.

