package com.adclick.service;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "click_aggregates")
@IdClass(ClickAggregate.ClickAggregateId.class)
public class ClickAggregate {

    @Id
    @Column(name = "granularity")
    private String granularity;

    @Id
    @Column(name = "dimension_type")
    private String dimensionType;

    @Id
    @Column(name = "dimension_id")
    private String dimensionId;

    @Id
    @Column(name = "window_start")
    private Instant windowStart;

    @Column(name = "org_id")
    private String orgId;

    @Column(name = "window_end")
    private Instant windowEnd;

    @Column(name = "click_count")
    private long clickCount;

    public ClickAggregate() {
    }

    public String getGranularity() { return granularity; }
    public void setGranularity(String granularity) { this.granularity = granularity; }
    public String getDimensionType() { return dimensionType; }
    public void setDimensionType(String dimensionType) { this.dimensionType = dimensionType; }
    public String getDimensionId() { return dimensionId; }
    public void setDimensionId(String dimensionId) { this.dimensionId = dimensionId; }
    public Instant getWindowStart() { return windowStart; }
    public void setWindowStart(Instant windowStart) { this.windowStart = windowStart; }
    public String getOrgId() { return orgId; }
    public void setOrgId(String orgId) { this.orgId = orgId; }
    public Instant getWindowEnd() { return windowEnd; }
    public void setWindowEnd(Instant windowEnd) { this.windowEnd = windowEnd; }
    public long getClickCount() { return clickCount; }
    public void setClickCount(long clickCount) { this.clickCount = clickCount; }

    public static class ClickAggregateId implements Serializable {
        private String granularity;
        private String dimensionType;
        private String dimensionId;
        private Instant windowStart;

        public ClickAggregateId() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ClickAggregateId)) return false;
            ClickAggregateId that = (ClickAggregateId) o;
            return Objects.equals(granularity, that.granularity)
                    && Objects.equals(dimensionType, that.dimensionType)
                    && Objects.equals(dimensionId, that.dimensionId)
                    && Objects.equals(windowStart, that.windowStart);
        }

        @Override
        public int hashCode() {
            return Objects.hash(granularity, dimensionType, dimensionId, windowStart);
        }
    }
}
