:doc:`Home <../index>`

.. index :: Section Node

Section Node
============

Sometimes it makes sense to mark a part of the decision tree as handling a specific issue. For example, "this section deals with asserting dataset provenance" or "this section assert the case's status with regards to civil law". For these cases, model developers can use the ``[section]`` node. This node groups nodes logically. It even allows adding a title to the node group.

::

  [>id< section:
    {title:Section title} <-- Optional
    [ask:....]
    [set:....]
    [section: ... ]
  ]


Skipping the Rest of a Section
------------------------------

Often, sections start by asserting whether they are relevant to the case at all. Thus, it is useful to be able to skip the rest of the section, in case we find out that it is not relevant. To this end, PolicyModels supports the ``[continue]`` keyword::

  [section:
    {title: Health Data}
    [ask:
      {text: Are there any related health issues?}
      {answers:
        {no: [continue]}
      }
    ]

    ... handle case health issues ...

  ]

