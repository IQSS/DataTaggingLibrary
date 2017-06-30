:doc:`Home <../index>`

#Import Node
=============

Makes the nodes of an *imported* decision graph available for the current graph, under a specified prefix.

::

  [#import storage: aux/storage.dg]


In the above example, the nodes in decition graph "storage.dg" located at a sub-directory named "aux", are available to the importing graph using the ``storage`` prefix, like so::

  [#import storage: aux/storage.dg]
  [call: aux>encryption]
  [call: aux>open-formats]


.. warning:: The prefix must be uniqe within an importing file. If a few import statements specify the same prefix, the PolicyModels compiler will get confused.
