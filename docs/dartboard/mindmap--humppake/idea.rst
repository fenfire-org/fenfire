======================
MindMapping on Fenfire
======================


Structure
=========

- data nodes (may contain arbitrary amount of multiline text or an image)
- paper nodes (identifies paper canvases with unique textures)

Naming connections and storing coordinates needs something
extra... Could connection names and placing coordinates be added
afterwards by reificating connection/statement (*"A is linked to B"*
or *"A is placed on B"*)?

2D structure view
=================

- shows vanishing focus+context network of the plain structure
- if reified statements are used, they should be abstracted
- data nodes are represented by multiline vobs
- paper nodes are represented by vobs with their papers' unique background
- connections between vobs are filleted (`named connections`_?)
- if direction of the connection matters, form of fillet could show the direction
- non-connected data nodes could be entered into `floating buffer`_
- some special key / button / action for creating connected / non-connected paper node,
  maybe also some `special papers`_ are supported
- nodes from buffer could be connected to accursed node
- nodes could be marked into `marked node list`_
- nodes from marked node list could be linked to accursed node
- when accursed data node is placed on any paper, teared part of that paper
  (focused to accursed node) is floating as a buoy
- when a paper node is accursed, teared part of its 2D paper canvas
  (focused to accursed node) is floating as a buoy
- 2D canvas buoys are shown as distorted_
- besides keybindings, mouse / pen could be used with `RMB action switch`_ method

.. _`named connections`: ../naming_filleted_connections--humppake/idea.gen.html
.. _`floating buffer`: ../floating_buffer--humppake/idea.gen.html
.. _`marked node list`: ../marked_node_list--humppake/idea.gen.html
.. _`RMB action switch`: ../rmb_action_switch--humppake/idea.gen.html
.. _distorted: ../distorting_2d_canvas--humppake/idea.gen.html
.. _`special papers`: ../special_canvases--humppake/idea.gen.html

- adding new nodes, removing old nodes, marking nodes, creating connections between nodes etc...

2D paper canvas view
====================

- every 2D paper canvas has representative paper node in the structure
- on single 2D paper canvas is shown only data nodes that are connected
  to its representative paper node in the structure
- data nodes are shown as multiline vobs
- connections within the nodes shown on paper canvas are shown as
  filleted connections on canvas (`named connections`_?)
- if direction of the connection matters, form of fillet could show the direction
- connections to nodes that are not on canvas are shown as connections
  to buoys
  
  + buoys can be teared parts of other papers (shown as distorted_),
    focused to target node
  + one buoy for every paper where target node is placed on
  + if target node is not placed on any paper a focused and vanishing 
    `2D structure view is shown as buoy`_

- nodes from floating buffer can be dragged & dropped onto canvas
- nodes from marked node list can be dragged & dropped onto canvas
- when a single data node is accursed, a 2D structured view focused
  to that node is shown as a buoy
- like canvas can be zoomed, it can also be distorted_ to see the big picture

- adding new nodes: nodes added to canvas will be connected to representing
  paper node on the structure
- removing old nodes: node will be removed from the structure only if it has
  no connections to any other node but the paper node representing
  active canvas
- moving nodes on canvas
- creating and removing connection between nodes on canvas

.. _`2D structure view is shown as buoy`: ../views_as_buoys--humppake/idea.gen.html

Transition between views is done via buoys.

Special keybinding to hide buoys (and floating buffer, and marked node
list), but only as long as the key is pressed.

Topic map
=========

.. UML:: mindmap_topicmap
   :menu: 0

   page (distorted) "Distorting 2D canvas"
	link
		../distorting_2d_canvas--humppake/idea.gen.html
   page (buffer) "Floating buffer"
	link
		../floating_buffer--humppake/idea.gen.html
   page (nodelist) "Marked node list"
	link
		../marked_node_list--humppake/idea.gen.html
   page (named) "Naming filleted connections"
	link
		../naming_filleted_connections--humppake/idea.gen.html
   page (rmb) "RMB action switch"
	link
		../rmb_action_switch--humppake/idea.gen.html
   page (buoys) "Views as buoys"
	link
		../views_as_buoys--humppake/idea.gen.html
  
   page MindMap
	link
		idea.gen.html
	use distorted
	use buffer
	use nodelist
	use named
	use rmb
	use buoys

   ---
   horizontally(25, hor_a, buoys, distorted, named);
   horizontally(25, hor_b, nodelist, MindMap, buffer);
   vertically(25, vert, distorted, MindMap, rmb);
