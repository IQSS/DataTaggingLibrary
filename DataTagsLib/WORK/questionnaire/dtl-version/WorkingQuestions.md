# DTL General Musings

## Questions
* How are code tags different? Why the upper case?
* Authentication is defined as a set of {none, email, OA, pass}:
	* Can `none` be implied by an empty set instance?
	* `none` + anything else means ..?
* Same as above re: `Standards/not applicable`.
* Case-sensitivity?
* `DataType/Effort` &rarr; `Identifiability`?
* `Harm/MaxControl` is worded as a handling result while the rest describe the harm (e.g. `shamed`). Maybe use `Unbounded`?
* What's the difference between `DUA/Sharing/Organization` and `DUA/Sharing/Group`?

## Insights
* Whitespace in identifiers may not be desirable. Still, we can have some common-case heuristics that would make things easily readable in the default case:
	* ALLCAPS stays all caps: `FERPA` &rarr; `FERPA`
	* CamelCase becomes words: `CommonRule` &rarr; `Common Rule`
		* Corner case: `SignWithID` &rarr; `Sign With ID` (i.e. caps sequences are kept together)
* Suggestion re: `n/a` value:
	* in simple types: needed for "tags are fully specified" test.
	* in aggregate types, can use an empty set?
* Accessing member values: rather go for file-system-like notation of `top/second/third` than the programming like `top.second.third`, as this is probably more familiar to topic specialists. Also, as no arithmetic expressions will be used (right?), no need to keep the `/` sign for division.
* Entities defined in the language (both tags and nodes) should be accessible by ID from outside, to allow add-on documentation. For that purpose, the paths to possible values (`top/second`...) and the node ids/pathes will be used.
* Participating data types are:
	* enums
	* sets of enums
	* maps from name to on of the above

## Data Definition Language
* Maps of name->value are defined using the `are` keyword (in long form) or with no keywork (in short form)
	* e.g. `Fruits: apple, banana, orange.`
* These values are defined later. They can be globally defined, or defined using type path
	* global: `Apple: one of McIntosh, Macoun, GoldenDelicious.`
	* path: `/Fruits/Banana: one of Cavendish, Plantain, Rajah.`
* Compound values can only have a single field of each type. Can't have a Compound value with 2 fields of type DUA. This alleviates the need to name the fields, as the type is a good enough reference, but at a loss of (a currently unused) flexibility. Is this a problem?