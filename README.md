# phosphorus
Real time record matching engine

28/02/2015: Initial POC  
phosphorus : 0.0.1 / vert.x : 2.1.5 / FoundationDB : 3.0.7 / FDB Java API : 3.0.2

Only blocking implemented. Load PCC records into FDB using python script. At command prompt enter: vertx runmod org.ishafoundation~phosphorus~0.0.1. Send post requests (name string) to localhost:8080 to receive back a list of matching record ids
