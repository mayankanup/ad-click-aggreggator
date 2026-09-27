package com.adclick.flink;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WindowTruncatorTest {

    @Test
    void truncates() {
        Instant t = Instant.parse("2026-09-27T14:37:45.123Z");
        assertEquals(Instant.parse("2026-09-27T14:37:00Z"), WindowTruncator.truncateToMinute(t));
        assertEquals(Instant.parse("2026-09-27T14:00:00Z"), WindowTruncator.truncateToHour(t));
        assertEquals(Instant.parse("2026-09-27T00:00:00Z"), WindowTruncator.truncateToDay(t));
        assertEquals(Instant.parse("2026-09-01T00:00:00Z"), WindowTruncator.truncateToMonth(t));
        assertEquals(Instant.parse("2026-01-01T00:00:00Z"), WindowTruncator.truncateToYear(t));
    }
}
