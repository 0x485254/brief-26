# API Documentation

This document provides information about the REST API endpoints available in this Spring Boot starter application.

## Base URL

When running locally, the base URL is:

```
http://localhost:8080
```

## Endpoints

### Welcome Endpoint

Returns a welcome message and the current status of the application.

- **URL**: `/`
- **Method**: `GET`
- **Auth required**: No
- **Permissions required**: None

#### Success Response

- **Code**: 200 OK
- **Content example**:

```json
{
  "message": "Welcome to Spring Boot Starter API",
  "status": "running"
}
```

### Health Check Endpoint

Returns the current health status of the application.

- **URL**: `/health`
- **Method**: `GET`
- **Auth required**: No
- **Permissions required**: None

#### Success Response

- **Code**: 200 OK
- **Content example**:

```json
{
  "status": "UP"
}
```

## Actuator Endpoints

Spring Boot Actuator provides several production-ready endpoints. The following are enabled:

- **Health**: `/actuator/health` - Shows application health information
- **Info**: `/actuator/info` - Displays application information
- **Metrics**: `/actuator/metrics` - Shows metrics information for the application

## Error Responses

### 404 Not Found

```json
{
  "timestamp": "2023-11-01T12:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "path": "/non-existent-path"
}
```

### 500 Internal Server Error

```json
{
  "timestamp": "2023-11-01T12:00:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/some-path"
}
```