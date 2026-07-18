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

RUN addgroup -S platform && adduser -S processing -G platform
COPY --from=build /build/target/processing-service.jar app.jar
RUN chown processing:platform app.jar
USER processing

EXPOSE 8080 8081

ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:+UseSerialGC -XX:MaxMetaspaceSize=96m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
