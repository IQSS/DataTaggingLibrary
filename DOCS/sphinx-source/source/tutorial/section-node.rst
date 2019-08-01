:doc:`Home <../index>` / :doc:`index`

==--================
The ``Section`` Node
====================

Decision graph nodes can be grouped into *sections*. This is useful when a set of nodes deals with a certain issue, and we want to group them semantically. For this, PolicyModels offers the ``[section]`` node.

A section node has two components. An optional title, and a the set of decision graph nodes it groups::

  [>section-id< section:
    {title: Sample Section Title}
    [ask:
        {text: ..... }
        {answer:
          .....
        }
    ]
    [set: Slot=value]
  ]

Section nodes can be nested as needed.

Sample Graph with Sections
~~~~~~~~~~~~~~~~~~~~~~~~~~

Suppose we want the user to select three independent sets of animals: dogs, cats, and frogs. The graph below uses sections to logically group the nodes that deal with each animal type together. Note that sections can be inlined with normal flow of the graphs (as is the ``>frogs<`` section), or live outside the normal flow, and be referred to by a :doc:`call </decision-graphs/call-node>` (like ``>dogs<`` and ``>cats<`` below). In the latter case, if the intension is to execute only the called section, it has to be followed by an :doc:`end </decision-graphs/end-node>` node.

.. include :: code/section-node/graph.dg
   :code:
   :number-lines:


.. figure :: code/section-node/graph-dg.png

  Visualization of a decision graph with sections. This visualization was done using
  the ``-style=f11`` flag.


This completes the types of nodes PolicyModels currently has to offer. The last tutorial will look into a :doc:`why the order of values in a slot matters<value-order>`.
