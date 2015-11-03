:doc:`index`

=============
System Setup
=============

First, let's get to a point where you can run Tags on your computer.

#. Clone DataTaggingLibrary's `repository`_
#. Using a command line console, navigate the the *DataTagsLib* folder in the repository you've just cloned.
#. Build the DataTagsLib .jar file. If you have `Ant`_ installed on your machine, type ``ant clean jar``. Otherwise, use an IDE. For NetBeans, creating project .jar files is done by right-clicking the project icon on the projects pane and selecting "clean and build".
#. You can now run CliRunner by typing ``java -jar dist/DataTagsLib``. You should see a message asking for a tag space and a decision graph file.

.. _repository: https://github.com/IQSS/DataTaggingLibrary
.. _Ant: http://ant.apache.org

.. image:: /tutorial/img/test-run.png
   :align: center

In order to create the visualizations, you'll also need `Graphviz`_.

.. _Graphviz: http://www.graphviz.org

.. tip:: Graphviz is needed only for the visualizations. You can go through this tutorial without using it - we have pictures here anyway.

All set up? Let's start with :doc:`hello-world-1`.
