package com.adclick.flink;

import com.adclick.common.AdClickEvent;
import com.adclick.common.AdClickTopics;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;
import java.util.List;

/**
 * Event-time pipeline:
 * Kafka(ad-clicks) -> JSON parse -> watermarks(30s) -> keyBy(impressionId)
 * -> dedup(48h TTL) -> fan-out MINUTE/HOUR/DAY x AD/ORG -> Postgres upsert.
 */
public class AdClickFlinkJob {

    public static void main(String[] args) throws Exception {
        String kafkaBootstrap = env("KAFKA_BOOTSTRAP_SERVERS", "kafka:9092");
        String jdbcUrl = env("POSTGRES_JDBC_URL", "jdbc:postgresql://postgres:5432/adclick");
        String dbUser = env("POSTGRES_USER", "adclick");
        String dbPassword = env("POSTGRES_PASSWORD", "adclick");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(60_000);

        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(kafkaBootstrap)
                .setTopics(AdClickTopics.CLICKS)
                .setGroupId("ad-click-flink")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();

        ObjectMapper mapper = new ObjectMapper();

        DataStream<AdClickEvent> parsed = env.fromSource(source,
                        WatermarkStrategy.noWatermarks(), "kafka-ad-clicks")
                .map(json -> mapper.readValue(json, AdClickEvent.class))
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<AdClickEvent>forBoundedOutOfOrderness(Duration.ofSeconds(30))
                        .withTimestampAssigner((e, ts) -> e.getEventTimeMillis()));

        DataStream<AdClickEvent> deduped = parsed
                .keyBy(AdClickEvent::getAdImpressionId)
                .process(new DedupFilterFunction())
                .name("dedup-by-impression-48h");

        DataStream<AggregateRecord> aggregates = deduped
                .flatMap((AdClickEvent e, org.apache.flink.util.Collector<AggregateRecord> out) -> {
                    List<AggregateRecord> rows = AggregationBuilder.build(e);
                    for (AggregateRecord r : rows) {
                        out.collect(r);
                    }
                })
                .returns(AggregateRecord.class)
                .name("fanout-minute-hour-day");

        aggregates.addSink(new PostgresUpsertSink(jdbcUrl, dbUser, dbPassword))
                .name("postgres-upsert");

        env.execute("ad-click-aggregator");
    }

    private static String env(String key, String def) {
        String v = System.getenv(key);
        return v != null && !v.isBlank() ? v : def;
    }
}
