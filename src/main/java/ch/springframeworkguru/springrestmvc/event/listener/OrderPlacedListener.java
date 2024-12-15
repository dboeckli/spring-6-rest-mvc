package ch.springframeworkguru.springrestmvc.event.listener;

import ch.guru.springframework.spring6restmvcapi.events.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderPlacedListener {

    @Async
    @EventListener
    public void listen(OrderPlacedEvent event) {
        log.info("Beer Order Event Listener called for event: " + event.getClass().getName());
        log.info("Beer Order Event Listener called for event: {}", event.getBeerOrderDTO().getId());
        log.info("Current Thread name: " + Thread.currentThread().getName());
        log.info("Current Thread ID: " + Thread.currentThread().threadId());
        // TODO: IMPLEMENT PROCESS ORDER LOGIC HERE. SEND TO KAFKA
    }    
    
}
