package com.adclick.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdClickEventJsonTest {

    @Test
    void roundTrip() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        AdClickEvent e = new AdClickEvent("ad-1-2", "imp-1", "org-1", 1700000000000L,
                "user-1", "127.0.0.1", "agent", "https://example.com/");
        String json = mapper.writeValueAsString(e);
        AdClickEvent back = mapper.readValue(json, AdClickEvent.class);
        assertEquals("ad-1-2", back.getAdId());
        assertEquals("imp-1", back.getAdImpressionId());
        assertEquals("org-1", back.getAdOrgId());
        assertEquals(1700000000000L, back.getEventTimeMillis());
    }
}
