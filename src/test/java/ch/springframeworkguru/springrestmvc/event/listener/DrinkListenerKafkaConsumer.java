package ch.springframeworkguru.springrestmvc.event.listener;

import ch.springframeworkguru.springrestmvc.config.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class DrinkListenerKafkaConsumer {
    AtomicInteger iceColdMessageCounter = new AtomicInteger(0);
    AtomicInteger coldMessageCounter = new AtomicInteger(0);
    AtomicInteger coolMessageCounter = new AtomicInteger(0);

    @KafkaListener(groupId = "KafkaIntegrationTest", topics = KafkaConfig.DRINK_REQUEST_ICE_COLD_TOPIC)
    public void listenIceCode() {
        log.info("I am listening - ice cold");
        iceColdMessageCounter.incrementAndGet();
    }

    //list cold beer
    @KafkaListener(groupId = "KafkaIntegrationTest", topics = KafkaConfig.DRINK_REQUEST_COLD_TOPIC)
    public void listenCold() {
        log.info("I am listening - cold");
        coldMessageCounter.incrementAndGet();
    }

    //listen cool beer
    @KafkaListener(groupId = "KafkaIntegrationTest", topics = KafkaConfig.DRINK_REQUEST_COOL_TOPIC)
    public void listenCool() {
        log.info("I am listening - cool");
        coolMessageCounter.incrementAndGet();
    }
}
