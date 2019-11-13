:doc:`Home <../index>` / :doc:`index`

===============
Call, Part, End
===============

PolicyModels allows creating small decision graphs. These graphs are referred to as "parts". To run such a graph, it has to be called from another graph. That other graph may be a ``part`` itself, or it may be the main graph. This mechanism, which is PolicyModels' equivalent of procedure calls, is useful in a few cases:

#. When a decision process is needed in a few places, that process can (*should*, really) be put in a part and called from these places. This way, the decision graph is more readable, and updates to the decision process are done in a single place. If you have to do the same changes in a few places, eventually one place will be forgotten and this is how bugs happen.
#. As decision graphs grow, it is sometimes convenient to split them into sub graphs, so they are easier to manage. This also helps in making decision graph more "semantic" -- focus on **what** is decided on rather than **how** it is being decided. Semantic graphs are easier to read, understand, and modify.


Defining a Part
---------------

The following code contains a ``part`` whose id is ``sample-part``::

  [>n1< node]
  [>n2< node]
  [-->sample-part<
    [>pn1< node]
    ...
    [>pn2< node]
  --]
  [>n3< node]

In the above code, nodes ``>n1<``, ``>n2<``, and ``>n3<`` belong to the main decision graph, while nodes ``>pn1<`` and ``>pn2<`` belong to the part ``sample-part``. If this graph would be executed, the traversal order will be ``n1->n2->n3``. Nodes ``>pn1<`` and ``>pn2<`` will not be visited, as ``sample-part`` will not be traversed. It will be kept in memory, though, so other nodes could call it.

.. tip:: It is customary not to mix parts with the main decision graph. Either put all the parts at the end of the file (after the main graph) or at the top of the file, followed by the main graph. Parts could also be placed in :doc:`other files<import-node>`.

``Call``-ing a Part
-------------------

``call`` nodes have a simple body, consisting of the part ID the runtime should "call". When the runtime gets to a ``[call]`` node, it pushes that node on to a stack. It then starts traversing the part whose id the ``[call]`` node specifies. This traversal terminates when the runtime engine hits an ``end`` node, or reaches the end of the part (``--]``). At that time, the aforementioned ``call`` node is popped from the stack, and the runtime engine continues the previous graph traversal from that node's successor.

Consider the following code:

.. code ::

  [>n1< node]
  [>n2< call: sample-part]
  [>n3< node]

  [-->sample-part<
    [>pn1< node]
    [>pne< end]
    [>pn2< node]
  --]


The node traversal order will be: ``n1->n2->pn1->pne->n3``. Note that the interview will not get to ``>pn2<``, since when it will arrive at ``>pne<`` it will return to the originating ``[call]`` node (``>n2<``) and continue from there to ``>n3<``.

.. tip :: In CliRunner, it is possible to inspect the current call stack by typing ``\stack`` at the prompt.

.. note :: The ``call``/``end`` mechanism might look like a low-level way of managing a call stack. That's because it is. Future versions of the language may contain higher level mechanisms. The idea behind having ``call``/``end`` is to gain enough experience with interview management so that we will be able to come up with the correct higher level mechanism (in the same way that ``for`` and ``while`` constructs emerged from ``goto``).


:doc:`Next up<reject>`, we will see what you can do when you have to terminate an interview, for example when realizing a dataset that cannot be accepted to a data repository.
