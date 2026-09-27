package com.adclick.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ClickAggregateRepository repo;

    @Test
    void rendersDashboard() throws Exception {
        when(repo.findTop50ByGranularityAndDimensionTypeAndDimensionIdOrderByWindowStartDesc(
                eq("DAY"), eq("AD"), eq("ad-0-0"))).thenReturn(List.of());
        mvc.perform(get("/").param("granularity", "DAY").param("dimensionType", "AD").param("dimensionId", "ad-0-0"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("rows"));
    }
}
