# --- Build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

# --- Runtime stage ---
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S platform && adduser -S gateway -G platform
COPY --from=build /build/target/gateway.jar app.jar
RUN chown gateway:platform app.jar
USER gateway

# App traffic on 8080, actuator (health/prometheus) on a separate port so it
# can be scraped/probed independently of app traffic.
EXPOSE 8080 8081

# Keep heap small and predictable for an 8GB dev machine running many
# services at once. Container memory limit (set at deploy time) should be
# roughly Xmx + 150-200MB for metaspace/threads/native overhead.
ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseSerialGC -XX:MaxMetaspaceSize=96m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
