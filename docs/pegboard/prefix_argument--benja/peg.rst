====================================================================
``prefix_argument--benja``: The prefix argument of keyboard commands
====================================================================

:Author:       Benja Fallenstein
:Revision:     $Revision: 1.1 $
:Last-Modified: $Date: 2003/03/31 09:37:41 $
:Date-Created: 2002-10-31
:Status:       Accepted


A number of other PEGs (1022, ``containment--benja``, and 
``search_bindings--benja``) have referenced what I call the
"prefix argument" mechanism: a mechanism that specifies whether
keyboard actions act on the marked cell(s), the other window,
or a previously entered cell number etc. In this PEG, let's
specify this mechanism with more scrunity.


Historical precedent
--------------------

Emacs commands normally read their arguments through the
minibuffer, after they have been called. For example, if I
type '``C-x C-f``' (``find-file``) to open a file, Emacs says
"``Find file:``" in the minibuffer and allows me to enter
a file name. I type the name in *after* I typing '``C-x C-f``.'

However, there is one exception: A command may take a numerical
"prefix argument," which can be specified in various ways.
Often, but not always, this argument will specify 
the number of times the command is to be executed.

Vi uses ``"`` to specify registers as a prefix; PUIs have
the current selection, which is a prefix to many commands
like copying or searching ("search only in the selected area").


Specification
-------------

"The prefix argument" is an ordered set of cells 
that a binding may use. (By "ordered set" I mean a list on which
each element may appear only once; this is similar to a zz rank,
except that it cannot loop.) The user interface should show
the current prefix argument in a reasonable way.

Currently, the prefix argument determined as follows:

    - If a search according to ``search_bindings--benja``
      has just been finished (according to the PEG, by pressing
      the space bar twice), and this search has returned more
      than zero cells, these cells are the prefix argument.
      (The order is defined by the search policy, configurable
      by the user in an as-yet-undefined way).
    - Else, if there any cells are marked, they are
      the prefix argument. (Marks are ordered; this is outside
      the scope of this PEG, but normally the cell that
      was marked first is the first in the list.)
    - Else, there is no prefix argument.

Bindings usually use the prefix argument in one of two ways:

    - "Prefix or other:" If there is no prefix argument, the cell
      accursed in the other window is used instead. Examples:
      Connect, Put (see ``containment--benja``), Go (the 'g' key).
    - "Prefix or this:" If there is no prefix argument, the cell
      accursed in *this* window is used instead. Examples:
      Hop, Break, Clone, Delete, Excise, New Cell.

Additionally, toggling the mark (the 'm' key) should work on a cell
returned by a search, but toggling the mark on all marked cells
doesn't make much sense-- so we need a third case here. There may be
more special cases, but the above are the usual ones.

In the future, it should also be possible to set the prefix argument
through at least two more mechanisms:

    - Entering a cell number. (If you've typed '``734``,' the cell
      of that number is the prefix argument.)
    - Building an expression, using menus. It should be possible
      to build expressions like "all emails on gzz-commits which
      are newer than three days" easily, using the zaubertrank.
      (Once such an expression is built, it'll become
      the prefix argument. For example, building the above expression,
      then hitting 't' and a direction will clone all those emails
      in a single rank.)

The details of these are out of scope for this PEG; a future PEG
will deal with them. Once they are implemented, the prefix argument
will be determined as follows:

    - The latest search result, cell number or expression, if any.
    - Else, the marked cells, if any.
    - Else, there is no prefix argument.

That means if you enter a cell number, then perform a search,
the cell number will be thrown away and the search will be
the prefix argument; if you perform a search and then enter
a cell number, the search will be thrown away; and so on.


Changes
=======

None specified: This PEG does not specify an implementation. 
The point is to specify a UI behavior and to define for future PEGs 
what "prefix-or-this" and "prefix-or-other" mean.

\- Benja
