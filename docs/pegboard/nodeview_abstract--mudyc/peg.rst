=====================================================================
PEG nodeview_abstract--mudyc: Abstract node view, context and content
=====================================================================

:Author:   Matti Katila
:Stakeholders: Matti Katila
:Last-Modified: $Date: 2003/04/04 11:18:30 $
:Revision: $Revision: 1.8 $
:Status: Incomplete  


Introduction
""""""""""""

This PEG includes three abstract which are

    - Node content ('NodeContent and first definition of NodeView')
    - Node content state ('xyzState')
    - Node content with view context ('xyzContext')
    - My proposal for abstract NodeView 
 
       - xyz implicates Text, Image, Sound and Page etc.

The content_handler--mudyc PEG will explain the enfilade 
handling of nodes. This PEG only tells how node is to be 
placed knowning nothing about the content.



1) Simple way to place a node without a fear of wrong span
==========================================================


Abstract
--------

In the past we did usually have only text to show for user.
Today we have more media types: pagespans and images at least.
The View framework has been designed too much for text only. 
Although text is the most common view media we should 
easily be able to show any content in nodes.


Issues
------

   - Should getsize return float[2] or Dimension?


Changes
-------

We need an abstract layer where we can just place a node and 
where we shouldn't need to know what kind of types of spans 
are in the node's enfilade. 

::

   CellView was abstract class:
   
      public abstract void place(Cell c, VobScene vs, int box,
                                 ViewContext context);
      public void getSize(Cell c, float scale, ViewContext context,
                          float[] out) {
	  getSize(c, context, out);
      }
   }

I suggest that we abstract ViewContext away and let 
someone else to handle the view context['3)']. 
Now, make a new interface from the rest:

::

     public interface NodeContent {
         void place(RDFNode node, VobScene vs, int box);
         void getSize(RDFNode node, float[] size, float scale=1);
     }

We want to be able just to say nodeview.place(...); and know nothing 
about view context nor node's content.

Note:
   - **Very important thing** is that we don't know anything
     about the content. We can only assume what an enfilade includes. 
     That's why I like to suggest additional methods to the interface
     which would then be:

::

    public interface NodeContent {
         void place(RDFNode node, VobScene vs, int box);
         void getSize(RDFNode node, float[] size, float scale=1);

         // has methods - ask from enfilade if it contains specified span.
         boolean hasText(RDFNode node);
         boolean hasImage(RDFNode node);
         boolean hasPageSpan(RDFNode node);
    }

NodeContent interface should be implemented by *NodeView*.



2) Simple way to change the state of the node content view
==========================================================


Abstract
--------

It hasn't been very easy to say: "Write this text with red color" or 
"draw pagespan without the background texture". As I previously said about 
node content we don't even know if we have text and/or pagespan but **if we have**
we want to change it's state to this or that.


Issues
------

None


Changes
-------

I suggest several interfaces to be implemented by suitable content state handler: 

::

   public interface TextState {
      // color
      void setColor(Color c);
      Color getColor();

      // font
      void setFont(Style style);
      Style getFont();
   }

   public interface PageState {
      void setBackgroundTexVisible(boolean b);
      boolean isBackgroundTexVisible();
   }      


These would be easily called from NodeView public attributes, i.e.,
nodeview.text.setColor(Color.red); or nodeview.page.setBackgroundTexVisible(false);



3) Definied way to handle view context when placing a node
==========================================================


Abstract
--------

View context isn't very easy task. When you place a node it's correct time to ask from
view context if it has plans for current node. View context might want to set 
cursor offset, pagespan view coordinates or it might want to set text's 
color more important by reddishing it for example.


Issues
------

None


Changes
-------

When NodeView has a new node to be placed it should mention it to ViewContext.

So ViewContext should implement the following interface:

::

   public interface NodeViewContext {
      nodeIsToBePlaced(RDFNode current, NodeView nv);
   }

And now ViewContext is able to do it's job if we define new interfaces:

::

    public interface TextContext {
        void setCursorOffset(int off);
        int getCursorOffset();
    }

    public interface PageContext {
        void setViewCoords(float x, float y);
        float[] getViewCoords();
    }

Of course ViewContext can change also states, i.e., text color and font, defined in the '2)'.



4) NodeView
===========

My proposal for NodeView:

::

    public abstract class NodeView implements NodeContent {

        // Space/FenfireContext
        // for asking content - see the PEG content_hanler--mudyc followed be this PEG
        protected Space/FFC/Fen foo;

        // ViewContext - see 3)
        protected ViewContext viewContext;

        // Current node - see 3)
        // NodeView places nodes one by one.
        protected RDFNode current;

        // correct state/context handlers.- see 2) and 3)
        // and see the PEG content_hanler--mudyc followed by this PEG.
        public TextHandler text;
        public ImageHandler image;
        public PageHandler page;
    }

It's important that all 1), 2) and 3) are combined in NodeView because it's so often 
used. Nobody wants to use continuos type casting with long interface names with Java.

