# Spring MongoDB Demo

REST API project developed in Java using Spring Boot 3 and MongoDB.

## Features

- **HandlerInterceptor** is utilized to intercept requests to verify whether they contain the required HTTP header attribute.
- **HandlerMethodArgumentResolver** is employed to retrieve a header value and assign it to an argument type in controller methods.
- **AbstractMongoEventListener** is utilized to persist historical data for each POST, UPDATE, PATCH, and DELETE operation.
- **AuditorAware** is utilized to insert values for auditable attributes such as username, document version, and date of operations in the persisted document.
- **Converter** and **ConverterFactory** are used to customize data transformations, such as converting enums to String or Object, and formatting LocalDate to Date (UTC, no offset) for MongoDB persistence and retrieval.
- **ConstraintValidator** is used to validate request fields and their associated business rules by declaring an annotation in the request class.
- **Logging** are employed to track application logs using Log4j2.
- **Global Exception Handler** is implemented to manage errors.
- **RestTemplate** is used for making HTTP requests to REST APIs.
- **SSL/TLS** is configured to establish secure connections to MongoDB.
- **Unit Tests** are implemented covering the entire application.

## Requirements

- Java 17
- Spring Boot 3.3.4
- Apache Maven 3.8.6

## References

This project utilizes the following libraries:

- [spring-common-parent](https://github.com/erebelo/spring-common-parent) to manage the Spring Boot version and provide common configurations for plugins and formatting.
- [spring-common-lib](https://github.com/erebelo/spring-common-lib) for shared utilities and features such as:
  - **Logging**: Provides Log4j2 with ECS (Elastic Common Schema) layout (for non-local environments).
  - **Http**: Provides pre-configured HTTP client utilities, offering customizable RestTemplate configurations for efficient connection management, proxy settings, and basic authentication.
  - **Utils**: Includes utility classes for serialization, object mapping, managing thread context in asynchronous operations, and configuring asynchronous task execution.
- Git hooks are set up for code formatting using [Git Hooks Setup](https://github.com/erebelo/spring-mongodb-demo/tree/main/git-hooks).

## Run App

### Local Environment

- Run the `SpringMongoDBDemoApplication` class as Java Application.

### Non-Local Environments

For environments other than local, additional configurations are required:

- **Generate Keystore**: Generate a `mongodb-keystore.p12` file and place it in the `/resources` directory. This keystore will store the necessary certificates. For detailed steps on generating the required certificates and creating the keystore file, follow the instructions in the [Enable SSL/TLS on MongoDB Server](https://github.com/erebelo/spring-mongodb-demo/tree/main/docs/ssl-tls-setup.md).
- **Set Environment Variables**: Set the following environment variables to configure the application: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, and `DB_SSL_KEYSTORE_PASSWORD`.

## Collection

[Project Collection](https://github.com/erebelo/spring-mongodb-demo/tree/develop/collection)

## AWS Demo

[Spring MongoDB Demo](https://api.erebelo.com/spring-mongodb-demo/swagger-ui/index.html)

## AWS Deployment

Follow the [AWS Docker](https://github.com/erebelo/aws-docker/tree/main) guide to deploy a **Java App** and **MongoDB** instances and how to set up **Nginx** as a reverse proxy with a valid Wildcard SSL/TLS certificate.
