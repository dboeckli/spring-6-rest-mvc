package ch.springframeworkguru.springrestmvc.entity;

import ch.springframeworkguru.springrestmvc.service.dto.BeerStyle;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Beer {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    //@GenericGenerator(name = "UUID", type = UuidGenerator.class) // Deprecated, has been replaced with above
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Integer version;

    @NotNull
    @NotBlank
    @Size(max=50)
    @Column(length = 50, nullable = false)
    private String beerName;

    @NotNull
    private BeerStyle beerStyle;

    @NotNull
    @NotBlank
    private String upc;
    
    private Integer quantityOnHand;

    @NotNull
    private BigDecimal price;
    
    @CreationTimestamp
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    private LocalDateTime updateDate;
}
