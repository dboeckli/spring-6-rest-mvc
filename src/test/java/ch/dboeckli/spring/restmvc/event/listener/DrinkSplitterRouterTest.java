package ch.dboeckli.spring.restmvc.event.listener;

import ch.dboeckli.spring.restmvc.config.KafkaConfig;
import ch.guru.springframework.spring6restmvcapi.dto.BeerDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderLineDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerStyle;
import ch.guru.springframework.spring6restmvcapi.events.DrinkRequestEvent;
import ch.guru.springframework.spring6restmvcapi.events.OrderPlacedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class DrinkSplitterRouterTest {

    private KafkaTemplate<String, Object> kafkaTemplate;

    private DrinkSplitterRouter drinkSplitterRouter;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        kafkaTemplate = mock(KafkaTemplate.class);
        drinkSplitterRouter = new DrinkSplitterRouter(kafkaTemplate);
    }

    @ParameterizedTest
    @EnumSource(BeerStyle.class)
    void routesBeerStyleToExpectedDrinkTopic(BeerStyle beerStyle) {
        BeerOrderLineDTO beerOrderLine = beerOrderLine(beerStyle);

        drinkSplitterRouter.receive(orderWith(beerOrderLine));

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(kafkaTemplate).send(eq(expectedTopic(beerStyle)), eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(DrinkRequestEvent.class)
            .extracting(event -> ((DrinkRequestEvent) event).getBeerOrderLineDTO())
            .isEqualTo(beerOrderLine);
    }

    @Test
    void sendsOneDrinkRequestPerOrderLine() {
        BeerOrderLineDTO lager = beerOrderLine(BeerStyle.LAGER);
        BeerOrderLineDTO ipa = beerOrderLine(BeerStyle.IPA);
        BeerOrderLineDTO gose = beerOrderLine(BeerStyle.GOSE);

        drinkSplitterRouter.receive(orderWith(lager, ipa, gose));

        verify(kafkaTemplate).send(eq(KafkaConfig.DRINK_REQUEST_ICE_COLD_TOPIC), any(DrinkRequestEvent.class));
        verify(kafkaTemplate).send(eq(KafkaConfig.DRINK_REQUEST_COOL_TOPIC), any(DrinkRequestEvent.class));
        verify(kafkaTemplate).send(eq(KafkaConfig.DRINK_REQUEST_COLD_TOPIC), any(DrinkRequestEvent.class));
        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void ignoresOrderPlacedEventWithoutBeerOrder() {
        drinkSplitterRouter.receive(OrderPlacedEvent.builder().build());

        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void ignoresOrderPlacedEventWithoutOrderLines() {
        OrderPlacedEvent orderPlacedEvent = OrderPlacedEvent.builder()
            .beerOrderDTO(BeerOrderDTO.builder().id(UUID.randomUUID()).build())
            .build();

        drinkSplitterRouter.receive(orderPlacedEvent);

        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void ignoresOrderPlacedEventWithEmptyOrderLines() {
        OrderPlacedEvent orderPlacedEvent = OrderPlacedEvent.builder()
            .beerOrderDTO(BeerOrderDTO.builder().id(UUID.randomUUID()).beerOrderLines(Set.of()).build())
            .build();

        drinkSplitterRouter.receive(orderPlacedEvent);

        verifyNoInteractions(kafkaTemplate);
    }

    private static String expectedTopic(BeerStyle beerStyle) {
        return switch (beerStyle) {
            case LAGER, PILSNER, SAISON -> KafkaConfig.DRINK_REQUEST_ICE_COLD_TOPIC;
            case GOSE, WHEAT -> KafkaConfig.DRINK_REQUEST_COLD_TOPIC;
            case STOUT, PORTER, ALE, IPA, PALE_ALE -> KafkaConfig.DRINK_REQUEST_COOL_TOPIC;
        };
    }

    private static BeerOrderLineDTO beerOrderLine(BeerStyle beerStyle) {
        return BeerOrderLineDTO.builder()
            .id(UUID.randomUUID())
            .beer(BeerDTO.builder().id(UUID.randomUUID()).beerName("Test Beer").beerStyle(beerStyle).build())
            .build();
    }

    private static OrderPlacedEvent orderWith(BeerOrderLineDTO... beerOrderLines) {
        Set<BeerOrderLineDTO> lines = new LinkedHashSet<>(Arrays.asList(beerOrderLines));
        return OrderPlacedEvent.builder()
            .beerOrderDTO(BeerOrderDTO.builder().id(UUID.randomUUID()).beerOrderLines(lines).build())
            .build();
    }

}
