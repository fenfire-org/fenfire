FenMap
======


- combines 2D views familiar from GZZ / Loom and canvases+buoys
  familiar from FenPDF

- 2D view used by defauld is something like Vanishing WheelView

- three kind of nodes: data nodes, paper nodes and glue nodes

  * data nodes can contain arbitrary amount of multiline text or image

  * paper nodes identifier paper canvases with unique textures

  * glue nodes hold coordinates, between connection of data and paper 

- glue nodes are special, they are created implicitly by MindMap Canvas 
  and are not shown by default in 2D view (and never on MindMap Canvas)

- two new elements are introduced 2D view as buoy and marked cell list
  familiar from GZZ

  * when on MindMap Canvas, 2D view as buoy, shows the location of
    selected node on 2D view

  * also the transitions between 2D views and MindMap canvas are
    done via buoys

- about connectiong datas to paper:
 
  * first a direct connection is made, this is easy also in 2D view

  * no besides of a direct connection no glue nodes exist when
    changing to MM Canvas a (0,0) glue node is added for
    the first connection

  * later a new glue node between a new connection from data node
    to paper node is added every time, when new duplicate of 
    the same data node is added on the same paper

- when on 2D view, paper nodes have their unique background
  as vob background

- on 2D view, nodes can be marked as in GZZ, marked nodes
  apper to Marked list

- a button or shortkey exist for creating a new paper

- after creating the new paper, marked nodes from Marked list can
  be dragged and dropped to MM canvas

- about properties

  * at first some default property is used and that property is
    never shown (neither on canvas or 2DView)

  * later, the default hidden property can be replaced by adding
    a new property

  * on canvas, properties can be added by clicking the fillet
    between nodes and writing the property inside it

  * 2D view needs some special key combination for editing properties

- distorted canvas

  * distorted canvas can be used at leas on buoys to show a little
    more surroundings for of the linked node inside the buoy

  * also the main canvas could be shown as distorted, but probably
    it should not be that by defaul
