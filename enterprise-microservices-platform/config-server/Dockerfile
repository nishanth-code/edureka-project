# Multi-stage build
FROM maven:3.8.1-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests -pl config-server

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/config-server/target/config-server-*.jar app.jar
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
