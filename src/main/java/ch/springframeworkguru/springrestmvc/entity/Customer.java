package ch.springframeworkguru.springrestmvc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    //@GenericGenerator(name = "UUID", type = UuidGenerator.class) // Deprecated, has been replaced with above
    @Column(unique = true, columnDefinition = "varchar", updatable = false, nullable = false)
    private UUID id;

    @Version
    private String version;

    private String customerName;

    private LocalDate createdDate;

    private LocalDate lastModifiedDate;
}
