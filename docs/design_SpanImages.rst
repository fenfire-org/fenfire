=================================
Getting and using images of spans
=================================

Introduction
============

Spans (see Alph) are used in Fenfire as the basis of text- and image-based
media. An important primitive in the Fenfire platform is showing the image
contained in a span. However, to do this efficiently and in accordance with the
focus+context principle, we need to load and unload levels of detail of images
flexibly. 

Libvob provides the basic architectural features for this: (XXX link to memorypool design)
competing degradable allocations from a pool of memory, and mipzip files and classes
that allow easy loading/unloading of mipmap levels.

What is left for Fenfire to provide is the infrastructure to go from spans to vobs
and as easily as possible from the vobs to the usage information to be given to the
memory pool.

Requirements
============

There are several details which will make life difficult:

- For ``PageImageSpan`` objects, there are several possible 
  mappings from span 
  to vob, since there are several possible renderings: without background
  paper, with background paper, with halos or blurring on the background
  paper &c.  Obviously, the code to go from an OpenGL texture to the
  rendered vob must be pluggable to allow for these features and future
  extensions in rendering of images of text.

- Both the whole page and fragments of the page may be desired 
  as separate vobs

- The coordinate system of the page in the texture is not fixed between 
  different spans; different resolutions might be used.

- unused spans need to be garbage collected and their textures freed: 
  must be able to handle spaces with 10^6 different span images.

- Need to be able to get width/height of the finished vob - what are 
  its extents in the cs it will be placed to

- Should be able to say (optionally) that the given span is put into 
  an important or unimportant place; although this may be unnecessary
  if the pixel counting works and culling is used

User-visible interface
======================

The interfaces ``SpanImageFactory`` and ``SpanImageVob`` in the package
``org.fenfire.spanimages`` are the 

..  UML:: fenfirespansint1

    jlinkpackage org.fenfire.spanimages

    class SpanImageFactory "abstract"
	jlink
	methods
	    static getDefaultInstance()
	    getSpanImageVob(ImageSpan s)

    dep "create" SpanImageFactory SpanImageVob

    class SpanImageVob "interface"
	jlink
	inherit org.nongnu.libvob.Vob

    class org.nongnu.libvob.Vob "interface"
	jlink

    class org.nongnu.alph.ImageSpan "interface"
	jlink
    
    dep "use" SpanImageFactory org.nongnu.alph.ImageSpan

    ---
    horizontally(90, xx, SpanImageVob, SpanImageFactory);
    vertically(90, yy, org.nongnu.libvob.Vob, SpanImageVob);
    vertically(90, zz, org.nongnu.alph.ImageSpan, SpanImageFactory);

Apart from the option of creating different ``SpanImageFactory`` objects
with different properties for e.g. page backgrounds, this is all the
other classes need to see.

Implementation
==============

The implementations are in ``org.fenfire.spanimages.gl``
(once an AWT implementation is made, it will be in the package
``org.fenfire.spanimages.fuzzybear``).

``ImageSpan`` vs ``PageImageSpan``
----------------------------------

It is reasonable to expect different treatment of 
``ImageSpan`` and ``PageImageSpan`` objects: 
for ``PageImageSpan`` objects, we will often want libpaper backgrounds and
text-enhancing transformations.

..  UML:: fenfirespansint2

    jlinkpackage org.fenfire.spanimages
    class SpanImageFactory "abstract"
	jlink

    jlinkpackage org.fenfire.spanimages.gl
    class MuxSpanImageFactory 
	jlink
	realize SpanImageFactory

    foo = assoc MuxSpanImageFactory role(mux) - multi(1) role(imagefact) SpanImageFactory
    bar = assoc MuxSpanImageFactory role(mux) - multi(1) role(pageimagefact) SpanImageFactory
    ---
    SpanImageFactory.c = (0,0);
    horizontally(80, xx, SpanImageFactory, MuxSpanImageFactory);

    sk=-.35;
    foo.p = (MuxSpanImageFactory.c{sk,1}..SpanImageFactory.c{sk,-1});
    bar.p = (MuxSpanImageFactory.c{sk,-1}..SpanImageFactory.c{sk,1});

The ``MuxSpanImageFactory`` object
delegates calls to one factory for ``PageImageSpan`` objects
and to the other
for plain ``ImageSpan`` objects.

Caching of ``SpanImageVob`` objects
-----------------------------------

The caching is taken care by another step added to the chain:

.. UML:: fenfirespans_caching

    jlinkpackage org.fenfire.spanimages
    class SpanImageFactory "abstract"
	jlink

    class CachingSpanImageFactory
	realize SpanImageFactory
	jlink
	fields
	    Map cache

    foo = assoc CachingSpanImageFactory role(cache) - multi(1) role(orig) SpanImageFactory

    ---
    SpanImageFactory.c = (0,0);
    horizontally(80, xx, SpanImageFactory, CachingSpanImageFactory);
    sk = -.35;
    foo.p := (CachingSpanImageFactory.c{sk,1}..SpanImageFactory.c{sk,-1});

The ``CachingSpanImageFactory`` will first check its cache and 
only if it does not find the object cached will it recreate it.

Repository of loaded textures
-----------------------------

The twin classes
``PageScrollBlockImager`` and ``ImageScrollBlockImager``
take care of mapping spans to OpenGL textures (mipzips).

..  UML:: fenfirespans_sbimg

    jlinkpackage org.fenfire.spanimages.gl

    class ScrollBlockImager "abstract"
	jlink
	fields
	    File tmpdir
	methods
	    abstract getSingleImage(ImageSpan sp)

    class PageScrollBlockImager
	jlink
	inherit ScrollBlockImager

    qual pq
	fields
	    String scrollBlockId
	    int page
	assoc compos multi(0..1) - SingleImage

    class ImageScrollBlockImager
	jlink
	inherit ScrollBlockImager

    qual iq
	fields
	    String scrollBlockId
	assoc compos multi(0..1) - SingleImage

    class SingleImage
	jlink
	fields

    ---
    pq.n = PageScrollBlockImager.s;
    iq.n = ImageScrollBlockImager.s;

    horizontally(40, xx, PageScrollBlockImager, ImageScrollBlockImager);

    vertically(50, yy, ScrollBlockImager, xx, SingleImage);

    vertically(80, zz, SingleImage);
    

An important architectural feature is that the classes are not static:
this allows us to, e.g, plug in filters for the images of ``PageImageSpan``.

The Single Image class
----------------------

The class used by the repositories to represent the single images
is ``SingleImage``.

..  UML:: fenfirespans_sbimg_single

    jlinkpackage org.fenfire.spanimages.gl

    class SingleImage
	jlink
	assoc compos multi(1) - multi(1) org.nongnu.libvob.gl.MipzipLoader
	assoc compos multi(1) - multi(1) org.nongnu.libvob.gl.GL.TexAccum
	fields
	    float missingPixels[20]
	    long lastUpdate
	    String scrollBlock
	    int page
	    float resolution

    jlinkpackage org.nongnu.libvob.gl

    class org.nongnu.libvob.gl.GL.TexAccum
	jlink


    class org.nongnu.libvob.gl.MipzipLoader
	jlink
	fields
	    float origWidth, origHeight

    ---
    horizontally(60, ww, org.nongnu.libvob.gl.MipzipLoader,
		    org.nongnu.libvob.gl.GL.TexAccum);
    vertically(60, yy, SingleImage, ww);


Mapping Images to Paper objects
-------------------------------

The libPaper paper abstraction is useful for rendering sections of the
images, with various settings. The input should be the TexGen matrix
for the paper texture, and the GL texture object.

We may want to change this interface to include the scale of the
characters on the paper at some point to allow better text enhancement.

..  UML:: fenfirespans_paper

    jlinkpackage org.fenfire.spanimages.gl

    class PaperMaker "interface"
	jlink
	methods
	    Paper makePaper(SingleImage img, float[] texgen)

    dep "create" PaperMaker org.nongnu.libvob.gl.Paper
    dep "use" PaperMaker SingleImage

    class SingleImage
	jlink

    class org.nongnu.libvob.gl.Paper
	jlink

    ---
    horizontally(100, xx, PaperMaker, org.nongnu.libvob.gl.Paper);
    vertically(70, yy, PaperMaker, SingleImage);

Statistics accumulation and memory pool handling
------------------------------------------------

Now we come to the *raison d'etre* of this architecture: centralized
handling of the feedback from vobscene rendering.  The ``TexAccum`` class in
Libvob is able to accumulate the approximate number of pixels rendered
at each mipmap level of each texture. This is collected 
by the ``SingleImage``
.

Because the ``MemoryPartitioner`` approach is a bit hard 
for us to interface with here
(the quality - calling time stuff is not optimal for us) we have 
our own partitioner.

The ``PoolManager`` keeps a set of active textures.

..  UML:: fenfirespans_texaccum

    jlinkpackage org.fenfire.spanimages.gl

    class org.nongnu.libvob.gl.GL.StatsCallback "interface"
	jlink

    class PoolManager
	jlink
	realize org.nongnu.libvob.gl.GL.StatsCallback
	assoc multi(1) - multi(*) SingleImage
	methods
	    SpanImageVob makeVob(SingleImage i, Paper p, float[] texgen, float w, float h)

    class SingleImage

    ---
    vertically(60, xx, org.nongnu.libvob.gl.GL.StatsCallback, PoolManager, SingleImage);

    

The final piece: the default implementation of ``SpanImageFactory``
-------------------------------------------------------------------

The class ``DefaultSpanImageFactory`` uses the above bits to implement
the ``SpanImageFactory`` interface. 

The only task it needs to do is to itself is to create the texgen matrix
from the dimensions of the actual span versus the whole page.

..  UML:: fenfirespans_imgfactoryimpl

    jlinkpackage org.fenfire.spanimages

    class SpanImageFactory "abstract"
	jlink

    jlinkpackage org.fenfire.spanimages.gl

    class DefaultSpanImageFactory
	realize SpanImageFactory
	jlink

    dep "use" DefaultSpanImageFactory ScrollBlockImager
    dep "use" DefaultSpanImageFactory PaperMaker
    dep "use" DefaultSpanImageFactory PoolManager

    class ScrollBlockImager "abstract"
	jlink

    class PoolManager
	jlink

    class PaperMaker "interface"
	jlink

    ---

    DefaultSpanImageFactory.c = (0,0);

    vertically(60, xx, ScrollBlockImager, PaperMaker, PoolManager);
    horizontally(60, yy, SpanImageFactory, DefaultSpanImageFactory, xx);


