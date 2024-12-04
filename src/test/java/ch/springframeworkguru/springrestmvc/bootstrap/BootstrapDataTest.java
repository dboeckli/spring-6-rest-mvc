package ch.springframeworkguru.springrestmvc.bootstrap;

import ch.springframeworkguru.springrestmvc.config.CacheConfiguration;
import ch.springframeworkguru.springrestmvc.repository.BeerRepository;
import ch.springframeworkguru.springrestmvc.repository.CustomerRepository;
import ch.springframeworkguru.springrestmvc.service.BeerCsvService;
import ch.springframeworkguru.springrestmvc.service.BeerCsvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
    CacheManager cacheManager;

    BootstrapData bootstrapData;

    @BeforeEach
    void setUp() {
        bootstrapData = new BootstrapData(beerRepository, customerRepository, beerCsvService, cacheManager);
    }

    @Test
    void testSetBootstrapData() throws Exception {
        bootstrapData.run((String) null);

        assertAll(
                () -> assertEquals(2413, beerRepository.count()),
                () -> assertEquals(3, customerRepository.count())
        );
    }

}
