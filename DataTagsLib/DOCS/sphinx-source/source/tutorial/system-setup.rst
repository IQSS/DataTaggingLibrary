:doc:`index`

=============
System Setup
=============

First, let's get to a point where you can run the PolicyModels toolset on your computer. For this, you'll need to install a few pieces of software. All requred software is free and open-source.

Mandatory
~~~~~~~~~

Java JDK (as opposed to JRE).

  The Java JDK will install the ``java`` terminal command. This command is used to run the PolicyModels development application. Java is very common and might already be installed; test that by typing ``java -version`` on a Terminal [*]_ application. Java is open-source and there are multiple implementations of it. One of them can be found `here`_.

DataTagsLib

  This is the software used to create, test, and work with policy models. Download it from the `Releases page`_ at the DataTaggingLibrary GitHub repository. You'll need the file *PolicyModels-[version_number_goes_here].zip*. Once downloaded, extract the .zip file to a new directory. That directory should contain a file named *DataTagsLib.jar*, as well as some other files and directories.

.. tip :: the `Releases page`_ contains many releases. Unless you need a specific one, you probably want the latest release, which is at the top of the page.


Optional
~~~~~~~~
The items below are not crucial to developing a model, but they do make it easier and we heartly recommend using them.

Atom text editor

  `Atom`_ is a text editor geared towards editing computer languages. It is the recommended editor for editing policy models, as it offers, out of its proverbial box, various features such as automatic highlight of matchning braces, code completion, and a rich set of keyboard shortcuts. And also because of the next item in this list.

Atom PolicyModels Langauge Support Package

  This language pacakge improves Atom's support of PolicyModels by adding syntax highlighting, commenting, and ready-made code snippents.

GraphViz

  This software is used to visualize models. You can download it from `graphviz.org`_. It is also available on most package managers, such as ``yum`` and ``apt-get`` on Linux, and `homebrew`_ on Mac OS.

Sample Models

  This is a set of models that highlight various features of PolicyModels. It can be found in the project's `Releases page`_, and is called *sample-models.zip*.

.. _Atom: https://atom.io/
.. _language support package: https://atom.io/packages/language-datatags
.. _graphviz.org: http://www.graphviz.org

.. tip:: Graphviz is needed only for the visualizations. You can go through this tutorial without using it - we have pictures here anyway.

Testing the Setup
~~~~~~~~~~~~~~~~~~

After installing the above applications, open a terminal window, and type::

  java -jar <path-to-new-directory>/DataTagsLib.jar

You should see a screen like the one below:

.. image:: /tutorial/img/test-run.png
   :align: center

.. tip:: The path to the ``DataTagsLib.jar`` file may be realtive. In particular, if the terminal window's current directory contains ``DataTagsLib.jar``, it suffices to type ``java -jar DataTagsLib.jar``.

Type ``\q`` and press ``enter`` to exit the application.


All set up? Let's start with :doc:`hello-world-1`.


.. [*] A terminal is the application used to run applications with command line interface. On Macs it's called "Terminal" and can be found in ``/Applications/Utilities/Terminal.app``. In Windows, it's called "CMD" and can be opened by opening the "start" menu, navigating to "All Programs", then "Accessories", then selecting "Command Prompt" (or by typing ``cmd`` in the run dialog).

.. _here: http://www.oracle.com/technetwork/java/javase/downloads/index.html
.. _Releases page: https://github.com/IQSS/DataTaggingLibrary/releases
.. _homebrew: https://brew.sh/
