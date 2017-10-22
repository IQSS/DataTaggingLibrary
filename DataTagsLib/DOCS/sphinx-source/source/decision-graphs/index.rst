:doc:`Home <../index>`

Decision Graphs Language Reference
==================================

The decision graph part of a questionnaire is composed of nodes. During the interview, the tagging engine is traversing the nodes of the decision graph. There are a few types of nodes, most of them are associated with an instruction to the engine. Nodes can have IDs. Currently, node ids are useful for referencing them from other nodes. In the future, the ids will also be used for localization.

Node Structure
--------------

A node declaration is surrounded by ``[`` and ``]``. It has a head, which contain its type and possibly an ID. It may have a body, in which case the body and the head will be separated by ``:``. It may also have sub-nodes, surrounded by ``{`` and ``}``. A node id may contain digits, letters, and any of the characters ``.,/~?!()@:#$%^&*_+-``. Here are some node example::

  [end] <-- end node, no body and no id.
  [>the-end< end] <-- end node with the id "the-end"
  [reject: We cannot accept your dataset. Sorry.] <-- a node with head and body
  [ask:           <-- a node with body consisting of sub-nodes.
    {text: why?}
    {answers:
      {why what?: [set: Subject=unclear][end]}
      {why not?: [call: explain]}}]


Graph Traversal Order (or, Control Flow)
----------------------------------------
Node types can be divided to three groups:

.. glossary::

  Through Node
    A node the engine passes through - that is, the engine visits the node, and moves to the next node in the program syntax. These nodes have a single "next" node.
    Example: :any:`set-node`.

  Terminating Node
    A node that pops the current frame in the interview stack, or terminates the entire interview. These nodes have no "next" node.
    Example: :any:`end-node`.

  Branching Node
    A node that can have more than a single "next" node.
    Example: :any:`ask-node`.
    

Throughout the execution, the engine keeps a single value of the top-level type, referred to as *Current Value*. This value can be altered by :doc:`set nodes<set-node>`. At the end of the interview, this value holds the final tagging result. Additionally, the engine maintains a call stack of nodes. Pushing onto the stack is done by :doc:`call nodes<call-node>`. Popping is done by :doc:`end nodes<end-node>`.


.. toctree::
   :glob:
   :titlesonly:
   :maxdepth: 2

   set-node
   ask-node
   consider-when-node
   call-node
   end-node
   reject-node
   todo-node
   section-node
   import-node
