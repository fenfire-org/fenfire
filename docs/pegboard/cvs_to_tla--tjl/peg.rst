=============================================================
PEG cvs_to_arch--tjl: Move to a better versioning system
=============================================================

:Author:   Tuomas J. Lukka
:Status:   Current

Having the FenPDF prototype in production use requires more
stability than we've needed so far. CVS is not good for
branching and inspection of commits *before* allowing
them into the official tree.

I propose we move to arch.


Issues
======

- Do humppake and Benja and jvk want to run their own official trees?

    RESOLVED: Irrelevant for this PEG

- Should **everything** be done in arch?

    RESOLVED: No. Some things are more hassle than necessary there.
    ``manuscripts`` and ``spaces`` shall still be primarily
    handled through CVS, and mirrored to arch.

- Is the information of this PEG obsoleted because of year 2003 is
  history and it's a year 2004 already?  

    mudyc: No, just use the archive name fenfire@fenfire.org--2004a
    instead of fenfire@fenfire.org--2003. Things have changed a bit as
    usually. 


Changes
=======

(to be moved to docs/ upon acceptance)

The fenfire projects will be moved out of CVS and into arch.
CVS commits will be **strongly discouraged** except for project managers, 
and except for PEGs: mirroring the arch trees to CVS is desirable.
Exception::

    manuscripts
    spaces

will be through CVS.

The arch categories 
of the projects will be named with the current CVS directory names::

    glmosaictext
    alph
    alph-depends
    callgl
    controllers
    fenfire
    fenfire-depends
    libvob
    libvob-depends
    loom
    manuscripts
    navidoc
    navidoc-depends
    spaces
    storm
    storm-depends

The official fenfire archive
----------------------------

The official fenfire archive will be named ::

    fenfire@fenfire.org--2003

and the current official archive name is::
    
    fenfire@fenfire.org--2004a

initially located in Himalia. It is accessible through http
as well as sftp.

In it, each subproject will have a single mainline branch: ``dev``,
with other branches (e.g. ``testing``, ``stable``) to be added later.

The greatest change from the previous development model
is that **only the project maintainer (tjl) may check in to the official archive**.

This is not as bad as it sounds, as arch allows mutual merges
and other branches between developers, so this is not limiting in any way.
The benefit is that all code gets explicit pre-commit review
instead of the current post-commit-review-if-I-have-time-to-read-my-email.

This model also requires that the project maintainer be extremely
responsive: patches should not remain in the queue for more than
a day or a weekend - if there's going to be 
a longer pause, someone else should be found to stand in for 
the maintainer.

The reason for this model is twofold:

1) messing up the official tree is harder as there are more eyes on the code.
   We really need stability since the production use has started.

2) developers will be motivated to write better code due to the knowledge
   that the maintainer will actually read the code and accept
   or reject it (explaining why and what needs to be done for acceptance).

The developers should have their own arch trees mirrored to himalia for easy merge
by the project manager.

Arch versions
=============

We need star-merge. 
Tla 1.1 is not yet released.

Because of this, you need to get tla-1.1pre5 (or later) and compile it
for yourself.

Examples
========

How would this work, then?

Getting tla
-----------

Home page::

    http://regexps.srparish.net/www/

Distribution page::

    http://regexps.srparish.net/src/tla/

Get version 1.1pre5 and compile it (once it goes into debian, use that, but 
the debian version (1.0) is too old.

Setting up tla
--------------

Set up tla with the your personal id, as explained in the Tla documentation.

Getting the official version
----------------------------

In the following, I'll use ``me@bar--2003`` as the local archive and ``/home/me/{archives}`` as
your local archive dir. Adjust accordingly.

First, register the location of the archive you are mirroring from::

    tla register-archive fenfire@fenfire.org--2003-SOURCE sftp://himalia.it.jyu.fi/home/lukka/{archives}/fenfire@fenfire.org--2003

Or if you are using anonymous access via http::

    tla register-archive fenfire@fenfire.org--2003-SOURCE http://himalia.it.jyu.fi/archives/fenfire@fenfire.org--2003

Then, create the local mirror to your own hard drive.
You *could* use it directly but it's *safer* if we have lots of distributed
mirrors::

    tla make-archive --mirror-from fenfire@fenfire.org--2003-SOURCE /home/me/{archives}/fenfire@fenfire.org--2003

And finally, mirror the archive::

    tla archive-mirror fenfire@fenfire.org--2003

The last command is what you'll repeat to get the latest changes to your mirror.


**IMPORTANT**: NEVER COMMIT TO THE MIRROR. Arch will complain but may leave lock files that
you then need to clean up properly. Not fun.

Next, get the official version of the full fenfire tree from the archive::

    tla get fenfire@fenfire.org--2003/ff--dev--0.1 ff

The name `ff` is the "full fenfire", i.e. all project trees of the subprojects,
plus dependencies. The second argument is the directory name to use for the tree,
you can omit it to use the fully qualified name or give your own.

The `ff` tree is an ``arch`` *config*, i.e. the subproject project trees are not automatically
obtained but ::

    tla buildcfg ff--dev--0.1

will get them for you. Make sure you have enough disk space. The name is the same
as the branch only by choice - see ``tla`` documentation about configs.

**IMPORTANT**: DO NOT RUN BUILDCFG TWICE OR AFTER YOU MAKE CHANGES. It copies files
that are "on the way" to save directories and recovering is not fun.

Using buildcfg for ff is not obligatory but it does make things easier. You could
just as well check out all subprojects yourself.

Now: the buildcfg tree is just the official version - you can't really *do* anything
with it except build and run (remember symlinks for fenfire-priv and tmpimg).
You should not edit it - that would be misusing arch badly.
You need local branches for that.

Note in the ``ff`` directory there is the ``DIRS`` file for easy foreach loops.

Branching
---------

To really use arch, you need to make local branches from the official source
and submit your changes back to the official tree.
First, create the local archive::

    tla make-archive me@bar--2003 /home/me/{archives}/me@bar--2003

**IMPORTANT**: ``me@bar--2003`` **MUST** be unique.

Set it as your default archive::

    tla my-default-archive me@bar--2003

Next, create local categories for any of the trees you intend to modify.
Let's say you don't think you'll be editing anything except fenfire 
(you can add more later) ::

    tla archive-setup fenfire--bar--0.1

Now, create the branch by tagging from the official revision::

    tla tag fenfire@fenfire.org--2003/fenfire--dev--0.1 fenfire--bar--0.1

This should create the first revision of the ``fenfire--bar--0.1`` branch.
Now, remove the fenfire directory from ``ff/`` and get a new one::

    rm -rf fenfire          # XXX Careful!!
    tla get fenfire--bar--0.1 fenfire

Now you have your own modifiable copy in the fenfire dir.

Now, there are LOTS of variations. You could have created your own config,
etc. Read the arch docs for all that. Now, we'll make the first change
to the local fenfire tree (self-referentially, that'll be the inclusion of this
text in the cvs_to_tla PEG -- I'm testing these commands as we speak).

Commit the change::

    tla commit -L"Added a step-by-step explanations to cvs_to_tla PEG"

This commit created the version ``fenfire--bar--0.1--patch-1`` from the base
version ``fenfire--bar--0.1--base-0``.

Submitting changes
------------------

There are several ways to submit it to the maintainer. The simplest (for people
with accounts on himalia) is to mirror your local archive to himalia::

    tla make-archive --mirror me@bar--2003 sftp://himalia.it.jyu.fi/home/me/{archives}/me@bar--2003

(it's important to have it in the ``{archives}`` subdir so that the irc archbot 
knows it's an archive) and then (everytime you've made a change)::

    tla archive-mirror me@bar--2003

mirrors the changes.

Then you just inform the maintainer with a merge request::

    Could you merge

    me@bar--2003
    sftp://himalia.it.jyu.fi/home/me/{archives}/me@bar--2003
    fenfire--bar--0.1--patch-1

and then things can go several ways: 1) the maintainer accepts and merges
2) the maintainer rejects, saying "we don't want this change" or 
3) the maintainer boomerangs it back: "yes, I'll accept but change it like X and Y first".

In the first case, you don't need to do anything.
In the second case, you should undo the patch in your local branch by ``tla replay --reverse``.
In the third case, things get interesting, especially if you have other patches
after that in your archive. The project manager will want to have small, easy changes
to be easily able to judge them so you *shouldn't* just make the change and send the latest
version! What you should do is to make a new branch for it:

    tla tag fenfire@fenfire.org--2003/fenfire--dev--0.1 fenfire--bar-pegpatch1--0.1

and then replay that exact patch to this branch and make the changes and send a merge
request for this branch.

If you're making a major change you think will have issues, you are probably best off *starting*
with a new branch (that's what Tjl did in the Functional branch example).


If you don't have access to himalia, you can still submit patches using changesets
(or by publishing your archive through http elsewhere, of course).

Now, let's get this patch as something we can send to the maintainer::

    tla get-patch fenfire--bar--0.1--patch-1

This creates the directory ``fenfire--bar--0.1--patch-1.patches``. We can look
at what it contains::

    tla show-changeset fenfire--bar--0.1--patch-1.patches
    tla show-changeset --diffs fenfire--bar--0.1--patch-1.patches

and then send that to tjl for incorporation in the mainline source.

This is an ideal case, where the patchset was directly what was wanted.
However, in most cases, the command ``tla mkpatch`` should be used.


Merging changes from the main branch
------------------------------------

After you have made changes to your local branch, and the maintainer has accepted
other peoples' patches to the main branch, you need to synch up your local tree.
The ``star-merge`` command is an excellent tool for this ::

    tla commit -L"Some changes"  # Make sure you have committed things exactly
    tla update --in-place .      # If you're using more than one checkout, make sure
                                   you're up to date

    tla star-merge fenfire@fenfire.org--2003/fenfire--dev--0.1 .
    tla commit -L"Merge"

Make note of all 'C' messages and do ::

    find . -name '*.rej'

afterwards to make sure nothing went wrong.

**IMPORTANT**: To get "clean commits" as described in the tla documentation, 
**never** star-merge on a dirty tree, i.e. always commit your local
changes *first*, and **only then**, star-merge.
This helps you as well: if something goes wrong with the star-merge,
tla undo will wipe out only the star-merge instead of your changes
as well. The clean changesets help the maintainer understand what
you're doing.



Initial Patch policy
--------------------

(This will certainly need revision and further PEGs to settle completely).

The initial policy (at least of Tjl) is:

- accept changes to Storm only from Benja
- accept changes to Navidoc only from humppake (except for the data structure parts
  which need to be moved out)

- accept any changes to ``lava/`` dirs
- accept any documentation / formatting cleanups if they are certain 
  to be in the right direction
- reject unpegged changes to frozen interfaces or classes if a PEG has
  not been made, unless there are really dire circumstances. These 
  classes will each be marked with the word "Frozen".

- in all other cases (as well as the above ones, occasionally), use 
  my best judgement

