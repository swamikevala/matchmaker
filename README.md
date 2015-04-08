#matchmaker
##Real time record matching engine

08/04/2015: Version 0.1
matchmaker 0.1 / vert.x : 2.1.5

Removed rules from config completely, instead provide a more generic "ScoringMethod" interface. Can implement rule-based or probabilistic models by writing custom implementations. Now use Gson for all Json parameter objects instead of Vert.x JsonObject (so less dependency on vertx framework). Refactored all config loading into new ConfigLoader class

04/04/2015: Version 0.1
matchmaker 0.1 / vert.x : 2.1.5

Renamed project to "matchmaker", added comprehensive data source config options (esp. for sql api), added blocking options for rule attributes (can set to true or false), fixed some dodgy logic also

01/04/2015: Version 0.1
phosphorus : 0.1 / vert.x : 2.1.5

Added connectionAPI interface, result caching, index auto-detect & build. Using MySQL instead of FDB.

19/03/2015: Version 0.1
phosphorus : 0.1 / vert.x : 2.1.5 / FoundationDB : 3.0.7 / FDB Java API : 3.0.2

Major restructuring, esp. Blocking interfaces (KeyGenerator, BlockingMethod, IndexAPI), to make architecture more flexible for different blocking approaches. Started making MySQL api classes (easier than FDB for viewing the index data during testing)

07/03/2015: Initial POC
phosphorus : 0.0.2 / vert.x : 2.1.5 / FoundationDB : 3.0.7 / FDB Java API : 3.0.2

Completed POC. Setup maven project build structure

04/03/2015: Initial POC
phosphorus : 0.0.2 / vert.x : 2.1.5 / FoundationDB : 3.0.7 / FDB Java API : 3.0.2

Converted all code to Java. Midway through implementing full matching POC. Current issue with sharing FDB database object.

28/02/2015: Initial POC

phosphorus : 0.0.1 / vert.x : 2.1.5 / FoundationDB : 3.0.7 / FDB Java API : 3.0.2
Only blocking implemented. Load PCC records into FDB using python script. At command prompt enter: vertx runmod org.ishafoundation~phosphorus~0.0.1. Send post requests (name string) to localhost:8080 to receive back a list of matching record ids






