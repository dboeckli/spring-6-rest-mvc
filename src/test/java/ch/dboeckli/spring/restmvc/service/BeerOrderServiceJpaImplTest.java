package ch.dboeckli.spring.restmvc.service;

import ch.dboeckli.spring.restmvc.entity.BeerOrder;
import ch.dboeckli.spring.restmvc.mapper.BeerOrderMapper;
import ch.dboeckli.spring.restmvc.repository.BeerOrderRepository;
import ch.dboeckli.spring.restmvc.repository.BeerRepository;
import ch.dboeckli.spring.restmvc.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BeerOrderServiceJpaImplTest {

    private BeerOrderRepository beerOrderRepository;

    private BeerOrderServiceJpaImpl beerOrderService;

    @BeforeEach
    void setUp() {
        beerOrderRepository = mock(BeerOrderRepository.class);
        beerOrderService = new BeerOrderServiceJpaImpl(beerOrderRepository, mock(BeerRepository.class),
                mock(CustomerRepository.class), mock(BeerOrderMapper.class), mock(ApplicationEventPublisher.class));
    }

    @Test
    void deleteBeerOrderRetriesAfterOptimisticLockingConflict() {
        UUID beerOrderId = UUID.randomUUID();
        given(beerOrderRepository.existsById(beerOrderId)).willReturn(true);
        willThrow(new ObjectOptimisticLockingFailureException(BeerOrder.class, beerOrderId)).willDoNothing()
            .given(beerOrderRepository)
            .deleteById(beerOrderId);

        assertThat(beerOrderService.deleteBeerOrder(beerOrderId)).isTrue();

        verify(beerOrderRepository, times(2)).deleteById(beerOrderId);
    }

    @Test
    void deleteBeerOrderReturnsFalseWhenNotFound() {
        UUID beerOrderId = UUID.randomUUID();
        given(beerOrderRepository.existsById(beerOrderId)).willReturn(false);

        assertThat(beerOrderService.deleteBeerOrder(beerOrderId)).isFalse();

        verify(beerOrderRepository, never()).deleteById(beerOrderId);
    }

}
