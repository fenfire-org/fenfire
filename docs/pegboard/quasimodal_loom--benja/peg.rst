============================================================
A preliminary quasimode-based interface for Fenfire and Loom
============================================================

:Author:	Benja Fallenstein
:Date:		2003-02-17
:Revision:	$Revision: 1.2 $
:Last-Modified: $Date: 2003/07/05 21:47:25 $
:Type:		User Interface
:Scope:		Major
:Status:	Postponed


Modes create mode errors. I've witnessed this in Gzz many
times, where users would try to type text into a cell
while in the wrong mode, creating a huge mess. Research
indicates that even sophisticated users cannot keep track
of modes always. When commands have habituated so that you
do them without having to think about them, you cannot
keep track of modes.

Research also indicates that *quasimodes* never create
mode errors. A quasimode is a mode that is in effect
while some indicator switch is physically activated.
For keyboard interaction, this means "while a modifier key
is held down".

The proposal below is a keyboard language for Loom 0.2,
the "usable RDF editor" release, based on quasimodes. 
After experience with this release, we will re-evaluate
the keyboard language.


Specification
=============

Text entered without pressing a modifier is simply normal
text input. It goes where the text cursor is. If there
is no text cursor, one is created at the end of the
currently focused node or literal.

The *Left* and *Right* keys are used to move the text
cursor. If there is no text cursor, *Left* creates one
at the beginning and *Right* creates one at the end
of the text in the focused node or literal.

Alt is used for the movement quasimode. That is, while you
hold down Alt (and no other modifier), you can move around
in the structure using the arrowsets known from zzstructure.
Our arrowsets are 9-key: we have ``uiojkln,.`` and
``wersdfxcv``. This allows convenient rotation of the
wheel view.

Pressing Shift while Alt is held down enters a special
command quasimode. The first key pressed after Shift
specifies a command; the interpretation of the following
keys depends on the command, but generally they are
interpreted as arrow arguments to the command. For
example, "N <dir>" may create a new node in the
given direction, and "H <dir>" may hop in the given
direction.

While in this command quasimode, we can give as many
arrows as we like. In a sense, this allows us to have
quasimodes for New, Hop and so on. The quasimode is
in effect until Shift is released.

For now, the important commands we need are creating
nodes and making and breaking connections. In order to
create arbitrary connections, we also need to be able
to mark resources. I suggest the following bindings:

Command mode, ``N <dir> [<dir> <dir>...]``
    Create a new node in the given direction. (We have
    a property setting specifying which property
    is used.) The node will be assigned a URN-5.
    If ``<dir>`` is left or right, we move onto the
    new node. If ``<dir>`` is a diagonal direction,
    we do not change the focus, but select the
    new node.

Command mode, ``B <dir> [<dir> <dir>...]``
    Break the connection in the given direction.
    Only works with left and right.

Command mode, ``M [<dir> <dir>...]``
    Marks the current node. The mark is set, not
    toggled; if there is a mark already, nothing
    happens. While in mark mode, giving left/right
    directions moves the focus and marks the
    newly focused node. Giving diagonal directions
    rotates the wheel, but doesn't mark anything.

Command mode, ``C <dir>``
    Connect the marked nodes in the given direction.
    To determine the property, the same setting
    as for new nodes is used.

Command mode, ``U [<dir> <dir>...]``
    Like ``M``, but unmarks; i.e., it removes marks
    where ``M`` adds them.

``Esc``
    Removes all marks.

For mouse-based interaction, we'll have menu items,
"New node (Left)," "New node (Right),"
"Break connection (Left)," "Connect marked (Right)"
and so on.

\- Benja