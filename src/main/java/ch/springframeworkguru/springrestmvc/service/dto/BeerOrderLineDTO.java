package ch.springframeworkguru.springrestmvc.service.dto;

import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Builder
@Data
public class BeerOrderLineDTO {
    private UUID id;
    private Long version;

    @Min(value = 1, message = "Quantity On Hand must be greater than 0")
    private Integer orderQuantity;

    private Integer quantityAllocated;

    private BeerDTO beer;

    private Timestamp createdDate;
    private Timestamp updateDate;
}
