:doc:`Home <../index>`

.. index:: Atomic Slot

Atomic Slot
=============

A slot that contains a single, atomic value (i.e. a value does not have any sub-values). Values have total ordering, based on their order in the slot definition. Atomic slots are defined using the ``one of`` keyword. Slots and values may have descriptions.

The following atomic slot definition creates a slot named ``Storage``, which can hold at most one of the values ``clear``, ``serverEncrypt``. ``clientEncrypt``, ``doubleEncrypt``. The values are ordered with ``clear`` being the least: ``clear < serverEncrypt < clientEncrypt < doubleEncrypt`` ::

  Storage: one of clear, serverEncrypt, clientEncrypt, doubleEncrypt.

One of the goals of our DataTags implementation is to be user friendly. Thus, the tag space language allows adding descriptions to the slots and values, like so::

  Storage [The way data are stored on the server.]: one of
    clear [Not encrypted at all],
    serverEncrypt [Encryption on the server, "at rest". Attacker cannot use the data by getting the files from the file system],
    clientEncrypt [Encryption on the client side. Data obtained from the server (e.g. buy data breach or subpeona)
                   cannot be used unless the depositor provides the password],
    doubleEncrypt [Encryption on the client, and then on the server. Both passwords are required in order to make use of the data].
