=============================================================
PEG fastviews--tjl: 
=============================================================

:Author:   Tuomas J. Lukka
:Last-Modified: $Date: 2003/08/06 16:27:53 $
:Revision: $Revision: 1.1 $
:Status:   Incomplete

This PEG contains considerations and plans related to making
view-building incremental.

Issues
======

The overall goal
================

It's best to make the overall goal explicit: we want to make 
a clean system where most of the incremental changes happen
automatically or semiautomatically --- with ideas similar
to referential transparency in functional programming languages.

This is what the observer framework (with e.g. 
getObservedConstGraph in ConstGraph) is all about.

In FenPDF, the efficiency goals would be that if there are A items altogether,
N items on the canvases that are seen,
n items that are really seen, 
c items that were changed, then view generation / changing 
should take O(c log N) time and frame rendering about O(n log N)
time. The factor N should not come in anywhere linearly, except
that the vobscenes will take O(N) memory.

If we go to a new node, the first time the node is rendered, it is allowed
to take O(N) time, naturally, but after that most things should be cached.

Also, if a view is seen on several buoys, the vobscene generation time
should only reflect at most one of them.

The easiest way to achieve this is to use vobscenes in the way a tree data
structure would be handled in functional languages: only the nodes on the
path from the changed nodes to root would be changed.

Steps along the way
===================

Making the mapping from Canvas2D to a VobScene incremental
is a little tricky, since 
the set of relations that need to be watched is not expressible as a simple
expression - either it'd require Swamp to support second-degree queries
or needs to place an observer for each node.

Using a simple observed graph for this purpose would be wasteful, as this
would require a complete redraw.
