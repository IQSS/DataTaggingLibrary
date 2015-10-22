
DataTaggingLibrary
===================

The `DataTags`_ project aims to allow researchers to create proper dataset handling and sharing policies,
even if they lack the required legal and technological expertise. This is done by means of a firendly interview.
The project is part of a the `Privacy Tools for Sharing Research Data`_ project. There are a few
approaches for developing such system. Tthis site uses tag spaces to describe data handling policies,
and an imperative approach based on *decision graphs* to decide on a policy.

.. _DataTags: http://datatags.org
.. _Privacy Tools for Sharing Research Data: http://http://privacytools.seas.harvard.edu

A decision graph is a graph (in the mathematical sense) with different type of nodes. When the
user goes through the interview, a *Decision Graph Engine* traverses the graph. When the engine
reaches a node, it performs an instruction associated with it. For example, such
instruction can be ``ask`` (ask the user a question) or ``set``.

Tag spaces are also described by a domain specific language.

This site documents those two langauges, and some language-related tools.

Contents:

.. toctree::
   :glob:
   :titlesonly:
   :maxdepth: 5

   language-general
   decision-graphs/index
   tag-spaces/index
   terms


Indices and tables
==================

* :ref:`genindex`
* :ref:`search`

.. * :ref:`modindex`
