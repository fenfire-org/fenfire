=============================================================
PEG src_utf8--tjl: UTF8 as our global encoding in all sources
=============================================================

:Author:   Tuomas J. Lukka
:Last-Modified: $Date: 2003/05/16 12:19:53 $
:Revision: $Revision: 1.2 $
:Status:   Current

We are having lots of issues with docutils because we use
Latin-1 encoding in our files to write e.g. "Jyväskylä".
We're forward-looking in most of the other stuff we do.
I suggest that we do the same in this matter: We should
do the right thing and never look back.

I propose that we agree that the days of Latin-1 are
past and move everything we do to UTF-8. 


Issues
======

- Will this be a problem with email? E.g. posting PEGs...

    RESOLVED: Maybe, but not an important one. 

    Many mail clients do already support UTF8.  If yours does not, it's
    still easy enough to read the few garbled symbols if there are any,
    and the important thing is that in CVS, things will work.


- Isn't UTF8 difficult to edit?

    RESOLVED: No, not any more.  Both emacs and vim support it.
    It's steadily gaining ground.

- Can we use UTF-8 with TeX? If not, what do we do?

    RESOLVED: Yes. There are two packages: utf8 inputencoding,
    and omega. So far, we've tried utf8, and will probably
    start using omega if it seems functional.

- Are you serious about using special unicode characters in identifiers?

    RESOLVED: Yes, occasionally, if they can help. However,
    much care is needed; never choose a character that looks
    like some other one. For instance, 2133 (SCRIPT CAPITAL M)
    is useless here. 



Changes
=======

In all ff subprojects, convert all files containing high-bit
characters (e.g. ä,ö) to UTF-8 encoding. (Including PEGs
like this one)

Explain this in README, along with instructions for the most
popular editors on how what to do.

Create a grep script which sniffs out Latin-1 ä, ö, Ä, Ö from new files.


