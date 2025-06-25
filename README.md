# Spring Boot Starter Project

A starter project for Spring Boot applications with Docker support.

## Overview

This project provides a foundation for building Spring Boot applications with:

- RESTful API endpoints
- Docker and Docker Compose configuration
- Comprehensive documentation

## Project Structure

```
.
├── src/                    # Source code
│   ├── main/
│   │   ├── java/           # Java source files
│   │   └── resources/      # Configuration files
│   └── test/               # Test files
├── docs/                   # Documentation
├── Dockerfile              # Docker configuration
├── docker-compose.yml      # Docker Compose configuration
├── pom.xml                 # Maven dependencies
└── README.md               # This file
```

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (optional)

### Running Locally

```bash
# Clone the repository
git clone https://github.com/yourusername/spring-boot-starter.git
cd spring-boot-starter

# Run the application
mvn spring-boot:run
```

The application will be available at http://localhost:8080

### Running with Docker

The project includes Docker configurations for three environments:

```bash
# For local development
docker-compose -f docker-compose.dev.yml up -d

# For pre-production testing
docker-compose -f docker-compose.preprod.yml up -d

# For production deployment
docker-compose -f docker-compose.prod.yml up -d

# View logs
docker-compose -f docker-compose.[env].yml logs -f
```

See the [Docker Deployment Guide](docs/docker-deployment.md) for detailed instructions.

## Available Endpoints

- `GET /` - Welcome message
- `GET /health` - Health check
- Actuator endpoints at `/actuator/*`

## Documentation

Detailed documentation is available in the `docs` directory:

- [Getting Started Guide](docs/getting-started.md)
- [API Documentation](docs/api-documentation.md)
- [Docker Deployment Guide](docs/docker-deployment.md)

## Customizing the Starter

1. Update the application name and description in `pom.xml`
2. Modify the package structure in `src/main/java`
3. Add your own controllers, services, and repositories
4. Configure additional dependencies in `pom.xml`
5. Update the documentation to reflect your changes

## License

This project is licensed under the MIT License - see the LICENSE file for details.
