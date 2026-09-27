package com.adclick.flink;

import com.adclick.common.AdClickEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure builder: one deduplicated event fans out to 6 rows
 * (MINUTE/HOUR/DAY x AD/ORG). No Flink runtime needed -> easy unit tests.
 */
public final class AggregationBuilder {

    private AggregationBuilder() {
    }

    public static List<AggregateRecord> build(AdClickEvent e) {
        Instant ts = Instant.ofEpochMilli(e.getEventTimeMillis());
        Instant minute = WindowTruncator.truncateToMinute(ts);
        Instant hour = WindowTruncator.truncateToHour(ts);
        Instant day = WindowTruncator.truncateToDay(ts);
        List<AggregateRecord> out = new ArrayList<>(6);
        out.add(new AggregateRecord("MINUTE", "AD", e.getAdId(), e.getAdOrgId(),
                minute, minute.plus(Duration.ofMinutes(1)), 1));
        out.add(new AggregateRecord("MINUTE", "ORG", e.getAdOrgId(), e.getAdOrgId(),
                minute, minute.plus(Duration.ofMinutes(1)), 1));
        out.add(new AggregateRecord("HOUR", "AD", e.getAdId(), e.getAdOrgId(),
                hour, hour.plus(Duration.ofHours(1)), 1));
        out.add(new AggregateRecord("HOUR", "ORG", e.getAdOrgId(), e.getAdOrgId(),
                hour, hour.plus(Duration.ofHours(1)), 1));
        out.add(new AggregateRecord("DAY", "AD", e.getAdId(), e.getAdOrgId(),
                day, day.plus(Duration.ofDays(1)), 1));
        out.add(new AggregateRecord("DAY", "ORG", e.getAdOrgId(), e.getAdOrgId(),
                day, day.plus(Duration.ofDays(1)), 1));
        return out;
    }

    public static String upsertSql() {
        return "INSERT INTO click_aggregates (granularity, dimension_type, dimension_id, org_id, window_start, window_end, click_count) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT (granularity, dimension_type, dimension_id, window_start) DO UPDATE SET "
                + "click_count = click_aggregates.click_count + EXCLUDED.click_count, window_end = EXCLUDED.window_end";
    }
}
