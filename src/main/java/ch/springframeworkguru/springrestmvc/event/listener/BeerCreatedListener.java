package ch.springframeworkguru.springrestmvc.event.listener;

import ch.springframeworkguru.springrestmvc.entity.BeerAudit;
import ch.springframeworkguru.springrestmvc.event.events.BeerCreatedEvent;
import ch.springframeworkguru.springrestmvc.mapper.BeerMapper;
import ch.springframeworkguru.springrestmvc.repository.BeerAuditRepository;
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
    public void listen(BeerCreatedEvent event) {
        log.info("BeerCreatedListener called for event: {}", event.getBeer().getId());
        log.info("BeerCreatedListener called for event: {}", event.getAuthentication().getName());
        log.info("Current Thread name: " + Thread.currentThread().getName());
        log.info("Current Thread ID: " + Thread.currentThread().threadId());
        
        BeerAudit beerAudit = beerMapper.beerToBeerAudit(event.getBeer());
        beerAudit.setAuditEventType("BEER_CREATED");
        if (event.getAuthentication() != null && event.getAuthentication().getName() != null) {
            beerAudit.setPrincipalName(event.getAuthentication().getName());
        }
        
        BeerAudit savedBeerAudit = beerAuditRepository.save(beerAudit);
        log.info("Beer Audit saved: {}", savedBeerAudit.getAuditId());
    }
    
}
