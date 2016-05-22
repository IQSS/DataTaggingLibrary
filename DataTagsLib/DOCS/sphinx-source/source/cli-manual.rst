:doc:`Home <index>`


Tags Console Application
=========================

Tags has a console application - that means it runs in a terminal and uses a text based, command line interface. The simplicity of this interface allows it to provide support for advanced language features early -- developing graphic user interface takes time.

The Tags console enables more than just going through an interview. It is a development environment, and offers powerful tools for the questionnaire developer. After the application is built (`ant clean jar`), the console application is available at ``dist/DataTagsLib.jar``. You can run it by typing ``java -jar dist/DataTagsLib.jar`` from the ``DataTagsLib`` directory in the project.

.. image:: /img/CliRunner.png
   :align: center

There are two ways of loading an interview into the application. The first is to provide the paths to a tagspace file and to a decision graph file as parameters, like so::

  java -jar dist/DataTagsLib.jar path/to/tag-space.ts path/to/decision-graph.dg

If these parameters are omitted, the console app asks for them.

Once the questionnaire is loaded, the application starts the interview. The user can type the answers. But there are special characters as well - typing ``?`` shows a list of available commands and their shortcuts::

  Reading definitions: WORK/dtl/0.8/definitions.ts
   (full:  WORK/dtl/0.8/definitions.ts)
  Reading decision graph: WORK/dtl/0.8/questionnaire.dg
   (full:  WORK/dtl/0.8/questionnaire.dg)
  # Run Started
  Do the data concern living persons?
  Possible Answers:
   - yes
   - no
  answer (? for help): ?
  Please type one of the following commands:
  \about: (\i)
  	What's this application all about.
  \ask: (\a)
  	Prints the current question again.
  \current-tags:
  	Print the current tags to the console.
  \debug-messages:
  	Toggles printing of debug messages.

  ... continued ...

Commands are executed by typing their name, preceded by ``\`` (e.g. ``\about`` for the "about" command). Common commands have shortcuts. These are shown, when available, in parentheses near the command name. Some commands expect parameters; these are passed after the command name, like so: ``\show-slot Name/Of/Slot``.

You are encouraged to read through the list. We will highlight some of the more powerful commands below.

Useful Commands for Users
--------------------------

about
  Shows information about the current questionnaire and the application in general.

current-tags
  Shows the current value of the tags.

show-slot
  Displays information about a slot and its possible values. Usage: ``\show-slot Name/Of/Slot``.

restart
  Start the questionnaire again. Also available via ``\r``.

ask
  After some information is printed to the console, you may forget what the question was. Don't scroll up - use ``\ask`` and the console app will happily ask you again. Don't like typing so much? Use ``\a``.

Useful Commands for Developers
-------------------------------

reload
  Reload the questionnaire, e.g. after some changes were made. Also available via ``\rr``. As the common practice for developing a questionnaire is to keep a Tags console running the questionnaire while editing it, this command is very useful.

find-runs
  Allows querying the questionnaire with questions like "which sets of answers would end up in a policy that allows clear storage?". Takes a tag value as a parameter (similar to those used in :doc:`set nodes</decision-graphs/set-node>`). Finds all the runs (i.e. sequences of nodes and answers) that will end up with a tag value that is a superset of the supplied tag. A tag value `a` is a superset of a tag value `b` if they both agree on the values in `b`'s non-empty slots. In other words, a slot in `a` must have the same value as the corresponding slot in `b`, unless that slot in `b` is empty, in which case the slot in `a` can have any value (or be empty as well).

  Example: ``\find-runs Storage=clear; Transit=encrypted`` will return all the run traces that will end up in a policy that requires clear storage and encrypted transmissions. Note that even for medium-sized interviews, this query can take a long time.

validate
  Validates the interview: Finds unused tag values, invalid call nodes, and unreachable nodes.

trace
  The run so far

visualize-ts, visualize-dg
  Create a visualization of the tag space (-ts) and the decision graph (-dg). Requires `Graphviz`_.

.. _Graphviz: http://www.graphviz.org

toJson
  Create a ``json`` file describing the questionnaire. The generated file can be used to generate an interactive visualization of the questionnaire, using DataTags' `interactive visualizer`_.

.. _interactive visualizer: http://iqss.github.io/DataTagsInteractiveVisualizer/
