package com.adclick.producer;

import com.adclick.common.AdClickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Fixed topology: 10 orgs, each with 20 ads.
 * org ids: org-0..org-9, ad ids: ad-&lt;orgIdx&gt;-&lt;adIdx&gt; (e.g. ad-3-17).
 */
public final class RandomClickGenerator {

    public static final List<String> ORG_IDS;
    public static final Map<String, List<String>> ORG_TO_ADS;

    static {
        List<String> orgs = new ArrayList<>(10);
        Map<String, List<String>> map = new LinkedHashMap<>();
        for (int o = 0; o < 10; o++) {
            String orgId = "org-" + o;
            orgs.add(orgId);
            List<String> ads = new ArrayList<>(20);
            for (int a = 0; a < 20; a++) {
                ads.add("ad-" + o + "-" + a);
            }
            map.put(orgId, Collections.unmodifiableList(ads));
        }
        ORG_IDS = Collections.unmodifiableList(orgs);
        ORG_TO_ADS = Collections.unmodifiableMap(map);
    }

    private RandomClickGenerator() {
    }

    public static AdClickEvent next(List<String> pastImpressions, double duplicateRate) {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        String impressionId;
        if (!pastImpressions.isEmpty() && rnd.nextDouble() < duplicateRate) {
            // Intentional duplicate: reuse a previous impression id.
            impressionId = pastImpressions.get(rnd.nextInt(pastImpressions.size()));
        } else {
            impressionId = UUID.randomUUID().toString();
            pastImpressions.add(impressionId);
        }
        String orgId = ORG_IDS.get(rnd.nextInt(ORG_IDS.size()));
        List<String> ads = ORG_TO_ADS.get(orgId);
        String adId = ads.get(rnd.nextInt(ads.size()));
        return new AdClickEvent(
                adId,
                impressionId,
                orgId,
                System.currentTimeMillis(),
                "user-" + rnd.nextInt(10000),
                "192.168.1." + rnd.nextInt(1, 255),
                "producer-loadgen/1.0",
                "https://example.com/page-" + rnd.nextInt(50));
    }
}
