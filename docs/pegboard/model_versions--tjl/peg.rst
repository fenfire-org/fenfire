=============================================================
PEG model_versions--tjl: 
=============================================================

:Author:   Tuomas J. Lukka
:Last-Modified: $Date: 2003/04/02 17:03:07 $
:Revision: $Revision: 1.1 $
:Status:   Incomplete

Versioning is one of the key design points in fenfire. The ability to browse,
diff and edit different versions of the same "document" is vital.
This PEG explains a way to represent different versions of the same model
internally in a single, unified model, so that it is easy for views to choose
how to show the differences.

Issues
======

- A better name for the composite model than diffplex?

Design
======

One of the major features of RDF is *reification*, i.e. the ability to
use triples to say something *about* a triple T, where T need not be "real"
in the sense of being contained in the present model.

Let's define the "diffplex" RDF model M of the models M_1, M_2 ... as follows:

    If **all** the models M_i contain a triple T, then M shall
    contain T.

    If **some** of the models M_i but not all contain a triple T = (s,p,o), then M shall
    contain an anonymous reification x of T, and shall contain triples (x, M, M_i)
    for each model M_i. The identifier for M is defined to be such that it shall
    never occur in the models M_i.

This model now contains all the information of the models M_i as well "strenghtened"
triples of the information shared between all M_i. 
