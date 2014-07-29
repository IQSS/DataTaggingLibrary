# DataTags Language Modularity

## Problem at hand: Namespacing
* For the tags, basically a top-down building
* For the interview, calls to other files

## Inspirations
* Scala relative imports
    - Ability to rename on import: `import java.util.collection.{HashMap => JavaHashMap }`
    - Packages are available as any normal scope
        + Hard to manage, community seems to prefer java-style imports ("chained").
* Play routes file import 
    - Allows users to add a subtree under a root
    

            GET /index                  controllers.Application.index()
            ->  /admin admin.Routes
            GET     /assets/*file       controllers.Assets.at(path="/public", file)

* Haskell
    - Pretty conservative. Declare, import, can rename and hide on import. Can also import qualified functions only. Empty import only imports type declarations.
* GNU SmallTalk's "Image"
    - Global `SystemDictionary`
    - Single dictionary, may end up shadowing classes out in the global scope
    - scope ("environment") is a dag
    - Program text can reference its `#Super` environment
    - Can reference from root (`Smalltalk.Tasks.MyTask`) or relatively (`Super.Super.Peter`)
* Squeak, Objective-C
    - No namespaces. Use prefixes to scope.
    - Objective-C has an "import" directive.

* Python
    - Files are "modules", directories are "packages"
    - File names are constrained - has to be named like a python value (`[a-zA-Z_][a-zA-Z0-9_]*`)
    - Each file is a scope
    - Script's working dir is root of default scope
        + *mmmm... default scope*
    - Allows renaming
