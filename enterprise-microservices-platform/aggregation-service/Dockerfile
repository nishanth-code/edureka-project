# Multi-stage build
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -pl aggregation-service

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/aggregation-service/target/aggregation-service-*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]
