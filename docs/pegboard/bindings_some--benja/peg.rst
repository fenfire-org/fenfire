===============================================
PEG ``bindings_some--benja``: Some key bindings
===============================================

:Author:   Benja Fallenstein
:Last-Modified: $Date: 2003/04/02 09:59:58 $
:Revision: $Revision: 1.2 $
:Status:   Irrelevant


I've implemented two new key bindings and am pegging them
retroactively (sorry)-- if we decide we want something
different, I can revert and do something else.


Changes
-------

The following key bindings are added:

``g``, ``G``
    If no cells are marked: In the data/ctrl window,
    respectively, go to the cell in the other window.

    If one cell is marked: Go to that cell.

    If more than one cell is marked: If the window
    is on one of the marked cells, go to the next one
    in the list (wrapping around if on the last one);
    if the window is not on one of the marked cells,
    go to the first one.

``r``, ``R``
    Reset the dimensions in the data/ctrl window,
    respectively, to the default dimension list.
    Currently this is hard-wired to (d.1,d.2,d.3)
    in the Fallback client, but when we have different
    defaults for different cursors, ``r`` will revert
    to the default dimension list for that cursor.

``o``, ``O``
    Go to the rootclone in the data/ctrl window,
    respectively.


Rationale
---------

``r`` is Ted's binding. ``o`` was ``r`` before, but had been
``o`` in Gzz 0.6, so there shouldn't be a lot of problems
with this.

In both 0.6 and the Perl prototype, ``g`` means "go to the cell
named by entering a cell number, first." We don't have cell
numbers currently; once we have, the above specification should
be amended with, "If a cell number has been entered, no matter
whether one or more cells have been marked: Go to the cell named
by that cell number."

The point is to generalize several bindings into a single one:
``g`` is, conceptually, "go to the prefix argument." The prefix
argument is the cell number, if one is entered; else, the marked
cells, if any; else, the cell in the other window. The dash key,
used for connecting, works the same way: If there are marked cells,
it connects to them; if not, it connects to the other window.
This is in tune with Ted's requirement that *all* bindings should
work with the marked cells, if any.

Since you cannot go to all marked cells at once, stepping through
the list makes sense. Marks do have order: the cell marked first
is first in the list; otherwise, the order of cells when using
the connect operation (dash key) would be random, which is *really*
not what we want. Using the binding like that makes sense if you
want to mark some place and later return to it, especially with
coplanar marking (once we'll have it).

\- Benja


