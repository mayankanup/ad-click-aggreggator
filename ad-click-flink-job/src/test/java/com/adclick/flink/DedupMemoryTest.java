package com.adclick.flink;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DedupMemoryTest {

    @Test
    void dropsDuplicateWithinTtl() {
        DedupMemory dedup = DedupMemory.with48h();
        assertTrue(dedup.keep("imp-1", 1_000L));
        assertFalse(dedup.keep("imp-1", 2_000L));
        assertEquals(1, dedup.size());
    }

    @Test
    void forgetsAfterTtl() {
        DedupMemory dedup = new DedupMemory(1_000L);
        assertTrue(dedup.keep("imp-1", 0L));
        assertTrue(dedup.keep("imp-1", 2_000L)); // expired -> kept again
    }
}
