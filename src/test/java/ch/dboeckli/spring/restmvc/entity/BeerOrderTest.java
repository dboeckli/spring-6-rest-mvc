package ch.dboeckli.spring.restmvc.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BeerOrderTest {

    private static final UUID ORDER_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    private static final Timestamp TIMESTAMP = Timestamp.valueOf(LocalDateTime.of(2024, 1, 2, 3, 4, 5));

    @Test
    void isNewWhenIdIsNull() {
        assertThat(new BeerOrder().isNew()).isTrue();
    }

    @Test
    void isNotNewWhenIdIsPresent() {
        assertThat(BeerOrder.builder().id(ORDER_ID).build().isNew()).isFalse();
    }

    @Test
    void setCustomerLinksBothSides() {
        Customer customer = Customer.builder().name("Jane").build();
        BeerOrder beerOrder = new BeerOrder();

        beerOrder.setCustomer(customer);

        assertThat(beerOrder.getCustomer()).isSameAs(customer);
        assertThat(customer.getBeerOrders()).containsExactly(beerOrder);
    }

    @Test
    void setCustomerIgnoresNull() {
        BeerOrder beerOrder = new BeerOrder();

        beerOrder.setCustomer(null);

        assertThat(beerOrder.getCustomer()).isNull();
    }

    @Test
    void setBeerOrderShipmentLinksBothSides() {
        BeerOrderShipment shipment = BeerOrderShipment.builder().trackingNumber("track-1").build();
        BeerOrder beerOrder = new BeerOrder();

        beerOrder.setBeerOrderShipment(shipment);

        assertThat(beerOrder.getBeerOrderShipment()).isSameAs(shipment);
        assertThat(shipment.getBeerOrder()).isSameAs(beerOrder);
    }

    @Test
    void setBeerOrderShipmentIgnoresNull() {
        BeerOrder beerOrder = new BeerOrder();

        beerOrder.setBeerOrderShipment(null);

        assertThat(beerOrder.getBeerOrderShipment()).isNull();
    }

    @Test
    void setBeerOrderLinesLinksBothSides() {
        BeerOrderLine first = BeerOrderLine.builder().orderQuantity(1).build();
        BeerOrderLine second = BeerOrderLine.builder().orderQuantity(2).build();
        BeerOrder beerOrder = new BeerOrder();

        beerOrder.setBeerOrderLines(Set.of(first, second));

        assertThat(beerOrder.getBeerOrderLines()).containsExactlyInAnyOrder(first, second);
        assertThat(first.getBeerOrder()).isSameAs(beerOrder);
        assertThat(second.getBeerOrder()).isSameAs(beerOrder);
    }

    @Test
    void setBeerOrderLinesIgnoresNull() {
        BeerOrder beerOrder = new BeerOrder();

        beerOrder.setBeerOrderLines(null);

        assertThat(beerOrder.getBeerOrderLines()).isNull();
    }

    @Test
    void allArgsConstructorWiresAssociations() {
        Customer customer = Customer.builder().name("Jane").build();
        BeerOrderLine line = BeerOrderLine.builder().orderQuantity(3).build();
        BeerOrderShipment shipment = BeerOrderShipment.builder().trackingNumber("track-1").build();

        BeerOrder beerOrder = new BeerOrder(ORDER_ID, 1L, new BigDecimal("12.34"), TIMESTAMP, TIMESTAMP, "ref-1",
                customer, Set.of(line), shipment);

        assertThat(beerOrder.getId()).isEqualTo(ORDER_ID);
        assertThat(beerOrder.getVersion()).isEqualTo(1L);
        assertThat(beerOrder.getPaymentAmount()).isEqualByComparingTo("12.34");
        assertThat(beerOrder.getCreatedDate()).isEqualTo(TIMESTAMP);
        assertThat(beerOrder.getUpdateDate()).isEqualTo(TIMESTAMP);
        assertThat(beerOrder.getCustomerRef()).isEqualTo("ref-1");
        assertThat(beerOrder.getCustomer()).isSameAs(customer);
        assertThat(beerOrder.getBeerOrderLines()).containsExactly(line);
        assertThat(line.getBeerOrder()).isSameAs(beerOrder);
        assertThat(beerOrder.getBeerOrderShipment()).isSameAs(shipment);
        assertThat(shipment.getBeerOrder()).isSameAs(beerOrder);
    }

}
