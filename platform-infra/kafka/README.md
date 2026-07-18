# kafka

Single broker, **KRaft mode** — no Zookeeper. Saves a whole JVM process and
~300-500MB of RAM versus classic Kafka, which matters on an 8GB machine.

## Image
`apache/kafka:3.7.0` (official KRaft-native image) — avoids needing a
separate Zookeeper image/container entirely.

## Suggested sizing (8GB dev machine)
- Memory: 512Mi request / 768Mi limit
- Storage: 1-2Gi PVC (single partition per topic, short retention — see below)
- `KAFKA_NUM_PARTITIONS=1`, `KAFKA_DEFAULT_REPLICATION_FACTOR=1`, `log.retention.hours=24` — this is a learning platform, not a durability exercise

## Topic catalog

| Topic | Producer | Consumer(s) | Purpose |
|---|---|---|---|
| `ingestion.events` | ingestion-service | processing-service, analytics-processor | Raw data accepted at the edge |
| `processing.completed` | processing-service | notification-service, analytics-processor | Result of core processing |
| `scheduler.triggers` | scheduler-service | processing-service | Cron-driven kickoff of app-tier work |
| `analytics.triggers` | analytics-scheduler | analytics-processor | Cron-driven kickoff of analytics jobs |
| `analytics.aggregates` | analytics-processor | (analytics-sqlclient reads via Postgres, not Kafka) | Computed aggregate events, persisted downstream |

Topics are created by each producing service on first startup
(`auto.create.topics.enable=true` is fine for this project) — no separate
topic-provisioning job needed.

## Environment variables consuming services expect to set
```
KAFKA_BOOTSTRAP_SERVERS=kafka:9092   # k8s Service DNS name
```

## Startup ordering note
Any service that produces or consumes on startup (i.e. all app-services and
analytics-services except analytics-sqlclient) should wait for Kafka's
broker port to accept connections first — your other initContainer candidate,
alongside the Postgres readiness check.

## Not included here
No StatefulSet, PVC, Service, or ConfigMap — those are yours to build.
