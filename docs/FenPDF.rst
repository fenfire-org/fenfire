The FenPDF applitude / application
==================================

Introduction
------------

FenPDF is the first usable product of the Fenfire project.  FenPDF is,
as the name implies, a PDF (also PS) viewer/browser but with the ability
to hyperlink and transclude parts of documents.

The primary audience for FenPDF is the academic researcher who has
a lot of literature to manage, with different aspects of different
problems touched by different articles. The reason this application 
was chosen is that we are able to conduct the first field trials 
on ourselves, which makes the free software process work better
("scratching the itch"). The same UI techniques are likely to be
applicable to other audiences as well, with some redesign.


Basic concepts
--------------

FenPDF operates on 2-dimensional *planes*, which the user
can hyperlink and write text on.
There are currently two kinds of planes:

PDF nodes
    (postscript is handled in the same way)
    These nodes contain the pages of a PDF (PS) file
    which are laid out next to each other. 
    They can be viewed with Fisheye local magnification.

Canvases
    These are the freely editable planes on which the user
    can type text or transclude (copy) parts of the PDF nodes.

There are three kinds of connections between nodes:

Structlink (structural link)

Transclusion
    This is a central concept of Xanalogical hypermedia,
    discovered by Ted Nelson. 

Creation time
    This is a semi-kludge for ensuring that all planes stay
    connected to each other through some route.

    All planes are 



Readability
-----------

One often mentioned issue with the backgrounds is readability.

For OpenGL platforms which support either the GL_NV_register_combiners
extension or the GL_ARB_fragment_program extension,
FenPDF is able to enhance readability with some novel
techniques (see ``org/fenfire/spanimages/gl/papermakers.py``).

For platforms without either extension, it is hard to do much.
