===========================================================================================
PEG textnodeview_locate_cursor--humppake: Locating cursor in textnodeview with broken lines
===========================================================================================

:Authors:  Asko Soukka
:Date-Created: 2003-08-14
:Last-Modified: $Date: 2003/08/15 07:26:32 $
:Revision: $Revision: 1.2 $
:Status:   Current
:Scope:    Trivial
:Type:     Interface, Feature

Mudyc asked me to think how the text cursor could be located within a
TextNodeView (currently ``org.fenfire.view.TextNodeView``). This PEG
describes my solution proposal for solving the problem: two new
methods for TextNodeView. One would returning the cursor position
according to coordinates within the TextNodeView and another would
return the coordinates of the cursor according to it's position (often
referred as offset) within the text.

Issues
======

Who is responsible for managing text cursor position? (The term
`position` means here the cursor's offset within the text string.)

	RESOLVED: The text cursor position is usually stored within
        a demo application. Also a spesific Cursor class may be used
        (``org.fengire.view.lava.Cursor``).

	RE-RESOLVED: Currently the text cursor position is usually stored
        within a demo application, but Mudyc seems to be developing more
        general cursor handling in Lava.

But there is also a method ``getCursorOffset`` in
``org.fenfire.view.lava.TextHandler`` (which the `TextNodeView``
inherits). Does it have any role?

	RESOLVED: AFAIK, currently it's not used anywhere.

	RE-RESOLVED: We need to ask Mudyc about the role of
        ``TextHandler`` methods in ``TextNodeView`` and also his
        cursor development in Lava.

	Anyway, this peg is not dependent on the way of storing the
        text cursor position.

Who is responsible for breaking a text string into lines?

	RESOLVED: Only the used ``TextNodeView`` knows. Currently, it
        uses LibVob's linebreaking code
        (``org.nongnu.libvob.linebreaking``). The linebreaking
	information is generated only when needed (when creating a
        ``Placeable`` object) and is not stored.

How can the linebreaking information be reached?

        RESOLVED: The ``TextNodeView`` objects do not store the
        linebreaking information but can reconstruct it.

Where could the information of linebreaking be reconstructed?

	RESOLVED: The reconstruction is complex and is is also most
        practical to do within ``TextNodeView``, since it has already
        the supportive methods to help using the linebreaking
        library.

	In ``TextNodeView`` only the text content is needed for
        linebreaking. When creating a placeable TextNodeView object,
        the text is fetched from the current node (required parameters
        are current ``ConstGraph`` and current node)

When is the linebreaking information needed?

	RESOLVED: With mudyc we ended up to two main use cases.

        1) I know the cursor offset, but in which coordinates I should
           draw the cursor into?

        2) I click the text with mouse and know the coordinates I
           clicked, but how I set the cursor offset according to those
           coordinates? (this should cover also the selection of text
           using mouse)
           
How could the needs be addressed?

	RESOLVED: Based on to the previous use cases, I suggest two
        new methods into ``TextNodeView``. They will reconstruct the
        linebreaking information using the supportive methods already
        in ``TextNodeView`` and use the public methods of
        ``linebreaking.HBroken`` and ``linebreaking.HChain`` to
        collect the information needed.
           
        The first method returns the cursor position for given
        coordinates within a TextNodeView and the other returns the
        coordinates of the cursor given its position
        (often referred as offset) within the text.

	The following (pseudocode) expression should be always true
        when a proper (existing) text cursor offset value is used::

		a == TextNodeView.getPos(TextNodeView.getXY(a))

Changes
=======

Into ``org.fenfire.view.TextNodeView``::

    /** Get position of the first character placed
     * after given coordinates.
     */
    public int getPos(ConstGraph g, Object node, float x, float y);

    /** Get coordinates before the given character position.
     * The Y coordinate is located below the line.
     */
    public void getXY(ConstGraph g, Object node, int pos, float[] xy);


Implementation notes
====================

``TextNodeView.getXY()`` returns the y coordinate located below the
line, because ``linebreaking.Hbroken.getLineOffset()`` also does that.
