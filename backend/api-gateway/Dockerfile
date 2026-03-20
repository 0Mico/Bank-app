FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .

# All module poms (needed for dependency resolution)
COPY common/pom.xml             ./common/
COPY api-gateway/pom.xml        ./api-gateway/
COPY auth-service/pom.xml       ./auth-service/
COPY payment-service/pom.xml    ./payment-service/
COPY account-service/pom.xml    ./account-service/
COPY transaction-service/pom.xml ./transaction-service/

# Download dependencies (cached until a pom changes)
RUN mvn dependency:go-offline -pl api-gateway -am -q

# Copy all source
COPY common/src             ./common/src
COPY api-gateway/src        ./api-gateway/src

# Build
RUN mvn clean package -pl api-gateway -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring
COPY --from=builder /app/api-gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]