=========================================================
``cellview_naming--benja``: Rename CellViews, freeze them
=========================================================

:Author:       Benja Fallenstein
:Last-Modified: $Date: 2003/04/02 09:59:58 $
:Revision:     $Revision: 1.2 $
:Status:       Irrelevant
:Date-Created: 2002-10-31


``CellInBox`` is a nice interface, but not a nice name. Let's rename
it to ``CellView``, and in all places that currently use
``CellView`` or ``CellContentView``, use ``CellView``, too.
Get rid of the current ``CellView`` and ``CellContentView`` subclasses.

Additionally, after this change, add ``CellView`` to the list
of frozen classes (or, when ``view_split--tjl`` is implemented,
simply add ``gzz.view`` to the list of frozen packages).

\- Benja
