# Getting Started with Spring Boot Starter

This guide will help you get started with this Spring Boot starter project.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (optional, for containerized deployment)

## Running Locally

1. Clone the repository
2. Navigate to the project directory
3. Run the application using Maven:

```bash
mvn spring-boot:run
```

4. The application will start on port 8080. You can access it at http://localhost:8080

## Building the Application

To build the application as a JAR file:

```bash
mvn clean package
```

The JAR file will be created in the `target` directory.

## Running the JAR

```bash
java -jar target/spring-boot-starter-0.0.1-SNAPSHOT.jar
```

## Available Endpoints

- `GET /` - Welcome message
- `GET /health` - Health check endpoint

## Configuration

The application can be configured through the `application.properties` file located in `src/main/resources`.

Key properties:

- `server.port` - The port the application runs on (default: 8080)
- `spring.application.name` - The name of the application