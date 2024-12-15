package ch.springframeworkguru.springrestmvc.event.listener;

import ch.guru.springframework.spring6restmvcapi.events.OrderPlacedEvent;
import ch.springframeworkguru.springrestmvc.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class OrderPlacedKafkaListener {
    AtomicInteger messageCounter = new AtomicInteger(0);

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = KafkaConfig.ORDER_PLACED_TOPIC)
    public void receive(OrderPlacedEvent orderPlacedEvent) {
        log.info("Received Kafka message: " + orderPlacedEvent);
        messageCounter.incrementAndGet();
    }

}
