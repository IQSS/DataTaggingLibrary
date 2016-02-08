:doc:`Home <../index>`

End Node
========

Terminates the current decision graph traversal. If the engine call stack is not empty, the top :doc:`call-node` is removed from the stack, and the engine continues to that node's successor. If the call stack is empty, the interview is terminates.

::

  [todo: Write a questionnaire]
  [end]

In the above example, the ``[end]`` node terminates the interview, since the engine does not have any ``[call]`` nodes at its call stack.

::

[>n1< call: s1]
[>n2< end]
[>s1< set: ... ]
[>s2< end]

In the above example, ``s2`` terminates the graph traversal started from the call node ``s1``. ``n2``, on the other hand, halts the interview, since when the engine gets to it there are no nodes in its call stack.
