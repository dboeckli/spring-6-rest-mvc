package ch.springframeworkguru.springrestmvc.event.listener;

import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderDTO;
import ch.guru.springframework.spring6restmvcapi.events.OrderPlacedEvent;
import ch.springframeworkguru.springrestmvc.config.KafkaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EmbeddedKafka(controlledShutdown = true, topics = {KafkaConfig.ORDER_PLACED_TOPIC}, partitions = 1, kraft = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderPlacedListenerTest {

    @Autowired
    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    OrderPlacedListener orderPlacedListener;

    @Autowired
    OrderPlacedKafkaListener orderPlacedKafkaListener;

    @BeforeEach
    void setUp() {
        kafkaListenerEndpointRegistry.getListenerContainers().forEach(container -> {
            ContainerTestUtils.waitForAssignment(container, 1);
        });
    }


    @Test
    void listen() {
        OrderPlacedEvent orderPlacedEvent = OrderPlacedEvent.builder().beerOrderDTO(BeerOrderDTO.builder()
            .id(UUID.randomUUID())
            .build()).build();

        orderPlacedListener.listen(orderPlacedEvent);

        await().atMost(20, TimeUnit.SECONDS).untilAsserted(() -> {
            assertEquals(1, orderPlacedKafkaListener.messageCounter.get());
        });
    }
    
}
