===========================================
``Known Features/Bugs``: Features of FenPDF
===========================================

:Date:		2003-04-15T15:53:31Z

.. contents::


Alphabets got lost
==================

:Date:     2003-07-07
:Observer: Benja and mudyc
:Located:  FenPDF/buoyoing, GL
:Notes:    Should not happend with awt.

Some off the charachters has been deleted from the texture memory.



Dragging before clickking jams FenPDF
=====================================

:Date:     2003-07-07
:Observer: mudyc
:Located:  FenPDF/buoyoing, GL
:Notes:    Don't know why and how. ``AbstractMainNode2D:: not a canvas view?``

The program jams.

-fixed: View2D chain was called with wrong parameters, see FastView.

