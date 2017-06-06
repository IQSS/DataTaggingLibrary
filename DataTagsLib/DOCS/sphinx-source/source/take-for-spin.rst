:doc:`Home <index>`

Take Tags for a Spin
======================

To start developing questionnaires using Tags and it's tools, you'll need Tags' console application, **CliRunner**. It is available from `tags binaries`_ at the DataTaggingLibrary GitHub repository. There are two .zip files
of interest in the release:

.. _tags binaries: https://github.com/IQSS/DataTaggingLibrary/releases

**PolicyModels-[version_goes_here].zip**
  Contains CliRunner and required Java libraries. You need this one.
**sample-models.zip**
  Contains some sample models so it's easier to get started. This file is optional.

.. note::
   CliRunner requires Java. You can freely download and install it from http://java.com.

.. tip:: When coding policy models, you may want to use the `language support package`_ for the `Atom`_ text editor.

.. _Atom: https://atom.io/
.. _language support package: https://atom.io/packages/language-datatags

Download and extract the **PolicyModels-[version].zip** file. Using a console, naviagte to the newly extracted  directory, and type::

  java -jar DataTagsLib.jar

You should see the DataTags logo, followd by a prompt. To load a policy model, such as those available from the extracted *sample-models.zip* file, type ``\load path/to/model``. To create a new model, type ``\new``. CliRunner will ask you for some data, and then create the model for you.

CliRunner
-----------------------

.. figure:: /img/CliRunner.png
   :align: center

   The CliRunner application is a swiss-army knife tool for policy model developers

The DataTaggingLibrary comes with a command line tool for developing questionnaires. It allows for:

* Performing an interview.
* Inspecting nodes and slots.
* *Visualizations* (requires `graphviz`_)
* Run inspections - trace, stack status, tag status

  .. _graphviz: http://www.graphviz.org

While developing the questionnaire, it is useful to keep a CliRunner open. The user can reload the
questionnaire by typing ``\reload`` on the console when needed.

To start CliRunner with a specific model, pass the path to its folder as a parameter, like so::

  java -jar DataTagsLib.jar path/to/model/

.. tip :: For a more structured introduction, see the :doc:`tutorial/index`
