package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    CustomerRepository customer;

    @Test
    void testSave() {
        Customer savedCustomer = customer.save(Customer.builder()
            .name("hallo")
            .build());

        assertAll(
            () -> assertNotNull(savedCustomer),
            () -> assertNotNull(savedCustomer.getId()),
            () -> assertNotNull(savedCustomer.getName()),
            () -> assertEquals("hallo", savedCustomer.getName())
        );
    }

}
