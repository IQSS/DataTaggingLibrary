:doc:`Home <../index>`

.. index:: Aggregate Slot

Aggregate Slot
===============

A slot that contains a set of values from its *item type*. Aggregate slots can be used to logically group multiple :doc:`atomic-slot`\s that have only yes/no values. They are defined using the ``some of`` keyword.
Below is a definition of an aggregate slot describing the different possible types of data subjects, that might be harmed if the dataset is misused::

  ProtectedDataSubjects: some of livingPersons, deadPeople,
                                 endangeredSpecies, rareMinerals.

Semantically, this is equivalent of creating four atomic slots::

  DataSubject_LivingPersons: one of yes, no.
  DataSubject_DeadPeople: one of yes, no.
  DataSubject_EndangeredSpecies: one of yes, no.
  DataSubject_RareMinerals: one of yes, no.

So, clearly, aggregate slots allow logical grouping of atomic slots while saving keystrokes.

As with :any:`atomic-slot`, description can be added to the slot and to its possible values::

  ProtectedDataSubjects [The type of entities that could be harmed by misuse of the data]: some of
    livingPersons [Living persons - including privacy issues],
    deadPeople [They don't know they're dead],
    endangeredSpecies [Endangered species need protection from poachers],
    rareMinerals [Disclosing location of rare minerals might lead to illegal mining].
