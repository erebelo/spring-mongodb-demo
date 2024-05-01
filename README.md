# Spring MongoDB Demo

REST API project developed in Java using Spring Boot 3 framework and MongoDB.

## Features

- **HandlerInterceptor** is utilized to intercept requests to verify whether they contain the required HTTP header attribute
- **HandlerMethodArgumentResolver** is employed to retrieve a header value and assign it to an argument type in controller methods
- **AbstractMongoEventListener** is utilized to persist historical data for each POST, UPDATE, PATCH, and DELETE operation
- **AuditorAware** is utilized to insert values for auditable attributes such as username, document version, and date of operations in the persisted document
- **Converter** and **ConverterFactory** are used to convert enum types to objects in the document before persisting and vice versa after fetching the data
- **ConstraintValidator** is used to validate request fields and their associated business rules by declaring an annotation in the request class
- **Logback/SLF4J** are employed to track application logs
- **Global Exception Handler** is implemented to manage errors
- **Template Method** design pattern used for different RestTemplate implementations
- **Unit Tests** are implemented covering the entire application

## Requirements

- Java 17
- Spring Boot 3.2.3
- Apache Maven 3.8.6

## Run App

- Set the following environment variables if running the project for a spring profile other than 'local': 'DB_HOST', 'DB_PORT', 'DB_NAME', 'DB_USERNAME', and 'DB_PASSWORD'
- Run the SpringMongoDBDemoApplication class as Java Application

## Collection

[Project Collection](https://github.com/erebelo/spring-mongodb-demo/tree/develop/collection)

## AWS Demo

[Spring MongoDB Demo](https://api.erebelo.com/spring-mongodb-demo/swagger-ui/index.html)

## AWS Deployment

Follow the [AWS Docker](https://github.com/erebelo/aws-docker/tree/main) guide to deploy a **Java App** and **MongoDB** instances and how to set up **Nginx** as a reverse proxy with a valid SSL/TLS (HTTPS) certificate.
