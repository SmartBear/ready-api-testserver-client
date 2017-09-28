# Swagger Assert4j Maven Plugin Samples

This module contains a number [sample recipes](src/test/resources/recipes) in JSON format that are 
executed via the maven-plugin as defined in the pom.xml using one of the RunSamplesLocally or 
 RunSampleRemotely profiles. 

You can run these samples using the local execution engine with

```
mvn integration-test -PRunSamplesLocally
```

You can run these samples using the remote execution engine with

```
mvn integration-test -PRunSamplesRemotely
```

By default the remote execution will use the public TestServer instance available at 
http://testserver.readyapi.io/ - change the defined endpoint properties in the pom to execute 
with a different server.
