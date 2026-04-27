# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY . .

RUN mvn clean package -pl api-gateway -am -DskipTests

# Runtime
FROM eclipse-temurin:17-jdk-alpine
RUN addgroup -S spring && adduser -S spring -G spring
VOLUME /tmp
COPY --from=build /app/api-gateway/target/*.jar app.jar
RUN chown spring:spring app.jar
USER spring
ENTRYPOINT ["java","-jar","/app.jar"]
