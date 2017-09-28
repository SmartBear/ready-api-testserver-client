# Swagger Assert4j Core 

This modules provides the core Java APIs for creating and executing Test recipes which can then be executed locally or
remotely as described in the [Concepts](../../CONCEPTS.md) document. 

* [Fluent vs Pojo API](#fluent-vs-pojo-api)
* [Recipes](#recipes)
* [Test Steps](#test-steps)
  * [REST Requests](#rest-requests)
    * [Parameters](#parameters) 
    * [Content](#content)
    * [Authentication](#authentication)
    * [Attachments](#attachments)
  * [SOAP Requests](#soap-requests)
  * [Property Transfers](#property-transfers)
  * [Delay](#delay)
  * [Script](#script)
  * [MockResponse](#mockresponse)
  * [JDBC Request](#jdbc-request)
  * [Properties](#properties)
  * [Plugin](#plugin)
* [Assertions](#assertions)
  * [HTTP Assertions](#http-assertions)
  * [Content Assertions](#content-assertions)
    * [JSON](#json)
    * [XML](#xml)
  * [Script](#script)
* [Extractors](#extractors)
* [Executing Recipes](#executing-recipes)
  * [Execution Listeners](#execution-listeners)
  * [Recipe Filters](#recipe-filters)
* [Results](#results)
  * [Transaction Logs](#transaction-logs)
  
  
# Fluent vs Pojo API

To simplify the creation of Test Recipes POJOs that get serialized to the corresponding JSON Recipes before execution this
module provides a fluent API layer that attempts to provide a more "verbal" way of expressing Recipes through code. All
the examples below will use this fluent API - but if you're interested in the underlying POJO classes please have a look 
at the source or core javadocs. The [java samples module](../samples/java) contains examples using both the fluent and
pojo approach. 

# Recipes

Assert4j expresses tests as "recipes" - which are an ordered list of steps that are executed one after the other when
executed. There are a fair number of built-in test steps (outlined below) - and there is an underlying extension 
mechansim for providing custom steps also.

Building a recipe with the fluent API is easiest done with the [TestRecipeBuilder](src/main/java/io/swagger/assert4j/TestRecipeBuilder.java)
class:

```java
TestRecipe recipe = TestRecipeBuilder.buildRecipe( ...list of TestStepBuilder objects... ):

// optionally add some more teststeps
recipe.addStep( ...another TestStepBuilder object... );

// execute the test!
RecipeExecutionResult result = RecipeExecutorBuilder.buildDefault().executeRecipe( recipe );
``` 

# Test Steps

Test steps represent the actual actions performed during the execution of a test, the 
[TestSteps](src/main/java/io/swagger/assert4j/teststeps/TestSteps.java) class provides factory
methods for creating TestStepBuilders for each supported test step type - as you will see below. 

## REST Requests

The TestSteps class provides convenience methods for the 
common HTTP Verbs:

```java
TestRecipe recipe = TestRecipeBuilder.buildRecipe(  
    GET( "http://petstore.swagger.io/v2/store/inventory" ),
    POST( "http://petstore.swagger.io/v2/store/order" ),
    DELETE( "http://petstore.swagger.io/v2//pet/{petId}"),
    GET( "http://petstore.swagger.io/v2/pet/findByStatus" )
):
``` 

### Parameters

Both the DELETE and last GET in this example require parameter values - let's add them:

```java
TestRecipe recipe = TestRecipeBuilder.buildRecipe(  
    GET( "http://petstore.swagger.io/v2/store/inventory" ),
    POST( "http://petstore.swagger.io/v2/store/order" ),
    DELETE( "http://petstore.swagger.io/v2//pet/{petId}").
        withPathParameter( "petId", "1" )
    ,
    GET( "http://petstore.swagger.io/v2/pet/findByStatus" ).
        withQueryParameter( "status", "test")
):
``` 
 
If you need to add multiple parameters you can use 
 
```java
TestRecipe recipe = TestRecipeBuilder.buildRecipe(  
  GET( "http://petstore.swagger.io/v2/pet/findByStatus" ).
     withParameters( 
         query( "status", "test"),
         query("limit", "10")
     )
):
 ``` 

### Content

Adding content to a POST or PUT is straight-forward:

```java
TestRecipe recipe = TestRecipeBuilder.buildRecipe(  
    POST( "http://petstore.swagger.io/v2/store/order" ).
        withMediaType( "application/json").
        withRequestBody( ...some object that can be serialized to JSON... )
):
``` 

the built in serialization support json, yaml and xml media types.

### Authentication

If you need to add authentication to your request, then use one of the factory methods
provided by the [Authentications](src/main/java/io/swagger/assert4j/auth/Authentications.java) class.

```java
TestRecipe recipe = TestRecipeBuilder.buildRecipe(  
  GET( "http://petstore.swagger.io/v2/pet/findByStatus" ).
     withParameters( 
         query( "status", "test"),
         query("limit", "10")
     ).
     withAuthentication(
        basic( "username", "password"),
        oAuth2().
            withAccessToken( ... ) 
     )
):
 ``` 

### Attachments



## SOAP Requests

## Property Transfers

## Delay

## Script

## MockResponse

## JDBC Request

## Properties

## Plugin

# Assertions

## HTTP Assertions

## Content Assertions

### JSON

### XML

## Script

# Extractors

# Executing Recipes

## Execution Listeners

## Recipe Filters

# Results

## Transaction Logs






