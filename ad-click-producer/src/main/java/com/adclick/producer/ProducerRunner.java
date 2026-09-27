package com.adclick.producer;

import com.adclick.common.AdClickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * CLI entrypoint. Examples:
 *   java -jar ad-click-producer-1.0.0.jar --count=100 --duplicate-rate=0.05
 *   java -jar ad-click-producer-1.0.0.jar --count=2147483647 --min-sleep-ms=1000 --max-sleep-ms=5000
 */
@Component
public class ProducerRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ProducerRunner.class);

    private final ClickProducerService producer;

    public ProducerRunner(ClickProducerService producer) {
        this.producer = producer;
    }

    @Override
    public void run(ApplicationArguments args) {
        int count = intOpt(args, "count", 100);
        double duplicateRate = doubleOpt(args, "duplicate-rate", 0.05);
        int minSleepMs = intOpt(args, "min-sleep-ms", 1000);
        int maxSleepMs = intOpt(args, "max-sleep-ms", 5000);
        if (maxSleepMs < minSleepMs) {
            maxSleepMs = minSleepMs;
        }

        log.info("Starting load-gen: count={} duplicateRate={} sleep=[{},{}]ms (10 orgs x 20 ads)",
                count, duplicateRate, minSleepMs, maxSleepMs);
        List<String> pastImpressions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            AdClickEvent event = RandomClickGenerator.next(pastImpressions, duplicateRate);
            producer.send(event);
            log.info("[{}/{}] sent adId={} orgId={} impressionId={}",
                    i + 1, count, event.getAdId(), event.getAdOrgId(), event.getAdImpressionId());
            if (i < count - 1) {
                sleepRandom(minSleepMs, maxSleepMs);
            }
        }
        log.info("Load-gen done: sent {}", count);
    }

    private void sleepRandom(int minMs, int maxMs) {
        try {
            int sleep = minMs >= maxMs ? minMs
                    : ThreadLocalRandom.current().nextInt(minMs, maxMs + 1);
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int intOpt(ApplicationArguments args, String name, int def) {
        try {
            if (args.containsOption(name)) {
                return Integer.parseInt(args.getOptionValues(name).get(0));
            }
        } catch (Exception ignored) {
        }
        return def;
    }

    private double doubleOpt(ApplicationArguments args, String name, double def) {
        try {
            if (args.containsOption(name)) {
                return Double.parseDouble(args.getOptionValues(name).get(0));
            }
        } catch (Exception ignored) {
        }
        return def;
    }
}
