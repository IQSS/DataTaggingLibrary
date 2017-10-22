:doc:`Home <../index>`


.. index :: Set Node

Set Node
========

A node that sets sub values in the :any:`Current Value`. Multiple sub values can be set by the same node.

Set nodes are through nodes - after updating the current value, the engine goes on to the next node.

The below examples use the following policy space::

  DataTags: consists of Mid1, Mid2.
  Mid1: consists of Bottom1, Bottom2.
  Mid2: consists of Bottom2, Bottom3.
  Bottom1: one of b1a, b1b, b1c.
  Bottom2: some of of b2a, b2b, b2c.
  Bottom3: one of b3a, b3b, b3c.

Putting the value ``b1a`` in the atomic slot ``DataTags/Mid1/Bottom1``::

  [set: DataTags/Mid1/Bottom1=b1a]

Adding the values ``b2b`` and ``b2c`` to the aggregate slot ``DataTags/Mid2/Bottom2``::

  [set: DataTags/Mid2/Bottom2+=b2b, b2c]

Combining these operations in the same node::

  [set: DataTags/Mid1/Bottom1=b1a; DataTags/Mid2/Bottom2+={b2b, b2c}]

For brevity, it is possible to use unique suffixes instead of the full path. The below examples are equivalent to the former one::

  [set: Mid1/Bottom1=b1a; Mid2/Bottom2+=b2b, b2c]
  [set: Bottom1=b1a; Mid2/Bottom2+=b2b, b2c]

Note that we could not further abbreviate ``Mid2/Bottom2`` to ``Bottom2``, as slot ``DataTags/Mid1`` also contains a sub-slot called ``Bottom2``. Thus, referencing just ``Bottom2`` would be ambiguous, and is therefore not supported.
