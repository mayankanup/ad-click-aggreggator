package com.adclick.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ClickAggregateRepository extends JpaRepository<ClickAggregate, ClickAggregate.ClickAggregateId> {

    List<ClickAggregate> findByGranularityAndDimensionTypeAndDimensionIdAndWindowStartBetweenOrderByWindowStartAsc(
            String granularity, String dimensionType, String dimensionId, Instant from, Instant to);

    List<ClickAggregate> findTop50ByGranularityAndDimensionTypeAndDimensionIdOrderByWindowStartDesc(
            String granularity, String dimensionType, String dimensionId);
}
