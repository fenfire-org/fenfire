==========================================================================
PEG fenpdf_v1_spec_1--tjl: FenPDF v1 first specification
==========================================================================

:Authors:  Tuomas J. Lukka
:Date-Created: 2003-07-28
:Last-Modified: $Date: 2003/08/13 12:01:37 $
:Revision: $Revision: 1.14 $
:Status:   Incomplete
:Stakeholders: benja, mudyc, humppake
:Scope:    Major
:Type:     Policy

.. Affect-PEGs:


We need a common vision for what FenPDF 1.0 will look like. 
Currently there are lots of good ideas, but several people
pulling in slightly different directions.

Everything in this PEG is very much under discussion - if you see
something you don't like, say it


Issues
======

- Should ``CONTENTLINK`` RDF vocabulary be included?
  We should be as conservative as possible; we shall have
  transclusions anyway.

  OTOH, clinks have their uses.

  Hmm, for v1.0 maybe we should have *either* clinks *or*
  pdf transclusions but not both?

      RESOLVED: Later. Again, not absolutely vital

      [benja]
      If we have content links but not PDF transclusions, we need content
      links to text -> bit difficult to show.
     
      Or, a way to make a link between a node and an enfilade. That could
      actually be good... hm. To the user, it would be functionally very
      similar to a structlink.
     
      Transclusion is probably better if we have only one of the two, but
      having both would also be good.
     
      If we have clinks, when the user has selected a piece of PDF, it would
      be good to have a "Create link" button which creates a new paper with an
      empty node that's linked to the PDF. Actually, would also be good to
      have this when the user is on a canvas and has selected a text node.
      Then this would be a simple, universal way of creating a new annotation
      to something.
     
      Or maybe "make paper" should always work like this if there's a selected
      node? When making a new paper, you almost always want it to be linked to
      the current paper?
     
      For text nodes it could also be in the context menu (if we don't have a
      way to select them in the interface)...

- RDF for cursors / bookmarks? Is just two views good enough
  for browsing? Accidental severing of contacts. Need some way to get anywhere.

      RESOLVED: TREETIME for now. The least invasive and easiest way to achieve
      connectedness.

- "overall view" of the whole space? -- embed to 2D somehow for a map?

      RESOLVED: Later. Needs experiments first

- ``rmb_action_switch--humppake``?
  Gives more actions for the mouse, but may be confusing; the UI is definitely
  not self-explanatory then.

      RESOLVED: Later. Needs experiment and is more useful when we have
      more types of objects.

- Do we need bookmarks and overall views - would simply connecting the previous
  and next canvas and pageimagescrolls in time order?

      RESOLVED: Use TREETIME and provide interface for it.

- Should the elements in the system contain their insertion time?
  As wallclock time or a tree (when merging)?

      RESOLVED: Use TREETIME and provide interface for it.

- [benja] I don't like the overloading of click+drag. Isn't there a
  better way? Maybe Shift+drag to create the transclusion after
  selecting? Hm.

  What actions *destroy* the current selection (unselect it)?
  Should every click destroy the selection? (Even panning?) If so, maybe
  the click+drag to create transclusion could be tolerable.

      RESOLVED: Shift+drag is not possible, as it needs to 
      start another selection reliably. 

      Every click destroys the selection. This way,
      there will be no annoying things that get easily stuck.
 
- [benja] How to adjust the view separator position without middle button?

      RESOLVED: Later - in 1.1. In 1.0, assume middle button.
 
- [benja] How to unlink two nodes on same paper?

      RESOLVED: In v1.0, the nodes would be shown as buoys,
      so in the usual way.


- [benja] Shift+click+drag is certainly not something you can figure
  you can just figure out (see requirements). What to do about that?

      PROPOSED RESOLUTION: Show text in the background always: 
      shift-drag to select? Or just treat this as the single thing
      that people need to be shown about this UI?

- Should left-button click+drag select or move?
  This is a complicated issue with many arguments for and against.
  Click + drag for moving relegates selecting to using shift
  which is not as obvious.

      PROPOSED RESOLUTION: move (i.e. pan). Shift for select. Clicking and dragging
      for moving *feels* right.

- [benja] How is TREETIME supposed to be *shown*?

      PROPOSED RESOLUTION: Buoy-like things which don't move with
      the center node, on the left and right edge of screen,
      and go underneath the focused node when it's zoomed..

- [benja] Will transclusions of *text* be supported, and if so, how?

      PROPOSED RESOLUTION: Later. No experience yet with them,
      not absolutely vital for 1.0.

 
- [benja] Can I enter text in both foci, or only in one?
  [tjl] This question is really "is there a change of
  focus between the two foci".

      PROPOSED RESOLUTION: Yes, the insertion cursor determines where
      the text goes. 
 
- [benja] How to move nodes on a canvas? Rearrangement seems quite
  important, so it would be bad to leave it to a later version.
  [tjl] Another dragging-like activity. We have three: pan, select, movenode.

      PROPOSED RESOLUTION: two different ways: click&drag on text node with insertion
      cursor, or Ctrl-drag.

      Transclusions should also be selectable

- [benja] How to create structlinks?

      PROPOSED RESOLUTION: Two buttons between top & bottom views, showing graphically
      the different directions of connection::

           /       \
          *--*   *--*  
	    /     \

- [benja] This describes only mouse bindings. How does the keyboard work?
  How is a new node created? How do I edit an existing node? Is a text
  cursor shown, and when, and how does it work? These need to be specified.

      PROPOSED RESOLUTION: The way it works now, mostly:
      new node is created when there is no insertion cursor in the last-clicked
      canvas, at the center.

      A text cursor is shown in the insertion mode, works like a normal
      text cursor.

      Clicking on a text node places the insertion cursor there.

      There are no command keys, only normal cursor motion.

- Should there be a plain Quit button or a Save&Quit button?

      RESOLVED: Plain quit. As tuukkah puts it, it's the
      simplest form of undo ;)

Introduction
============

This is a PEG that's different from most - it changes the whole perspective.
Instead of looking outwards and pushing the envelope, this PEG tries
for the first time to actually contain a complete working system.

This PEG will select what goes in and what does not for FenPDF 1.0.

Not having something in this feature set does certainly not mean that
it's abandoned or that it's a bad idea: it just means that we will
not include that functionality into FenPDF 1.0 even if it is implemented,
in order to keep things simple or for some other reason.

FenPDF 1.0 is kind of a "director's cut" (a term Ted sometimes used), 
and it's expected that there will be parallel versions, with different
names and identities that do almost the same thing but differently.
These are good for experiments and competition is healthy -- the same
components can be assembled differently by different people.

This spec is **extremely** conservative as to which features are taken 
in. We need a working baseline 1.0 that remains the same and functional.
We need to get *using* is asap. The policy is that we take in exactly what
*has* to be there for a working FenPDF. Including TREETIME or something like
it is because all points of the system must remain reachable.

This is the first spec PEG - once accepted, the specification here will be moved 
to the main docs/ directory and be amended only through future PEGs.

Note that evolution of the FenPDF PEG is not the only way to add new features
and e.g. canvas / document types. FenPDF is only the first applitude 
we will try to settle. There will be others (fencode, fenwrite, ... ?) which
will operate within the same graph.

Implementation
==============

The FenPDF 1.0 client shall be implemented in a new
file, ``org/fenfire/bin/fenpdf10.py``. This means
that buoying is still free ground for experiments.

It is really strict that this new demo shall **never** refer,
directly or indirectly, to any lava code.

This means that before 1.0 release, all code in lava that
is needed has to be PEGged and accepted.


Structure
=========

In a sense, this the most important part of this PEG: this part specifies
the structure that will be used within our research group when test-using
fenpdf. No other RDF nodes will be allowed in the common data base, until
the next version of FenPDF (1.1?), or v1.0 of some other applitude is defined.

Everyone may use their own user interfaces, but the shared structure is
set in stone here.

RDF
---

The structure behind FenPDF consists 
of the RDF structure in ``CANVAS2D``, ``FF``, ``RDF`` (type), and ``STRUCTLINK``.

As long as FenPDF is the only applitude used,
all other RDF words are either randomly generated ``urn-5`` words or literals.

This means that the structure is: 

- Canvases containing nodes at specific locations

- directional links between nodes

- contents for the nodes

Xanalogical
-----------

As this is FenPDF, version 1.0 will support only text and page spans.
Content links will not be supported, only transclusions.
For text spans, URN5 text spans will be used - storing the keystrokes
in their own blocks is an extra complication for now.

User interface
==============

While the Structure is an important issue, the user interface is the most *difficult* one.
There are several possible directions and we need to select one for FenPDF 1.0 ---
one that we will provide as a backwards-compatibility option in later versions of FenPDF
if it is seen to be good.

Requirements
------------

- Need to be able to easily get whole PDF zoomed, without bg, easily movable

- Easily get 2-view mode for linking 

- should be possible to figure out without manual

- easy to insert new PDFs - error handling?

- buoys carefully tuned: no clutter, easy to understand and control

    - debug mode: show where buoy locations come from!

- easy switch between fullscreen and windowed mode - window size taken 
  into account naturally

Foci
----

Two foci, shown on top of each other.

Buoy placement
--------------

There are several different kinds of buoys in this system:

Canvas-Canvas
    These are directed and can occur on either side.
    The buoy should show some context but not too much
    to avoid going to too small zoom.

    These are shown for structlinks and nothing else.

Canvas-PDF Transclusion
    Always to the right.
    Max. 20 pages of the whole document are shown.
    

PDF-Canvas Transclusion
    Always to the left
    Here, enough context needs to be shown since the transclusion 
    is just the same as the anchor.

Treetime interface
------------------

Show small buoy-like things at very edge of screen horizontally for 
next and previous canvas.


Bindings
--------

Left mouse button:
    
    click
	go to. If clicked on a text node in the focused view2d,
	add an insertion cursor.

    click + drag on focused view2d
	pan. If on node with insertion cursor, move the node

    click + drag on a selection in focused view2d (PDF only)
	drag into other view (Canvas) for creating transclusion

    shift + click + drag on focused view2d
	select

	On PDF view, select rectangle

	On canvas view, select part of text

    ctrl + click + drag
	move nodes and transclusions


Right mouse button:
    
    click + drag on focused view2d
	adjust zoom. Up = zoom out, down = zoom in.

    click
	context menu

middle mouse button:

    click + drag anywhere
	move view separator up / down

    click on focused view2d
	X11 paste to insertion cursor

Keys: insert text and move insertion cursor. If no insertion cursor, 
make new node on last-clicked / moved view.

This state could be shown by, e.g., crosshairs.

Context Menus
-------------

On struct-connected buoys: "Unlink this buoy"

For any object, "Delete this X" where X is "Text", "Paper", "PDF file", "transclusion", "structlink"


Visible buttons
---------------

Action buttons everywhere:

    Home

    Menu

The menu contains

	Import PS/PDF

	New paper

	Save



	Quit

Toggles everywhere:

    Show bgs

Toggles in pdf mode (i.e., when a PDF node is focused):

    Reading zoom

This means that there will be 3 or 4 buttons there always.

The buttons shall be placed to the upper left corner, stacked vertically. 

Some space between action buttons and toggles.

Versioning / Merging
====================

At first, in research use, merge using CVS for the RDF.


