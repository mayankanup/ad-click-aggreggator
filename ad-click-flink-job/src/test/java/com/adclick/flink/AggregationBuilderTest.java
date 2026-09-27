package com.adclick.flink;

import com.adclick.common.AdClickEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AggregationBuilderTest {

    @Test
    void fansOutTo10Rows() {
        AdClickEvent e = new AdClickEvent("ad-1-2", "imp-1", "org-1",
                Instant.parse("2026-09-27T14:37:45Z").toEpochMilli(),
                "u", "ip", "agent", "ref");
        List<AggregateRecord> rows = AggregationBuilder.build(e);
        assertEquals(10, rows.size());
        long minuteAd = rows.stream().filter(r -> r.getGranularity().equals("MINUTE") && r.getDimensionType().equals("AD")).count();
        long minuteOrg = rows.stream().filter(r -> r.getGranularity().equals("MINUTE") && r.getDimensionType().equals("ORG")).count();
        assertEquals(1, minuteAd);
        assertEquals(1, minuteOrg);
        assertTrue(rows.stream().allMatch(r -> r.getCount() == 1));
    }

    @Test
    void monthYearBoundariesAreCalendarCorrect() {
        AdClickEvent e = new AdClickEvent("ad-0-1", "imp-2", "org-0",
                Instant.parse("2026-01-15T10:00:00Z").toEpochMilli(),
                "u", "ip", "agent", "ref");
        List<AggregateRecord> rows = AggregationBuilder.build(e);
        AggregateRecord monthAd = rows.stream()
                .filter(r -> r.getGranularity().equals("MONTH") && r.getDimensionType().equals("AD"))
                .findFirst().orElseThrow();
        assertEquals(Instant.parse("2026-01-01T00:00:00Z"), monthAd.getWindowStart());
        assertEquals(Instant.parse("2026-02-01T00:00:00Z"), monthAd.getWindowEnd());
        AggregateRecord yearAd = rows.stream()
                .filter(r -> r.getGranularity().equals("YEAR") && r.getDimensionType().equals("AD"))
                .findFirst().orElseThrow();
        assertEquals(Instant.parse("2026-01-01T00:00:00Z"), yearAd.getWindowStart());
        assertEquals(Instant.parse("2027-01-01T00:00:00Z"), yearAd.getWindowEnd());
    }

    @Test
    void upsertUsesOnConflict() {
        assertTrue(AggregationBuilder.upsertSql().contains("ON CONFLICT"));
    }
}
