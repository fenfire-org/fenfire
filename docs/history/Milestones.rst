============================================================
Fenfire (formerly Gzz (formerly GZigZag)) project milestones
============================================================

..  very much incomplete - please add stuff questions:

    - when were the name changes
    - when were the buoys invented?
    - when did Kimmo Wideroos come and go?

This document contains the most important events in the project's
history.  It's intended to be *honest*, so it does contain several of
the mistakes we made.  When reading, please remember that most such
documents would only explain the positive aspects.

1999
====

After returning to Finland from his Junior Fellowship at Harvard
University, USA, Tuomas reads Ted's talk about ZigZag. Something
clicks: Tuomas notices this this system would be very close to several
things he's been longing for in his computer use.

He contacts Ted Nelson and after some emails, they agree that Tuomas
can develop a free version, if Ted gets to do his "director's cut".

The project starts as a work project for some students, Vesa Parkkinen
and Heikki Maaranen, as a C++ version using Gtk for graphics.

In parallel, Benja Fallenstein writes a ZigZag implementation
in Python.

2000
====

Spring:
-------

For portability and ease of programming (garbage collection &c) the
Java version is started by Tuomas. The work project finishes.

The first version of libvob (called Flobs) is created.

Ted Nelson visits Jyväskylä and plants a seed to at least Antti-Juhani
Kaijanaho.

Summer: 
-------

Funded by Sonera, Antti-Juhani Kaijanaho (student at Jyväskylä) and
Tuukka Hastrup (high school student in Helsinki) join the project.

Asko Soukka hears about ZZ from Tuukka Hastrup.

Benja Fallenstein also joins, using his own time (as a college student
in Germany).

Zobs: Java objects whose fields are read from the zzStructure are
developed to help coding.

Antti-Juhani is developing the first cellular language.

Tuomas works a lot on versioning, solving some of the issues with
versioning ZZ-like sequences.

Fall:
-----

Rauli Ruohonen, a CS student at Helsinki University

Antti-Juhani works on Thales Clang, completing a partial design.

While working on Thales Clang, Antti-Juhani designs an extended, fully
general cursor mechanism, for use as a pointer system in Thales Clang,
which is adopted (http://xanadu.com.au/mail/zzdev/msg00515.html).

Tuomas has the idea of Heraclitus Clang, work on Thales Clang is
abandoned but Heraclitus is never moved beyond initial idea stage.

Antti-Juhani starts working on release engineering.  First
prerelease in September, first full release (0.1.0) two weeks later.
0.4.0 is released in December.

Benja designs and implements Flowing Clang, a simple imperative
clang with a dataflow-like view. Benja starts to implement
the key bindings in Flowing Clang, but this doesn't succeed because
there is no way to share the ZZ space containing the
bindings definitions, yet.

Antti-Juhani also becomes the hostmaster/postmaster for
www.gzigzag.org etc. After abandoning Clang work, Antti-Juhani
goes on to design a network protocol for sharing spaces.

Katariina Ervasti (a graduated humanist from Vaasa) joins the project.

.. (AJK) (I can remember Cat
   working with us before the move to Agora, she came aboard around the
   time of Ted's visit, I believe.)

.. Tuomas - don't forget Nile! :-)

2001
====

Spring: 
-------

Tero Mäyränen (local student) join the project.

Tuomas is ??? weeks on sick leave.

During a visit to Finland, Benja starts a redesign of the
vob system, based on hierarchical structures. However,
there are many design problems that cannot be resolved.
The design is put on ice.

Antti-Juhani continues working on release engineering.  Three releases
are made (0.5.0, 0.5.1 and 0.6.0).  He also worked on making new
releases understand old data (a frequent complaint from Marlene at the
time).

Antti-Juhani implements code to import past versions of a space
read-only to the "head" version.
http://xanadu.com.au/mail/zzdev/msg01043.html)

Antti-Juhani's work on the network protocol continues and a spec is
nearly finished.  He implements a stripped-down version.

A complete rewrite of GZigZag is started, destined to be 0.8.
In many ways more ambitious than the previous version,
this rewrite is never finished; it is abandoned after 1 1/2
years when the project switches to using RDF (see below).

Tuomas comes up with the idea of Mediaserver and the network protocol
stuff is abandoned in favour of Mediaserver.

While contributing to Mediaserver, Antti-Juhani comes up with the name
Storm (for STORage Module) for a subcomponent of Mediaserver. He
writes the first version of the future HTTP-Kit as part of
Mediaserver.

Katariina Ervasti leaves the project. Antti-Juhani Kaijanaho takes
over her paperwork duties.

Summer:
-------

Rauli Ruohonen invents the name "Buoys."

Tuomas, Tuukka, Benja and Antti-Juhanni give presentations at the
ZigZag workshop at the HyperText'01 conference. 
Antti-Juhani Kaijanaho and Benja Fallenstein deliver a joint talk on
ZZ programming langauages (http://www.mit.jyu.fi/antkaij/plinzz.html).

Kimmo presents a short paper at the HT'01 conference.

Fall:
-----

Antti-Juhani continues administrative stuff: release engineering
(0.6.1 is released), hostmaster/postmaster, paperwork.  He no longer
participates very actively in substance stuff.

Antti-Juhani leaves the project as a paid member at the end of the
year.

Asko Soukka starts to use GZZ 0.6 in hope of better re-usability and
interconnectivity of his notes. Because Asko used to create his notes
as mind maps, from fall 2001 to spring 2002 he did some wheel view
development to "emulate" mind mapping on GZZ.

Tuomas is invited by Harri Oinas-Kukkonen to give a visiting lecture in
Oulu, where Toni Alatalo has tried out the early ZigZag Perl implementation
and GZigZag releases before. The lecture is archived on `video`_
and gives nice, though old, overall vision and descriptions of the parts.
Toni Alatalo has been using 0.6 releases for work (notes, outlining) and 
hanging around the project since, and also Erno Kuusela was excited.

2002
====

Spring:
-------

Antti-Juhani Kaijanaho continues release engineering as volunteer
(0.6.2 and 0.6.3 are released).

GZigZag project is asked to cease using that name by Ted Nelson.  The
name is changed to GZZ in March.  GZigZag is removed from Debian, and
it was hoped that a renamed 0.6.4 would be released soon.  However,
nobody did the necessary boring work of renaming to the stable code,
so GZZ never entered Debian.

Tuomas redesigns the vob system based on coordinate systems.

Janne Kujala starts working full-time with the project.

Tuomas invents unique backgrounds using procedural texturing and
fillets (softened connections).

Summer:
-------

Tuomas and Janne design CallGL library as a simple way of abstracting 
"libpaper" internals. CallGL turns out to be extremely useful for other 
vob code, too.

Fillets published in Information Visualization '02 -conference in London.

Basic design of storm (xanalogical hypertext using GUIDs) published by
Tuomas and Benja at HT'02. Benja gives the presentation as Tuomas
doesn't want to travel to the US.

In an attempt to make Gzz 0.8 usable, Benja does ad-hoc extensions 
to the vob system to allow for hierarchy (coordinate systems
containing other coordinate systems). In cooperation with Tuomas,
the design is improved.

The PEG process is started.

Irregular edges invented

Fall:
-----

Asko Soukka (a user-friendly information technology student at
Jyväskylä University) joins the projects as a conscientious objector
(person undergoing non-military service). Asko gets introduced with
the new development version and sets his target to create a new mind
map note tool until fall 2003.

Matti Katila (mathematical information technology student at Jyväskylä
univ.) joins the project.

Tuomas and Janne make the mistake of overreaching by trying to send
articles about both irregular edges and unique backgrounds to the same
conference.  Neither article passes because both had to be finished in
a hurry. However, encouragingly, the referees' reports are
contradictory, implying that there *is* a lot of potential.

2003
====

Spring:
-------

The idea for building Lego controllers invented in a discussion about
how we could order Legos with the project's funds ;) ;)

The secret controller (secret because a patent may be applied)
invented.

US patent after all, and is building a commercial version. After the
initial shock, we react by changing the name of the project, splitting
the project (so that as many as possible parts are not depending on
each other) and starting to use RDF.  Despite all this, we still have
respect for his earlier work on Xanadu and decide to keep that aboard.

RDF turns out to be successful: it's a standard format with already
several tools, and does not share zzStructure's problems. What seemed
like a catastrophe has actually turned out well.

Asko reimplements his wheel view for Loom, the possible successor of
the basic GZZ client. Wheel view seem to have at least some potential
to show plain RDF structure as a spatial *hyper space*.

Asko works for a independent release Navidoc: the collection of
project's documentation utilies. An article about Navidoc's ability to
crosslink between distinct documentation using imagemapped UML
diagrams is submitted to HyperText '03 conference, but gets slightly
rejected because of its immaturity.

During Benja's visit to Finland near easter time, we decide to focus
our energies as a first push towards FenPDF, a PDF/PS reader / organizer
using Xanalogical media. Lots of hard work towards a deadline that turns out
didn't exist ;).

Storm P2P is implemented and experimented with.

.. _video: http://media.oulu.fi/video/lukka/Lukka_opendivx_mp3_GZigZag.avi


