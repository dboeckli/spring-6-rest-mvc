package ch.springframeworkguru.springrestmvc.repository;

import ch.springframeworkguru.springrestmvc.entity.Beer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Testcontainers
@Slf4j
@ActiveProfiles("localmysql")
class MySqlIT {
    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.4.4");

    @Autowired
    BeerRepository beerRepository;

    @DynamicPropertySource
    static void mySqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
    }
    
    @Test
    void testListBeers() {
        List<Beer> beers = beerRepository.findAll();
        assertFalse(beers.isEmpty());
    }
}
