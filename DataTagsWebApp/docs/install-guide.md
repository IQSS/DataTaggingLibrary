# Install Guide
DataTags project has two main components:
    * *DataTagsLib* - Language kit, complete with parsers, compilers, and a runtime engine. This is a java project, created in NetBeans.
    * *DataTagsWebApp* - a Play Framework web application, that uses the DataTagsLib, among other things.
To work on DataTagsLib, all you need to do is clone the repo and open it with NetBeans.

To work on the web application, you'll need:

* typesafe activator
* scala
* sbt

The best way to get all of them, is to install them via a package manager. On a Mac, that boils down to going to [homebrew](http://brew.sh), installing it, and, opening a terminal and typing:
    
    $ brew install scala
    $ brew install typesafe-activator

Play, and this DataTagsWebApp, also use [Less](http://lesscss.org), to make the creation of css less painful. While Play has a built-in support for this, you install a version that would allow you to experiment with it outside of Play. To do that, install *npm* (the node.js package management system)

    $ brew install npm

And then install Less

    $ npm install -g less



## Trouble shooting
* In case of a currpt installation, e.g. when any of the scala commands fail over a missing dependency (such as `scala.tools.nsc.Global`) try deleting the caches of maven, ivy and sbt:
    - `~/.m2`
    - `~/.ivy2`
    - `~/.sbt`

On the terminal, this would mean:

    rm -rf ~/.m2
    rm -rf ~/.ivy2
    rm -rf ~/.sbt
