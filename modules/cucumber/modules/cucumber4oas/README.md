## Swagger/OAS Cucumber Extensions

This module allows for inline definitions of when/then vocabularies inside an OAS definition using OAS extensions, 
which  can then be used to write and execute Cucumber scenarios without any further requirement for writing
any StepDef code. The vocabularies are added using custom OAS extensions and dynamically processed when
executing corresponding scenarios using the runner in this module.

Contents:
* [A simple x-cucumber-when/x-cucumber-then example](#a-simple-x-cucumber-whenx-cucumber-then-example)
* [Adding parameters and assertions](#adding-parameters-and-assertions)
* [Parameterizing x-cucumber-when statements](#parameterizing-x-cucumber-when-statements)
* [Using the standard OAS/REST Vocabulary](#using-the-standard-rest--oas-vocabularies)
* [x-cucumber-when Reference](#x-cucumber-when-reference)
* [x-cucumber-then Reference](#x-cucumber-then-reference)
* [Assertion Reference](#assertion-reference)
  * [JSON Assertion](#json-assertion)
  * [Content Assertion](#content-assertion)
  * [Header Assertion](#header-assertion)
  * [XML Assertion](#xml-assertion)
* [Using the OASBackend](#using-the-oasbackend)
* [Running with Docker and Maven](#running-with-docker-and-maven)
* [Running with JUnit](#running-with-junit)

### A simple x-cucumber-when/x-cucumber-then example

A quick example - consider the following search operation from the SwaggerHub public REST API 
(greatly abbreviated for the sake of this example) OAS 2.0 definition - hosted on SwaggerHub at 
https://app.swaggerhub.com/apis/olensmar/registry-api-cucumber/cucumber4oas-demo

```yaml
paths:
  /specs:
    get:
      operationId: searchApisAndDomains
      parameters:
      - name: specType
        in: query
        type: string
        default: ANY
        enum:
        - API
        - DOMAIN
        - ANY
      - name: owner
        in: query
        type: string
      responses:
        200:
          schema:
            $ref: '#/definitions/ApisJson'
```

We might want to write a scenario to test this API as follows:

```gherkin
Feature: SwaggerHub REST API

  Scenario: Default API Listing
    When performing a default search
    Then a search result is returned
```

Traditionally this would require us to write corresponding StepDefs in our language of choice and 
execute them with cucumber. Instead, this module allows us to define the above when/then
vocabulary inside the OAS definition using x-cucumber extensions:

```yaml
paths:
  /specs:
    get:
      x-cucumber-when:
      - performing a default search
      operationId: searchApisAndDomains
      parameters:
      - name: specType
        in: query
        type: string
        default: ANY
        enum:
        - API
        - DOMAIN
        - ANY
      - name: owner
        in: query
        type: string
      responses:
        200:
          schema:
            $ref: '#/definitions/ApisJson'
           x-cucumber-then:
           - a search result is returned
``` 

As you can see - a `x-cucumber-when` extension has been added to the operation, and a corresponding 
`x-cucumber-then` extension has been added to the default response. 

Now all we have to do is add a given statement pointing to the modified OAS definition:

```gherkin
Feature: SwaggerHub REST API
  
  Background:
    Given the OAS definition at https://api.swaggerhub.com/apis/olensmar/registry-api-cucumber/cucumber4oas-demo

  Scenario: Default API Listing
    When performing a default search
    Then a search result is returned
```

and we're ready to run our scenario using this projects docker image:

```shell script
docker run -v /Users/olensmar/features:/features smartbear/readyapi4j-cucumber4oas /features/swaggerhub-sample.feature -p pretty
Feature: SwaggerHub REST API

  Background:                                                                                          # /features/swaggerhub-sample.feature:3
    Given the OAS definition at https://api.swaggerhub.com/apis/olensmar/registry-api-cucumber/cucumber4oas-demo # OASStepDefs.theOASDefinitionAt(String)

  Scenario: Default API Listing      # /features/swaggerhub-sample.feature:6
    When performing a default search # OASStepDefs.aRequestToOperationWithParametersIsMade(String,String)
    Then a search result is returned # OASStepDefs.theResponseIs(String)

1 Scenarios (1 passed)
3 Steps (3 passed)
0m5.706s
```

Using our extensions the runner can now dynamically map our gherkin vocabulary to the corresponding operation 
and expected result - making the REST API call to the host specified in the OAS definition. No coding required!

### Adding parameters and assertions

It's pretty common to want to assert an API response for specific values or content, which is
traditionally done in code/tooling using an assertion mechanism. Fortunately this is possible here
as well:

```yaml
paths:
  /specs:
    get:
      x-cucumber-when:
      - performing a default search
      - when: searching for swaggerhub apis
        parameters:
          specType: API
          owner: swaggerhub
     ...
      responses:
        200:
          ...
          x-cucumber-then:
          - a search result is returned
          - then: at least 10 items are returned
            assertions:
           - type: json
             path: $.apis.length()
             value: 10
```

Here we've extended both the x-cucumber-when and x-cucumber-then definitions:
* added a x-cucumber-when that sets the specType and owner operation parameters to API and swaggerhub respectively
* added a x-cucumber-then definition asserts the response for specific content using 
a JSON-Path expression - which allows us for functional validation of the API being called (cucumber4oas 
supports a number of assertions - see the entire list below).

Our updated feature is now:

```gherkin
Feature: SwaggerHub REST API
  
  Background:
    Given the OAS definition at https://api.swaggerhub.com/apis/olensmar/registry-api-cucumber/cucumber4oas-demo

  Scenario: Default Specs Listing
    When performing a default search
    Then at least 10 items are returned

  Scenario: SwaggerHub API listing
    When searching for swaggerhub apis
    Then a search result is returned
```

Once again no coding required - just run this feature as we did above and cucumber4OAS will dynamically make the 
corresponding calls and assert the responses.

```shell script
docker run -v /Users/olensmar/features:/features smartbear/readyapi4j-cucumber4oas /features/swaggerhub-sample.feature -p pretty
Feature: SwaggerHub REST API

  Background:                                                                                          # /features/swaggerhub-sample.feature:3
    Given the OAS definition at https://api.swaggerhub.com/apis/olensmar/registry-api-cucumber/cucumber4oas-demo # OASStepDefs.theOASDefinitionAt(String)

  Scenario: Default Specs Listing       # /features/swaggerhub-sample.feature:6
    When performing a default search    # OASStepDefs.aRequestToOperationWithParametersIsMade(String,String)
    Then at least 10 items are returned # OASStepDefs.theResponseIs(String)

  Background:                                                                                          # /features/swaggerhub-sample.feature:3
    Given the OAS definition at https://api.swaggerhub.com/apis/olensmar/registry-api-cucumber/cucumber4oas-demo # OASStepDefs.theOASDefinitionAt(String)

  Scenario: SwaggerHub API listing     # /features/swaggerhub-sample.feature:10
    When searching for swaggerhub apis # OASStepDefs.aRequestToOperationWithParametersIsMade(String,String)
    Then a search result is returned   # OASStepDefs.theResponseIs(String)

2 Scenarios (2 passed)
6 Steps (6 passed)
0m7.974s
```

### Parameterizing x-cucumber-when statements

In our example we had hard-coded the owner in the x-cucumber-when statement to "swaggerhub" - but what if we want the writer
of the scenario to define that value themselves? We can do this by adding arguments to our x-cucumber-when statements:

```yaml
paths:
  /specs:
    get:
      x-cucumber-when:
      - performing a default search
      - when: searching for {owner} apis
        parameters:
          specType: API
     ...
```

Using the curly-bracket syntax with an existing parameter name we can now specify any owner in our scenario - which will
be used as the value for the owner parameter in the underlying request.

```gherkin
Feature: SwaggerHub REST API
  
  Background:
    Given the OAS definition at https://api.swaggerhub.com/apis/olensmar/registry-api-cucumber/cucumber4oas-demo

  Scenario: Default Specs Listing
    When performing a default search
    Then at least 10 items are returned

  Scenario: SwaggerHub API listing
    When searching for swaggerhub apis
    Then a search result is returned

  Scenario: SmartBear API listing
    When searching for smartbear apis
    Then a search result is returned
```

Surround values with spaces with quotes (escape quotes with a backslash) if needed.

### Using the standard REST / OAS vocabularies

The [default vocabularies](../../README.md#api-stepdefs-reference) for testing REST APIs provided by ReadyAPI4j are still at your disposal
when writing your scenarios - so you can intermix these with your custom definitions provided 
via x-cucumber extensions. For example we could have written the above Feature using these stepdefs 
entirely without the need for any cucumber extensions:

```gherkin
Feature: SwaggerHub REST API
  
  Background:
    Given the OAS definition at https://api.swaggerhub.com/apis/olensmar/registry-api-cucumber/cucumber4oas-demo

  Scenario: Default Specs Listing
    When a request to searchApisAndDomains is made
    Then a 200 response is returned
     And the path $.apis.length() equals 10

  Scenario: SwaggerHub API listing
    When a request to searchApisAndDomains is made
     And type is API
     And owner is SwaggerHub
    Then a 200 response is returned
``` 

Running the above results in:

```shell script
docker run -v /Users/olensmar/features:/features smartbear/readyapi4j-cucumber4oas /features/swaggerhub-sample2.feature -p pretty
Feature: SwaggerHub REST API

  Background:                                                                                          # /features/swaggerhub-sample2.feature:3
    Given the OAS definition at https://api.swaggerhub.com/apis/olensmar/registry-api-cucumber/cucumber4oas-demo # OASStepDefs.theOASDefinitionAt(String)

  Scenario: Default Specs Listing                  # /features/swaggerhub-sample2.feature:6
    When a request to searchApisAndDomains is made # OASStepDefs.aRequestToOperationIsMade(String)
    Then a 200 response is returned                # RestStepDefs.aResponseIsReturned(String)
    And the path $.apis.length() equals 10         # RestStepDefs.thePathEquals(String,String)

  Background:                                                                                          # /features/swaggerhub-sample2.feature:3
    Given the OAS definition at https://api.swaggerhub.com/apis/olensmar/registry-api-cucumber/cucumber4oas-demo # OASStepDefs.theOASDefinitionAt(String)

  Scenario: SwaggerHub API listing                 # /features/swaggerhub-sample2.feature:11
    When a request to searchApisAndDomains is made # OASStepDefs.aRequestToOperationIsMade(String)
    And type is API                                # OASStepDefs.parameterIs(String,String)
    And owner is SwaggerHub                        # OASStepDefs.parameterIs(String,String)
    Then a 200 response is returned                # RestStepDefs.aResponseIsReturned(String)

2 Scenarios (2 passed)
9 Steps (9 passed)
0m5.658s
```
As you can see this calls the existing step definitions defined in the OASStepDefs/RestStepDefs classes (as did the above examples).

## x-cucumber-when reference

`x-cucumber-when` must be provided for a specific operation and can be one of the following:
* a single string value that can optionally contain parameterizations (see above)
* an array containing single string values or `when` objects containing the following properties
  * a `when` single string value optionally containing parameterizations
  * a `parameters` map containing `name:value` pairs where name is the name of an existing parameter for the containing 
    operation and value is the value it should be assigned 

See examples of both of these under [Adding Parameters and Assertions](#adding-parameters-and-assertions) above.

## x-cucumber-then reference 

`x-cucumber-then` must be provided for a specific response and can be one of the following:
* a single string value 
* an array containing single string values or `then` objects containing the following properties
  * a `then` single string value
  * an `assertions` array containing `assertion` objects as described below

See examples of both of these under [Adding Parameters and Assertions](#adding-parameters-and-assertions) above.

## Assertion reference

As described above it is possible to add assertions to `x-cucumber-then` extensions, the below assertions are 
currently available. All assertions require a `type` property as described below.

### json assertion

The following properties are available for the json assertion
* `type: json` 
* `path`: a json-path expression to apply to the response body (mandatory)
* one of the following
  * `value`: an expected value returned by the json-path expression
  * `regex`: a regular expression that must match the value returned by the json-path expression
  * `count`: the expected number of items returned by the json-path expression
* if none of these three is specified the assertion checks that any value for the json-path expression exists

### content assertion

The following properties are available for the content assertion
* `type: contains`
* one of the following
  * `content`: an expected value contained in the response
  * `regex`: a regular expression that must match the entire response

### header assertion

The following properties are available for the header assertion
* `type: header` 
* `name`: the name of the header to assert
* optionally one of the following
  * `value`: the expected value of the header
  * `regex`: a regular expression that must match the header value
* if neither of these is specified the assertion checks that the header exists in the response, ignoring its value

### xml assertion

The following properties are available for the xml assertion
* `type: xpath` 
* `path`: an xpath expression to apply to the response body (required)
* `value`: the expected value to be returned by the xpath expression (required)

## Using the OASBackend

Internally this module provides a custom [cucumber-jvm](https://github.com/cucumber/cucumber-jvm) Backend that:
- Loads the [default REST StepDefs](../../README.md#api-stepdefs-reference) provided by ReadyAPI4j
- Dynamically maps the x-cucumber-when/then statements in a given OAS definition to the default StepDefs
- Loads any other StepDefs provided to the cucumber runtime as normal
 
As shown above the OASBackend can be run either via its docker image available on dockerhub at 
https://hub.docker.com/repository/docker/smartbear/readyapi4j-cucumber4oas or via the jar file built by module.

Running the docker image requires one to map a local volume containing feature file(s) into a volume of the container 
and then specifying that volume (or a file in it) as the feature-file argument to the cucumber runtime. Since the 
docker image simple runs the existing Cucumber CLI all corresponding command-line options apply.

The jar file can be used similarly (without the requirement to map a volume of course). It's main class is set to the 
Cucumber CLI (io.cucumber.core.cli.Main) - for example the last above command could be run with:

```shell script
java -jar target/readyapi4j-cucumber4oas-1.0.0-SNAPSHOT.jar /Users/olensmar/features/swaggerhub-sample2.feature -p pretty
Feature: SwaggerHub REST API

  Background:                                                                                          # /Users/olensmar/features/swaggerhub-sample2.feature:3
    Given the OAS definition at https://api.swaggerhub.com/apis/olensmar/registry-api-cucumber/cucumber4oas-demo # OASStepDefs.theOASDefinitionAt(String)

  Scenario: Default Specs Listing                  # /Users/olensmar/features/swaggerhub-sample2.feature:6
    When a request to searchApisAndDomains is made # OASStepDefs.aRequestToOperationIsMade(String)
    Then a 200 response is returned                # RestStepDefs.aResponseIsReturned(String)
    And the path $.apis.length() equals 10         # RestStepDefs.thePathEquals(String,String)

  Background:                                                                                          # /Users/olensmar/features/swaggerhub-sample2.feature:3
    Given the OAS definition at https://api.swaggerhub.com/apis/olensmar/registry-api-cucumber/cucumber4oas-demo # OASStepDefs.theOASDefinitionAt(String)

  Scenario: SwaggerHub API listing                 # /Users/olensmar/features/swaggerhub-sample2.feature:11
    When a request to searchApisAndDomains is made # OASStepDefs.aRequestToOperationIsMade(String)
    And type is API                                # OASStepDefs.parameterIs(String,String)
    And owner is SwaggerHub                        # OASStepDefs.parameterIs(String,String)
    Then a 200 response is returned                # RestStepDefs.aResponseIsReturned(String)

2 Scenarios (2 passed)
9 Steps (9 passed)
0m4.121s
```

## Running with Docker and Maven

You can use the [fabric8 maven docker plugin](https://dmp.fabric8.io/) to run your feature files with maven and the above
mentioned docker image (you'll need to have docker installed in this setup). 

The following build profile executes the features in the src/test/features folder for the `test` maven build step. 

```xml
...
    <profile>
        <id>run-cucumber4oas</id>
        <build>
            <plugins>
                <plugin>
                    <groupId>io.fabric8</groupId>
                    <artifactId>docker-maven-plugin</artifactId>
                    <version>0.31.0</version>
                    <configuration>
                        <verbose>true</verbose>
                        <images>
                            <image>
                                <alias>readyapi4j-cucumber4oas</alias>
                                <name>smartbear/readyapi4j-cucumber4oas:latest</name>
                                <run>
                                    <!-- map features folder to /features in the container -->
                                    <volumes>
                                        <bind>
                                            <volume>${project.basedir}/src/test/features:/features</volume>
                                        </bind>
                                    </volumes>
                                    <!-- run all features in mapped volume with pretty printing -->
                                    <cmd>/features -p pretty</cmd>
                                    <log>
                                        <color>blue</color>
                                    </log>
                                    <!-- expect tests to pass -->
                                    <wait>
                                        <exit>0</exit>
                                    </wait>
                                </run>
                            </image>
                        </images>
                    </configuration>
                    <executions>
                        <execution>
                            <id>package</id>
                            <phase>test</phase>
                            <goals>
                                <goal>start</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </build>
    </profile>
...
```

Simply run this with (this sample is included in this module)

```
mvn test -P run-cucumber4oas
```

## Running with JUnit

Since the OASBackend is discovered by the Cucumber runtime you can run your tests as usual with JUnit. First
add the this dependency to your maven project (it includes the cucumber-jvm 4.7.x runtime):

```xml
...
    <dependency>
        <groupId>com.smartbear.readyapi</groupId>
        <artifactId>readyapi4j-cucumber4oas</artifactId>
        <version>${project.version}</version>
    </dependency>
...
```

and create your JUnit/Cucumber runner class:

```java
package com.smartbear.readyapi4j.cucumber.samples;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "html:target/cucumber"},
        features = {"src/test/resources/cucumber"}
        )
public class CucumberTest {
}
```

This will run all features found in the src/test/resources/cucumber folder using the OASBackend. 

