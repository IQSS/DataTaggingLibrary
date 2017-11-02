:doc:`Home <index>`

.. index :: Folder Layout
.. index :: Folder Structure
.. index :: Directory Structure

Folder Layout
====================

A policy model consists of a directory containing at least three files:

* A :doc:`policy model description<policy-model>` file, normally named ``policy-model.xml``.
* A policy space definition file
* The main decision graph file

A policy model directory may include other files, e.g. for supporting materials or documentation. Localizations, if any, live in a sub-directory named ``languages``.

.. code::

  sample-model/
  ├── policy-model.xml
  ├── definitions.ts
  ├── graph.dg
  └── languages
      ├── en_US
      ├── en_GB
      ├── fr_FR
      ├── Ancient-Latin
      └── he_IL

*Policy model directory layout. The ``languages`` folder is optional, and contains localization data.*
