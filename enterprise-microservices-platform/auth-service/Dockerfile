# Multi-stage build
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -pl auth-service

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/auth-service/target/auth-service-*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
