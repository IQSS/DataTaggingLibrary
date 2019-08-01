:doc:`Home <../index>` / :doc:`index`


---------
Node IDs
---------

Any node can have an ID (actually, at runtime, they all do). We have already seen node IDs in interview traces. Those IDs were automatically assigned, though. To assign an ID to a node in the decision graph, enclose the ID between ``>`` and ``<``, like so::

  [>nodeId< set: s=v]

Node id is not a free text. Is can contain only letters, numbers, and the characters ``-_.``. This is because node ids are used in various environments, and some of these environments pose restrictions on what characters can be used.

An ID serves as a node's persistent identifier. It can be used to reference nodes from external systems, such as localization efforts and questionnaire reviews (as in "the question text in node mr13 should be revised to..."). Node ids can also be used for invoking sub-graphs, as we will see in the :doc:`next section <call-and-end>`.
