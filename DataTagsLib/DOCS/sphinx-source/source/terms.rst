:doc:`Home <index>`

Terms and Their Meaning
=======================

.. glossary::


  DataTags
    A set of dataset handling policies made to match the harm a given datset may cause.
    See *Sweeney L, Crosas M, Bar-Sinai M. Sharing Sensitive Data with Confidence: The Datatags System. Technology Science. 2015101601. October 16, 2015.* http://techscience.org/a/2015101601

  Decision Graph
    A graph describing a decision process. Composed of various types of nodes.
    Defined over a tag space.

  Interview
    The act of a user answering a questionnaire. The PolicyModels equivalent of a program running.

  PolicyModel
    A pair of matching decision graph, a policy space, metadata, and possibly value inferrers.

  Policy Space
    A multi-dimensional space defined by slots, which are discrete, ordinal dimensions.

  Current Value
    The tags being set by the interview engine. An instance of the top-level compound type. Like all such instances, defines a point in the tag space of the questionnaire.

  CliRunner
    The model developer's main tool. This is a console application, that can be used to work with policy models. It contains many commands, e.g to create, analyze, query, visualize, inspect, and execute a policy model.

  PolicyModelsServer
    A web application that can host models online. PolicyModelsServer is an open-source project managed separatly from the main PolicyModels library and commandline environment. Its sources are available on `here <https://github.com/IQSS/DataTaggingServer>`_.
