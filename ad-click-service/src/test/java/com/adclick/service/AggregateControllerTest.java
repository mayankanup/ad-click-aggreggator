package com.adclick.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AggregateController.class)
class AggregateControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ClickAggregateRepository repo;

    @Test
    void rangeReturnsRows() throws Exception {
        ClickAggregate row = new ClickAggregate();
        row.setGranularity("DAY");
        row.setDimensionType("AD");
        row.setDimensionId("ad-0-1");
        row.setWindowStart(Instant.parse("2026-09-27T00:00:00Z"));
        row.setWindowEnd(Instant.parse("2026-09-28T00:00:00Z"));
        row.setClickCount(7);
        when(repo.findByGranularityAndDimensionTypeAndDimensionIdAndWindowStartBetweenOrderByWindowStartAsc(
                eq("DAY"), eq("AD"), eq("ad-0-1"), any(), any())).thenReturn(List.of(row));

        mvc.perform(get("/api/aggregates")
                        .param("granularity", "DAY")
                        .param("dimensionType", "AD")
                        .param("dimensionId", "ad-0-1")
                        .param("from", "2026-09-01T00:00:00Z")
                        .param("to", "2026-09-28T00:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clickCount").value(7));
    }
}
