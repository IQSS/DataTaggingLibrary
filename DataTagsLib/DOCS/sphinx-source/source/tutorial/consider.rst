:doc:`Home <../index>` / :doc:`index`

===================================================
``consider`` and ``when`` and Conditional Branching
===================================================

:doc:`[ask] nodes<../decision-graphs/ask-node>` specify a question to the user, and then further specify a list of possible answers, complete with a sub-graph to run for each answer. PolicyModels offers another way of branching between sub-graphs: based on the current values in the slots. This is done using the ``[consider]`` and ``[when]`` nodes. It might be useful to think about them as ``[ask]`` nodes that ask the runtime engine instead of the user. Or, for people familiar with programming, as the PolicyModels equivalent of an ``if`` statement.

``Consider``\ing a Single Slot
------------------------------

Consider a questionnaire pretaining to medical datasets. The granularity of the dataset in question matters a lot when one needs to decide how protected it should be. After spending some time with the user, asserting the granularity of the data, the :download:`below questionnaire <code/consider-when/consider-when.dg>` uses a :doc:`consider<../decision-graphs/consider-when-node>` node to decide whether the data should be encrypted at rest and/or while in transit.


.. literalinclude :: code/consider-when/consider-when.dg
    :linenos:
    :lines: 45-55

A ``[consider]`` node has to specify the slot it is considering - this is done by the ``{slot:}`` sub-node (line 2 in the code above). It then lists the sub-graph to traverse for each option, under the ``{options:}`` sub-slot (lines 3-7). An optional ``{else:}`` sub-node (line 8) specifies what to do if none of values listed in ``{options:}`` is current when the considered slot is traversed.


``when`` Considering More Than a Single Slot
--------------------------------------------

The ``[consider]`` node considers only a single slot. To consider the values of a few slots, we use the ``[when]`` node. The sample code below invokes the privacy section of the questionnaire, if the data subjects are living persons, or if they are deceased persons and the data contains medical information.

.. literalinclude :: code/consider-when/consider-when.dg
    :linenos:
    :lines: 18-24


The ``[when]`` node has a sub-node for each value combination that requires special action (lines 2 and 3 above). Again, an optional ``{else:}`` sub-nodes allows the model developer to specify what needs to be done in case none of the explicitly specified options match the current slot values.


.. note:: :doc:`later<inference>` in this tutorial, we'll examine another way of achieving similar functionality, but in a very different way.

In the :doc:`next tutorial<todo-node>`, we'll see how to create placeholder nodes.
