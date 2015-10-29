:doc:`Home <../index>`

To-Do Node
==========

A placeholder node, to be later replaced with real a decision graph. Can be referenced from :doc:`Call nodes<call-node>`, so that when the node is replaced with the decision graph that implements it, the referring code does not need to change.

::

  [todo: implement tagging for financial data]

``[todo]`` nodes are useful for top-down implementation approaches, where the questionnaire structure is decided early on, but its parts are implemented gradually, like so::

  [ask
    {text: Do the data contain health information?}
    {answers:
      {yes: [call healthInfo]}}]
  [ask
    {text: Do the data contain financial information?}
    {answers:
      {yes: [call financialInfo]}}]
  [todo: tag for data use agreements]
  [end]

  [>healthInfo< todo: implement!]
  [end]

  [>financialInfo< todo: implement!]
  [end]

The questionnaire in the above example can be executed even though three parts of it have not been implemented yet.
