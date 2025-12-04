package ch.dboeckli.spring.restmvc.entity;

import ch.guru.springframework.spring6restmvcapi.dto.BeerOrderLineStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BeerOrderLine {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp updateDate;
    @Builder.Default
    @Min(value = 1, message = "Quantity On Hand must be greater than 0")
    private Integer orderQuantity = 1;
    @Builder.Default
    private Integer quantityAllocated = 0;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private BeerOrderLineStatus orderLineStatus = BeerOrderLineStatus.NEW;
    @ManyToOne
    private BeerOrder beerOrder;
    @ManyToOne
    private Beer beer;

    public boolean isNew() {
        return this.id == null;
    }
}
