package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customer;

    @Test
    void testSave() {
        Customer savedCustomer = customer.save(Customer.builder()
                .customerName("hallo")
                .build());

        assertAll(
                () -> assertNotNull(savedCustomer),
                () -> assertNotNull(savedCustomer.getId()),
                () -> assertNotNull(savedCustomer.getCustomerName()),
                () -> assertEquals("hallo", savedCustomer.getCustomerName())
        );
    }

}