:doc:`index`

====================
The ``Reject`` Node
====================

A DataTags tagging system matches a data handling policy to a dataset. Theoretically, every dataset should have a matching policy. The reality is a bit more complex: there is a special case. Datasets that were obtained by violating laws or regulations cannot be automatically accepted to repositories (if at all). For these cases, Tags has a ``reject`` node. When the runtime gets to a ``reject`` node, the interview stops, and the rejection reason is shown to the user.


---------------------
It's Not Us, Its You
---------------------

A ``reject`` node has a textual body, explaining the rejection reason. They look like this:

.. code ::

  [reject: Data cannot be accepted.
           It is illegal to gather data by hunting down baby unicorns.]


.. figure:: img/baby-unicorn.png

  Baby unicorn (illustration by the author)


In the following questionnaire (files: :download:`tag space<code/reject/education.ts>`, :download:`decision graph<code/reject/education.dg>`), the interview starts with a short validation of the legality of the dataset. If the dataset is, indeed, legal, the interview continues. Otherwise, it ends on the ``reject`` node in line 12.

.. include :: code/reject/education.dg
   :code:
   :number-lines:


.. warning :: This questionnaire is, of course, is intended to show the usage of the ``reject`` node. NOT TO BE USED AS LEGAL ADVICE.


.. figure :: img/education.png

  The education interview.


.. figure :: img/rejection.png

  Rejection message from the DataTaggingServer, due to a likely PPRA violation.


In the :doc:`next tutorial<todo-node>`, we'll see how to create placeholder nodes.
