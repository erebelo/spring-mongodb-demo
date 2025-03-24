# Spring MongoDB Demo

REST API project developed in Java using Spring Boot 3 and MongoDB.

## Features

- **HandlerInterceptor**: Intercepts requests to verify whether they contain the required HTTP header attribute.
- **HandlerMethodArgumentResolver**: Retrieves a header value and assigns it to an argument type in controller methods.
- **AbstractMongoEventListener**: Persists historical data for each POST, UPDATE, PATCH, and DELETE operation.
- **AuditorAware**: Inserts values for auditable attributes such as `username`, document `version`, and `date` of operations in the persisted document.
- **Converter** and **ConverterFactory**: Customize data transformations, such as converting enums to `String` or `Object`, and formatting `LocalDate` to `Date` (UTC, no offset) for MongoDB persistence and retrieval.
- **ConstraintValidator**: Validates request fields and associated business rules by declaring an annotation in the request class.
- **Asynchronous Task Execution**: Uses `CompletableFuture` to handle tasks asynchronously, optimizing performance by managing threads and enabling non-blocking operations.
- **Logging**: Tracks application logs using `Log4j2`.
- **Global Exception Handler**: Manages errors across the application.
- **RestTemplate**: Facilitates HTTP requests to REST APIs.
- **SSL/TLS**: Configures secure connections to MongoDB.
- **Unit Tests**: Comprehensive test coverage for the entire application.
- **Bulk Insert**: Implements a reusable bulk insert engine using MongoDB's `BulkOperations` to efficiently insert large datasets while gracefully tracking failures.

## Requirements

- Java 17
- Spring Boot 3.x.x
- Apache Maven 3.8.6

## Libraries

- [spring-common-parent](https://github.com/erebelo/spring-common-parent): Manages the Spring Boot version and provide common configurations for plugins and formatting.
- [spring-common-lib](https://github.com/erebelo/spring-common-lib): Provides shared utilities and features, including:
  - **Logging**: Log4j2 with ECS (Elastic Common Schema) layout (for non-local environments).
  - **Http**: Pre-configured HTTP client utilities with customizable RestTemplate configurations for efficient connection management, proxy settings, and basic authentication.
  - **Utils**: Utility classes for serialization, object mapping, thread context management in asynchronous operations, and configuring asynchronous task execution with `CompletableFuture`.

## Configuring Maven for GitHub Dependencies

To pull the `spring-common-parent` and `spring-common-lib` dependencies, follow these steps:

1. **Generate a Personal Access Token**:

   Go to your GitHub account -> **Settings** -> **Developer settings** -> **Personal access tokens** -> **Tokens (classic)** -> **Generate new token (classic)**:

   - Fill out the **Note** field: `Pull packages`.
   - Set the scope:
     - `read:packages` (to download packages)
   - Click **Generate token**.

2. **Set Up Maven Authentication**:

   In your local Maven `settings.xml`, define the GitHub repository authentication using the following structure:

   ```xml
   <servers>
     <server>
       <id>github-spring-common-parent</id>
       <username>USERNAME</username>
       <password>TOKEN</password>
     </server>
     <server>
       <id>github-spring-common-lib</id>
       <username>USERNAME</username>
       <password>TOKEN</password>
     </server>
   </servers>
   ```

   **NOTE**: Replace `USERNAME` with your GitHub username and `TOKEN` with the personal access token you just generated.

## References

- [MongoDB Setup](https://github.com/erebelo/spring-mongodb-demo/blob/main/docs/mongodb-setup.md): Instructions for MongoDB setup and index creation.
- [Git Hooks Setup](https://github.com/erebelo/spring-mongodb-demo/tree/main/git-hooks): Automated Git hooks to enforce code formatting standards with Maven and the Spotless plugin.

## Run App

### Local Environment

- Run the `SpringMongoDBDemoApplication` class as Java Application.

### Non-Local Environments

For environments other than local, additional configurations are required:

- **Set Environment Variables**: Set the following environment variables to configure the application: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, and `DB_SSL_KEYSTORE_PASSWORD`.
- **Generate Keystore**: Generate a `mongodb-keystore.p12` file and place it in the `/resources` directory. This keystore will store the necessary certificates. For detailed steps on generating the required certificates and creating the keystore file, follow the instructions in the [Enable SSL/TLS on MongoDB Server](https://github.com/erebelo/spring-mongodb-demo/blob/main/docs/ssl-tls-setup.md).

## Collection

[Project Collection](https://github.com/erebelo/spring-mongodb-demo/tree/develop/collection)

## AWS Demo

[Spring MongoDB Demo](https://api.erebelo.com/spring-mongodb-demo/swagger-ui/index.html)

## AWS Deployment

Follow the [AWS Docker](https://github.com/erebelo/aws-docker/tree/main) guide to deploy a **Java App** and **MongoDB** instances and how to set up **Nginx** as a reverse proxy with a valid Wildcard SSL/TLS certificate.
