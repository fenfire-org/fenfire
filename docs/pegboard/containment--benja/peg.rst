==================================================================
PEG ``containment--benja``: First cut at the containment mechanism
==================================================================

:Author:       Benja Fallenstein
:Last-Modified: $Date: 2003/04/02 09:59:58 $
:Revision:     $Revision: 1.2 $
:Date-Created: 2002-10-28
:Status:       Irrelevant


Ted has specified a containment mechanism for zzstructure
[http://xanadu.com.au/mail/zzdev/msg01569.html]. I need this
implemented in Gzz for the email client.


Issues
------

- Viewing a cell containing other cells.
- Editing a cell containing other cells.

It would be possible to handle containment in the views,
but that would mean that the edit bindings would have to
be changed too; it would be more convenient to use the
``CellTexter`` interface.

However, this creates additional difficulties. As the very
first step, let's only do viewing, in the views; that means
that you can see the whole contained text, but you cannot
move the edit cursor beyond the first cell. As for the next
step, well, let's see what our experiences are.


Changes
-------

For now, let's handle containment through a 
``gzz.zzutil.Containment`` class with the following methods::

    public static String getContainedText(Cell c) {
	// ...
    }

    public static Enfilade1D getContainedEnfilade(Cell c) {
	// ...
    }

These methods interpret the containment structure and return the
respective representation of the content.

Additionally, new ``p``/``P`` bindings as (IIRC) specified by Ted:
``p`` puts the left window's cell at the end of the right window's
list of contained cells; ``P`` does the reverse. (Actually, let's
use the prefix mechanism explained in PEG 1022: other window if
no cells are marked; the marked cells, if any; in the future,
the cell identified by number, if any.) If the cell to be put
"inside" another cell is already connected in a containment
structure (i.e., is already inside some other cell), it is
cloned and the clone is put instead.

These won't be turned on by default: ``p``/``P`` will only be usable
when calling the client with the ``--use-containment-keys`` parameter.
When using ``make`` to run the client, set the ``CONTAINMENT``
environment variable to ``on``::

    make run CONTAINMENT=on

To encapsulate the bindings properly, let's have an additional method
in ``Containment``::

    public static void addContainedCell(Cell add, Cell into) 
	                          throws IllegalArgumentException {
	// ...
    }

This method puts ``add`` at the end of the list of cells contained
in ``into``, *if* ``add`` has no connections on ``d..contain-list``
and no negward connection on ``d.contain``. Otherwise, it throws
an ``IllegalArgumentException``.

It doesn't do the cloning because that behavior is considered
specific to the binding. In the binding, we can then write::

    try {
	addContainedCell(add, into);
    } catch(IllegalArgumentException _) {
	addContainedCell(add.zzclone(), into);
    }

\- Benja
