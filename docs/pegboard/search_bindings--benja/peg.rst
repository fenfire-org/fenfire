====================================================================
``search_bindings--benja``: Convenient bindings for full-text search
====================================================================

:Author:       Benja Fallenstein
:Revision:     $Revision: 1.1 $
:Last-Modified: $Date: 2003/03/31 09:37:41 $
:Date-Created: 2002-10-31
:Status:       Accepted


Convenient search bindings are extremely important for the smooth
operation of Gzz. It must be possible to jump to any cell
in the space by typing in a few significant characters from its
contents as an alternative to navigating across the whole space.
Examples include quick access to menu items; quickly finding
a cell to clone or connect to; or quickly finding a function
definition to clone into a clang program.

We need this if we want to compare to text identifier-based
interfaces (like the command line, or textual programming languages)
in efficiency. I expect that for some day-to-day tasks, we'll be
able to work more than twice as fast with good search bindings.

This PEG proposes a keyboard language for searching using the
spacebar, because a space is arguably the easiest-to-hit key of all.
This conflicts with Ted's prefered space bar binding.


Scope
-----

This PEG describes how to enter and exit search mode. It only touches
on what will happen *inside* search mode. This should be
configurable since there are many desirable alternatives;
the interfaces for this need another PEG.


Description
-----------

I propose that:

- Pressing the space bar once enter "search mode."
- In search mode, characters and spaces entered will be passed to a
  user-specifyable "search policy" object (implementing, for example,
  case-insensitive full-text search). The search policy object
  will decide on a cell that is the "current search result"
  (bindings not specified in this PEG will allow moving between
  different possible search results); while in search mode, this cell
  will somehow be shown on the screen.
- Entering two spaces in a row will terminate search mode. The
  current search result will be made the next action's 
  prefix argument.

This means that the user does *not* automatically move to the cell.
This is one option, but not the only one. Writing the space bar as
``SP``, it should be possible to type:

- ``SP foo SP SP g`` to move to cell "foo" in the right window
- ``SP foo SP SP G`` to move to it in the left window
- ``SP foo SP SP m`` to mark it
- ``SP foo SP SP Enter`` to execute it
- ``SP foo SP SP - <direction>`` to connect it to the current cell
- ``SP foo SP SP t <direction>`` to clone it

and so on. This allows for extremely fast operation in 
many common cases, where we don't really want to move to
the cell, just do something with it (for example, connect).

Additionally, it would be nice if ``SP foo Enter`` were
an abbreviation for moving to cell "foo" directly (in the
right window). This is more like searching works in e.g. Emacs
(where to move to word "foo" by searching for it, you type
``Ctrl-S foo Enter``).

**As generally all new bindings, these will be optional for now.
Passing ``--search-bindings-benja`` to Gzz.py will enable them.**


Taking Ted's binding?
---------------------

In the cursor spec, Ted has specified that the space bar
should rotate through the different windows (bring each
of them front, in order). By specifying that in normal mode,
the space bar enters search mode, this PEG obviously is in
violation of that spec.

Personally, I do think that using the space bar for searching
is more important (*except* in arrowset reassignment mode,
where I'm all for making the space bar cycle through windows).
Cyclically bringing windows front doesn't seem that practical
to me, anyway: What if I want windows 3 and 7 on the list front,
right now? Window managers have both keyboard and mouse commands
that handle this easily, though they are admittedly not quite
as fast as using the space bar. (If we really need our own
binding, I'd suggest something easy-to-type but other than
the space bar, for example '``a``' or '``.``').

At the moment, we don't have the ability to bring different
windows front anyway. I propose to:

- Use the space bar to enter search mode for now;
- Implement a command line or other option once bringing
  different windows front is implemented that will make
  the space bar bring the next window front;
- Try what we like better;
- If we decide that Ted's binding is better, use it;
- If we decide that using it for search is better, show
  both options to Ted and ask his opinion;
- If he decides that he likes his way better, think again. :)
  We could decide to follow his opinion, or we could
  fork the "Director's Cut" that implements his preference.


\- Benja
