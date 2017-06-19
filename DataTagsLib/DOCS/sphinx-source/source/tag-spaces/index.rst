:doc:`Home <../index>`

Defining Policy Spaces
=======================

Policy spaces are used to describe a set of entities with multiple orthogonal aspects. A policy space has an arbitrary amount of discrete dimensions. *A point in a policy space is a single policy*. For example, when describing a dataset handling policy, the way the data is encrypted at rest is independent of the type of authentication needed to access the data. Thus, when creating a policy space for describing dataset handling policies, it makes sense to define the dimensions ``storage`` and ``authenticationType``. Dimensions are defined using atomic slots (see below), where the possible coordiantes are given by the possible slot values.

Some described aspects can be correlated: a policy that requires a strong authentication for downloading data is more likely to require encrypted storage than a policy that allows anonymous downloads. Still, it is possible to create a policy that requires encrypted storage and allows anonymous downloads, and so it is useful to be able to describe it (and, perhaps, warn the user against it).

In PolicyModels, policy spaces are defined using slots. There are three types of slots: *Atomic*, which directly defines a dimension of a tag space, and *Aggregate* and *Compound*, which are convenience slots used to manage sets of policy space dimensions. Slots are *named* and *typed*. That is, each slot is associated with a name and a type of values it can contain. Values defined for one slot cannot be stored in another. Additionally, slots and values can have a description, intended for user friendly explanation.

.. note:: Currently, the top-level slot name is defined by the :doc:`../policy-model`

Slot Types
----------

.. toctree::
   :maxdepth: 2

   atomic-slot
   aggregate-slot
   compound-slot
   todo-slot
