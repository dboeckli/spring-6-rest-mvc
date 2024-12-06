package ch.springframeworkguru.springrestmvc.entity;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@ToString
public class BeerOrder {

    public BeerOrder(UUID id, 
                     Long version, 
                     Timestamp createdDate, 
                     Timestamp lastModifiedDate, 
                     String customerRef, 
                     Customer customer, 
                     Set<BeerOrderLine> beerOrderLines,
                     BeerOrderShipment beerOrderShipment) {
        this.id = id;
        this.version = version;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.customerRef = customerRef;
        this.setCustomer(customer);
        this.setBeerOrderLines(beerOrderLines);
        this.setBeerOrderShipment(beerOrderShipment);
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false )
    private UUID id;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;

    public boolean isNew() {
        return this.id == null;
    }

    private String customerRef;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<BeerOrderLine> beerOrderLines;

    @OneToOne(cascade = CascadeType.PERSIST)
    private BeerOrderShipment beerOrderShipment;

    public void setCustomer(Customer customer) {
        if (this.customer != null) {
            this.customer = customer;
            customer.getBeerOrders().add(this);
        }
    }

    public void setBeerOrderShipment(BeerOrderShipment beerOrderShipment) {
        if(beerOrderShipment != null) {
            this.beerOrderShipment = beerOrderShipment;
            beerOrderShipment.setBeerOrder(BeerOrder.this);
        }
    }
    
    public void setBeerOrderLines(Set<BeerOrderLine> beerOrderLines) {
        if (this.beerOrderLines != null) {
            this.beerOrderLines = beerOrderLines;
            beerOrderLines.forEach(beerOrderLine -> beerOrderLine.setBeerOrder(this));
        }
    }

}
