package com.adclick.producer;

import com.adclick.common.AdClickEvent;
import com.adclick.common.AdClickTopics;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClickProducerService {

    private static final Logger log = LoggerFactory.getLogger(ClickProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ClickProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void send(AdClickEvent event) {
        if (event.getEventTimeMillis() == 0) {
            event.setEventTimeMillis(System.currentTimeMillis());
        }
        try {
            String key = event.getAdImpressionId() != null ? event.getAdImpressionId() : event.getAdId();
            String value = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(AdClickTopics.CLICKS, key, value);
        } catch (Exception e) {
            log.error("Failed to serialize/send click event", e);
            throw new IllegalStateException("Failed to send click event", e);
        }
    }
}
