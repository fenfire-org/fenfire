
==========================================================================
PEG applitude_manager--mudyc: Fenfire Applitude Management
==========================================================================

:Authors:  Matti Katila
:Date-Created: 2004-08-19
:Status:   Incomplete

:Stakeholders: benja, jvk, humppake
:Scope:    Major
:Type:     Policy|Architecture|Interface

:Affect-PEGs: buoydesign--tjl, fenfire_vision--benja_tjl
:Affect-IDEAs: fenfire_ui_vision--benja


It's time to start building the view concept needed to run applitudes
in the fenfire environment in future. I think good starting point is
window managers found from X, i.e., different view managers may be
constructed for different needs. 

In Fenfire there are also other requirements for window manager than
they usually used to have. In Fenfire they should be responsible for
managing buoys (buoys is the main reason that we can not use current
window managers). 

.. *Do one thing and do it well.* There are another needs for applitude
   management as well but this peg issues mainly rendering, viewing,
   buoys and event handling. Other issues to be considered in other pegs
   are at least:

    - Xanalogical copy&paste buffer used in fenfire.
    - Single data model and how access control to it is represented in
      the ui.
    - Representing versioning in the ui.


Issues
======

* Can we use any of the current window managers?

    RESOLVED: No we can't, too X specific. Though, we would be pleased
    if applitude management could be handled by X's window manager,
    Windows or OsX. We shall learn from X's architechture of course.

* Is it possible to bind applitudes to native window managers?

    Later perhaps. 

* It sounds like a lob system. How applitude manager differs from it?

    Basically applitude manager manages systems that consist of views
    that are build from Lobs. And, Lob system doesn't know anything about
    buoys for example. 


Definitions
===========

* View port is basically same as classic window in current system but
  view port may be also pannable and zoomable.
* Applitude is basically program. Applitude differs from program in a
  way that it shall interoperate better with other applitudes, e.g.,
  everything is linkable between them (different applitudes).


Overview
========

Create a few new classes: FServer, Applitude, ApplitudeManager. 

The F server class (a'la X server), which has main method to start the
whole thing. It should be as stateless as possible [1] and do very
little about anything [2]. It has a list of created applitude
references and view manager. F class is just simple delegate program
which uses libvob to handle events to manager. The manager shall be
changeable in any given moment.

ApplitudeManager class controls everything in screen. It requests 
Applitudes and buoys to render. It uses child vob scenes and
functional caching to obtain reasonable speed requirements. Manager
delegates events that it doesn't use itself to applitudes. 

Applitude, what is it? First time when I wrote this PEG I though that we
can just say that we have applitudes like windows in the screen and
floating buoys. Ok, then I realize that there are plenty of programs
that don't show anything on screen. So what should applitude be and
how it operates?  

Applitude rethinked. Applitude is about IPC(InterProcess
Communications). There can be these agent applitudes run on background
which robotically search things but don't show anything. To create a
one you could call::

    FServer f;
    f.createApplitude(new RobotApplitude())
    
where in FServer::

    public void createApplitude(Applitude newApp) {
        applitudes.add(newApp);   // synchronized set
	newApp.setF(this);
	newApp.register();
    }

So that FServer would be the central process of communication of
applitude management. Now if this Robot wants to show something on
screen it may register to be renderable::


    ThemeWidget theme = null;

    public void register() {
        // register to be renderable
        f.environment.register(this, "render")

	// get ThemeWidget	
	ThemeWidget[] tmp = new ThemeWidget[1];
	f.environment.request("ThemeWidget", tmp);  
	theme = tmp[0];   // no need for casting!
    }

And when new vob scene is constructed and applitude manager thinks
that robot applitude shall be rendered, it just gives a scene to it and
everything works.

Security issues are the biggest concerns. In the example above the
"ThemeWidget" was reserver from the environment before hand. Who has
a right to reserve such a mapping? Easy way could be fastest. Who
first reservest the mapping shall dominate it. Because FServer first
starts the manager which starts theme widget and then user
applitudes, them widget would be faster than user programs.

In Windows a normal program can go to register and switch off the
built in firewall. This shall newer happen within Fenfire.


Technical Thoughs
=================

Security
--------

Child vob scenes with clipping could make the system more secure since
untrusted applitudes can not mess with the main scene. What could we
obtain if this kind of strict policy is in use? There may in any case
be applitudes that don't render anything at all, or applitudes that are
background processes like virus scanner or mail polling util.

Traditional X server literally serves the screen. It's a policy good
enough. The risk is that something renders a spurious but real looking
applitude which then performs malicious activities to system.


Threading Applitudes
--------------------

So far Fenfire has been run mostly in single process. If that is
going to change, how new applitudes are started? How third party
applitudes are started? 

Changing Scenes
---------------

.. UML:: ScreenAnimation

    class WindowAnimation
         jlink
              org.nongnu.libvob.buoy.WindowAnimation

Imagine that we have a clock with second hand in some area, i.e., new
scene should be changed for every second. Animation is set to 1.8 for
going into a buoy. Around half way of the animation the clock wants to
change scene which should not be doable. How to grade clock program's
request to change scene lower than user program's request?


Responsibilities
================

Main class
----------

- Delegate events to applitude manager.
- If requested it may change manager to another one.
- Keeps list of Applitude references.


ApplitudeManager
----------------

- Does know what applitudes are rendered and which are not.
- Control view ports, e.g., place, size and rendering.
- Creates child vobscenes for different view ports in super lazy
  functional manner.
- ApplitudeManager may do (as other window managers do):

    - place applitudes virtually in different screens.
    - build button panel that can be used to activate/deactivate
      applitudes (like w95 for example).
    - grab keys, e.g., global short-cuts for cut&paste, kill wiev
      port etc.     

ViewPort
--------

- Knows where in screen the wiew port is and how big it is.
- Know zoom and magnitude. 

Applitude
---------

- May draw the applitude to given scene and coordinate system.
- Delegates events to actions.




.. UML:: ApplitudeManagement

    class BuoyViewMainNode
         jlink
              org.nongnu.libvob.buoy.BuoyViewMainNode

    class Main
         assoc Applitude
         dep "delegates_events" ApplitudeManager
         fields
             manager: ApplitudeManager
             applitudes: List<Applitude>
         methods
             setManager(ApplitudeManager): void
             getManager(): ApplitudeManager
             addApplitude(Applitude)
             removeApplitude(Applitude)

    class Applitude
         assoc BuoyViewMainNode
         inherit ViewPort

    class ViewPort
         fields
             x,y,w,h: int (pixels)
	     depth, zoom, magnitude: float
	     ?? vs: VobScene
         methods
	     moveAbsolutely(int x, int y): void             
             moveRelative(int x, int y): void

    class ApplitudeManager
         use Applitude
         fields
             main: Main
	     currentApp: Applitude
         methods
	     setCurrentApplitude(Applitude)

    ---
    horizontally(80, inherits, ViewPort, BuoyViewMainNode); 
    vertically(70, alfa, inherits, Applitude); 
    vertically(80, cecilia, alfa, ApplitudeManager); 
    horizontally(150, beeta, cecilia, Main);


References
==========

* [1] Christophe Tronche, 'Xlib programming: a short tutorial', available
  online http://tronche.com/gui/x/xlib-tutorial/ 

* [2] James Gosling, 'Window System Design: If I had it to do over
  again in 2002.', December 9, 2002, available online
  http://weblogs.java.net/jag/wsd.pdf. 

* 'System Overview', available online
  http://developer.apple.com/documentation/MacOSX/Conceptual/SystemOverview/ 

* Keith Packard, 'Life in X Land', available online
  http://keithp.com/~keithp/talks/fosdem2004/siframes.html 

* 'The (Re)Architecture of the X Window System',
  http://keithp.com/~keithp/talks/xarch_ols2004/ 


