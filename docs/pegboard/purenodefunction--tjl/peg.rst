=============================================================
PEG purenodefunction--tjl: 
=============================================================

:Author:   Tuomas J. Lukka
:Last-Modified: $Date: 2003/08/17 14:02:45 $
:Revision: $Revision: 1.3 $
:Status:   Accepted

The NodeFunction framework is misused in several places
in Fenfire, as there are lots of node functions for
which the invariants do not hold.

This PEG suggests a way forwards to both not require too
great changes to current code but allow pure functional
semantics to be specified and applied.

Issues
======


Changes
=======

Change NodeFunction spec to not specify any invariants for it.

Add a new interface, ``PureNodeFunction`` as a subinterface
of NodeFunction.

It defines no methods but explicitly defines the invariant.

CachedNodeFunction shall only accept PureNodeFunctions.

PureNodeFunction-implementing classes SHALL BE IMMUTABLE -
all members will be final. Changing members will work
by creating a new instance with a changed member.
