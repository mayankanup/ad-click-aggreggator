package com.adclick.flink;

import com.adclick.common.AdClickEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure builder: one deduplicated event fans out to 10 rows
 * (MINUTE/HOUR/DAY/MONTH/YEAR x AD/ORG). No Flink runtime needed -> easy unit tests.
 */
public final class AggregationBuilder {

    private AggregationBuilder() {
    }

    public static List<AggregateRecord> build(AdClickEvent e) {
        Instant ts = Instant.ofEpochMilli(e.getEventTimeMillis());
        Instant minute = WindowTruncator.truncateToMinute(ts);
        Instant hour = WindowTruncator.truncateToHour(ts);
        Instant day = WindowTruncator.truncateToDay(ts);
        Instant month = WindowTruncator.truncateToMonth(ts);
        Instant year = WindowTruncator.truncateToYear(ts);
        List<AggregateRecord> out = new ArrayList<>(10);
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
        out.add(new AggregateRecord("MONTH", "AD", e.getAdId(), e.getAdOrgId(),
                month, nextMonth(month), 1));
        out.add(new AggregateRecord("MONTH", "ORG", e.getAdOrgId(), e.getAdOrgId(),
                month, nextMonth(month), 1));
        out.add(new AggregateRecord("YEAR", "AD", e.getAdId(), e.getAdOrgId(),
                year, nextYear(year), 1));
        out.add(new AggregateRecord("YEAR", "ORG", e.getAdOrgId(), e.getAdOrgId(),
                year, nextYear(year), 1));
        return out;
    }

    static Instant nextMonth(Instant monthStart) {
        return monthStart.atZone(java.time.ZoneOffset.UTC).plusMonths(1).toInstant();
    }

    static Instant nextYear(Instant yearStart) {
        return yearStart.atZone(java.time.ZoneOffset.UTC).plusYears(1).toInstant();
    }

    public static String upsertSql() {
        return "INSERT INTO click_aggregates (granularity, dimension_type, dimension_id, org_id, window_start, window_end, click_count) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT (granularity, dimension_type, dimension_id, window_start) DO UPDATE SET "
                + "click_count = click_aggregates.click_count + EXCLUDED.click_count, window_end = EXCLUDED.window_end";
    }
}
