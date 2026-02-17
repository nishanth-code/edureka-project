# Multi-stage build
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -pl notification-service

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/notification-service/target/notification-service-*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
