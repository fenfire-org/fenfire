======================================================================================
``newbie-experiences.rst``: A newbie's first experiences about using FenPDF 
======================================================================================

:Date:		2003-9-12
:Revision:	$Revision: 1.8 $
:Last-Modified:	$Date: 2003/09/12 11:23:33 $
   

This document describes a newbie's first experiences about using FenPDF. The document
consists of notes that a newbie has encountered while using FenPDF. Hopefully,
these discoveries will assist FenPDF's authors to develop even better software.

The newbie started his experiments on 2003-9-11.

Actions
=======

Doesn't know how to create connections [2003-9-11]

The creation of connections is somewhat complicated (left-right stuff) [2003-9-12]

What is the "left-right" stuff anyway? Is "from left to right" connection
identical to "from right to left" connection? [2003-9-12]

"Delete this node" vs. "Destroy this canvas": why different words for similar
operations? Or, are they not similar? [2003-9-12]

While editing a text in a node, newline command is shift-enter, right? Little
confusing this Mathematica-like newlining...[2003-9-11]

User Interface
==============

When FenPDF is started, the function of two overlapping canvases in unclear, i.e.,
is there two frames, are they different view, or..? [2003-9-11]

While editing a text in a node, why the text is red? [2003-9-12]

Miscellaneous
=============

Lack of documention, i.e., actions (mouse/keyboards) which are available
for a end user. This problem affects almost everything. BAD. [2003-9-11]

FenPDF starts really sloooooowly [2003-9-11]

While FenPDF starts, a lot of expections can be seen in the console (the reason
that there are no scroll blocks available that are binded to RDF graph). Really
confusing! [2003-9-11]

Bugs
====

PDF/PS import doesn't work [2003-9-12]

Occasional crashes when pointing to a canvas (non PDF/PS), i.e.
when moving to left/right between canvases. Usually the first
step succees but when moving to an another canvas in same direction,
FenPDF crashes. [2003-9-11, 2003-9-12]

Console shows ::
EXCEPTION WHILE UPDATING!
java.lang.Error: Loading blocktmpfile - java.io.FileNotFoundException: Block: 
urn:x-storm:1.0:application/pdf,6dpeqsbe74cqqqgq5t4xn6se7yis2ura.tcdojgvbkfexkzuewmzhxj6dlbocxyfwzpkcccq
        at org.nongnu.alph.impl.StormAlph.getBlockFile(StormAlph.java:75)
        at org.nongnu.alph.impl.PageImageScroll.generatePageInfo(PageImageScroll.java:99)


Also, when I started from "Home", selected "Our Work" and pointed
mouse's left button for writing/creating a node, FenPDF crashed [2003-9-12]

Console shows ::

java.lang.Exception: java.lang.Exception: Invalid coordsys ind!
Exception in mouse
Exception in thread "Thread-2" Traceback (innermost last):
  (no code object) at line 0
java.lang.Exception: Invalid coordsys ind!
        at org.nongnu.libvob.gl.GL.transform(Native Method)
	
This happens most frequently and prevents using FenPDF!!!
Update: The Lead programmer of FenPDF says that pressing C-s twice
should prevent FenPDF to crash.
	

While editing text in a node, backspace doesn't work properly, i.e.,
user cannot which characters are actually deleted until user hits
a key or ends editing (focus to somewhere else) [2003-9-11]

While editing text in a node, a del key doesn't work at all. [2003-9-11]



