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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Beer {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
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
    @JdbcTypeCode(value = SqlTypes.SMALLINT)
    private BeerStyle beerStyle;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String upc;
    
    private Integer quantityOnHand;

    @NotNull
    private BigDecimal price;
    
    @OneToMany(mappedBy = "beer")
    @ToString.Exclude
    private Set<BeerOrderLine> beerOrderLines;

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(mappedBy = "beers")
    private Set<Category> categories = new HashSet<>();
    
    public void addCategory(Category category) {
        categories.add(category);
        category.getBeers().add(this);
    }

    public void removeCategory(Category category) {
        categories.remove(category);
        category.getBeers().remove(this);
    }

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
}
