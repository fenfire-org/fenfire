==========================================================================
``view_interfaces--humppake``: View interface enhancements
==========================================================================

:Authors:   Asko Soukka, Benja Fallenstein
:Date-created: 2003-02-25
:Last-Modified: $Date: 2003/03/31 09:37:41 $
:Revision: $Revision: 1.1 $
:Status:   Incomplete
:Scope:    Major (probably)
:Type:     Architecture

This PEG discusses enhancements for View interfaces. Such are needed
to enable self stretching CellViews, flexible and easy creation of
visual connections (including fillets).

Issues
------

- how are connections created?

	PARTLY RESOLVED: Connections are created from View, but
        CellView should do the final mounting the connection to
        the cell's edge. CellView has method which returns the
        box coordinate system of mounting point.

  - how is the direction of connection delivered to CellView?

	PARTLY RESOLVED: As an angle.
 
  - how would fillets be make work with this?

- how is stretching enabled?

	RESOLVED: Stretching is enabled from View.

- should stretching be restricted?

	RESOLVED: Yes. The CellView has a minWidth, minHeight,
        maxWidth and maxHeight computed somehow.

- how strething is handled?

	RESOLVED: The idea is that View requests the size from 
        CellView and then created valid box coordinate system for it.

	The CellView return at least (minWidth,
        minHeight). If that doesn't suffice for the content, we scale 
        horizontally up to (maxWidth, minHeight). If that doesn't 
        suffice either, we use maxWidth and scale vertically
	up to maxHeight.
	
	For a single-line ccv maxHeight = minHeight. (The point being
	to place an upper bound on the cell size, in order to avoid
	the larger-than-screen cells that were so horrible in 0.6's
	stretch-vanishing view.)

	To compute the minWidth etc. parameters, the ccvs would
	usually get two strings, like the current "XXXXXXXXX" ones,
	and a line count. They'd compute the minWidth using the first
	string and the maxWidth using the second (by computing the
	width of that string using their TextStyle).  The minHeight
	would be the height of a single line (i.e.
	textStyle.getHeight()) and the maxHeight would be minHeight *
	line count.

- how is the size of CellView queried from content (e.g. from
  multiline)?

- Should aspect ratio be preserved?

	RESOLVED: Probably not very useful: re-wrapping the lines when
	the cell grows both x- and y-wise is bound to look ugly. Still
	some future CellView like BallCellView could need that. Though,
	not primary, but should be enabled later on.

- Is there need for rasters and what would they do?

                                                                               

