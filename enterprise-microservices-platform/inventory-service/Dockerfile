# Multi-stage build
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -pl inventory-service

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/inventory-service/target/inventory-service-*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
