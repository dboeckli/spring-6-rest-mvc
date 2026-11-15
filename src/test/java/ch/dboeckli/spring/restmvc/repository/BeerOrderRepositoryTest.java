package ch.dboeckli.spring.restmvc.repository;

import ch.dboeckli.spring.restmvc.bootstrap.BootstrapData;
import ch.dboeckli.spring.restmvc.config.CacheConfiguration;
import ch.dboeckli.spring.restmvc.entity.BeerOrder;
import ch.dboeckli.spring.restmvc.entity.BeerOrderShipment;
import ch.dboeckli.spring.restmvc.entity.Customer;
import ch.dboeckli.spring.restmvc.service.BeerCsvServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import({ BootstrapData.class, BeerCsvServiceImpl.class, CacheConfiguration.class })
class BeerOrderRepositoryTest {

    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @Transactional
    @Rollback(true)
    // Customer and BeerOrder relations are not persisted in the database by this test
    void testAddBeerOrder() {
        Customer testCustomer = customerRepository.findAll().getFirst();

        BeerOrder newBeerOrder = BeerOrder.builder()
            .customerRef("Test Order")
            .customer(testCustomer)
            .beerOrderShipment(BeerOrderShipment.builder().trackingNumber("123456789").build())
            .build();

        BeerOrder savedBeerOrder = beerOrderRepository.save(newBeerOrder);
        Customer savedCustomer = customerRepository.save(testCustomer);

        beerOrderRepository.flush(); // Needed to trigger the association with the
                                     // customer

        assertNotNull(savedBeerOrder);
        assertNotNull(savedCustomer);
        assertEquals(7, beerOrderRepository.count());
        assertEquals("123456789", savedBeerOrder.getBeerOrderShipment().getTrackingNumber());

    }

}
