# Multi-stage build
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -pl service-registry

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/service-registry/target/service-registry-*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
