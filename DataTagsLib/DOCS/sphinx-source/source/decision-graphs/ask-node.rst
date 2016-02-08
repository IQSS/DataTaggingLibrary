:doc:`Home <../index>`

Ask Node
========

When the engine arrives at an ``[ask]`` node, it presents a question to the user. Ask nodes define a set of possible answers. Each possible answer has its own decision sub-graph, executed by the engine when that answer is selected.
When the engine completes the execution of the selected sub-graph, it moved on to the node that's syntactically after the ``[ask]`` node.

As legal and technological questions can be daunting, it is possible to clarify terms that appear in the question. This is done by using the optional ``{terms: ...}`` node.

::

  [ask:
    {text: Do the data concern living persons?}
    {answers:
      {yes: [set: livingPersons=yes][call: privacySection ] }
      {no: [call: nonHuman] }}]
  [ask:
    {text: Do the data contain personally identifying information, as defined under HIPAA?}
    {terms:
      {Personally identifying information: This means the name, address, fingerprints...}
      {HIPAA: Health Insurance Portability and Accountability Act}}]

In the above example, the user will be asked the first question. If she chooese *yes*, the ``livingPersons`` slot will contain the value ``yes``, and the interview
will proceed to a node with ID ``privacySection``. If she chooses *no*, the interview will proceed to the node with id ``nonHuman``. In both cases, after the return
from the ``[call]`` node, the user will be asked the second question.

Special Case: Yes/No Questions
------------------------------
A common pattern in questionnaires is to have a yes/no question, where one answer leads to a series of questions before continuing, and the opposite just moves to the next question. For example, the questionnaire may ask the user whether a dataset contains health data before calling the health data section. In effect, this translates to an ``[ask]`` node that has a sub-graph for one answer only.

In order to support this, if a node contains only a single "yes" or "no" answer, the other answer is assumed to be implicit - it is added automatically, and points to the node after said ``[ask]`` node.::

  [>q1< ask:
    {text: Do the data contain health information?}
    {answers:
      {yes: [call: healthSection]}}]
  [>q2< ask:
    {text: Do the data contain criminal records?}
    ...

When the engine arrives at node ``q1``, the user will have a choice of both *yes* and *no*, even though only *yes* is specified. When choosing *no*, the user will be transferred to ``q2``.
