package ch.springframeworkguru.springrestmvc.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
public class CustomerDTO {

    private String customerName;

    private UUID id;

    private String version;

    private LocalDate createdDate;

    private LocalDate lastModifiedDate;

}
