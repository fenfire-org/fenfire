==========================================================================
PEG view_nodetypemanager--humppake: NodeTypeManager
==========================================================================

:Authors:  Asko Soukka
:Date-Created: 2003-09-09
:Last-Modified: $Date: 2003/09/12 11:14:51 $
:Revision: $Revision: 1.10 $
:Status:   Current
:Stakeholders: tjl, mudyc
:Scope:    Minor
:Type:     Interface

This PEG introduces a new interface to generalize the handling of
different (visual) node types in Fenfire.

The visualization of ImageScrollBlock in Fenfire requires a new
nodetype besides the current node types for 2DCanvases (papers) and
PageScrollBlocks (PDFs). Unfortunately, the selecting between those
two node types is fixed in the current code. The interface introduced
by this PEG is needed to generalize the way of handling several
different node types and therefore make it easier to add new types
also later on.

Issues
======

Are node types relevant anywhere else than in visualization?

	RESOLVED: Yes, they are. We have been planning the use of
        local keybindings depending on the type of the current main
        node (know also as *plane*, or *focus*).

What methods the use of node type dependent keybindings would need from
interface ``NodeTypeManager``?

	RESOLVED: The module handling keybindings has no particular use for
	AbstractNodeType2D designed for visualization (and it using it would
        make the architecture less flexible), but a simple Id
        specifying the node type would	be enough and simpler to use:

	  int getNodeTypeId(Object o);

	NOTE: The class implementing ``NodeTypeManager`` should define constants for
	return vales of ``getNodeTypeId``.

	RE-RESOLVED: String as the return value has less restrictions.

Wouldn't String be better ``getNodeTypeId()`` return value than integer?

	RESOLVED: Right. Int as the return value would be too dangerous,
        because	we can't control that two distinct applitudes (developed
        without knowing each other) don't use overlapping values.

	Using string allows e.g. usage of URN-5 as an unique
        identifier for a node type.

	Let it be::

	  String getNodeTypeId(Object o);

	RE-RESOLVED: Object as the return value has even less restrictions :)

Wouldn't Object be even better ``getNodeTypeId()`` return value than String?

	RESOLVED: Yeah, right... Using object allows e.g. usage of individual
        nodes as an unique identifier for a node type. Altought, using Object
	may complicate the use of identifier (since we have to know it's type
	from the context). Still the usage of Object as return value
        is versatile and therefore could be a good solution in such
        common level interface like this.

	Let it be::

	  Object getNodeTypeId(Object o);

Introduction
============

Currently the BuoyViewConnectors have a public attribute for every
node type they can recognized (*currently only
two*). BuoyViewConnetors' public node type attributes are set by a
Fenfire applitude (*currently FenPDF*) after it has created both the
needed BuoyViewConnectors and sufficient node types.

	NOTE: Node types in Fenfire are currently
	inherited from ``org.fenfire.view.buoy.AbstractNodeType2D``,
	which implements ``org.nongnu.libvob.buoy.BuoyViewNodeType``.

A major problem with the current implementation is that also the logic
for resolving the node type is written into BuoyViewConnectors -
currently every BuoyViewConnector must have its own code to recognize
all the existing node types. This decentralization has already became
a bottleneck for adding new node types or BuoyViewConnectors.

This PEG resolves the bottleneck by centralizing the recognization of
the node type. This PEG proposes an interface
``org.fenfire.view.NodeTypeManager`` with a method for querying the
type for a node object::

	BuoyViewNodeType getNodeType(Object o);

Fenfire applitudes should implement the interface only once, but so
that the node type querying method is reachable from all the relevant
parts of the applitude. Of course, the implementation of the
interface, holds the logic for resolving node types in that applitude.

Changes
=======

Create interface ``org.fenfire.view.NodeTypeManager``::

	/** Interface for methods to resolve the (visual) type
	 * of a node object.
	 */
	public interface NodeTypeManager {
    
	    /** Resolve an identifier object unique to the
	     * node type of the given node object.
	     * @param o The node object, which node type is to be resolved.
	     * @return An id object for the node type of the given node.
	     */
	    public Object getNodeTypeID(Object o);
	
	    /** Resolve the node type for the given node object.
	     * @param o The node object, which node type is to be resolved.
	     * @return An AbstractNodeType2D for the given node object.
	     */
	    public BuoyViewNodeType getNodeType(Object o);
	}

Implement the interface into FenPDF (``org.fenfire.fenpdf.FenPDFNodeTypeManager``).

Modify ``org.fenfire.fenpdf.appearance.views.Views()`` to use
FenPDFNodeTypemanager and to set it also into created
BuoyViewConnectors.

Modify BuoyViewConnectors ``org.fenfire.view.buoy.TTConnector`` and
``org.fenfire.view.buoy.TransclusionConnector`` to use
BuoyViewNodeTypes instead of AbstractNodeViewType2D.

Modify BuoyViewConnectors ``org.fenfire.view.buoy.TTConnector`` and
``org.fenfire.view.buoy.TransclusionConnector`` to use
NodeTypeManager.



