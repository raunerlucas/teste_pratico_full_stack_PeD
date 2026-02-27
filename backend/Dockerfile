# ============================
# Stage 1: Build
# ============================
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw dependency:resolve -B
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# ============================
# Stage 2: Runtime
# ============================
FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api-docs || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
