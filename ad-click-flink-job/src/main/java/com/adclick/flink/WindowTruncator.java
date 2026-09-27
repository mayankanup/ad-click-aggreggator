package com.adclick.flink;

import java.time.Instant;
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
}
