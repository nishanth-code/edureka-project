# Multi-stage build
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -pl api-gateway

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/api-gateway/target/api-gateway-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
