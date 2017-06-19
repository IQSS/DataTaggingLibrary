
PolicyModels
===================

(Formerly: DataTaggingLibrary)


.. .. image:: /img/datatags-logo-large.png
..    :align: center

The policyModels project aims to allow modeling policies, such as used for data handling or welfare benefits. More generally, it uses the concept os decision graphs and policy spaces to allow interactive policy-related decision, as well as policy-related tools, such as visualizations and model queries.

.. tip:: Want to take this language for a spin? :doc:`Here's how<take-for-spin>`.

PolicyModels started as part of the `DataTags`_ project, which aims to allow researchers to create proper dataset handling and sharing policies. To avoid confusion, the term *DataTags* now refers to the concept of creating a set of levels that detail the harm inherent to, and proper handling of, a dataset. The term *PolicyModels* refers to the modeling language and toolset covered in this guide. Describing the possible handling policies under a DataTags scheme can be done using a PolicyModels' policy space; modeling a decision process that guides the user to the appropriate tag for a give dataset can be done using a PolicyModels decision graph.

*While we transition to the new naming scheme, the terms "DataTags" and "PolicyModels" are used interchangeably in this document. We're working on this.*

The project is part of a the `Privacy Tools for Sharing Research Data`_ project. There are a few
approaches for developing such system. This site uses tag spaces to describe data handling policies,
and an imperative approach based on *decision graphs* to decide on a policy.

.. _DataTags: http://datatags.org
.. _Privacy Tools for Sharing Research Data: http://http://privacytools.seas.harvard.edu


.. admonition :: Academic References

   The DataTags concept, as well as a previous version of the code described here,
   was presented in the paper: Sharing Sensitive Data with Confidence: The Datatags System.

   Sweeney L, Crosas M, Bar-Sinai M. Sharing Sensitive Data with Confidence: The
   Datatags System. *Technology Science.* 2015101601. October 16, 2015. http://techscience.org/a/2015101601

   The technical background and mathematical concepts behind the PolicyModels language are described in

   Bar-Sinai M, Sweeney L, Crosas M. DataTags, Data Handling Policy Spaces and the Tags Language.
   *Proceedings of the 2016 IEEE Security and Privacy Workshops (SPW)*, May 2016, San-Jose, CA. https://doi.org/10.1109/SPW.2016.11

   When using DataTags/PolicyModels in scholarly work, please consider citing the appropriate paper from above.


A decision graph is a graph (in the mathematical sense) with nodes of various types. When the
user goes through the interview, a *Decision Graph Engine* traverses the graph. When the engine
reaches a node, it performs an instruction associated with it, such
as ``ask`` (ask the user a question) or ``set``.

Policy spaces are also described by a domain specific language.

This site documents those two languages, and some language-related tools.

.. note:: The code for this project is open source, and available at a `GitHub Repository`_. The code and this site were developed by the `Institute for Quantitative Social Science`_ and the `Data Privacy Lab`_ at Harvard University, with the help of students from US REU programs and the `computer science department at the Ben-Gurion University of the Negev, Israel`_.

.. _Institute for Quantitative Social Science: http://iq.harvard.edu
.. _Data Privacy Lab: http://dataprivacylab.org
.. _GitHub Repository: https://github.com/IQSS/DataTaggingLibrary
.. _computer science department at the Ben-Gurion University of the Negev, Israel: http://in.bgu.ac.il/en/natural_science/cs/Pages/default.aspx

What's Here
--------------

.. toctree::
   :glob:
   :titlesonly:
   :maxdepth: 1

   language-general
   take-for-spin
   tutorial/index
   decision-graphs/index
   tag-spaces/index
   policy-model.rst
   folder-layout
   cli-manual
   localizations
   terms


Indices and tables
------------------

* :ref:`genindex`
* :ref:`search`

.. * :ref:`modindex`
