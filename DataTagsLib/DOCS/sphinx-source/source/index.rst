
DataTaggingLibrary
===================

.. image:: /img/datatags-logo-large.png
   :align: center

The `DataTags`_ project aims to allow researchers to create proper dataset handling and sharing policies,
even if they lack the required legal and technological expertise.
The project is part of a the `Privacy Tools for Sharing Research Data`_ project. There are a few
approaches for developing such system. This site uses tag spaces to describe data handling policies,
and an imperative approach based on *decision graphs* to decide on a policy.

.. _DataTags: http://datatags.org
.. _Privacy Tools for Sharing Research Data: http://http://privacytools.seas.harvard.edu


.. admonition :: Academic Reference

   The DataTags concept, as well as a previous version of the code described here,
   was presented in the paper: Sharing Sensitive Data with Confidence: The Datatags System.

   Sweeney L, Crosas M, Bar-Sinai M. Sharing Sensitive Data with Confidence: The
   Datatags System. *Technology Science.* 2015101601. October 16, 2015. http://techscience.org/a/2015101601


A decision graph is a graph (in the mathematical sense) with different type of nodes. When the
user goes through the interview, a *Decision Graph Engine* traverses the graph. When the engine
reaches a node, it performs an instruction associated with it, such
as ``ask`` (ask the user a question) or ``set``.

.. tip:: Want to take this language for a spin? :doc:`Here's how<take-for-spin>`.

Tag spaces are also described by a domain specific language.

This site documents those two languages, and some language-related tools.

.. note:: The code for this project is open source, and available at a `GitHub Repository`_. The code and this site were developed by Michael Bar-Sinai, through an affiliation with the `Institute for Quantitative Social Science`_ and the `Data Privacy Lab`_ at Harvard University, with the help of students from US REU programs and the `computer science department at the Ben-Gurion University of the Negev, Israel`_.

.. _Institute for Quantitative Social Science: http://iq.harvard.edu
.. _Data Privacy Lab: http://dataprivacylab.org
.. _GitHub Repository: https://github.com/IQSS/DataTaggingLibrary
.. _computer science department at the Ben-Gurion University of the Negev, Israel: http://in.bgu.ac.il/en/natural_science/cs/Pages/default.aspx

Contents:

.. toctree::
   :glob:
   :titlesonly:
   :maxdepth: 5

   language-general
   take-for-spin
   tutorial/index
   decision-graphs/index
   tag-spaces/index
   cli-manual
   terms


Indices and tables
==================

* :ref:`genindex`
* :ref:`search`

.. * :ref:`modindex`
