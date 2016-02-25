:doc:`Home <../index>`

Call Node
=========

Makes the engine traverse another part of the decision graph before moving on to the syntactically next node.
The equivaluent of a procedure call in other programming languages.

Consider the following graph::

  [>n1< call: s1]
  [>n2< end]
  [>s1< set: hello=world]
  [>s2< set: lorem=ipsum]
  [>s3< end]

The engine starts at node ``n1``, which calls node ``s1`` (technically, ``n1`` is pushed onto a call stack at this point). The engine then moves to nodes ``s2`` and ``s3`` as usual. ``s3``, being an :doc:`end-node`, terminates the current traversal (technically - pops the stack) and the engine  moves to ``n1``'s successor, ``n2``. ``n2`` is again an :doc:`end-node`, so the current traversal is terminated again. As the engine did not get to ``n2`` from any ``[call]`` node (technically - the call stack is empty), the interview halts. Thus, the nodes are executed in the following order:

.. raw:: html

  <code> n1 &rarr; s1 &rarr; s2 &rarr; s3 &rarr; n2</code>
