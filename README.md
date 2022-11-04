# Spring MongoDB Demo
REST API project developed in Java using Spring Boot framework and MongoDB.

----------

## Features
- HandlerInterceptor has been used to intercept the request to check whether the request contains the required http header attribute  
- HandlerMethodArgumentResolver has been used to get a header value and set it into a argument type from controller method
- AbstractMongoEventListener has been used to persist a history data for each POST, UPDATE, and DELETE operations
- AuditorAware has been used to insert the values for auditable attributes such as username, document version, and date of operations in the persisted document
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