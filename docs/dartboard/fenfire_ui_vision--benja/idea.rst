============================
Fenfire UI vision -- my take
============================

:Author: Benja Fallenstein
:Date:   2003-09-14

This is a document briefly describing how I am envisioning
the full Fenfire user interface, at the moment.

The basic thing is the Loom structure, the "zz views for RDF:" 
Nodes shown in a focus+context view, which you can move through,
and you can select which RDF properties to show, swapping them
in and out as you go.

This already enables many useful things: Storing addresses,
birthdays, telephone numbers, IBIS argument structures
(e.g., X is a proposed resolution to issue Y),
TODO lists, bug lists, project schedules, budgets etc.

Then we have applitude-specific views: Views that show one
kind of information structure in a nicer way. For example,
events in time on a calendar, or on a spiral like Ted
has proposed; or events on a map (at the points where
they have happened/will happen); or a chart of the things
in our budget.

Actually, views will try to be as general as possible,
and often useful for more than one applitude. For example,
we'll have table views, and the chart view will be able
to show many kinds of data, and of course we'll have
spatial canvas views, which will enable us to place *anything*
on a 2D canvas.

We'll be able to switch between the different views
of a thing, most likely through a menu. (Cycling through them
as in ZZ will not do when we have many of them.) We'll choose
a view from a menu, and often we'll set a couple of view
parameters: For example, we'll tell the table view which
RDF properties to show in the columns (like in the Loom views),
we'll tell the chart view which properties to show
graphically, and so on.

Now, each of these views will tell the system which vobs
correspond to which node in the RDF graph. This will allow
us to *annotate* these views.

For example, below each event, we could show the money
that we spend on this event, in small, red digits.
**In any view showing this event.** It doesn't matter
whether the event is shown in a calendar view, in a
spiral time view, in a table view, in a chart view,
on a spatial canvas, or in a basic Loom view: The expense
for that event will always float below it in small red digits.

Like choosing which properties to show in a Loom view,
the user will choose from a set of selections which
annotation views to show at any time, switching them
on and off as they like.

The most important annotation view will not be that
money view, though. It will be the basic Loom view.

If you have an event in your calendar, and you want
to record that this event is a meeting with Carli,
then you simply make an RDF structure to that effect--
subject: the meeting; property: ``event:with``;
object: Carli, say.

Then, you tell Fenfire to show the ``event:with`` property.

No matter which view you're using, when the event is shown
somewhere, the RDF connection to Carli will be shown--
as a buoy.

If you click on "Carli," what will be shown depends on your
view settings. I expect that you will be able to select
different views for the different types of nodes you have;
so when you have selected "spiral time view" for events,
when your focus is on "Carli," that setting has no effect,
because Carli isn't an event, but when you go back to the
meeting event (all connections are bi-directional), it
will be shown in the time view.

If no other view is specified for a node type, a basic
Loom view will be used. So if you have no view setting
for people, when you click on "Carli," you'll look at
a Loom structure. Connected negwards on ``event:with``
will be the meeting.

In the loom view, the meeting will generally be shown as a node
no larger than any Loom node, no matter what your special
view settings for events are when those events are focused.
But when you actually move onto the event, the spiral time view
will again occupy most of the screen and the RDF connections
will only be shown as buoys.

(It is then vital that the Loom bindings work for buoys.)

---------------------------------------------------------------------------

Therefore, any node's RDF properties can be shown
as buoys. (A side-effect is that this can always be used
for introspection, e.g. you can make the x/y positions
of objects on a canvas visible through this mechanism.)

Many applitudes will deeply make use of this. You don't
need to come up with a way to show details about an event
in a calendar view; you simply tell users to show
these-and-these RDF properties as buoys.

This makes applitudes naturally extensible: The user needs
some more, the user shows some more. When you add properties,
it looks natural and integrated, because the applitude
*always* uses this mechanism to show additional information.

---------------------------------------------------------------------------

Ok, I think basically that's the view system. Then,
of course, you have menus with commands, which can be
tailor-made to an applitude ("make appointment," something
like that). Users can create their own menus; they're
just some RDF structure shown in a special way (always
present on the screen or so).

Also, there needs to be a good way to store and organize
view presets: E.g., to create a money view, you'd select
"View showing text below node" or something, select the
RDF property to show (``money:cost``), select small
text size and red color. Then you'd put this
view-with-parameters into a "view" menu as "Expenses," easily
showable and hidable.

(Note BTW that I use "menu" to include things containing toggles--
we have lots of those, switching particular RDF properties 
on and off for example.)

Finally, you want menus to be context-dependent very likely--
i.e., when you're scheduling, you want to see the menu with
"Make appointments" readily, and so on.

Then, basically, I think that's it.