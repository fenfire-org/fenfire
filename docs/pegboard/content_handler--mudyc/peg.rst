==========================================================================
PEG content_handler--mudyc: Constructing simple handlers for specific span
==========================================================================

:Author:   Matti Katila
:Stakeholders: Matti Katila
:Last-Modified: $Date: 2003/04/04 11:18:30 $
:Revision: $Revision: 1.1 $
:Status: Incomplete  


Abstract
--------

This PEG is close to PEG nodeview_abstract--mudyc where I did
suggest simple method to place a node without any information
about the content of a node. Now, in this PEG, I've a new solution
for the problem that we still don't know a way to handle placing
part of the job.


Issues
------

None


Design
------

NodeView, which is responsible to place a node, must get information
of the content of the node. Space/FFC provides this information with 
method getEnfilade. Once we have enfilade we can split it smaller ones
so that TextSpan, ImageSpan and PageSpan etc. are in their own enfilades.
Now we can give the pieces of content to correct handler(text,image and page etc.)
where it is placed in a vob and putted in the vobscene.


Changes
-------

We need a new interface for different content handlers:

::

    public interface ContentHandler {
        void place(VobScene vs, int box, Enfilade enf);
        void getSize(Enfilade enf, float[] size);        
    }

Now we go back to nodeview_abstract--mudyc PEG and get the missing pieces from there:

::

    public abstract class TextHandler implements ContentHandler, TextState, TextContext{
        
       // implement...

    }

    public abstract class PageHandler implements ContentHandler, PageState, PageContext {

       // implement...

    }

etc...

