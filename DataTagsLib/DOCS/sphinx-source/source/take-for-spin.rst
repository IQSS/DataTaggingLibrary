:doc:`Home <index>`

Take Tags for a Spin
======================

In order to work with Tags, take these steps:

* Clone the project from https://github.com/IQSS/DataTaggingLibrary.
* When using `NetBeans IDE`_, open the cloned directory - it contains a NetBeans project.

  *  For other IDEs, create a project and import the code. All needed .jar files are in the ``DataTagsLib/lib`` directory.

* In the code, package `edu.harvard.iq.datatags.mains`_ contains classes that can be run from the command line or as an IDE target. These classes are:

  * ``DecisionGraphCliRunner.java``: run an interactive command line interview.
  * ``DecisionGraphCompilint.java``: Compiles and visualizes a tag space and a decision graph.
  * ``DecisionGraphValidations.java`` and ``QuestionnaireValidations.java``: run a few validations on the graph.

* The repository currently has a large questionnaire (tag definitions and decision graph), developed by the `Privacy Tools for Sharing Research Data`_ project, at the ``DataTagLib/WORK/dtl/0.8`` directory. Some simpler questionnaires are coming soon.

.. _NetBeans IDE: http://www.netbeans.org
.. _edu.harvard.iq.datatags.mains: https://github.com/IQSS/DataTaggingLibrary/tree/master/DataTagsLib/src/edu/harvard/iq/datatags/mains
.. _Privacy Tools for Sharing Research Data: http://http://privacytools.seas.harvard.edu


The CliRunner
-----------------------

.. image:: /img/CliRunner.png
   :align: center

The DataTaggingLibrary comes with a command line tool for developing questionnaires. It allows for:

* Performing an interview.
* Inspecting nodes and slots.
* *Visualizations* (requires `graphviz`_)
* Run inspections - trace, stack status, tag status

.. _graphviz: http://www.graphviz.org

While developing the questionnaire, it is useful to keep a CliRunner open. The user can reload the
questionnaire by typing ``\reload`` on the console when needed.

Main class for CliRunner is ``edu.harvard.iq.datatags.mains.DecisionGraphCliRunner.java``. The class takes two parameters - path to the definitions file, and a path to the decision graph file.
