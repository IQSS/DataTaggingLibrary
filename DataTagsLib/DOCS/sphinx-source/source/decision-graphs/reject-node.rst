:doc:`Home <../index>`

.. index:: Reject Node

Reject Node
===========

Terminates the interview, and marks the dataset as *unacceptable*. This is an extreme mean, that should be kept for extreme cases, such as when the data was obtained illegally, or when maintaining them would breach some law.

::

  [ask {text: Where the data obtained by breaking and entry?}
    {answers:
      {yes: [Reject: Cannot deposit data obtained by breaching the law.]}}]
  ...
