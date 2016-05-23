:doc:`Home <../index>`

Consider and When Nodes
=======================

``[consider]`` and ``[when]`` nodes are similar to :doc:`ask node`, but instead of asking the user what to do, they look at the current :any:`Tags Value`. Use these nodes to avoid asking questions whose answers can be inferred from information the questionnaire already has. While both nodes look at the :any:`Tags Value`, ``when`` nodes are able to look at any combination of values, while ``consider`` nodes provide a more syntactically pleasent way of examining a single slot.

Both nodes provide an ``else`` sub-node, which is selected when none of the other options matches. These nodes are similar to the ``if`` and ``switch`` keywords in other languages.

Consider node
-------------

::
  <* SubjectType may have been collected above *>
  [>cn< consider:
    {slot: SubjectType}
    {options:
      {mineral: [set: Harm=minimal]}
      {animal:  [set: Harm=moderate]}
      {human:   [call: assesHumanHarm]}
    }
    {else: <-- this part is optional
      [call: open-data]
    }
  ]

In the above example, we use a ``consider`` node to infer the appropriate value of the ``Harm`` slot from the value at the ``SubjectType`` slot. If, for example, ``>cn<`` is arrived at when ``SubjectType`` contains ``animal``, the questionnaire declares the harm the dataset in question may cause is ``moderate``. If, on the other hand, ``>cn<`` is arrived at when the subject type is ``human``, the questionnaire goes into a special section for assessing the harm.



When node
---------

When nodes are a more general form of ``[consider]``. They can describe any combination of values in the slots.

::
  [when:
    {Subjects+=livingPresons: [call: privacy]}
    {Subjects+=deceasedPresons; Domains += medical: [call: privacy]}
    {else:
      [call: open-data]
    }
  ]
