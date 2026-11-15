package ch.dboeckli.spring.restmvc.entity;

import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderLineStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BeerOrderLineTest {

    @Test
    void isNewWhenIdIsNull() {
        assertThat(new BeerOrderLine().isNew()).isTrue();
    }

    @Test
    void isNotNewWhenIdIsPresent() {
        assertThat(BeerOrderLine.builder().id(UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc")).build().isNew())
            .isFalse();
    }

    @Test
    void builderAppliesDefaults() {
        BeerOrderLine line = BeerOrderLine.builder().build();

        assertThat(line.getOrderQuantity()).isEqualTo(1);
        assertThat(line.getQuantityAllocated()).isZero();
        assertThat(line.getOrderLineStatus()).isEqualTo(BeerOrderLineStatus.NEW);
    }

}
