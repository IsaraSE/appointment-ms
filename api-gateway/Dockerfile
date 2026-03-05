# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY user-service user-service
COPY api-gateway api-gateway
COPY booking-service booking-service
COPY notification-service notification-service
COPY appointment-service appointment-service

RUN mvn clean package -pl api-gateway -am -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
VOLUME /tmp
COPY --from=build /app/api-gateway/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring
ENTRYPOINT ["java","-jar","/app.jar"]
