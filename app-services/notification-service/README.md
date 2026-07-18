# notification-service

## Purpose

Consumes `processing.completed` events from Kafka and simulates notification
delivery by writing a log message. It intentionally has no database, cache,
or external notification provider.

## API

| Method | Path | Description |
|---|---|---|
| GET | `/api/notifications/health` | Lightweight application health response |
| GET | `/actuator/health` | Liveness/readiness health endpoint |
| GET | `/actuator/prometheus` | Prometheus metrics endpoint |

## Dependencies

- Kafka — consumes `processing.completed`
- PostgreSQL: **none**
- Redis: **none**

## Exposed ports

- `8080` — application traffic
- `8081` — actuator health and Prometheus metrics

## Required environment variables

See `.env.example` for the complete configuration.

- `KAFKA_BOOTSTRAP_SERVERS`

## Startup dependencies

The Kafka broker must be reachable at startup. This service is independent of
the other application services; it waits for events after it starts.

## Build and run locally

```bash
# Build and run tests
mvn clean package

# Run (Kafka must be available)
KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
java -jar target/notification-service.jar

# Build the image
docker build -t enterprise-platform-notification-service:0.1.0 .
```

## Image naming and versioning

- Image: `<your-registry>/enterprise-platform-notification-service:<version>`
- Version independently: update `pom.xml` and tag `notification-service/v0.1.0`
