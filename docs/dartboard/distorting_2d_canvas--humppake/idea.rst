==================================
Distortion attribute for 2D canvas
==================================


How should 2D canvases be distorted?

Since generally distortable/diceable vob architecture is still *under
development* for some time, FishEye transformation wouldn't work with
vobs.

One solution could be that we won't even try to distort single vobs,
but only their relative sizes and distances. The focused vob would be
in it's original size, but step by step vobs would be drawn smaller
and closer each other. This distortation way is a bit familiar from
vanishing views in GZZ.

.. image:: distortion-example.png

Distorted 2D canvases could be useful i.e. in buoys.

Discussion
==========

As this distortation wouldn't be continuous, could background be
distorted at all?
