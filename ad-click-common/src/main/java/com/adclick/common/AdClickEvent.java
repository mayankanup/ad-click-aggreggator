package com.adclick.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Canonical ad-click event sent by standalone Kafka producers.
 * eventTimeMillis is the event-time used by Flink watermarks.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdClickEvent {

    @JsonProperty("adId")
    private String adId;

    @JsonProperty("adImpressionId")
    private String adImpressionId;

    @JsonProperty("adOrgId")
    private String adOrgId;

    /** Event-time in epoch millis (UTC). */
    @JsonProperty("eventTimeMillis")
    private long eventTimeMillis;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("ip")
    private String ip;

    @JsonProperty("userAgent")
    private String userAgent;

    @JsonProperty("referrer")
    private String referrer;

    public AdClickEvent() {
    }

    public AdClickEvent(String adId, String adImpressionId, String adOrgId, long eventTimeMillis,
                        String userId, String ip, String userAgent, String referrer) {
        this.adId = adId;
        this.adImpressionId = adImpressionId;
        this.adOrgId = adOrgId;
        this.eventTimeMillis = eventTimeMillis;
        this.userId = userId;
        this.ip = ip;
        this.userAgent = userAgent;
        this.referrer = referrer;
    }

    public String getAdId() { return adId; }
    public void setAdId(String adId) { this.adId = adId; }

    public String getAdImpressionId() { return adImpressionId; }
    public void setAdImpressionId(String adImpressionId) { this.adImpressionId = adImpressionId; }

    public String getAdOrgId() { return adOrgId; }
    public void setAdOrgId(String adOrgId) { this.adOrgId = adOrgId; }

    public long getEventTimeMillis() { return eventTimeMillis; }
    public void setEventTimeMillis(long eventTimeMillis) { this.eventTimeMillis = eventTimeMillis; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getReferrer() { return referrer; }
    public void setReferrer(String referrer) { this.referrer = referrer; }
}
