package ch.dboeckli.spring.restmvc.bootstrap;

import ch.dboeckli.spring.restmvc.config.CacheConfiguration;
import ch.dboeckli.spring.restmvc.repository.*;
import ch.dboeckli.spring.restmvc.service.BeerCsvService;
import ch.dboeckli.spring.restmvc.service.BeerCsvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({BeerCsvServiceImpl.class, CacheConfiguration.class})
class BootstrapDataTest {

    @Autowired
    BeerCsvService beerCsvService;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    BeerOrderLinesRepository beerOrderLinesRepository;

    @Autowired
    BeerAuditRepository beerAuditRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    CacheManager cacheManager;

    BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
        bootstrapData = new BootstrapData(beerRepository, customerRepository, beerCsvService, beerOrderRepository, cacheManager);
    }

    @Test
    void testSetBootstrapData() throws Exception {
        bootstrapData.run((String) null);
        assertAll(
            () -> assertEquals(503, beerRepository.count()),
            () -> assertEquals(3, customerRepository.count()),
            () -> assertEquals(6, beerOrderRepository.count()),
            () -> assertEquals(12, beerOrderLinesRepository.count()),
            () -> assertEquals(0, beerAuditRepository.count()),
            () -> assertEquals(0, categoryRepository.count()),
            () -> assertEquals(0, cacheManager.getCacheNames().size())
        );
    }

}
