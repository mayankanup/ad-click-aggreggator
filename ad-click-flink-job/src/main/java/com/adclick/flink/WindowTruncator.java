package com.adclick.flink;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/** Pure window-truncation helpers (UTC). Fully unit-tested, no Flink runtime needed. */
public final class WindowTruncator {

    private WindowTruncator() {
    }

    public static Instant truncateToMinute(Instant t) {
        return t.truncatedTo(ChronoUnit.MINUTES);
    }

    public static Instant truncateToHour(Instant t) {
        return t.truncatedTo(ChronoUnit.HOURS);
    }

    public static Instant truncateToDay(Instant t) {
        return t.truncatedTo(ChronoUnit.DAYS);
    }

    public static Instant truncateToMonth(Instant t) {
        LocalDate d = t.atZone(ZoneOffset.UTC).toLocalDate();
        return d.withDayOfMonth(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    public static Instant truncateToYear(Instant t) {
        LocalDate d = t.atZone(ZoneOffset.UTC).toLocalDate();
        return d.withDayOfYear(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
