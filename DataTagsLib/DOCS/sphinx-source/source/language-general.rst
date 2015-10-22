:doc:`Home <index>`


General Langauge Notes
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


Files
-----

Files are assumed to be encoded in Unicode. by convension, files describing tags spaces have a ``.ts`` suffix, while files describing decision graphs have a ``.dg`` one.

.. index:: Top-Down Support

.. _top-down:

Top-Down Support
----------------

As developing a full questionnaire may benefit from a top-down approach, it is possible to add placeholders for unimplemented parts. This allows questionnaire developers to reference those parts now, and implement them later. Creating placeholders is done using the ``TODO`` keyword (in tag spaces) and the ``[todo]`` node in decision graphs.

In the below example, ``Policy`` already contains ``Sharing``, although is it not implemented yet ::

  Policy: consists of Security, Sharing.
  Sharing: TODO.
  Security: consists of ...

.. todo:: show an example in the decision graph.


Language Name
-------------
Used to be "datatags", but as there are now more efforts for creating DataTags, we don't want to hog the name.
So we're working on it... suggestions welcome!
