====================================================================
``chug_shear_twirl--benja``: Ted's Chug, Shear, and Twirl operations
====================================================================

:Author:       Benja Fallenstein
:Date-Created: 2002-11-03
:Last-Modified: $Date: 2003/03/31 09:37:41 $
:Revision:     $Revision: 1.1 $
:Status:       Irrelevant


Ted has (more or less) specified three operations we have not
implemented so far: *Chug*, *Shear*, and *Twirl*. This PEG
gives (hopefully) unambiguous specifications for them and
assigns them key bindings.

As usual, these will be optional at first; they will be activated
by calling Gzz.py with ``--chug-shear-twirl``.


Issues
======

- What should the key bindings be? Ctrl-C for chug could be nice,
  and Ctrl-T for twirl could also work, but Ctrl-S is already
  taken for saving. (Ctrl-H?)


Chug
====

An example of the Chug operation (Chug down, with the cursor on ``C``)::


     Before                 After
     ------                 -----

        A                     
        |                     
    B - C - D             B - A - D
        |                     |
        E                     C
                              |
                              E


After 'Chug', the cursor is on 'A'. The idea of Chug is, "Move this
row/column one step in the given direction;" in the example above,
"Move this column one step down."

However, Chug only operates on a single cell, unless specified
otherwise. For example, with the cursor on ``C`` again::


     Before                 After
     ------                 -----

        A                     
        |                     
    B - C - D             B - A - D
        |                     |
    E - F - G                 C
                              |
                          E - F - G


That is, only the cells connected to ``C`` are reconnected
to ``A``; the cells connected to ``F`` are *not* reconnected
to ``C``. To reconnect them too, all that is needed is marking
both ``C`` and ``F``; then the effect of chugging down is::


     Before                 After
     ------                 -----

        A                     
        |                     
    B - C - D             B - A - D
        |                     |
    E - F - G             E - C - G
                              |
                              F


This way, we have all the options we can want through
the general prefix argument mechanism.

Chug operates on two dimensions at once, which we will
call D and E. D is the dimension we chug in, the y axis in
the example. E is the dimension whose connections are
changed, the x axis in the example. Chug is not defined
for D = E.

If we denote as I the direction (posward or negward)
in which we chug, and as (-I) the opposite direction,
the specification of Chug on a set of cells (S) is:

    - Let C be the set of connections along dimension E,
      of all cells in S. Each of the connections is a pair
      (negside, posside). A connection along E is in C
      if its negside and/or is posside is in S.
    - Let C' be C with the following change: All cells in
      S are replaced with their (-I)ward neighbour on D.

    - Unmake all connections in C.
    - Make all connections in C'.
    - If the cursor is on a cell in S, move it to its
      (-I)ward neighbour on D.

There are a number of preconditions to Chug. If these are not
satisfied, an apology is rendered and Chug is not executed.
The preconditions are:

    - D != E.
    - All cells in S which are connected on E have a
      (-I)ward connection on D.
    - If a cell in S is connected on E, and it has a
      (-I)ward neighbour on D which is not in S, then
      the neighbour is not connected on E.

The Chug key binding takes a single direction ('Down' 
in the example above). This must be on the X or on the Y axis.
If it is on the X axis, D will be the X and E the Y axis.
If it is on the Y axis, D will be the Y and E the X axis.



Shear
=====

Shear is like Chug, but one-sided-- that is, only posward
or only negward connections are reconnected. Therefore,
it needs to be given two directions: which direction to shear,
and in which direction to shear it (in that order).
For example, with the cursor on ``C``, 'Shear Left Down'
has the following effect::


     Before                 After
     ------                 -----

        A
        |
    B - C - D             B - A
        |                     |
        E                     C - D
                              |
                              E

XXX



Twirl
=====

Twirl rotates a set of connections into another dimension.
For example, with ``A`` and ``B`` marked, 'Twirl Right Down'
works as follows::


     Before                 After
     ------                 -----

  A - B - C - D		      A
			      |
			      B
			      |
			      C - D


I.e., the *rightward* connections of ``A`` and ``B`` are made
into *downward* connections. Twirl does not move the cursor.
    
Twirl operates on two dimensions D1 and D2, which may be equal.
It also operates on two directions d1 and d2 (each posward
or negward). If D1 = D2 and d1 = d2, Twirl is a no-op.

The specification of Twirl on a set of cells (S) is:

    - Let C be the set of connections on D1, in direction d1.
      Each of the connections is a pair (negside, posside).
    - Unmake all connections in C, along D1.
    - If d1 != d2, reverse all pairs in C (exchange the first
      and second element).
    - Make all connections in C, this time along D2.

The following precondition must be satisfied (otherwise,
nothing is done, and an apology is rendered):

    - If a cell in S has a connection on D1 in direction d1,
      it has no connection on D2 in direction d2.


\- Benja
