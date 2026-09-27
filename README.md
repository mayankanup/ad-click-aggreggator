# ad-click-aggreggator

Real-time ad-click aggregation: Kafka -> Flink (dedup + MINUTE/HOUR/DAY/MONTH/YEAR x AD/ORG) -> Postgres -> Spring Boot API + Thymeleaf UI.

## Services

| Service | Port | Notes |
|---|---|---|
| Kafka (external) | 29092 | topic `ad-clicks` |
| Flink UI | 8081 | submit `ad-click-flink-job` jar |
| Backend API + UI | 8080 | `/`, `/api/aggregates`, `/api/aggregates/latest` |
| Postgres | 5432 | db `adclick`, table `click_aggregates` |

## E2E

```bash
# 1. Infra + backend
docker compose up -d --build
docker compose ps

# 2. Build Flink job jar
mvn -B -q -pl ad-click-flink-job -am package

# 3. Submit job (Flink UI :8081 -> Submit New Job, upload ad-click-flink-job-1.0.0.jar,
#    main class com.adclick.flink.AdClickFlinkJob, env KAFKA_BOOTSTRAP_SERVERS=kafka:9092)

# 4. Generate load (10 orgs x 20 ads, 1-5s jitter, 5% duplicates)
mvn -pl ad-click-producer spring-boot:run "-Dspring-boot.run.arguments=--count=50 --duplicate-rate=0.05 --min-sleep-ms=1000 --max-sleep-ms=5000"

# 5. View
# UI: http://localhost:8080/?granularity=DAY&dimensionType=AD&dimensionId=ad-0-0
# API: http://localhost:8080/api/aggregates/latest?granularity=DAY&dimensionType=AD&dimensionId=ad-0-0
```

## Recovery (current)

Flink checkpoints every 60s (see Story 8 for hardening: durable checkpoints, restart strategy, transactional sink).
