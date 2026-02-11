# Quick Start Guide

## 1️⃣ Build the Project

```bash
cd enterprise-microservices-platform
mvn clean package -DskipTests
```

## 2️⃣ Start Services with Docker Compose

```bash
docker-compose up -d
```

Wait for all services to be healthy (check logs if needed):
```bash
docker-compose logs -f
```

## 3️⃣ Verify All Services Are Running

```bash
docker-compose ps
```

Expected output: 9 services running (+ PostgreSQL + Kafka + Zookeeper)

## 4️⃣ Register & Login User

### Register:
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@123"
  }'
```

### Login:
```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test@123"
  }' | jq -r '.token')

echo "Token: $TOKEN"
```

## 5️⃣ Create a Product

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "category": "Electronics"
  }'
```

## 6️⃣ Add Stock to Inventory

```bash
curl -X POST http://localhost:8080/inventory/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "productId": 1,
    "quantity": 100
  }'
```

## 7️⃣ Create an Order

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "userId": 1,
    "productId": 1,
    "quantity": 5
  }'
```

## 8️⃣ Get Aggregated Product Info

```bash
curl -X GET http://localhost:8080/aggregate/product/1 \
  -H "Authorization: Bearer $TOKEN" | jq
```

Response includes product + stock availability!

## 🔍 Access Service Dashboards

| Service | URL |
|---------|-----|
| Eureka | http://localhost:8761/eureka |
| Swagger UI (Auth) | http://localhost:8081/swagger-ui.html |
| Swagger UI (Product) | http://localhost:8082/swagger-ui.html |
| Swagger UI (Inventory) | http://localhost:8083/swagger-ui.html |
| Swagger UI (Order) | http://localhost:8084/swagger-ui.html |

## 🛑 Stop All Services

```bash
docker-compose down
```

## 📊 View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f auth-service

# Recent logs
docker-compose logs --tail 100 order-service
```

## 🐛 Troubleshooting

### Services not starting?
```bash
docker-compose logs service-registry
```

### Port already in use?
```bash
# Kill service on port 8080
lsof -ti:8080 | xargs kill -9
```

### PostgreSQL connection issues?
```bash
# Check database status
docker ps | grep postgres
```

## 📝 Notes

- JWT token valid for 24 hours
- Each service has its own PostgreSQL database
- Kafka automatically creates topics
- Service discovery happens automatically via Eureka
- Circuit breakers protect inter-service calls

## 🎯 Next Steps

1. Explore service-to-service calls via OpenFeign
2. Monitor Kafka events in notification-service
3. Test circuit breaker by stopping inventory-service
4. Review API documentation in Swagger UI
5. Check logs for monitoring examples

