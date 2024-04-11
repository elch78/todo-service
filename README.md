# Todolist Demo

This is a recruiting assignment using Spring Boot framework to implement a simple todo
list service. Adapters for REST and Spring Data JDBC are provided.

# Prerequisits

- Java 17+ installed

# Build

`mvn clean package`

# Run

The standard Spring Boot mechanism

either

`mvn spring-boot:run`

or

`java -jar ./target/todo-service-0.0.1-SNAPSHOT.jar`

# Build Docker image

Build jar first, then

`docker build . -t demo/todolist`

run Docker image with

` docker run -d -p 8080:8080 --name demo-todolist demo/todolist`
