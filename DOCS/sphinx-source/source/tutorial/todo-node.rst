:doc:`Home <../index>` / :doc:`index`

===========================
Placeholders
===========================

Sometimes we know the structure of a questionnaire before we know the details. For example, we may know we want to interview the user about the subjects' consent, special data use agreements that pertain to the dataset, and required audits of the dataset store. But, we may not know the exact questions yet.

We can still write the questionnaire, using a top-down approach. For this, we have the ``todo`` node (goes well with the :doc:`/tag-spaces/todo-slot`). These nodes, unlike comments, are part of the execution of the questionnaire. They appear in visualizations and traces, and can be referred to from ``call`` nodes. But they do not change the global tags or the flow of the interview.

In the following questionnaire (:download:`.ts<code/todos/todos.ts>`, :download:`dg<code/todos/todos.dg>`), no tags are set, but the structure is already decided on.


.. include :: code/todos/todos.dg
   :code:

The to-do questionnaire. Note that ``todo`` nodes can be used as ``call`` destinations, just like any other node.


.. figure :: img/todos.png

  Visualization of the todo decision graph. It is clear what needs to be done, but not how.

.. figure :: img/todo-ts.png

  Visualization of the todo tag space. The structure is known, but the details aren't.


As models grow, there's a need to arrange the nodes in some physical and logical grouping. Our :doc:`next section<section-node>` shows how.
