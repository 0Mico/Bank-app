FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY common/pom.xml             ./common/
COPY api-gateway/pom.xml        ./api-gateway/
COPY auth-service/pom.xml       ./auth-service/
COPY payment-service/pom.xml    ./payment-service/
COPY account-service/pom.xml    ./account-service/
COPY transaction-service/pom.xml ./transaction-service/

RUN mvn dependency:go-offline -pl account-service -am

COPY common/src             ./common/src
COPY account-service/src    ./account-service/src

RUN mvn clean package -pl account-service -am -DskipTests


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
USER spring
COPY --from=builder /app/account-service/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]