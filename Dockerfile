# Build stage
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first (cached layer)
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage - use JRE instead of JDK to save ~100MB+ RAM
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# JVM memory tuning for Railway (512MB plan)
# -XX:MaxRAMPercentage=75 => use at most 75% of container memory for heap
# -XX:+UseG1GC => G1 garbage collector, better for constrained memory
# -XX:+UseStringDeduplication => deduplicate identical strings to save heap
# -Xss512k => reduce thread stack size from default 1MB to 512KB
# -XX:MaxMetaspaceSize=128m => cap metaspace to prevent unbounded growth
# -XX:+ExitOnOutOfMemoryError => restart container on OOM instead of hanging
ENTRYPOINT ["java", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-XX:+UseStringDeduplication", \
  "-Xss512k", \
  "-XX:MaxMetaspaceSize=128m", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]