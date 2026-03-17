FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY common/pom.xml             ./common/
COPY api-gateway/pom.xml        ./api-gateway/
COPY auth-service/pom.xml       ./auth-service/
COPY payment-service/pom.xml    ./payment-service/
COPY account-service/pom.xml    ./account-service/
COPY transaction-service/pom.xml ./transaction-service/

RUN mvn dependency:go-offline -pl transaction-service -am -q

COPY common/src                 ./common/src
COPY transaction-service/src    ./transaction-service/src

RUN mvn clean package -pl transaction-service -am -DskipTests -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring
COPY --from=builder /app/transaction-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]