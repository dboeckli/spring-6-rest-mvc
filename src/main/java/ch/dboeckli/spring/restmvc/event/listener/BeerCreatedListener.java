package ch.dboeckli.spring.restmvc.event.listener;

import ch.dboeckli.spring.restmvc.entity.BeerAudit;
import ch.dboeckli.spring.restmvc.event.events.*;
import ch.dboeckli.spring.restmvc.mapper.BeerMapper;
import ch.dboeckli.spring.restmvc.repository.BeerAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class BeerCreatedListener {

    private final BeerAuditRepository beerAuditRepository;
    private final BeerMapper beerMapper;

    @Async
    @EventListener
    public void listen(BeerEvent event) {
        log.info("Beer Event Listener called for event: " + event.getClass().getName());
        log.info("Beer Event Listener called for event: {}", event.getBeer().getId());
        log.info("Beer Event Listener called for event: {}", event.getAuthentication().getName());
        log.info("Current Thread name: " + Thread.currentThread().getName());
        log.info("Current Thread ID: " + Thread.currentThread().threadId());

        String eventType;
        switch (event) {
            case BeerCreatedEvent beerCreatedEvent -> eventType = "BEER_CREATED";
            case BeerPatchEvent beerPatchedEvent -> eventType = "BEER_PATCHED";
            case BeerUpdateEvent beerUpdatedEvent -> eventType = "BEER_UPDATED";
            case BeerDeleteEvent beerDeletedEvent -> eventType = "BEER_DELETED";
            default -> eventType = "UNKNOWN";
        }

        BeerAudit beerAudit = beerMapper.beerToBeerAudit(event.getBeer());
        beerAudit.setAuditEventType(eventType);
        if (event.getAuthentication() != null && event.getAuthentication().getName() != null) {
            beerAudit.setPrincipalName(event.getAuthentication().getName());
        }

        BeerAudit savedBeerAudit = beerAuditRepository.save(beerAudit);
        log.info("Beer Audit saved: {}", savedBeerAudit.getAuditId());
    }
}
