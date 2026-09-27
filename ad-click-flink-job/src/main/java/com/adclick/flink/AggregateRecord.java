package com.adclick.flink;

import java.time.Instant;
import java.util.Objects;

/** Flink window output row mapped to click_aggregates table. */
public class AggregateRecord {

    private String granularity; // MINUTE, HOUR, DAY
    private String dimensionType; // AD, ORG
    private String dimensionId;
    private String orgId; // set for AD level, null for ORG level rollup key itself
    private Instant windowStart;
    private Instant windowEnd;
    private long count;

    public AggregateRecord() {
    }

    public AggregateRecord(String granularity, String dimensionType, String dimensionId,
                           String orgId, Instant windowStart, Instant windowEnd, long count) {
        this.granularity = granularity;
        this.dimensionType = dimensionType;
        this.dimensionId = dimensionId;
        this.orgId = orgId;
        this.windowStart = windowStart;
        this.windowEnd = windowEnd;
        this.count = count;
    }

    public String getGranularity() { return granularity; }
    public String getDimensionType() { return dimensionType; }
    public String getDimensionId() { return dimensionId; }
    public String getOrgId() { return orgId; }
    public Instant getWindowStart() { return windowStart; }
    public Instant getWindowEnd() { return windowEnd; }
    public long getCount() { return count; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregateRecord)) return false;
        AggregateRecord that = (AggregateRecord) o;
        return count == that.count && Objects.equals(granularity, that.granularity)
                && Objects.equals(dimensionType, that.dimensionType)
                && Objects.equals(dimensionId, that.dimensionId)
                && Objects.equals(windowStart, that.windowStart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(granularity, dimensionType, dimensionId, windowStart);
    }
}
