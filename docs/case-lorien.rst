=============
Lorien's case
=============

About installing Fenfire on clean Debian installation at
lorien.it.jyu.fi.

Status 16.6.2003:
=================

All except Loom compiles. (Loom doesn't have proper target in Makefile)

- Kaffe-jikes was still using old Kaffe classes. Relinked
  /usr/share/kaffe/Klasses.jar to /usr/local/kaffe/jre/lib/rt.jar.
- Makefiles to use $JAVAC in storm, alph, loom, fenfire...
- Added /usr/local/kaffe/jre/lib/rt.jar to CLASSPATH

Still problem with Kaffe+Jython: *sys-package-mgr*: skipping bad jar, 
'/usr/local/kaffe/jre/lib/rt.jar'

I resolved that by adding rt.jar from current Debian package (Kaffe 
1.07) into CLASSPATH.

Status 5.6.2003:
================

Changes
-------

- installed libgmp3-dev
- installed kaffe from CVS
- changed environmental variables to point new Kaffe and
  use jikes-kaffe as compiled instead of kjc provided with Kaffe 

Status
------

ANT don't work::

    BUILD FAILED
    file:/home/atsoukka/fenfire/fenfire/build.xml:28: Unable to find a javac compiler;
    com.sun.tools.javac.Main is not on the classpath.

MouseWheelEvent is in CVS Kaffe, but now got HBox/HBoxVob not found error,
changed kjc to jikes and got a lot of warnings and some errors::

    *** Semantic Error: The import "java/awt/event/MouseWheelEvent" is not
    valid, since it does not name a type in a package.

    *** Semantic Error: A candidate for type "MouseWheelEvent" was found,
    but it is invalid and needs to be fixed before this type will
    successfully compile.

    *** Semantic Error: No applicable overload was found for a constructor
    of type "java.lang.Error". Perhaps you wanted the overloaded version
    "Error(java.lang.String $1);" instead?

Navidoc works with single files, but crashed unexceptedly after about dozen
of reST -files::

    buildStackTrace((nil)): can't allocate stackTraceInfo
    kaffe-bin: exception.c:463: dispatchException: Assertion
    `baseframe != ((void *)0)' failed.

Status 3.6.2003:
================

All C++ code seemed to compile (callgl, glmosaictext, libvob).

ANT didn't work::

    BUILD FAILED
    java.lang.ExceptionInInitializerError:
    [exception was java.lang.NoClassDefFoundError:
    Lorg/apache/tools/ant/taskdefs/optional/splash/SplashScreen;]

Libvob's JAVA code didn't compile::

    org/nongnu/libvob/impl/gl/GLScreen.java:1:
    error:Import of type  "java/awt/event/MouseWheelEvent"
    from unnamed package [JLS 7.6]

    java/awt/event/MouseWheelEvent not yet in Debian's Kaffe?

Javadoc didn't work::

    java.lang.ClassNotFoundException: sun/tools/javadoc/Main

    RESOLVED: Classes should be installed outside Kaffe.

Jython seemed to work, but Navidoc crashed it with::

    os.putenv("MPINPUTS", "../navidoc/mp/")

    File "rst2any.py", line 32, in ?
    File "/home/atsoukka/fenfire/navidoc/config.py", line 32, in ?
    File "javaos.py", line 139, in __setitem__
    File "javaos.py", line 122, in _LazyDict__populate
    File "javaos.py", line 269, in _getEnvironment
    File "javaos.py", line 221, in execute
    java.lang.InternalError: fork() not provided
    ...
    
Packages installed explicitly (and caused a lot of packages to install
implicitly via apt-get) after default Debian installation from
unstable:

NVidia drivers and dev libraries
================================

- nvidia-kernel-driver
- nvidia-glx
- nvidia-glx-dev

OpenGL libraries
================

- xlibmesa-gl-dev
- xlibmesa3-gl
- xl-ibmesa-glu-dev
- libglut3-dev

Other libraries
===============

- gdk-pixbuf-dev
- libboost-dev
- libfreetype6-dev
- libgmp3-dev

Compilers and tools
===================

- ant
- kaffe
- g++-3.2
- jikes-kaffe
- jikes
- doc++

Environmental variables for Kaffe and Jikes
===========================================

- CLASSPATH=.:/usr/local/kaffe/lib:/usr/local/kaffe/jre/lib
- JAVAC=jikes-kaffe
- JAVAHOME=/usr/local/kaffe
- JAVA_HOME=/usr/local/kaffe
- PATH=/usr/local/kaffe/bin:$PATH 

.. - CLASSPATH=.:/usr/lib/kaffe/lib:/usr/lib/kaffe/jre/lib
   - JAVAC=jikes-kaffe
   - JAVAHOME=/usr/lib/kaffe
   - JAVA_HOME=/usr/lib/kaffe
   - PATH=/usr/lib/kaffe/bin:$PATH 
