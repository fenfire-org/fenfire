=============================================================
PEG linktypes--tjl: Renaming link types
=============================================================

:Author:   Tuomas J. Lukka
:Last-Modified: $Date: 2003/05/12 15:28:46 $
:Revision: $Revision: 1.6 $
:Status:   Accepted

In vocabprocess--tjl, renaming xu links and
PP links to CLink and dLink, respectively,
received opposition. This PEG is for discussing
the nomenclature of the vocabularies.


Issues
======

- Do we really need to rename things?

    RESOLVED: Yes. xuLink because of potential
    trademark stuff (improbable but possible;
    the vocabularies are far more hassle to change 
    than e.g. web pages and javadocs), and PP link
    because PP is an entirely nondescriptive specific
    term for a generic concept, and is a *finnish*
    acronym of all things.

- What should be the new name for what used to be called
  xu links, i.e. links between permanent fluid media
  content?

  Suggestions have been clink, cLink, contentLink, alphLink

    RESOLVED: Content Link. This seems to be one that
    everyone can live with. Namespace class CONTENTLINK.

- What should be the new name for what used to be called
  PP links, i.e. links between nodes

  Suggestions have been dlink, dLink, LINK.linkedTo, 
  STRUCTLINK.linkedTo.
    
    RESOLVED: Structlink. Namespace class STRUCTLINK.

Changes
=======


In vocabularies (and possibly elsewhere), the connections
that are now called xu links and PP links should
be renamed to content links and d-links.





