:doc:`Home <../index>`

Defining Tag Spaces
====================

Tag spaces are used to describe a set of entities with multiple orthogonal aspects. A tag space has an arbirtaty amount of descrete dimensions, each defined by a tag. *A point in a tag space is a single entity*. For example, when describing a dataset handling policy, the way the data is encrypted at rest is independant of the type of authenticatoin needed to access the data. Thus, when creating a tag space for describing dataset handling policies, it makes sense to define the tags ``storage`` and ``authenticationType``.

Some described aspects can be correlated: a policy that requires a strong authentication for downloading data is more likely to require encrypted storage than a policy that allows annonymous downloads. Still, it is possible to create a policy that requires encrypted storage and allows annonymous downloads, and so it is useful to be able to describe it (and, prehaps, warn the user against it).

In DataTags Decision Graphs, tag spaces are defined using slots. There are three types of slots: *Atomic*, which directly defines a dimension of a tag space, and *Aggregate*, and *Compound*, which are convenient slots used to manage sets of tag space dimensions. Slots are *named* and *typed*. That is, each slot is assiciated with a name and a type of values it can contain. Values defined for one slot cannot be stored in another. Additionally, slots and values can have a description, intended for user friendly explanation.

.. note:: Currently, the top-level slot has to be a compound slot named ``DataTags``. This is going away in the future - the top level slot will have to be compound, but will have a user-defined name.

Slot Types
----------

.. toctree::
   :maxdepth: 2

   atomic-slot
   aggregate-slot
   compound-slot
   todo-slot
