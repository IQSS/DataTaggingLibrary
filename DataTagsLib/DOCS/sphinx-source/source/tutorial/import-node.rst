:doc:`index`

================
When Graphs Grow
================

Sometimes it makes sense to split a decision graph file into a few files. Some examples might be:

Reuse parts of the graph

  When a part of the decision graph handles a issue common to a few graphs, that part can be stored in its own file and imported to those graphs. This is an improvement over copying and pasting the code, which also duplicates errors and makes maintenantce harder.

Easier collaboration

  When a decision graph is split such that colleboratoers do not edit the same file at the same time, collaboration becomes easier (no merge conflicts!).

Modularity

  If a part of a decision graph is likely to change often, creating a dedicated file for it would make those changes easier to do. It is also easier to mix and match graph parts by altering imports than it is to mix and match them by copying and pasting code.

Easier editing

  It's easier to edit and reason about smaller files.

Divide and Import
-----------------

In order to import one decision graph into another, we use the ``[#import]`` node. These nodes have to appear before any other nodes in the graph. An import node references a file, and gives it a local name that will be used to refer to the nodes in it. Note the ``#`` before the word "import", stressing that this is not an instruction node.

Here's a simple graph using imports::

  [#import health: sub-graphs/health2-final1.dg]
  [#import enc: sub-graphs/encryption-EU.dg]

  [call: health>hippa]
  [call: enc>at-rest]

The first ``#import`` node references the "health2-final.dg" file at a directory called "sub-graphs". The path is resolved relative to the policy model's directory, so in this example "sub-graphs" must be a sub directory of that directory. The importing decision graph can now call the nodes in "health2-final.dg" using the specified prefix ``health``. This is done by the first ``call`` node.
