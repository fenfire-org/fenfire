=============================================================
PEG view_split--tjl: Split gzz.view
=============================================================

:Author:   Tuomas Lukka
:Last-Modified: $Date: 2003/04/02 09:59:58 $
:Revision: $Revision: 1.2 $
:Status:   Irrelevant

The package ``gzz.view`` is growing too much.
Should be split.

Issues
------

- Is ``gzz.view.vanishing`` necessary?

    RESOLVED: No, not yet.

- impl.cell or some other way, e.g.
  gzz.view.cell and gzz.view.cell.impl?

    RESOLVED: gzz.view.cellviews.

Changes
-------

Split ``gzz.view`` into

    gzz.view
	Main interfaces
    gzz.view.views
	Various views
    gzz.view.cellviews
	Cellviews: inside a box
    gzz.view.decor
	Classes for decorating views.
