package ch.dboeckli.spring.restmvc.mapper;

import ch.dboeckli.spring.restmvc.entity.Beer;
import ch.dboeckli.spring.restmvc.entity.BeerOrder;
import ch.dboeckli.spring.restmvc.entity.BeerOrderLine;
import ch.dboeckli.spring.restmvc.entity.BeerOrderShipment;
import ch.dboeckli.spring.restmvc.entity.Customer;
import ch.guru.springframework.spring6restmvcapi.dto.BeerDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderLineDTO;
import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderLineStatus;
import ch.guru.springframework.spring6restmvcapi.dto.BeerStyle;
import ch.guru.springframework.spring6restmvcapi.dto.CustomerDTO;
import ch.guru.springframework.spring6restmvcapi.dto.create.BeerOrderShipmentDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BeerOrderMapperTest {

    private static final UUID ORDER_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    private static final UUID CUSTOMER_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

    private static final UUID LINE_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    private static final UUID BEER_ID = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");

    private static final UUID SHIPMENT_ID = UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee");

    private static final Timestamp CREATED_DATE = Timestamp.valueOf(LocalDateTime.of(2024, 1, 2, 3, 4, 5));

    private static final Timestamp UPDATE_DATE = Timestamp.valueOf(LocalDateTime.of(2024, 2, 3, 4, 5, 6));

    private final BeerOrderMapper mapper = new BeerOrderMapperImpl();

    @Test
    void beerOrderDtoToBeerOrderReturnsNullForNullDto() {
        assertThat(mapper.beerOrderDtoToBeerOrder(null)).isNull();
    }

    @Test
    void beerOrderToBeerOrderDtoReturnsNullForNullEntity() {
        assertThat(mapper.beerOrderToBeerOrderDto(null)).isNull();
    }

    @Test
    void beerOrderDtoToBeerOrderMapsNestedGraph() {
        BeerOrder beerOrder = mapper.beerOrderDtoToBeerOrder(fullBeerOrderDto());

        assertThat(beerOrder.getId()).isEqualTo(ORDER_ID);
        assertThat(beerOrder.getVersion()).isEqualTo(3L);
        assertThat(beerOrder.getPaymentAmount()).isEqualByComparingTo("12.34");
        assertThat(beerOrder.getCustomerRef()).isEqualTo("ref-1");
        assertThat(beerOrder.getCreatedDate()).isEqualTo(CREATED_DATE);
        assertThat(beerOrder.getUpdateDate()).isEqualTo(UPDATE_DATE);

        Customer customer = beerOrder.getCustomer();
        assertThat(customer.getId()).isEqualTo(CUSTOMER_ID);
        assertThat(customer.getVersion()).isEqualTo(7);
        assertThat(customer.getName()).isEqualTo("Jane");
        assertThat(customer.getEmail()).isEqualTo("jane@example.com");

        assertThat(beerOrder.getBeerOrderLines()).hasSize(1);
        BeerOrderLine line = beerOrder.getBeerOrderLines().iterator().next();
        assertThat(line.getId()).isEqualTo(LINE_ID);
        assertThat(line.getOrderQuantity()).isEqualTo(5);
        assertThat(line.getQuantityAllocated()).isEqualTo(2);
        assertThat(line.getOrderLineStatus()).isEqualTo(BeerOrderLineStatus.NEW);
        assertThat(line.getBeer()).isNotNull();
        assertThat(line.getBeer().getId()).isEqualTo(BEER_ID);

        BeerOrderShipment shipment = beerOrder.getBeerOrderShipment();
        assertThat(shipment.getId()).isEqualTo(SHIPMENT_ID);
        assertThat(shipment.getTrackingNumber()).isEqualTo("track-1");
    }

    @Test
    void beerOrderToBeerOrderDtoMapsNestedGraph() {
        BeerOrderDTO dto = mapper.beerOrderToBeerOrderDto(fullBeerOrder());

        assertThat(dto.getId()).isEqualTo(ORDER_ID);
        assertThat(dto.getVersion()).isEqualTo(3L);
        assertThat(dto.getPaymentAmount()).isEqualByComparingTo("12.34");
        assertThat(dto.getCustomerRef()).isEqualTo("ref-1");
        assertThat(dto.getCreatedDate()).isEqualTo(CREATED_DATE);
        assertThat(dto.getUpdateDate()).isEqualTo(UPDATE_DATE);

        CustomerDTO customer = dto.getCustomer();
        assertThat(customer.getId()).isEqualTo(CUSTOMER_ID);
        assertThat(customer.getVersion()).isEqualTo("7");
        assertThat(customer.getName()).isEqualTo("Jane");
        assertThat(customer.getEmail()).isEqualTo("jane@example.com");

        assertThat(dto.getBeerOrderLines()).hasSize(1);
        BeerOrderLineDTO line = dto.getBeerOrderLines().iterator().next();
        assertThat(line.getId()).isEqualTo(LINE_ID);
        assertThat(line.getOrderQuantity()).isEqualTo(5);
        assertThat(line.getQuantityAllocated()).isEqualTo(2);
        assertThat(line.getOrderLineStatus()).isEqualTo(BeerOrderLineStatus.NEW);
        assertThat(line.getBeer()).isNotNull();
        assertThat(line.getBeer().getId()).isEqualTo(BEER_ID);

        BeerOrderShipmentDTO shipment = dto.getBeerOrderShipment();
        assertThat(shipment.getId()).isEqualTo(SHIPMENT_ID);
        assertThat(shipment.getTrackingNumber()).isEqualTo("track-1");
    }

    @Test
    void beerOrderDtoToBeerOrderHandlesMissingCustomerVersion() {
        CustomerDTO customerDto = CustomerDTO.builder().id(CUSTOMER_ID).name("Jane").build();
        BeerOrderDTO dto = BeerOrderDTO.builder().customer(customerDto).build();

        BeerOrder beerOrder = mapper.beerOrderDtoToBeerOrder(dto);

        assertThat(beerOrder.getCustomer().getVersion()).isNull();
    }

    @Test
    void beerOrderToBeerOrderDtoHandlesMissingCustomerVersion() {
        Customer customer = Customer.builder().id(CUSTOMER_ID).name("Jane").build();
        BeerOrder beerOrder = BeerOrder.builder().customer(customer).build();

        BeerOrderDTO dto = mapper.beerOrderToBeerOrderDto(beerOrder);

        assertThat(dto.getCustomer().getVersion()).isNull();
    }

    @Test
    void beerOrderDtoToBeerOrderHandlesNullNestedValues() {
        BeerOrderDTO dto = new BeerOrderDTO();
        dto.setBeerOrderLines(null);

        BeerOrder beerOrder = mapper.beerOrderDtoToBeerOrder(dto);

        assertThat(beerOrder.getCustomer()).isNull();
        assertThat(beerOrder.getBeerOrderLines()).isNull();
        assertThat(beerOrder.getBeerOrderShipment()).isNull();
    }

    @Test
    void beerOrderToBeerOrderDtoHandlesNullNestedValues() {
        BeerOrderDTO dto = mapper.beerOrderToBeerOrderDto(new BeerOrder());

        assertThat(dto.getCustomer()).isNull();
        assertThat(dto.getBeerOrderLines()).isNull();
        assertThat(dto.getBeerOrderShipment()).isNull();
    }

    @Test
    void beerOrderDtoToBeerOrderHandlesOrderLineWithoutBeer() {
        BeerOrderDTO dto = BeerOrderDTO.builder()
            .beerOrderLines(Set.of(BeerOrderLineDTO.builder().id(LINE_ID).build()))
            .build();

        BeerOrder beerOrder = mapper.beerOrderDtoToBeerOrder(dto);

        assertThat(beerOrder.getBeerOrderLines()).hasSize(1);
        assertThat(beerOrder.getBeerOrderLines().iterator().next().getBeer()).isNull();
    }

    @Test
    void beerOrderToBeerOrderDtoHandlesOrderLineWithoutBeer() {
        BeerOrder beerOrder = BeerOrder.builder()
            .beerOrderLines(Set.of(BeerOrderLine.builder().id(LINE_ID).build()))
            .build();

        BeerOrderDTO dto = mapper.beerOrderToBeerOrderDto(beerOrder);

        assertThat(dto.getBeerOrderLines()).hasSize(1);
        assertThat(dto.getBeerOrderLines().iterator().next().getBeer()).isNull();
    }

    private static BeerOrderDTO fullBeerOrderDto() {
        return BeerOrderDTO.builder()
            .id(ORDER_ID)
            .version(3L)
            .paymentAmount(new BigDecimal("12.34"))
            .customerRef("ref-1")
            .customer(CustomerDTO.builder().id(CUSTOMER_ID).version("7").name("Jane").email("jane@example.com").build())
            .beerOrderLines(Set.of(BeerOrderLineDTO.builder()
                .id(LINE_ID)
                .version(1L)
                .orderQuantity(5)
                .quantityAllocated(2)
                .orderLineStatus(BeerOrderLineStatus.NEW)
                .beer(BeerDTO.builder().id(BEER_ID).beerName("Beer").beerStyle(BeerStyle.IPA).build())
                .build()))
            .beerOrderShipment(BeerOrderShipmentDTO.builder().id(SHIPMENT_ID).trackingNumber("track-1").build())
            .createdDate(CREATED_DATE)
            .updateDate(UPDATE_DATE)
            .build();
    }

    private static BeerOrder fullBeerOrder() {
        return BeerOrder.builder()
            .id(ORDER_ID)
            .version(3L)
            .paymentAmount(new BigDecimal("12.34"))
            .customerRef("ref-1")
            .customer(Customer.builder().id(CUSTOMER_ID).version(7).name("Jane").email("jane@example.com").build())
            .beerOrderLines(Set.of(BeerOrderLine.builder()
                .id(LINE_ID)
                .version(1L)
                .orderQuantity(5)
                .quantityAllocated(2)
                .orderLineStatus(BeerOrderLineStatus.NEW)
                .beer(Beer.builder().id(BEER_ID).beerName("Beer").beerStyle(BeerStyle.IPA).build())
                .build()))
            .beerOrderShipment(BeerOrderShipment.builder().id(SHIPMENT_ID).trackingNumber("track-1").build())
            .createdDate(CREATED_DATE)
            .updateDate(UPDATE_DATE)
            .build();
    }

}
