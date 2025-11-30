## Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /workspace

# Copy only build descriptors first to leverage Docker layer caching
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Pre-fetch dependencies (optional but speeds up subsequent builds)
RUN ./mvnw -B -q dependency:go-offline

# Copy the rest of the source
COPY src src

# Build the executable jar
RUN ./mvnw -B -q package -DskipTests

## Runtime stage
FROM eclipse-temurin:17-jre-alpine

ENV APP_HOME=/opt/app \
    JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=default

WORKDIR ${APP_HOME}

COPY --from=builder /workspace/target/relativesHelp-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]

