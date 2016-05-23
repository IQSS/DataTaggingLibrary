:doc:`Home <index>`

Take Tags for a Spin
======================

To start developing questionnaires using Tags and it's tools, you'll need Tags' console application, **CliRunner**. It is available from
the `tags binaries`_ from the DataTaggingLibrary GitHub repository. There are two .zip files
of interest in the release:

.. _tags binaries: https://github.com/IQSS/DataTaggingLibrary/releases

* **DataTags-[version_goes_here].zip** CliRunner and Java libraries. You need this one.
* **sample-questionnaires.zip** Some sample questionnaires. Optional.

.. note::
   CliRunner requires Java. You can freely download and install it from http://java.com.

.. tip:: When coding Tags questionnaires, you may want to use the `language support package`_ for the `Atom`_ text editor.

.. _Atom: https://atom.io/
.. _language support package: https://atom.io/packages/language-datatags

Download and extract the **DataTags-1.0-beta.zip** file (the version number may be different, this is an example). Using a console, naviagte to the newly extracted  directory, and type::

  java -jar DataTagsLib.jar

You should see the DataTags logo, followd by a prompt for a tags space file.

The CliRunner
-----------------------

.. figure:: /img/CliRunner.png
   :align: center

   The CliRunner application is a swiss-army knife tool for questionnaire developers

The DataTaggingLibrary comes with a command line tool for developing questionnaires. It allows for:

* Performing an interview.
* Inspecting nodes and slots.
* *Visualizations* (requires `graphviz`_)
* Run inspections - trace, stack status, tag status

  .. _graphviz: http://www.graphviz.org

While developing the questionnaire, it is useful to keep a CliRunner open. The user can reload the
questionnaire by typing ``\reload`` on the console when needed.

To start CliRunner with a specific questionnaire, pass the tagspace and decision graph as parameters, like so::

  java -jar DataTagsLib.jar path/to/tag-space.ts path/to/decision-graph.dg

.. tip :: For a more structured introduction, see the :doc:`tutorial/index`
