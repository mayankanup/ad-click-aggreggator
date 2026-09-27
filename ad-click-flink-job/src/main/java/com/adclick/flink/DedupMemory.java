package com.adclick.flink;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pure in-memory mirror of the Flink dedup logic for unit tests:
 * remembers impressionIds for 48h (configurable). Returns true if first-seen.
 */
public class DedupMemory {

    private final long ttlMillis;
    private final Map<String, Long> seen = new LinkedHashMap<>();

    public DedupMemory(long ttlMillis) {
        this.ttlMillis = ttlMillis;
    }

    public static DedupMemory with48h() {
        return new DedupMemory(48L * 60 * 60 * 1000);
    }

    /** @return true if this impression should be kept (not a duplicate). */
    public synchronized boolean keep(String impressionId, long eventTimeMillis) {
        evict(eventTimeMillis);
        if (impressionId == null) {
            return true;
        }
        if (seen.containsKey(impressionId)) {
            return false;
        }
        seen.put(impressionId, eventTimeMillis);
        return true;
    }

    private void evict(long now) {
        seen.entrySet().removeIf(e -> now - e.getValue() > ttlMillis);
    }

    public synchronized int size() {
        return seen.size();
    }
}
