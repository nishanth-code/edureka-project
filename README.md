# Enterprise Microservices Platform

Production-grade enterprise microservices architecture built with **Java 17**, **Spring Boot 3.x**, and **Spring Cloud**.

## 📋 Project Structure

```
enterprise-microservices-platform/
├── pom.xml (parent)
├── docker-compose.yml
├── Jenkinsfile
│
├── service-registry/          # Eureka Server (Port 8761)
├── config-server/              # Spring Cloud Config Server (Port 8888)
├── api-gateway/                # Spring Cloud Gateway (Port 8080)
│
├── auth-service/               # Authentication Service (Port 8081)
├── product-catalog-service/    # Product Management (Port 8082)
├── inventory-service/          # Stock Management (Port 8083)
├── order-service/              # Order Processing (Port 8085)
├── notification-service/       # Notifications (Port 8086)
├── aggregation-service/        # BFF Pattern (Port 8086)
```

## 🏗️ Technology Stack

- **Java 17+** - Latest LTS version with new language features
- **Spring Boot 3.3.0** - Latest stable version
- **Spring Cloud 2023.0.2** - LTS release
- **PostgreSQL 15** - Relational database per service
- **Eureka** - Service discovery
- **Spring Cloud Gateway** - API Gateway with routing
- **OpenFeign** - Declarative HTTP client
- **Resilience4j** - Circuit breaker & fault tolerance
- **JWT** - Token-based authentication
- **Docker** - Containerization with multi-stage builds
- **Jenkins** - CI/CD pipeline
- **OpenAPI/Swagger** - API documentation

## 🔍 Service Details

### Service Registry (Port 8761)
- Eureka Server for dynamic service discovery
- Auto-registration of all microservices
- Healthcheck monitoring

### Config Server (Port 8888)
- Centralized configuration management
- Git-based configuration storage
- Dynamic configuration updates

### API Gateway (Port 8080)
- Spring Cloud Gateway
- JWT validation & token propagation
- Route management to all services
- Global logging filter
- Resilience4j integration

### Auth Service (Port 8081)
- User registration & login
- BCrypt password encryption
- JWT token generation
- PostgreSQL persistence

### Product Catalog Service (Port 8082)
- CRUD operations for products
- Category management
- Search & filtering
- OpenAPI documentation
- Spring Data JPA

### Inventory Service (Port 8083)
- Stock tracking & management
- Stock availability checks
- Real-time quantity updates
- Product-inventory mapping

### Order Service (Port 8084)
- Order creation & management
- OpenFeign client to Inventory Service
- Circuit breaker with Resilience4j
- Kafka event publishing
- Transactional processing

### Notification Service (Port 8085)
- Kafka consumer for order events
- Event-driven architecture
- Asynchronous notification handling

### Aggregation Service / BFF (Port 8086)
- Backend for Frontend pattern
- Aggregates data from Product & Inventory services
- Single endpoint for combined data
- Circuit breaker protection

## 🗄️ Database Configuration

Each service has its own PostgreSQL database:

| Service | Database | Port |
|---------|----------|------|
| Auth | auth_db | 5433 |
| Product | product_db | 5434 |
| Inventory | inventory_db | 5435 |
| Order | order_db | 5436 |

## 📦 Generated Files

### Total Generated:
- **9 pom.xml** files (1 parent + 8 services)
- **9 Dockerfile** files (multi-stage builds)
- **9 application.yml** files (configuration)
- **53 Java classes** (service implementation)
- **1 docker-compose.yml** (orchestration)
- **1 Jenkinsfile** (CI/CD pipeline)

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Maven 3.8+
- Java 17+
- PostgreSQL client (optional)

### Build all services:
```bash
cd enterprise-microservices-platform
mvn clean package
```

### Start all services with Docker Compose:
```bash
docker-compose up -d
```

### Services will be available at:
```
Service Registry:      http://localhost:8761/eureka
Config Server:         http://localhost:8888
API Gateway:           http://localhost:8080
Auth Service:          http://localhost:8081/swagger-ui.html
Product Service:       http://localhost:8082/swagger-ui.html
Inventory Service:     http://localhost:8083/swagger-ui.html
Order Service:         http://localhost:8084/swagger-ui.html
Aggregation Service:   http://localhost:8086/swagger-ui.html
```

## 🔐 Authentication

All protected endpoints require a JWT token in the Authorization header:

```bash
Authorization: Bearer <token>
```

### Register user:
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123"}'
```

### Login:
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123"}'
```

## 📊 Architecture Features

### Service-to-Service Communication
- **OpenFeign** for synchronous calls
- **Kafka** for asynchronous events
- **Resilience4j** circuit breakers for fault tolerance

### Layered Architecture
Each service follows enterprise layering:
- **Controller** - REST endpoints
- **Service** - Business logic
- **Repository** - Data access (Spring Data JPA)
- **Entity** - Domain models (JPA)
- **DTO** - Request/Response objects (Java Records)
- **Config** - Configuration classes
- **Exception** - Global exception handling

### API Gateway Responsibilities
- JWT validation & token propagation
- Request routing to services
- Global logging & metrics
- Rate limiting (Resilience4j)
- Cross-cutting concerns

### Database per Service Pattern
- Each service owns its database
- No shared databases
- Event-driven synchronization via Kafka

## 🔄 Event-Driven Flow

1. **Order Service** creates order
2. **Order Service** publishes `OrderCreatedEvent` to Kafka
3. **Notification Service** consumes event
4. **Notification Service** sends notification

## 📈 Code Quality

- **Constructor Injection** - No field injection
- **Global Exception Handler** - @RestControllerAdvice
- **Input Validation** - @Valid annotations
- **Logging** - SLF4J throughout
- **Java 17 Features** - Records for DTOs, sealed enums
- **SonarQube Ready** - Jenkinsfile includes analysis stage

## 🔧 CI/CD Pipeline (Jenkinsfile)

Pipeline stages:
1. **Checkout** - Clone repository
2. **Build** - Maven clean verify
3. **Unit Tests** - Run JUnit tests
4. **Code Quality** - SonarQube analysis
5. **Build Docker Images** - Multi-stage builds
6. **Deploy** - Docker Compose orchestration
7. **Health Check** - Service verification

## 📝 Configuration Management

All services configured via:
- **application.yml** - Service-specific config
- **Config Server** - Centralized management
- **Environment variables** - Docker/K8s overrides

Example override:
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://host:port/db
```

## 🧪 Testing Structure

Each service includes test-ready structure:
- **Unit Tests** - Service layer (@DataJpaTest, Mockito)
- **Integration Tests** - @SpringBootTest
- **Controller Tests** - @WebMvcTest

## 📦 Docker Multi-Stage Build

Optimized Dockerfile pattern:
- **Stage 1** - Maven build environment
- **Stage 2** - Lightweight runtime (openjdk:17-jdk-slim)
- **Result** - Minimal image size (~300MB vs 1GB+)

## 🔗 Key Endpoints

### Auth Service
```
POST   /api/auth/register     - Register user
POST   /api/auth/login        - Login user
```

### Product Service
```
POST   /api/products          - Create product
GET    /api/products/{id}     - Get product
GET    /api/products          - List all products
GET    /api/products/category/{cat} - Filter by category
GET    /api/products/search?name=... - Search products
PUT    /api/products/{id}     - Update product
DELETE /api/products/{id}     - Delete product
```

### Inventory Service
```
POST   /api/inventory/add     - Add stock
PUT    /api/inventory/update  - Update stock
GET    /api/inventory/product/{id} - Get inventory
POST   /api/inventory/check-availability - Check stock
POST   /api/inventory/decrease - Decrease stock
```

### Order Service
```
POST   /api/orders            - Create order
GET    /api/orders/{id}       - Get order
GET    /api/orders/user/{userId} - User orders
GET    /api/orders/product/{productId} - Product orders
```

### Aggregation Service
```
GET    /api/aggregate/product/{id} - Combined product + inventory
```

## 🛡️ Security

- **JWT Authentication** - Stateless token-based auth
- **API Gateway Validation** - Central token verification
- **Context Propagation** - X-User-Name, X-User-Role headers
- **BCrypt Password** - Encrypted password at rest

## 📊 Monitoring & Observability

Health checks available at:
```
GET /actuator/health     - Service health
GET /actuator/metrics    - Service metrics
GET /actuator/info       - Service info
```

## 🚨 Error Handling

Global exception handler (@RestControllerAdvice):
- Consistent error response format
- Proper HTTP status codes
- Detailed logging
- ValidationException handling

## 📖 API Documentation

OpenAPI/Swagger UI available for each service at `/swagger-ui.html`

## 🤝 Contributing

Follow enterprise standards:
- Java 17 conventions
- Layered architecture
- Constructor injection
- Comprehensive logging

## 📝 License

This is a training project for Edureka.

---

**Last Updated:** February 2024
**Platform Version:** 1.0.0
