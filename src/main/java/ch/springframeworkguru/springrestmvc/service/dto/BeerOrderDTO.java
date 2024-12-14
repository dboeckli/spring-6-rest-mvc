package ch.springframeworkguru.springrestmvc.service.dto;

import ch.springframeworkguru.springrestmvc.service.dto.create.BeerOrderShipmentDTO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
public class BeerOrderDTO {
    private UUID id;
    private Long version;

    private BigDecimal paymentAmount;

    private String customerRef;

    private CustomerDTO customer;

    private Set<BeerOrderLineDTO> beerOrderLines;

    private BeerOrderShipmentDTO beerOrderShipment;

    private Timestamp createdDate;
    private Timestamp updateDate;
}
