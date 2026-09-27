package com.adclick.flink;

import java.util.Objects;

/**
 * Flink window output row mapped to click_aggregates table.
 * Window bounds are epoch millis (long) so the record stays Kryo/POJO-friendly
 * on any JDK (java.time.Instant breaks Kryo without --add-opens java.time).
 */
public class AggregateRecord {

    private String granularity; // MINUTE, HOUR, DAY, MONTH, YEAR
    private String dimensionType; // AD, ORG
    private String dimensionId;
    private String orgId; // set for AD level, same as dimensionId for ORG level
    private long windowStartMillis;
    private long windowEndMillis;
    private long count;

    public AggregateRecord() {
    }

    public AggregateRecord(String granularity, String dimensionType, String dimensionId,
                           String orgId, long windowStartMillis, long windowEndMillis, long count) {
        this.granularity = granularity;
        this.dimensionType = dimensionType;
        this.dimensionId = dimensionId;
        this.orgId = orgId;
        this.windowStartMillis = windowStartMillis;
        this.windowEndMillis = windowEndMillis;
        this.count = count;
    }

    public String getGranularity() { return granularity; }
    public String getDimensionType() { return dimensionType; }
    public String getDimensionId() { return dimensionId; }
    public String getOrgId() { return orgId; }
    public long getWindowStartMillis() { return windowStartMillis; }
    public long getWindowEndMillis() { return windowEndMillis; }
    public long getCount() { return count; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AggregateRecord)) return false;
        AggregateRecord that = (AggregateRecord) o;
        return count == that.count && windowStartMillis == that.windowStartMillis
                && Objects.equals(granularity, that.granularity)
                && Objects.equals(dimensionType, that.dimensionType)
                && Objects.equals(dimensionId, that.dimensionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(granularity, dimensionType, dimensionId, windowStartMillis);
    }
}
