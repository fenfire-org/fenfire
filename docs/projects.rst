===================
The Fenfire Project
===================

Javadocs
========

..  UML:: javadocs

    package org.nongnu.navidoc.util
	jlink

    package org.nongnu.storm
	jlink

    package org.nongnu.alph
	use org.nongnu.storm
	jlink

    package GLMosaicText

    package CallGL

    package org.nongnu.libvob
	use GLMosaicText
	use CallGL
	jlink

    package org.fenfire.loom
	jlink    

    package org.fenfire
	use org.nongnu.storm
	use org.nongnu.alph
	use org.nongnu.libvob
	use org.fenfire.loom
	jlink

    ---
    org.fenfire.c = (0,0);

    horizontally(50, bar, org.nongnu.storm, org.nongnu.alph, org.nongnu.libvob, org.fenfire.loom);
    bar.c = org.fenfire.c + (0, -100);

    horizontally(50, foo, GLMosaicText, CallGL);
    foo.c = org.nongnu.libvob.c + (0, -100);

    org.nongnu.navidoc.util.c = GLMosaicText.c + (-50, -50);

Pegboards
=========

..  UML:: pegboards

    package Navidoc
	link navidoc
	     pegboard/pegboard.gen.html

    package Storm
	link storm
	     pegboard/pegboard.gen.html

    package Alph
	use Storm
	link alph
	     pegboard/pegboard.gen.html

    package Loom

    package GLMosaicText

    package CallGL

    package LibVob
	use GLMosaicText
	use CallGL
	link libvob
	     pegboard/pegboard.gen.html

    package Fenfire
	use Storm
	use Alph
	use LibVob
	use Loom
	link fenfire
	     pegboard/pegboard.gen.html

    ---
    Fenfire.c = (0,0);

    horizontally(50, bar, Storm, Alph, LibVob, Loom);
    bar.c = Fenfire.c + (0, -100);

    horizontally(50, foo, GLMosaicText, CallGL);
    foo.c = LibVob.c + (0, -100);

    Navidoc.c = GLMosaicText.c + (-50, -50);

Brief explanations
==================

-------
Navidoc
-------

:Maintainer: Asko Soukka

A software engineering documentation tool using ReST and providing
multidirectionally hyperlinked UML diagrams.

-----
Storm
-----

:Maintainer: Benja Fallenstein

A new type of distributed computer storage framework with global, unique
ids.

----
Alph
----

:Maintainer: Tuomas J. Lukka 

An implementation of xanalogical hypertext on top of the distributed Storm
library.

------------
GLMosaicText
------------

:Maintainer: Tuomas J. Lukka

An OpenGL library interfacing to freetype, providing
fonts in textures, able to dice.

Uses templates to provide flexible interfaces, allowing
flexible geometry processing by the programmer.

------
CallGL
------

:Maintainer: Janne V. Kujala

A library for 1) wrapping up OpenGL calls, 2) calling OpenGL
dynamically.

------
LibVob
------

:Maintainer: Tuomas J. Lukka

A graphical library providing automatic animation between unrelated
views, if the views are reasonably defined, and connections crosscutting
view hierarchies.

LibPaper (currently in LibVob, maybe split later)
-------------------------------------------------

:Maintainer: Janne V. Kujala and Tuomas J. Lukka

A library which creates unique backgrounds from given seed values.
The backgrounds are maximally recognizable as well as legible for black
text.


------------
Fenfire loom
------------

:Maintainer: Benja Fallenstein

An RDF browser/editor.

-------
Fenfire
-------

:Maintainer: Tuomas J. Lukka

The networked hyperstructured radical user interface.
This is the least well defined part of all our projects; 
the other projects represent the hardened parts of this project -- 
the code in fenfire is still soft and changing.

FenPDF (currently in Fenfire, maybe separated as soon as it works)
------------------------------------------------------------------

:Maintainer: Tuomas J. Lukka

A hyperstructured PDF viewer using xanalogical and RDF structure.

Swamp (currently in Fenfire, maybe separated soon)
--------------------------------------------------

:Maintainer: Tuomas J. Lukka

A Java RDF API focused on SPEED.

