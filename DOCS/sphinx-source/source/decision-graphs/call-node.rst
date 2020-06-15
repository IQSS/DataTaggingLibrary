:doc:`Home <../index>`

.. index:: Call Node

Call Node
=========

Makes the engine traverse another part of the decision graph before moving on to the syntactically next node.
The equivalent of a procedure call in other programming languages.

Consider the following graph::

  [>n1< call: p1]
  [>n2< ask: ...]

  [-->p1<
    [>n3< set: hello=world]
    [>n4< ask:
      {text: continue?}
      {answers:
        {no: [end]}
      }
    ]
    [>n5< set: lorem=ipsum]
  --]

The engine starts at node ``n1``, which calls node ``p1`` (technically, ``n1`` is pushed onto a call stack at this point). The engine then moves to nodes ``n3`` and ``n4`` as usual. If the user answer "no" to ``n4``, the engine reaches the :doc:`[end] node<end-node>`, which makes it return from the call (technically, to pop its stack). If the user answer "yes", the engine moves - as usual - to ``n5`` and then reaches the end of the ``part`` node. At this point the engine returns from the call (in the same manner as before). Either way, the engine arrives at node ``n2`` last.

To summarize the execution order of the above code:

.. raw:: html

  <code> n1 &rarr; p1 &rarr; n3 &rarr; n4 (&rarr; when user chooses "yes": n5) &rarr; n2 </code>
