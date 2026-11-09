# Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
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
# -XX:MaxRAMPercentage=65 => leave more room for metaspace, threads, native mem
# -XX:+UseSerialGC => lowest memory footprint GC for small containers
# -Xss256k => reduce thread stack size (256KB is safe for most web apps)
# -XX:MaxMetaspaceSize => class metadata cap. 96m fits the Railway plan but this app (JPA +
#    Swagger + WebSocket) can exceed it and die with OutOfMemoryError: Metaspace, so it is
#    configurable: docker-compose.yml sets MAX_METASPACE=256m for local use.
# -XX:ReservedCodeCacheSize=64m => cap JIT code cache
# -XX:+ExitOnOutOfMemoryError => restart container on OOM instead of hanging
# -XX:+TieredCompilation -XX:TieredStopAtLevel=1 => C1-only, saves ~50MB native
ENV MAX_METASPACE=96m
ENTRYPOINT ["sh", "-c", "exec java \
  -XX:MaxRAMPercentage=65.0 \
  -XX:+UseSerialGC \
  -Xss256k \
  -XX:MaxMetaspaceSize=${MAX_METASPACE} \
  -XX:ReservedCodeCacheSize=64m \
  -XX:+TieredCompilation \
  -XX:TieredStopAtLevel=1 \
  -XX:+ExitOnOutOfMemoryError \
  -Djava.security.egd=file:/dev/./urandom \
  -jar app.jar"]