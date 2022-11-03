# Spring MongoDB Demo
REST API project developed in Java using Spring Boot framework and MongoDB.

----------

## Features
- HandlerMethodArgumentResolver has been used to intercept the request to check and get a value from the headers by declaring an annotation
- AbstractMongoEventListener has been used to persist a history data for each POST, UPDATE, and DELETE operations
- AuditorAware had been used to insert the values for auditable attributes such as username, document version, and date of operations in the persisted document
- Converter and ConverterFactory have been used to convert the enum type to an object in the document before persisting and the document to an enum type after fetching the data  

----------

## Requirements
- Java 11
- Spring Boot 2.7.1
- Apache Maven 3.8.6

----------

## Environment Variables

----------

## Run App
Run the SpringMongoDBDemoApplication class as Java Application

----------

## Releases

----------

## Demo

----------