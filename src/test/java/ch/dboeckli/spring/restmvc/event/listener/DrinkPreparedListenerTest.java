package ch.dboeckli.spring.restmvc.event.listener;

import ch.dboeckli.spring.restmvc.entity.BeerOrderLine;
import ch.dboeckli.spring.restmvc.repository.BeerOrderLineRepository;
import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderLineDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderLineStatus;
import ch.guru.springframework.spring6restmvcapi.events.DrinkPreparedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class DrinkPreparedListenerTest {

    private BeerOrderLineRepository beerOrderLineRepository;

    private DrinkPreparedListener drinkPreparedListener;

    @BeforeEach
    void setUp() {
        beerOrderLineRepository = mock(BeerOrderLineRepository.class);
        drinkPreparedListener = new DrinkPreparedListener(beerOrderLineRepository);
    }

    @Test
    void marksOrderLineCompleteWhenFound() {
        UUID id = UUID.randomUUID();
        BeerOrderLine beerOrderLine = BeerOrderLine.builder().id(id).build();
        given(beerOrderLineRepository.findById(id)).willReturn(Optional.of(beerOrderLine));

        drinkPreparedListener.listen(drinkPreparedEvent(id));

        assertThat(beerOrderLine.getOrderLineStatus()).isEqualTo(BeerOrderLineStatus.COMPLETE);
        verify(beerOrderLineRepository).save(beerOrderLine);
    }

    @Test
    void doesNothingWhenOrderLineNotFound() {
        UUID id = UUID.randomUUID();
        given(beerOrderLineRepository.findById(id)).willReturn(Optional.empty());

        drinkPreparedListener.listen(drinkPreparedEvent(id));

        verify(beerOrderLineRepository).findById(id);
        verify(beerOrderLineRepository, never()).save(any());
    }

    private static DrinkPreparedEvent drinkPreparedEvent(UUID id) {
        return DrinkPreparedEvent.builder().beerOrderLineDTO(BeerOrderLineDTO.builder().id(id).build()).build();
    }

}
