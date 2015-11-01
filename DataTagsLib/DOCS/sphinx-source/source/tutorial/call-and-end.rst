:doc:`index`

=========
Call/End
=========

As questionnaires grow, it is sometimes convenient to split the decision graph into sub graphs, so they are easier to manage. In order to do this, Tags offers the ``call`` node. But first, we need to look at node IDs.

---------
Node IDs
---------

Any node can have an ID, and at runtime, they all do. We have already seen node IDs in interview traces. Those IDs were automatically assigned, though. To assign an ID to a node in the decision graph, enclose the ID between ``>`` and ``<``, like so::

  [>nodeId< set: s=v]


An ID serves as a persistent identifier of a node. It can be used to reference nodes from external systems, such as localization efforts and questionnaire reviews (as in "the question text in node mr13 should be revised to..."). Node ids can also be used for starting new traversals on the decision graphs.

-------------------
``Call``-ing a Node
-------------------

``call`` nodes have a simple body, consisting of the node ID the runtime should "call". When the runtime gets to such node, it pushes the current call node on to a stack. It then starts traversing the decision graph again, this time from the called node. This traversal terminates when the runtime engine hits an ``end`` node. At that time, the aforementioned ``call`` node is popped from the stack, and the runtime engine continues the previous graph traversal from that node's successor.


.. warning :: When using ``call`` nodes, it is required to mark the end of the top-level traversal with an ``end`` node. Otherwise, the traversal will continue to nodes that were previously called.


Consider the following code:

.. code ::

  [>c1< call: medicalRecords]
  [>e1< end]
  <* somewhere else in the code *>
  [>medicalRecords< ask: ... ]
  [>e2< end]

The order of execution will be ``c1``, ``medicalRecords``, ``e2``, ``e1``.


.. tip :: In CliRunner, it is possible to inspect the current call stack by typing ``\trace`` at the prompt.


.. note :: The ``call``/``end`` mechanism might look like a low-level way of managing a call stack. That's because it is. Future versions of the language will contain higher level mechanisms. The idea behind having ``call``/``end`` is to gain enough experience with interview management so that we will be able to come up with the correct higher level mechanism (in the same way that ``for`` and ``while`` constructs emerged from ``goto``).


:doc:`Next up<reject>`, we will see what you can do when you encounter a dataset that cannot be accepted to a data repository.
