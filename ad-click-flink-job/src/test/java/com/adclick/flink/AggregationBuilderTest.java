package com.adclick.flink;

import com.adclick.common.AdClickEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AggregationBuilderTest {

    @Test
    void fansOutTo6Rows() {
        AdClickEvent e = new AdClickEvent("ad-1-2", "imp-1", "org-1",
                Instant.parse("2026-09-27T14:37:45Z").toEpochMilli(),
                "u", "ip", "agent", "ref");
        List<AggregateRecord> rows = AggregationBuilder.build(e);
        assertEquals(6, rows.size());
        long minuteAd = rows.stream().filter(r -> r.getGranularity().equals("MINUTE") && r.getDimensionType().equals("AD")).count();
        long minuteOrg = rows.stream().filter(r -> r.getGranularity().equals("MINUTE") && r.getDimensionType().equals("ORG")).count();
        assertEquals(1, minuteAd);
        assertEquals(1, minuteOrg);
        assertTrue(rows.stream().allMatch(r -> r.getCount() == 1));
    }

    @Test
    void upsertUsesOnConflict() {
        assertTrue(AggregationBuilder.upsertSql().contains("ON CONFLICT"));
    }
}
