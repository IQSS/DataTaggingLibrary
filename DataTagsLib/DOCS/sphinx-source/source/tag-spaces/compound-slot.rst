:doc:`Home <../index>`

.. index:: Compound Slot

Compound Slot
=============

A compound slot, defined using the ``consists of`` keyword, consists of other slots, referred to as *sub-slots*. These slots can be of any type. Compound slots do not define tag space dimensions directly. Rather, they are are used to logically group other slots. Below is a compound slot defining the handling part of a dataset policy::

  Handling: consists of Storage, Transit, Authentication.

It is assumed that ``Storage``, ``Transit``, and ``Authentication`` are defined somewhere else in the ``.ts`` file. If you want to include a field that is not implemented yet, you can use the ``TODO`` definition, as explained in :ref:`top-down`.

A compound slot can have a description as well::

  Handling [Practical aspects of the developed dataset policy]:
      consists of Storage, Transit, Authentication.


.. caution:: Note that unlike the other slots, the value list cannot contain descriptions.
             In the above example, adding ``Storage [How the data are stored]`` will generate
             an error during parsing. To add a description to ``Storage``, add it to its
             definition (``Storage: one of ...```)
