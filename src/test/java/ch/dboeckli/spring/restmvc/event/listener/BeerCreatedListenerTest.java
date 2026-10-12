package ch.dboeckli.spring.restmvc.event.listener;

import ch.dboeckli.spring.restmvc.entity.Beer;
import ch.dboeckli.spring.restmvc.entity.BeerAudit;
import ch.dboeckli.spring.restmvc.event.events.BeerCreatedEvent;
import ch.dboeckli.spring.restmvc.event.events.BeerDeleteEvent;
import ch.dboeckli.spring.restmvc.event.events.BeerEvent;
import ch.dboeckli.spring.restmvc.event.events.BeerPatchEvent;
import ch.dboeckli.spring.restmvc.event.events.BeerUpdateEvent;
import ch.dboeckli.spring.restmvc.mapper.BeerMapper;
import ch.dboeckli.spring.restmvc.repository.BeerAuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class BeerCreatedListenerTest {

    private BeerAuditRepository beerAuditRepository;

    private BeerMapper beerMapper;

    private BeerCreatedListener beerCreatedListener;

    @BeforeEach
    void setUp() {
        beerAuditRepository = mock(BeerAuditRepository.class);
        beerMapper = mock(BeerMapper.class);
        beerCreatedListener = new BeerCreatedListener(beerAuditRepository, beerMapper);
    }

    @ParameterizedTest
    @MethodSource("beerEvents")
    void auditsBeerEventWithExpectedEventType(BeerEvent event, String expectedEventType) {
        BeerAudit beerAudit = stubMapperAndRepository(event.getBeer());

        beerCreatedListener.listen(event);

        assertThat(beerAudit.getAuditEventType()).isEqualTo(expectedEventType);
        verify(beerMapper).beerToBeerAudit(event.getBeer());
        verify(beerAuditRepository).save(beerAudit);
    }

    @Test
    void usesUnknownEventTypeForUnhandledBeerEvent() {
        Beer beer = beer();
        BeerAudit beerAudit = stubMapperAndRepository(beer);
        BeerEvent unknownEvent = new BeerEvent() {

            @Override
            public Beer getBeer() {
                return beer;
            }

            @Override
            public Authentication getAuthentication() {
                return null;
            }

        };

        beerCreatedListener.listen(unknownEvent);

        assertThat(beerAudit.getAuditEventType()).isEqualTo("UNKNOWN");
    }

    @Test
    void mapsBeerToAuditAndSavesIt() {
        Beer beer = beer();
        BeerAudit beerAudit = BeerAudit.builder().build();
        given(beerMapper.beerToBeerAudit(beer)).willReturn(beerAudit);
        given(beerAuditRepository.save(beerAudit)).willReturn(beerAudit);

        beerCreatedListener.listen(BeerCreatedEvent.builder().beer(beer).build());

        ArgumentCaptor<BeerAudit> savedAudit = ArgumentCaptor.forClass(BeerAudit.class);
        verify(beerAuditRepository).save(savedAudit.capture());
        assertThat(savedAudit.getValue()).isSameAs(beerAudit);
    }

    @Test
    void setsPrincipalNameWhenAuthenticationIsPresent() {
        Beer beer = beer();
        BeerAudit beerAudit = stubMapperAndRepository(beer);
        Authentication authentication = mock(Authentication.class);
        given(authentication.getName()).willReturn("beer-user");

        beerCreatedListener.listen(BeerCreatedEvent.builder().beer(beer).authentication(authentication).build());

        assertThat(beerAudit.getPrincipalName()).isEqualTo("beer-user");
    }

    @Test
    void doesNotSetPrincipalNameWhenAuthenticationIsAbsent() {
        Beer beer = beer();
        BeerAudit beerAudit = stubMapperAndRepository(beer);

        beerCreatedListener.listen(BeerCreatedEvent.builder().beer(beer).build());

        assertThat(beerAudit.getPrincipalName()).isNull();
    }

    @Test
    void doesNotSetPrincipalNameWhenAuthenticationNameIsNull() {
        Beer beer = beer();
        BeerAudit beerAudit = stubMapperAndRepository(beer);
        Authentication authentication = mock(Authentication.class);
        given(authentication.getName()).willReturn(null);

        beerCreatedListener.listen(BeerCreatedEvent.builder().beer(beer).authentication(authentication).build());

        assertThat(beerAudit.getPrincipalName()).isNull();
    }

    private BeerAudit stubMapperAndRepository(Beer beer) {
        BeerAudit beerAudit = BeerAudit.builder().build();
        given(beerMapper.beerToBeerAudit(beer)).willReturn(beerAudit);
        given(beerAuditRepository.save(beerAudit)).willReturn(beerAudit);
        return beerAudit;
    }

    private static Stream<Arguments> beerEvents() {
        Beer beer = beer();
        return Stream.of(Arguments.of(BeerCreatedEvent.builder().beer(beer).build(), "BEER_CREATED"),
                Arguments.of(BeerPatchEvent.builder().beer(beer).build(), "BEER_PATCHED"),
                Arguments.of(BeerUpdateEvent.builder().beer(beer).build(), "BEER_UPDATED"),
                Arguments.of(BeerDeleteEvent.builder().beer(beer).build(), "BEER_DELETED"));
    }

    private static Beer beer() {
        return Beer.builder().id(UUID.randomUUID()).beerName("Test Beer").build();
    }

}
