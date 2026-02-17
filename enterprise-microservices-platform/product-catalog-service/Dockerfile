# Multi-stage build
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -pl product-catalog-service

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/product-catalog-service/target/product-catalog-service-*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
