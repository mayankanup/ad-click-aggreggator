package com.adclick.service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/** Server-rendered dashboard: filter by granularity/dimension, Chart.js renders rows. */
@Controller
public class DashboardController {

    private final ClickAggregateRepository repo;

    public DashboardController(ClickAggregateRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/")
    public String dashboard(
            @RequestParam(defaultValue = "DAY") String granularity,
            @RequestParam(defaultValue = "AD") String dimensionType,
            @RequestParam(defaultValue = "ad-0-0") String dimensionId,
            Model model) {
        List<ClickAggregate> rows = repo
                .findTop50ByGranularityAndDimensionTypeAndDimensionIdOrderByWindowStartDesc(
                        granularity.toUpperCase(), dimensionType.toUpperCase(), dimensionId);
        model.addAttribute("granularity", granularity.toUpperCase());
        model.addAttribute("dimensionType", dimensionType.toUpperCase());
        model.addAttribute("dimensionId", dimensionId);
        model.addAttribute("rows", rows);
        return "dashboard";
    }
}
