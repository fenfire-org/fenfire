
=============================================================
PEG projectsplit--tjl: Splitting and renaming Gzz
=============================================================

:Author:   Tuomas J. Lukka
:Last-Modified: $Date: 2003/04/02 09:59:58 $
:Revision: $Revision: 1.2 $
:Status:   Implemented

Issues
======

- Do we need to get rid of zzStructure?

    RESOLVED: Yes. Using a patented technology that
    will not be openly licensed is strictly against our
    free software philosophy. 

- Does this take away the whole basis of the project?

    RESOLVED: No. Even though we *started* from zzStructure,
    the project has in fact outgrown it. The structure
    gave us good insights and points of view (bidirectionality,
    fine-grain hyperstructure, ...), but in practical applitudes
    the code to handle the structure was always more complicated
    than it should have needed to be.

    We have developed several interesting technologies
    that we can *easily* slot around some other structure
    (vobs, uml linker, storm, ...).

- What should the overall project be renamed to? Gzz is too close
  to zzstructure.
 
    RESOLVED: Fenfire. (It's synonymous to will-o'-the-wisp.)
    Several alternatives were considered, including 
    Growl, Galatea, Silklink, Baroque, Rilix and Qwer,
    but were discarded for people objecting, or for trademark
    reasons. Fenfire was the least-objected-to suggestion.

- What about Xanalogical hypertext? Can we use that?

    RESOLVED: Yes. Ted has said it's not patented, it was
    only protected by trade secrets, and since the technologies
    have now been openly disclosed, there are no limitations
    to their use.

- Should we really split the code?

    RESOLVED: Yes. It will be far easier for others
    to use our code in smaller, easily digestible pieces.

    It's a psychological thing: it's just easier for outsiders
    to approach.

- What granularity should we be splitting up with?

    RESOLVED: Maturity, function and independence.
    I.e. split metacode, storm, &c, but don't split 
    opengl and awt vob stuff.

    The idea is that we have the core project
    that produces spin-off projects every once
    in a while, as bodies of code mature and 
    are refactored into being more independent.

- Should we really have a separate XuStorm package?

    RESOLVED: Yes. It certainly doesn't belong in Storm: 
    putting any xanalogical stuff there will just muddle
    up potential users. On the other hand, combining
    it with higher level stuff makes it harder for someone
    to just get the xu stuff.

- Who will have what powers in the new packages?

    RESOLVED: Each package will have its own maintainer
    who will act as the "enlightened tyrant" of the package,
    having final say on PEGs for that package etc.
    This is a great responsibility and needs to be distributed;
    currently too much is in the hands of one person.
    
    The different packages and their goals and maintainers
    will hopefully clarify the goals and have the decisionmakers
    close to the actual work..

- Where does mipzip stuff belong?

    RESOLVED: Even though it is currently only used for xanalogical
    images, it belongs firmly in the vob package.

- What should be the basis for licenses?

    RESOLVED: Originality. If a package
    is just an implementation of something already well-known
    and already implemented somewhere, LGPL would be preferred.
    For original work, GPL is preferred because it gives more 
    incentive for others to make free software.

- Where does buoyview stuff belong?

    RESOLVED: LibVob. In the new formulation, it's independent
    of anything and is more graphical than anything else.

Changes
=======

The Gzz project will cease to exist: despite not being 
an acronym and being a buZZword, Gzz brings zzStructure to
mind too easily.

The code that constitutes Gzz will be split into new projects
in a layerwise fashion: some will depend on others but with
as much independence as possible. This will enable reuse by
others.

There will be an overall project called Fenfire which will
depend on all the others. Fenfire is the 'real' system
we're working towards.

The new projects (in order from least dependencies on others to most)
=====================================================================

The following sections explain the new projects and what needs
to be done for the first proper standalone release.

All projects shall continue using the PEG style process for
frozen APIs; this is especially important since now others may
begin to rely on those APIs. However, PEG 201 shall be changed
so that the respective maintainers of each package have the final
words on things.

The initial dependency structure is shown in the diagram below.
There *may* be at least one more package needed, for example if
the same classes in ``gzz.util`` are used by more than one package.

..  UML:: projectsplitting

    package UMLLinker

    package Storm

    package XuStorm
	use Storm

    package GLMosaicText

    package CallGL

    package LibVob
	use GLMosaicText
	use CallGL

    package LibPaper
	use LibVob
	use CallGL

    package Fenfire
	use Storm
	use XuStorm
	use LibVob
	use LibPaper

    ---
    Fenfire.c = (0,0);

    horizontally(100, bar, Storm, XuStorm, LibVob, LibPaper);
    bar.c = Fenfire.c + (0, -200);

    horizontally(200, foo, GLMosaicText, CallGL);
    foo.c = LibVob.c + (0, -200);

    UMLLinker.c = GLMosaicText.c + (-100, -50);



Additionally, **all** packages use UMLLinker for documentation.

The packages are briefly detailed below, with their maintainers.

---------
UMLLinker
---------

:Maintainer: Asko Soukka
:License: GPL

A software engineering documentation tool using ReST and providing
multidirectionally hyperlinked UML diagrams.

-----
Storm
-----

:Maintainer: Benja Fallenstein
:License: GPL

A new type of distributed computer storage framework with global, unique
ids.

Todo:
"""""

- Figure out whether to do the "storm2" headerless stuff?

- URN-5 code should be moved into this software

--------
Xu-Storm
--------

:Maintainer: Tuomas J. Lukka or Benja Fallenstein
:License: GPL

An implementation of xanalogical hypertext on top of the distributed Storm
library.

------------
GLMosaicText
------------

:Maintainer: Tuomas J. Lukka
:License: LGPL

An OpenGL library interfacing to freetype, providing
fonts in textures, able to dice.

Uses templates to provide flexible interfaces, allowing
flexible geometry processing by the programmer.

------
CallGL
------

:Maintainer: Janne V. Kujala
:License: LGPL

A library for 1) wrapping up OpenGL calls, 2) calling OpenGL
dynamically.

------
LibVob
------

:Maintainer: Tuomas J. Lukka
:License: GPL

A graphical library providing automatic animation between unrelated
views, if the views are reasonably defined, and connections crosscutting
view hierarchies.

Todo:
"""""

- Take out all Gzz-related renderables

- Make the renderable structure better and easily extensible by
  external code

- document well

- tools for linebreaking formatted text

--------
LibPaper
--------

:Maintainer: Janne V. Kujala
:License: GPL

A library which creates unique backgrounds from given seed values.
The backgrounds are maximally recognizable as well as legible for black
text.


Todo:
"""""

- License change to GPL

- Might it be possible / feasible / worth it to untie the code from LibVob;
  possibly later.

-------
Fenfire
-------

:Maintainer: Tuomas J. Lukka
:License: GPL

Todo:
"""""

- Figure out the new structure

- Write the code to make use of the new structure










