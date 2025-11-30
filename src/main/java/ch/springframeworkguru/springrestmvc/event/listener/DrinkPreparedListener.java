package ch.springframeworkguru.springrestmvc.event.listener;

import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderLineStatus;
import ch.guru.springframework.spring6restmvcapi.events.DrinkPreparedEvent;
import ch.springframeworkguru.springrestmvc.config.KafkaConfig;
import ch.springframeworkguru.springrestmvc.repository.BeerOrderLineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DrinkPreparedListener {

    BeerOrderLineRepository beerOrderLineRepository;

    @KafkaListener(topics = KafkaConfig.DRINK_PREPARED_TOPIC, groupId = "drinkPreparedListener")
    public void listen(DrinkPreparedEvent event) {
        log.info("### DrinkPreparedListener:  I'm listening..." + event);

        beerOrderLineRepository.findById(event.getBeerOrderLineDTO().getId()).ifPresentOrElse(beerOrderLine -> {
            beerOrderLine.setOrderLineStatus(BeerOrderLineStatus.COMPLETE);
            beerOrderLineRepository.save(beerOrderLine);
        }, () -> log.error("BeerOrderLine not found: {}", event.getBeerOrderLineDTO().getId()));
    }

}
