==========================================================================
PEG ``loom_viewtool--humppake``: ViewTool
==========================================================================

:Authors:   Asko Soukka, Benja Fallenstein
:Date-created: 2002-12-10
:Last-Modified: $Date: 2003/04/25 09:40:51 $
:Revision: $Revision: 1.2 $
:Status:   Incomplete
:Scope:    Minor
:Type:     Feature

This PEG is about creating a ViewTool. The ViewTool would offer easy-to-use
interface for prototyping new views - and lowering the treshold of starting
development of new view.

Motivation
----------

Yes, I (*humppake*) believe that the view interface in GZZ 0.8 is much more
flexible than the on in 0.6. Although, I worked on 0.8 for weeks, and I
still had problems with our coordinate system biased approach for drawing.
It's good, but feels too abstract at first sight, since you are used handle
only one coordinate system at time.

I think there should be easy-to-use interface for prototyping new views.
Something with you can start directly by putting some vobs into the space
and see the results without needing to think about optimal view spesific
coordinate systems first.

.. 
   Current View package
   --------------------

   UML-refer:: viewclasses


Current VanishingClient
-----------------------

In view ``gzz.view.VobVanishingClient`` has been done a lot of work for
abstracting some of the general things that view must do - and thus,
made them easier to do.

- method for placing all vobs using only one coordinate system
  (without even need to know that other exists), using this was
  familiar from GZZ 0.6
- easy method for creating connections

::

	/** An interface abstracting some things away from the vanishing view.
	 */
	public interface VanishingClient {
	    final int CENTER = 1;

	    Object getVobSize(Cell c, float fract, int flags, Dimension into);
	    void place(Cell c, Object o, float fract, int x0, int y0, int x1, int y1,
			int depth, float rot);

	    /** There should be a connection between the given cells.
	     * If one of the cells hasn't yet been placed, this means that 
	     * a stub in that direction should be drawn.
	     */
	    void connect(Cell c1, Cell c2, int dx, int dy);
	}

.. UML:: vanishingview

	jlinkpackage gzz.view

	class View "interface"
		jlink
		methods
			void render(VobScene vs, int box, ViewContext context)
	
	class BFRaster
		jlink
		methods
			void read(Cell center, Dim[] dims)
	
	class PlainVanishing
		jlink
		use BFRaster
		use VanishingClient
		methods
			void paint(int index, int x, int y, int d, int rdepth, float fract, float xalign, float yalign, int pdx, int pdy, float rot) 
			void connect(int index, int rdepth) 
			void render(VanishingClient client, ViewContext context, int px, int py) 
	
	class VobVanishingClient
		jlink
		realize View
		realize VanishingClient
		use PlainVanishing
	
	class VanishingClient "interface"
		jlink
		methods
			void connect(Cell c1, Cell c2, int dx, int dy)
			java.lang.Object getVobSize(Cell c, float fract, int flags, java.awt.Dimension into)
			void place(Cell c, java.lang.Object o, float fract, int x0, int y0, int x1, int y1, int depth, float rot)
	------------------------------------------------------------------
	PlainVanishing.c = (350, 200);
	  horizontally(50, plain_h, PlainVanishing);
	
	View.c = (0, 100);
	  horizontally(50, view_h, View, VobVanishingClient, BFRaster);

	VanishingClient.c = (350,0);
	  horizontally(50, client_h, VanishingClient);

Describing shortly (this will be replaced with sequence diagram): VobVanishingClient
implements both the View and VanishingClient interface. When its render() is called,
it will call PlainVanishing, where the views placing logic is handled. PlainVanishing
will then use VanishingClient's abstracted interface for placing cells.

Issues
------

- Should ViewTool hide the coordinate system biased approach like
  VanishingClient currently does?
	
	RESOLVED: Not. Learning the coordinate system approach is crucial
        for view development. Though, coordinate system should be easy
        to use. 

- How cells should be placed through ViewTool?

	RESOLVED: The drawing box, cell, its 2D coordinates, depth and
        scale could be passed to ViewTool's place. It will return
        an appropriate coordinate system for placing vob self by
        VobScene's put().

        ``int placeCS(Box box, Cell cell, float x,
        float y, float depth, float scale);``
  	
  - Should X and Y be coordinates of the origo or the top lef corner 
    of the drawn vob? If it is the origo, ViewTool places stretching
    CellViews correctly, otherwise the size of stretching CV should
    be queried manually from the View.	
  
  - Do we need anymore to get and use Dimension size from VobScene, 
    when we are using box coordinate systems?
   
- How connections should be created through ViewTool?
 
- How rasters could be used through ViewTool?

- Should basic views be rewritten using ViewTool?

- Is ViewTool only obvious shortcuts to add inside VobCoorder or VobScene?

Changes
-------

This is currently in its very beginning.

.. UML:: umltool

	jlinkpackage gzz.view
	
	class View "interface"
		jlink
		methods
			void render(VobScene vs, int box, ViewContext context)
	
	class XXXView
		realize View
		use XXXViewTool
	
	class XXXViewTool
		inherit AbstractViewTool
		use XXXRaster
		methods
			void connectionCS(int box, Cell c1, Cell c2, float cs[])
			int placeCS(int box, Cell c, int x, int y, int depth, float fract)
			methods for raster?
		
	class XXXRaster
		inherit AbstractRaster
	
	class AbstractViewTool "??????????"
	
	class AbstractRaster "??????????"
	-------------------------------------------------------------------
	XXXView.c = (0, 200);
	  horizontally(50, view_h, XXXView, View);
	
	XXXViewTool.c = (0,100);
	  horizontally(50, tool_h, XXXViewTool, XXXRaster);
	
	AbstractViewTool.c = (0,0);
	  horizontally(50, raster_h, AbstractViewTool, AbstractRaster);  

Finally basic views should rewrite using ViewTool.
