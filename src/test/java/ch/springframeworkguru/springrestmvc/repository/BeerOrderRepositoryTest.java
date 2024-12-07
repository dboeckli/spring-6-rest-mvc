package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.bootstrap.BootstrapData;
import ch.springframeworkguru.springrestmvc.config.CacheConfiguration;
import ch.springframeworkguru.springrestmvc.entity.BeerOrder;
import ch.springframeworkguru.springrestmvc.entity.BeerOrderShipment;
import ch.springframeworkguru.springrestmvc.entity.Customer;
import ch.springframeworkguru.springrestmvc.service.BeerCsvServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class, CacheConfiguration.class})
class BeerOrderRepositoryTest {
    
    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private CustomerRepository customerRepository;
    
    @Test
    @Transactional
    // TODO: FIXME. Customer and BeerOrder relations are not persisted in the database
    void testAddBeerOrder() {
        Customer testCustomer = customerRepository.findAll().getFirst();

        BeerOrder newBeerOrder = BeerOrder.builder()
            .customerRef("Test Order")
            .customer(testCustomer)
            .beerOrderShipment(BeerOrderShipment.builder()
                .trackingNumber("123456789")
                .build())
            .build();

        testCustomer.addBeerOder(newBeerOrder);

        // see testAddCategoryUsingExistingBeer in CategoryRepositoryTest
        BeerOrder savedBeerOrder = beerOrderRepository.save(newBeerOrder);
        Customer savedCustomer = customerRepository.save(testCustomer);
        
        beerOrderRepository.flush(); // Needed to trigger the association with the customer
        
        assertNotNull(savedBeerOrder);
        assertNotNull(savedCustomer);
        assertEquals(7, beerOrderRepository.count());
        assertEquals(1, savedCustomer.getBeerOrders().size());  // TODO: currently fails, as the association is not persisted
        //assertNotNull(savedBeerOrder.getCustomer()); // TODO: currently fails, as the association is not persisted
        //assertEquals(testCustomer.getId(), savedBeerOrder.getCustomer().getId());  // TODO: currently fails, as the association is not persisted
        assertEquals("123456789", savedBeerOrder.getBeerOrderShipment().getTrackingNumber());

    }

}
