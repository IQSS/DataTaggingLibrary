:doc:`Home <index>`

Take Tags for a Spin
======================

In order to work with Tags, take these steps:

* Clone the project from https://github.com/IQSS/DataTaggingLibrary.
* When using `NetBeans IDE`_, open the cloned directory - it contains a NetBeans project.

  *  For other IDEs, create a project and import the code. All needed .jar files are in the ``DataTagsLib/lib`` directory.

* In the code, package `edu.harvard.iq.datatags.mains`_ contains classes that can be run from the commandline or as an IDE target. These classes are:

  * ``DecisionGraphCliRunner.java``: run an interactive commandline interview.
  * ``DecisionGraphCompilint.java``: Compiles and visualizes a tag space and a decision graph.
  * ``DecisionGraphValidations.java`` and ``QuestionnaireValidations.java``: run a few validiations on the graph.

* The repository currently has a large questionnaire (tag definitions and decision graph), developed by the `Privacy Tools for Sharing Research Data`_ project, at the ``DataTagLib/WORK/dtl/0.8`` directory. Some simpler questionnaires are coming soon.

.. _NetBeans IDE: http://www.netbeans.org
.. _edu.harvard.iq.datatags.mains: https://github.com/IQSS/DataTaggingLibrary/tree/master/DataTagsLib/src/edu/harvard/iq/datatags/mains
.. _Privacy Tools for Sharing Research Data: http://http://privacytools.seas.harvard.edu
