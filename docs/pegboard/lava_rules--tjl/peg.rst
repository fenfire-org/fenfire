=============================================================
PEG lava_rules--tjl: Coding rules w.r.t. lava
=============================================================

:Author:   Tuomas J. Lukka
:Last-Modified: $Date: 2003/07/28 08:33:33 $
:Revision: $Revision: 1.2 $
:Status:   Current

There is still some vagueness about lava and its use.
Let's codify the basic rules.

Issues
======

Changes
=======

The rules for lava/ directories in all fenfire subprojects:
These shall be added to the CODING rules.

1) Lava is the place for free experimentation. Anyone may add anything
   to lava/ directories. However, of course it is expected that people
   are polite towards other peoples' lava/ experiments, not deleting code
   without asking on gzz-dev first &c. However, there are no fixed rules
   about what goes into lava, except for the normal license ones.

2) Non-lava code should *never* reference lava code.

3) Non-lava code **MUST NEVER** import lava classes. All references shall
   be fully qualified.

   Exceptions to 2) will sometimes happen to implement / test something,
   and are not absolutely forbidden. However, they are strongly discouraged
   and should stand out in code and be easily greppable.
   This **ABSOLUTE** rule will make it so.

   Instead of ::
   
	import org.nongnu.libvob.lava.placeable.Placeable;
	...
	...
		Placeable p = (Placeable)nodeView.f(fen.constgraph, node);

    or even the more horrible ::

	import org.nongnu.libvob.lava.placeable.*;
	...

    the following code **MUST** be used ::

		org.nongnu.libvob.lava.placeable.Placeable p = 
			(org.nongnu.libvob.lava.placeable.Placeable)nodeView.f(fen.constgraph, node);

    i.e. no imports.

    The same applies to python code, where the appropriate imports would 
    be ::

	from org import fenfire as ff
	import vob

    and then the reference above would be

	vob.lava.placeable.Placeable

    so that the "lava" part of the class name is always included.
