:doc:`Home <index>`


General Language Notes
=======================

.. index:: Comments

Comments
--------

Both tag space and decision graph files support line and block comments.

A line comment starts with ``<--`` and extends until the end of the line, like so::

  Fruit: one of apple, banana, orange. <-- A comment about the fruits.

A block comment is contained between ``<*`` and ``*>``, like so::

  <* the following node matches
   * a fruit to the user, based on
   * the user's abilities.
   *>
  [ask:
    {text: Can you peel?}
    {answers:
      {yes: [set: Fruit=banana]}
      {no:  [set: Fruit=apple]}
      <* User survey note:
         Some people peel apples too, we may need
         to revisit this culinary choice. *>
    }
  ]



.. index:: Files

Files
-----

Files are assumed to be encoded in Unicode. By convention, files describing policy spaces have a ``.pspace`` suffix, while files describing decision graphs have a ``.dg`` one.

.. index:: Top-Down Support

.. _top-down:

Top-Down Support
----------------

As developing a full questionnaire may benefit from a top-down approach, it is possible to add placeholders for unimplemented parts. This allows questionnaire developers to reference those parts now, and implement them later. Creating placeholders is done using the ``TODO`` keyword (in tag spaces) and the ``[todo]`` node in decision graphs.

In the below example of a tag space definition, ``Policy`` already contains ``Sharing``, although is it not impl`emented yet ::

  Policy: consists of Security, Sharing.
  Sharing: TODO.
  Security: consists of ...

In the below decision graph snippet, the ``EU-Compliance`` section is referenced from other nodes. At runtime, the engine will pass through it (so that it appears in the run logs). Yet, it's just a placeholder, to be replaced later with a full questionnaire::

  [ask:
    {text: Was some of the data collected in the EU?}
    {answers:
      {yes: [call: EU-Compliance]}}]
  <* More code here *>

  [>EU-Compliance< todo: This section will ensure compliance with the EU regulations.]


.. index:: Text Editor Support
.. index:: Atom Support


Text-Editor Support
-------------------

It's always easier to code when the text editor helps. PolicyModels has a `language support package`_
for the `Atom`_ text editor. It offers syntax colorizing and code templates. It could be installed from
Atom itself or by using ``apm``::

  apm install language-datatags

.. _language support package: https://atom.io/packages/language-datatags
.. _Atom: https://atom.io

.. figure:: /img/atom-support.png
   :align: center
   :alt: atom support package screenshot.

   Our language support package for the Atom text editor offers syntax coloring
   and code templates.
