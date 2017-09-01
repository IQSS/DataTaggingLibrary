:doc:`index`

================
Ordering Values
================

Different interview parts may step on each other toes. Consider a study involving student health data. Such study will be covered by health regulations, as well as by education system regulations. Suppose the data contains:

* Directory information about the students (not confidential, according to education records regulations), and
* Fully detailed medical history (confidential according to medical regulations)

Thus, the medical regulations part of a decision graph may conclude that the dataset has to be encrypted at rest, while the educational part may conclude that it can be stored in the clear. What's a DataTagging runtime engine to do?

--------------------------------
Total Order to the Rescue
--------------------------------

Atomic slots (slots that use the ``one of`` directive) are defined with the set of values that they can contain. Reminder:

.. code ::

  Storage: one of clear, encrypted, multipartyEncrypted.

The above definition defines three things:

* The ``Storage`` slot, and that it is an atomic slot.
* The values that can be placed in the slot (``clear``, ``encrypted``...)
* The total order of these values: ``clear`` < ``encrypted`` < ``multipartyEncrypted``

It is the last point -- the total ordering -- that allows Tags to resolve the issue. *Once a value is placed in an atomic slot, it can only be replaced by a greater value* according to the total ordering of the values defined for that slot. Thus, once one part of the decision graph has concluded that the data have to be encrypted (``[set: Storage=encrypt]``), no other part of the decision graph can decide they can be stored in the clear. In the above case, the data will always be (at least) encrypted, regardless of the order in which the medical and educational parts appear in the interview.

Sometimes it's easier to split a decision graph to a few files. Next, we'll look at how to do this using :doc:`imports<import-node>`.
