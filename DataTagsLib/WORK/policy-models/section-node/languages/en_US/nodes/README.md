# The `nodes` Localization Directory

This directory holds localization data for nodes. These can be questions (`[ask]`) or section infos. The name
of the file matches the node id.

The usual preference logic for localization alternatives applies here too. So for a node whose id is `ndid`,
the system will look for `ndid.md` file first. If this file does not exist, it will look for `ndid.txt`. If this
file does not exist as well, the text from the decision graph file will be used.
