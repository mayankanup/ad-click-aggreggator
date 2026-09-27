package com.adclick.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ClickAggregateRepositoryTest {

    @Autowired
    private ClickAggregateRepository repo;

    @Test
    void rangeQueryOrdersByWindowStart() {
        save("DAY", "AD", "ad-0-1", "2026-09-02T00:00:00Z", 2);
        save("DAY", "AD", "ad-0-1", "2026-09-01T00:00:00Z", 5);
        List<ClickAggregate> rows = repo
                .findByGranularityAndDimensionTypeAndDimensionIdAndWindowStartBetweenOrderByWindowStartAsc(
                        "DAY", "AD", "ad-0-1",
                        Instant.parse("2026-09-01T00:00:00Z"),
                        Instant.parse("2026-09-03T00:00:00Z"));
        assertEquals(2, rows.size());
        assertEquals(5, rows.get(0).getClickCount());
    }

    private void save(String g, String dimType, String dimId, String start, long count) {
        ClickAggregate r = new ClickAggregate();
        r.setGranularity(g);
        r.setDimensionType(dimType);
        r.setDimensionId(dimId);
        r.setOrgId("org-0");
        r.setWindowStart(Instant.parse(start));
        r.setWindowEnd(Instant.parse(start).plusSeconds(86400));
        r.setClickCount(count);
        repo.save(r);
    }
}
