package ch.springframeworkguru.springrestmvc.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

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
public class Customer {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Integer version;

    private String customerName;

    @Column(length = 255)
    private String customerEmail;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDate;
    
    @Builder.Default
    //@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "customer") // TODO: CHECK
    @ToString.Exclude
    private Set<BeerOrder> beerOrders = new HashSet<>();

    /*
    public void addBeerOder(BeerOrder beerOrder) {
        beerOrders.add(beerOrder);
        beerOrder.setCustomer(this);
    }

    public void removeBeerOder(BeerOrder beerOrder) {
        beerOrders.remove(beerOrder);
        beerOrder.getCustomer().removeBeerOder(beerOrder);
    }*/
}
