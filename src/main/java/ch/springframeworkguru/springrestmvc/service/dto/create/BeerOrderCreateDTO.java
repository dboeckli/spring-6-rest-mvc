package ch.springframeworkguru.springrestmvc.service.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class BeerOrderCreateDTO {

    private String customerRef;

    @NotNull
    private UUID customerId;

    private Set<BeerOrderLineCreateDTO> beerOrderLines;
    
}
