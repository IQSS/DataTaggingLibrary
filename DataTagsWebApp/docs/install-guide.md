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

To test your installation, open a terminal application. Navigate to the `datatags-app` folder, and type

    $ activator start

After a while (might be a long while, depends on how many items sbt has to download from the internet), you should be able to view your local version at http://localhost:9000

## Troubleshooting
* In case of a currpt installation, e.g. when any of the scala commands fail over a missing dependency (such as `scala.tools.nsc.Global`) try deleting the caches of maven, ivy and sbt:
    - `~/.m2`
    - `~/.ivy2`
    - `~/.sbt`

On the terminal, this would mean:

    rm -rf ~/.m2
    rm -rf ~/.ivy2
    rm -rf ~/.sbt


## Deploy to test server

Note: We try to hold multiple app versions on the server, to allow quick rollback when needed. This is done by having multiple application and questionnaire folders, and a symbolic link to a "current" one. Application folders have names like `app-MMDD`, and questionnaires have names like `q-MMDD`.

The local scripts are run from the `datatags-app` directory. *The scripts depend on having a valid `vars.sh` file in the `scripts` directory!*. To create one, copy the `vars-sample.sh` file in the `scripts` directory, update it according to your data, and rename it to `vars.sh`.

Currently, only UNIX is supported. These scripts are tested on Mac OS X, using Bash.

### Deploy app 
#### On local machine
Run the `upload-app.sh` script. It will create a file called `dist-mmdd.zip` where `mmdd` are the month and day, and will `scp` it to the server.

#### On server
1. Assume `dist` is the uploaded product of the previous stage.
2. `cd` to `tagging-server`
2. Run `./deploy-app.sh dist` to create the application folder, properly names and all.
3. The script will output the new app folder name, say `app-dist`
3. Run `./link-app.sh app-dist` to delete the current application symlink, create a new one pointing to `app-dist`, and restart the tagging server.
4. Validate the application is running by trying to use it.


### Deploy Questionnaire
We basically do the same thing - hold multiple questionnaires in directories named `q-MMDD` and have the application use a symbolic link called `q-current`.

#### On local machine
Questionnaire is stored in `datatags-app/public/questionnaire`

1. `cd` into the `datatags-app` directory.
2. run the `upload-questionnaire.sh` script from the `scripts` folder. (`../scripts/upload-questionnaire.sh`).

#### On Server
2. `cd` to `tagging-server`
3. Run the `link-q.sh` script with the questionnaire you want to link. Assuming it's called `q-1123`, that would be `./link-q.sh q-1123`. The script will update the symlinks, and restart the application.
4. Validate the application is running by trying to use it.