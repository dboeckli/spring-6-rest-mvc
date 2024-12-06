package ch.springframeworkguru.springrestmvc.service.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Builder
@Data
public class BeerOrderLineDTO {
    private UUID id;
    private Long version;

    private Integer orderQuantity;

    private Integer quantityAllocated;

    private BeerDTO beer;

    private Timestamp createdDate;
    private Timestamp lastModifiedDate;
}
