====================================
Merkle hash tries (a data structure)
====================================

:Author:   Benja Fallenstein
:Created:  2003-08-09
:Modified: $Date: 2003/08/19 05:59:23 $

This dart proposes a cryptographic data structure
that may be of use in Storm.

A *trie* is a tree which stores a set of strings,
with a leaf node for every string, and with
an internal node for every common prefix.
For example, ::


              [root]
               /  \
             [0]  [1]
             /      \
           [00]     [11]
           /   \      \
         0001  0010   [111]
                      /   \
                   1110   111101


The nodes with one child can also be
omitted for efficiency, i.e. ::


              [root]
               /  \
              /    \
             /      \
           [00]      \
           /   \      \
         0001  0010   [111]
                      /   \
                   1110   111101


A *hash trie* is a trie where the (bit)strings are
the *hashes* of the objects you actually
want to store.

A Merkle hash trie, then, is a hash trie stored
as a Merkle hash tree: The root and internal
nodes are labelled with the hash of their children.

In Storm, each node would be a Storm block, and
the root and internal nodes would contain the
block ids of their child nodes.

Obviously, we would also use Storm ids as the
hashes stored in the trie.

Hash tries can also be used to implement a
*mapping*, rather than a set: Then, each leaf node
stores a key/value pair (where both key and value
are hashes), and the key is used for traversing
the trie.

Merkle hash tries can be used to--

- give an efficient proof that a certain element
  is part of a set (this is what vanilla Merkle
  hash trees also do);
- give an efficient proof that a certain key
  maps to a certain value-- and *only* that value--
  in a mapping;
- give an efficient proof that a certain element
  or key doesn't exist in a set or mapping;
- given a whole trie, traverse the trie to
  find the value associated with a given key.

"Efficient" means that for ``n`` elements in the trie,
the proofs need only ``O(log n)`` space (and time
to verify).

Also, the data structure leans itself to versioning:
Storing an updated version of a trie needs only
``O(log n)`` additional space per added/modified/deleted
entry.

Finally, the tree automatically approximately
balances itself (because the hashes are approximately
randomly distributed).

Applications could include, for example,
certificate revocation trees.


File format
===========

Below, a proposed file (block) format for hash tries,
able to represent both sets and mappings.

As proposed above, every node of the tree is one
Storm block. The file format is binary, because this
stuff is relatively space-intensive. Each block
starts with a type byte::

    0x00 -- branch node (may be the root)
    0x01 -- leaf node of set (one element)
    0x02 -- leaf node of map (key/value pair)

Any other type byte is a fatal error; a processor
must abort.

A *branch node* contains two hashes (bitprints
in binary form, which means 160+192 bits or 44 bytes).
The first is the child node for '0' bits, the second
the child node for '1' bits.

The empty tree is designated by 44 0x00 (zero) bytes.
If a branch node has only one child, the other child
is given as 44 zero bytes.

A *leaf node of set* contains a single hash, while
a *leaf node of map* contains two hashes, the first
being the key, the second being the value.

A trie is referred to by the root's block id.

\- Benja
