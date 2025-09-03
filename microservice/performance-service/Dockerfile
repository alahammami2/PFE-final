# Multi-stage build for performance-service
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar
ENV JAVA_OPTS=""
EXPOSE 8083
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
