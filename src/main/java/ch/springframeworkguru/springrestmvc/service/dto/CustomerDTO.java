package ch.springframeworkguru.springrestmvc.service.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@Jacksonized
public class CustomerDTO {

    private UUID id;

    private String version;

    private String customerName;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

}
