:doc:`Home <index>`

Policy Model Description File
==============================

The policy model description file provides metadata about the policy model. Most of the metadata is optional, and so there is no need to provide external details (such as release date or DOI) before they are available. By convension, the model descriptio file is called ``policy-model.xml``. This convension allows CliRunner to automatically detect the model description file in a policy model folder.

Minimal Description Example
-----------------------------

Below is a minimal :download:`policy model description file<code/policy-model.xml>`.

.. literalinclude:: code/policy-model.xml
  :linenos:
  :language: xml

As seen above, the policy model description file contains a single XML node named ``policy-model``. It has a title (whose purpose is obvious), an a ``model`` sub-nodes. The ``model/space`` node defines which file defines the policy space, and what slot in the policy space is the root of that space (in other words, which slot is the top-level slot). The ``model/graph`` node defines which file contains the decision graph.

.. tip::  It is possible to call the model description file by a name other than ``policy-model.xml``. In such cases, the user has to provide the full path to the policy model description file when using CliRunner.


Nodes
------

policy-model
  contains all other nodes.

title
  The title of the policy model. Free text.

subtitle
  The subtitle of the model.

version
  The current model version. Can have ``doi`` attribute, in case this version has a DOI.

date
  Release date of this model version.

keywords
  A comma-separated list of keywords.

authors
  List of authors. Authors can be either a ``person`` or a ``group``. Both author types have a name, but the differ in the other fields they may have (see example below).

  A model can have any amount of authors.

model
  **mandatory**. Details which file is used as a policy space, and which file as a decision graph. The optional ``answers-order`` attribute decides whather *yes* will allways appear before *no* (``yes-first``), always appear after *no* (``yes-last``), or appear as ordered in the decision graph ``[ask]`` nodes (``verbatim``). The default (i.e. when the attribute is not present) is ``yes-first``.

model/space
  Contains a path the to file containing the policy space definition. The mandatory attribute ``root`` contains the name of the top-level slot. Path is relative to the root model directory.

model/graph
  Contains a path the to file containing the decision graph. Path is relative to the root model directory.

references
  Contains any number of referenes supporting the model. Each reference is describes using a free text contained in a ``reference`` node. ``reference`` nodes can have ``doi`` or ``url`` attributes. providing a direct link to the supporting material.


Full Description Example
--------------------------

Below is a fully detailed :download:`policy model file<code/policy-model-full.xml>`.

.. literalinclude:: code/policy-model-full.xml
  :linenos:
  :language: xml
