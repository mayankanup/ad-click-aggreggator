package com.adclick.service;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

/**
 * Read API over Flink-produced click_aggregates.
 * Example: GET /api/aggregates?granularity=DAY&dimensionType=AD&dimensionId=ad-0-1&from=..&to=..
 */
@RestController
@RequestMapping("/api/aggregates")
public class AggregateController {

    private final ClickAggregateRepository repo;

    public AggregateController(ClickAggregateRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<ClickAggregate> range(
            @RequestParam String granularity,
            @RequestParam String dimensionType,
            @RequestParam String dimensionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return repo.findByGranularityAndDimensionTypeAndDimensionIdAndWindowStartBetweenOrderByWindowStartAsc(
                granularity.toUpperCase(), dimensionType.toUpperCase(), dimensionId, from, to);
    }

    @GetMapping("/latest")
    public List<ClickAggregate> latest(
            @RequestParam String granularity,
            @RequestParam String dimensionType,
            @RequestParam String dimensionId) {
        return repo.findTop50ByGranularityAndDimensionTypeAndDimensionIdOrderByWindowStartDesc(
                granularity.toUpperCase(), dimensionType.toUpperCase(), dimensionId);
    }
}
