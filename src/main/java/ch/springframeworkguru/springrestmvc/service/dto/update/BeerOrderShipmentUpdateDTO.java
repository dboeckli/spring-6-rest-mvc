package ch.springframeworkguru.springrestmvc.service.dto.update;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BeerOrderShipmentUpdateDTO {

    @NotBlank
    private String trackingNumber;
    
}
