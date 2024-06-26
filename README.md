# Todolist Demo

This is a recruiting assignment using Spring Boot framework to implement a simple todo
list service. Adapters for REST and Spring Data JDBC are provided. The database schema
is managed with Flyway (see `src/main/resources/db`). Maven wrapper for environment
independent building.

Assumptions:

- list is short enough that we don't need pagination. The index on the database table
to support the filter was added nonetheless.

# Prerequisits

- Java 21+ installed

# Build

`./mvnw clean package`

# Run

The standard Spring Boot mechanism either

`./mvnw spring-boot:run`

or

`java -jar ./target/todo-service-0.0.1-SNAPSHOT.jar`

# Build Docker image

Build jar first, then

`docker build . -t demo/todolist`

run Docker image with

` docker run -d -p 8080:8080 --name demo-todolist demo/todolist`
