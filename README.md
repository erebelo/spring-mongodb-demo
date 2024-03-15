# Spring MongoDB Demo
REST API project developed in Java using Spring Boot framework and MongoDB.

----------

## Features
- HandlerInterceptor has been used to intercept the request to check whether the request contains the required http header attribute  
- HandlerMethodArgumentResolver has been used to get a header value and set it into a argument type from controller method
- AbstractMongoEventListener has been used to persist a history data for each POST, UPDATE, PATCH, and DELETE operations
- AuditorAware has been used to insert the values for auditable attributes such as username, document version, and date of operations in the persisted document
- Converter and ConverterFactory have been used to convert the enum type to an object in the document before persisting and the document to an enum type after fetching the data
- ConstraintValidator has been used to validate some request fields and its business rules by declaring an annotation in the request class
- Logback/SLF4J has been used to track the application logs
- CompletableFuture has been used to execute multiple, asynchronous requests in order to fetch paging data from downstream API
- Template Method design pattern has been used for different RestTemplate implementations

----------

## Requirements
- Java 17
- Spring Boot 3.2.3
- Apache Maven 3.8.6

----------

## Run App
Run the SpringMongoDBDemoApplication class as Java Application

----------

## Collection
[Project Collection][1]

----------

[1]: https://github.com/erebelo/spring-mongodb-demo/tree/develop/collection