package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.bootstrap.BootstrapData;
import ch.springframeworkguru.springrestmvc.entity.BeerOrder;
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
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerOrderRepositoryTest {
    
    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private CustomerRepository customerRepository;
    
    @Test
    @Transactional
    void testAddBeerOrder() {
        Customer testCustomer = customerRepository.findAll().getFirst();

        BeerOrder newBeerOrder = BeerOrder.builder()
            .customerRef("Test Order")
            .customer(testCustomer)
            .build();

        BeerOrder savedBeerOrder = beerOrderRepository.save(newBeerOrder);
        
        assertNotNull(savedBeerOrder);
        assertEquals(1, beerOrderRepository.count());
        assertEquals(2413, beerRepository.count());
        assertEquals(3, customerRepository.count());
        assertEquals(1, testCustomer.getBeerOrders().size());
        assertNotNull(savedBeerOrder.getCustomer());
    }

}
