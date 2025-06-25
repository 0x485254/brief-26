# Docker Deployment Guide

This guide explains how to deploy the Spring Boot application using Docker and Docker Compose for different environments (development, pre-production, and production).

## Prerequisites

- Docker installed on your machine
- Docker Compose installed on your machine

## Environment-Specific Deployments

This project includes three environment-specific Docker Compose configurations:

1. **Development** (`docker-compose.dev.yml`): For local development with hot-reloading and debugging
2. **Pre-production** (`docker-compose.preprod.yml`): For testing in a production-like environment
3. **Production** (`docker-compose.prod.yml`): For deploying to production with high availability

## Building and Running with Docker

### Building the Docker Image

To build the Docker image for the application:

```bash
docker build -t spring-boot-starter .
```

You can specify the environment during build:

```bash
docker build --build-arg SPRING_PROFILES_ACTIVE=prod -t spring-boot-starter .
```

### Running the Docker Container

To run the application in a Docker container:

```bash
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=dev spring-boot-starter
```

The application will be accessible at http://localhost:8080.

## Deploying with Docker Compose

Docker Compose simplifies the deployment process by managing multiple containers and their configurations.

### Development Environment

For local development with hot-reloading and debugging:

```bash
docker-compose -f docker-compose.dev.yml up -d
```

Features:
- Source code mounted as volumes for hot-reloading
- Remote debugging enabled on port 5005
- PostgreSQL database with development configuration
- All actuator endpoints exposed

### Pre-production Environment

For testing in a production-like environment:

```bash
docker-compose -f docker-compose.preprod.yml up -d
```

Features:
- Resource limits for CPU and memory
- PostgreSQL database with pre-production configuration
- Limited actuator endpoints
- Environment variable support for database credentials

### Production Environment

For deploying to production:

```bash
docker-compose -f docker-compose.prod.yml up -d
```

Features:
- Service replication (2 replicas)
- Health checks for application and database
- Resource limits optimized for production
- Environment variables for all sensitive information
- Persistent volume configuration for database

### Viewing Logs

To view the logs of the running containers:

```bash
docker-compose -f docker-compose.[env].yml logs -f
```

Replace `[env]` with `dev`, `preprod`, or `prod`.

### Stopping the Application

To stop the application:

```bash
docker-compose -f docker-compose.[env].yml down
```

## Configuration

### Environment Variables

Each environment has its own set of default environment variables in the Docker Compose files. You can override these by:

1. Creating a `.env` file in the project root
2. Passing variables directly in the command line:

```bash
POSTGRES_PASSWORD=secure_password docker-compose -f docker-compose.prod.yml up -d
```

### Environment-Specific Properties

The application includes environment-specific properties files:

- `application-dev.properties`: Development settings
- `application-preprod.properties`: Pre-production settings
- `application-prod.properties`: Production settings

These are activated automatically based on the `SPRING_PROFILES_ACTIVE` environment variable.

## Production Considerations

For production deployments, consider:

1. Using Docker secrets for sensitive information:
   ```bash
   docker secret create postgres_password /path/to/password/file
   ```

2. Setting up proper logging and monitoring:
   - The production configuration already includes file-based logging
   - Consider integrating with a log aggregation service

3. Using a container orchestration platform like Kubernetes for:
   - Automatic scaling
   - Rolling updates
   - Self-healing
   - Load balancing
