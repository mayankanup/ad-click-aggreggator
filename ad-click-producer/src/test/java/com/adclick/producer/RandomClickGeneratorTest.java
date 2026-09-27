package com.adclick.producer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomClickGeneratorTest {

    @Test
    void topologyIs10OrgsWith20AdsEach() {
        assertEquals(10, RandomClickGenerator.ORG_IDS.size());
        for (String org : RandomClickGenerator.ORG_IDS) {
            assertEquals(20, RandomClickGenerator.ORG_TO_ADS.get(org).size());
        }
    }

    @Test
    void generatedAdBelongsToOrg() {
        for (int i = 0; i < 50; i++) {
            var e = RandomClickGenerator.next(new java.util.ArrayList<>(), 0.0);
            assertTrue(RandomClickGenerator.ORG_TO_ADS.get(e.getAdOrgId()).contains(e.getAdId()));
        }
    }
}
