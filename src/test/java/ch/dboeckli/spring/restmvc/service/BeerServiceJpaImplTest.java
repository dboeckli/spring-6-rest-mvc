package ch.dboeckli.spring.restmvc.service;

import ch.dboeckli.spring.restmvc.entity.Beer;
import ch.dboeckli.spring.restmvc.event.events.BeerDeleteEvent;
import ch.dboeckli.spring.restmvc.event.events.BeerEvent;
import ch.dboeckli.spring.restmvc.mapper.BeerMapper;
import ch.dboeckli.spring.restmvc.repository.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class BeerServiceJpaImplTest {

    private BeerRepository beerRepository;

    private ApplicationEventPublisher eventPublisher;

    private BeerServiceJpaImpl beerService;

    @BeforeEach
    void setUp() {
        beerRepository = mock(BeerRepository.class);
        BeerMapper beerMapper = mock(BeerMapper.class);
        CacheManager cacheManager = mock(CacheManager.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        beerService = new BeerServiceJpaImpl(beerRepository, beerMapper, cacheManager, eventPublisher);
    }

    @Test
    void deleteBeerPublishesEventWithTheFullBeer() {
        UUID beerId = UUID.randomUUID();
        Beer beer = Beer.builder().id(beerId).beerName("Test Beer").build();
        given(beerRepository.findById(beerId)).willReturn(Optional.of(beer));

        assertThat(beerService.deleteBeer(beerId)).isTrue();

        ArgumentCaptor<BeerEvent> eventCaptor = ArgumentCaptor.forClass(BeerEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(BeerDeleteEvent.class)
            .extracting(event -> event.getBeer().getBeerName())
            .isEqualTo("Test Beer");
        verify(beerRepository).delete(beer);
    }

    @Test
    void deleteBeerReturnsFalseAndPublishesNothingWhenNotFound() {
        UUID beerId = UUID.randomUUID();
        given(beerRepository.findById(beerId)).willReturn(Optional.empty());

        assertThat(beerService.deleteBeer(beerId)).isFalse();

        verify(beerRepository, never()).delete(any(Beer.class));
        verifyNoInteractions(eventPublisher);
    }

}
