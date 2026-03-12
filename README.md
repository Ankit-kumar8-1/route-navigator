# 🗺️ Route Navigator

A Spring Boot REST API for finding top 3 routes between two locations with real city names, distance, ETA at multiple speeds, and fuel cost estimation.

---

## 📌 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Request & Response Examples](#request--response-examples)
- [Exception Handling](#exception-handling)
- [Database Schema](#database-schema)
- [External APIs Used](#external-apis-used)
- [Future Improvements](#future-improvements)

---

## Overview

Route Navigator is a backend REST API built with **Java Spring Boot 4** that helps users find the best routes between two locations. It fetches up to 3 alternate routes, shows actual cities on the route, calculates ETA at different speeds, and estimates fuel cost for the journey.

### ✨ Key Features

- 🛣️ Top 3 alternate routes between any two locations
- 🏙️ Actual city names on each route (via reverse geocoding)
- 📏 Total distance in kilometers for each route
- ⏱️ ETA at 60, 80, and 100 kmph
- ⛽ Fuel cost and fuel required estimation
- 📋 Search history — save, fetch, delete, and clear
- ⚠️ Centralized exception handling with meaningful error messages
- 📦 Standardized API response format for all endpoints

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 4 |
| Database | MySQL |
| ORM | Spring Data JPA / Hibernate |
| Routing API | OSRM (Open Source Routing Machine) |
| Geocoding API | OpenRouteService (ORS) |
| Build Tool | Maven |
| Testing | Postman |

---

## Project Structure

```
src
└── main
    └── java
        └── com
            └── navigation
                └── route_navigator
                    │   RouteNavigatorApplication.java
                    │
                    ├── config
                    │       AppConfig.java
                    │
                    ├── controller
                    │       HistoryController.java
                    │       RouteController.java
                    │
                    ├── dto
                    │       ApiResponse.java
                    │       HistorySaveRequest.java
                    │       RouteRequest.java
                    │       RouteResponse.java
                    │
                    ├── entities
                    │       SearchHistoryEntity.java
                    │       UserEntity.java
                    │
                    ├── exceptions
                    │       ApiCallException.java
                    │       GlobalExceptionHandler.java
                    │       InvalidInputException.java
                    │       ResourceNotFoundException.java
                    │
                    ├── repository
                    │       SearchHistoryRepository.java
                    │       UserRepository.java
                    │
                    └── service
                            ETAService.java
                            FuelService.java
                            HistoryService.java
                            RouteService.java
```

---

## Getting Started

### Prerequisites

- Java 17
- Maven
- MySQL
- OpenRouteService API Key → [Get Free Key](https://openrouteservice.org/)

---

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/route-navigator.git
cd route-navigator
```

---

### 2. Create MySQL Database

```sql
CREATE DATABASE route_navigator_db;
```

---

### 3. Configure `application.properties`

```properties
# App
spring.application.name=route-navigator
server.port=8080

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/route_navigator_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# OpenRouteService API Key
openrouteservice.api.key=your_api_key_here
```

---

### 4. Run the Application

```bash
mvn spring-boot:run
```

Application starts at → `http://localhost:8080`

---

### 5. Insert Test User

```sql
INSERT INTO users (name, email, password, created_at)
VALUES ('Test User', 'test@gmail.com', 'test123', NOW());
```

---

## API Endpoints

### 🛣️ Route APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/routes/find` | Fetch top 3 routes |
| GET | `/api/routes/eta?distanceKm=` | Calculate ETA at 3 speeds |
| GET | `/api/routes/fuel?distanceKm=&vehicleMileage=&fuelPrice=` | Estimate fuel cost |

### 📋 History APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/history/save` | Save a search |
| GET | `/api/history/{userId}` | Get user's search history |
| DELETE | `/api/history/{historyId}` | Delete single history entry |
| DELETE | `/api/history/clear/{userId}` | Clear all history of a user |

---

## Request & Response Examples

### POST `/api/routes/find`

**Request Body**
```json
{
    "fromLocation": "Delhi",
    "toLocation": "Mumbai",
    "userId": 1,
    "vehicleMileage": 15,
    "fuelPrice": 100
}
```

**Response**
```json
{
    "success": true,
    "message": "Routes fetched successfully",
    "data": [
        {
            "routeNumber": 1,
            "routeType": "Fastest",
            "totalDistanceKm": 1324.8,
            "citiesOnRoute": ["Delhi", "Gurugram", "Ajmer", "Vadodara", "Mumbai"],
            "etaAt60Kmph": "22 hrs 5 mins",
            "etaAt80Kmph": "16 hrs 34 mins",
            "etaAt100Kmph": "13 hrs 15 mins",
            "estimatedFuelCost": 8832.0
        }
    ],
    "timestamp": "2026-03-12T17:25:55"
}
```

---

### GET `/api/routes/eta?distanceKm=1324.8`

**Response**
```json
{
    "success": true,
    "message": "ETA calculated successfully",
    "data": {
        "at60Kmph": "22 hrs 5 mins",
        "at80Kmph": "16 hrs 34 mins",
        "at100Kmph": "13 hrs 15 mins"
    },
    "timestamp": "2026-03-12T17:29:32"
}
```

---

### GET `/api/routes/fuel?distanceKm=1324.8&vehicleMileage=15&fuelPrice=100`

**Response**
```json
{
    "success": true,
    "message": "Fuel cost calculated successfully",
    "data": {
        "distanceKm": 1324.8,
        "fuelRequiredLitres": 88.32,
        "fuelCostRupees": 8832.0
    },
    "timestamp": "2026-03-12T17:30:30"
}
```

---

### POST `/api/history/save`

**Request Body**
```json
{
    "userId": 1,
    "fromLocation": "Delhi",
    "toLocation": "Mumbai"
}
```

---

## Exception Handling

All errors return a standard response format —

```json
{
    "success": false,
    "message": "Error description here",
    "data": null,
    "timestamp": "2026-03-12T17:00:00"
}
```

| Exception | HTTP Status | When |
|---|---|---|
| `ResourceNotFoundException` | 404 Not Found | User / History not found |
| `InvalidInputException` | 400 Bad Request | Same from/to location, invalid input |
| `ApiCallException` | 503 Service Unavailable | OSRM / ORS API failure |
| `MethodArgumentNotValidException` | 400 Bad Request | @NotBlank / @NotNull validation fails |
| `Exception` | 500 Internal Server Error | Any unexpected error |

---

## Database Schema

```sql
-- Users Table
CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    created_at  DATETIME
);

-- Search History Table
CREATE TABLE search_history (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL,
    from_location   VARCHAR(255) NOT NULL,
    to_location     VARCHAR(255) NOT NULL,
    searched_at     DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

> Tables are auto-created by JPA — no manual creation needed ✅

---

## External APIs Used

### 1. OSRM — Open Source Routing Machine
- **Purpose** → Fetch top 3 alternate routes with distance and road steps
- **Cost** → Free, no API key required
- **Docs** → [http://project-osrm.org](http://project-osrm.org)

### 2. OpenRouteService (ORS)
- **Purpose** → Forward geocoding (city name → coordinates) and reverse geocoding (coordinates → city name)
- **Cost** → Free tier available
- **Docs** → [https://openrouteservice.org](https://openrouteservice.org)

---

## Future Improvements

- 🔐 JWT based authentication and authorization
- 👤 User register and login APIs
- 🛣️ Toll cost estimation per route
- ⛽ Support for multiple fuel types (petrol, diesel, CNG)
- 🌦️ Weather alerts on route
- ⏰ Best departure time suggestion based on traffic
- 📍 Waypoints / stopovers support

---

## Author  : Ankit Saahariya 

Built with ❤️ using Java Spring Boot
