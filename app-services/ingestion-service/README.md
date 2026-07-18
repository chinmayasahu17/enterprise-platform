# ingestion-service

## Purpose
Accepts external data submissions, persists each raw record in PostgreSQL, and
publishes an `ingestion.events` Kafka event. The downstream processing and
analytics services consume that event when they are added later.

## API

| Method | Path | Description |
|---|---|---|
| POST | `/api/ingestion/records` | Saves a raw submission and emits `ingestion.events` |
| GET | `/actuator/health` | Liveness/readiness health endpoint |
| GET | `/actuator/prometheus` | Prometheus metrics endpoint |

Example request:

```bash
curl -X POST localhost:8180/api/ingestion/records \
  -H 'Content-Type: application/json' \
  -d '{"source":"example-client","payload":"raw-data"}'
```

The gateway protects this path with its JWT filter. Direct calls are useful
only for local service testing.

## Dependencies

- PostgreSQL — stores records in `ingestion_schema.ingestion_records`
- Kafka — produces `ingestion.events`
- Redis: **none**

## Exposed ports

- `8080` — application traffic
- `8081` — actuator health and Prometheus metrics

## Required environment variables

See `.env.example` for the complete configuration.

- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_SCHEMA`
- `KAFKA_BOOTSTRAP_SERVERS`

## Startup dependencies

PostgreSQL and the Kafka broker must be reachable at startup. This service is
intentionally independent of the other application services. Hibernate creates
the configured `DB_SCHEMA` namespace and its table on first startup.

## Build and run locally

```bash
# Build and run tests
mvn clean package

# Run (PostgreSQL and Kafka must be available)
DB_HOST=localhost DB_PORT=5432 DB_NAME=platform DB_USER=platform \
DB_PASSWORD=platform DB_SCHEMA=ingestion_schema \
KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
java -jar target/ingestion-service.jar

# Build the image
docker build -t enterprise-platform-ingestion-service:0.1.0 .
```

## Image naming and versioning

- Image: `<your-registry>/enterprise-platform-ingestion-service:<version>`
- Version independently: update `pom.xml` and tag `ingestion-service/v0.1.0`
