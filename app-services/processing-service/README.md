# processing-service

## Purpose

Consumes `ingestion.events` from Kafka, simulates a simple processing step, persists the processed result in PostgreSQL, and publishes a `processing.completed` event.

## API

| Method | Path                           | Description                        |
| ------ | ------------------------------ | ---------------------------------- |
| GET    | `/api/processing/records`      | Lists processed records            |
| GET    | `/api/processing/records/{id}` | Fetches a processed record by id   |
| GET    | `/actuator/health`             | Liveness/readiness health endpoint |
| GET    | `/actuator/prometheus`         | Prometheus metrics endpoint        |

## Dependencies

- PostgreSQL — stores records in `processing_schema.processing_records`
- Kafka — consumes `ingestion.events` and publishes `processing.completed`
- Redis: **none**

## Exposed ports

- `8080` — application traffic
- `8081` — actuator health and Prometheus metrics

## Required environment variables

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_SCHEMA`
- `KAFKA_BOOTSTRAP_SERVERS`

## Startup dependencies

PostgreSQL and the Kafka broker must be reachable at startup. Hibernate creates the configured `DB_SCHEMA` namespace and its table on first startup.

## Build and run locally

```bash
mvn clean package

DB_HOST=localhost DB_PORT=5432 DB_NAME=platform DB_USER=platform \
DB_PASSWORD=platform DB_SCHEMA=processing_schema \
KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
java -jar target/processing-service.jar
```

## Image naming and versioning

- Image: `<your-registry>/enterprise-platform-processing-service:<version>`
- Version independently: update `pom.xml` and tag `processing-service/v0.1.0`
