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

* The repository currently has a large questionnaire (tag definitions and decision graph), developed by the `Privacy Tools for Sharing Research Data`_ project, at the ``DataTagLib/WORK/dtl/0.8`` directory. Note that this questionnaire is a proof-of-concept and should not be used in real-world scenarios. Additional, simpler questionnaires are also available at that directory. Code for the documentation site (the thing you read now) is available at ``DataTagLib/DOCS/sphinx-source/source/tutorial/code/``.
* When coding Tags questionnaires, you may want to use our `language support package`_ for the `Atom`_ text editor.

.. _NetBeans IDE: http://www.netbeans.org
.. _edu.harvard.iq.datatags.mains: https://github.com/IQSS/DataTaggingLibrary/tree/master/DataTagsLib/src/edu/harvard/iq/datatags/mains
.. _Privacy Tools for Sharing Research Data: http://http://privacytools.seas.harvard.edu
.. _Atom: https://atom.io/
.. _language support package: https://atom.io/packages/language-datatags

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

Main class for CliRunner is ``edu.harvard.iq.datatags.mains.DecisionGraphCliRunner``. The class takes two parameters - path to the definitions file, and a path to the decision graph file. It is also the main class for the .jar file, so it is also available for invocation via ``java -jar DataTagsLib.jar``.

.. tip :: For a more structured introduction, see the :doc:`tutorial/index`
